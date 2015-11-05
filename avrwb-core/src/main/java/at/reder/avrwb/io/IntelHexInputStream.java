/*
 * $Id$
 *
 * Copyright (C) 2015 Wolfgang Reder <wolfgang.reder@aon.at>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 *
 */
package at.reder.avrwb.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

public class IntelHexInputStream extends AbstractMemoryChunkInputStream
{

  private LineNumberReader reader;
  private final Queue<Integer> queue = new LinkedList<>();
  private int position = 0;
  private int offset = 0;

  public IntelHexInputStream(InputStream strm)
  {
    this(new InputStreamReader(strm, StandardCharsets.US_ASCII));
  }

  public IntelHexInputStream(Reader reader)
  {
    this.reader = new LineNumberReader(reader);
  }

  @Override
  public MemoryChunk read(ByteBuffer buffer) throws IOException
  {
    int chunkStart = position;
    int readOffset = buffer.position();
    boolean endOfRead = false;
    buffer.mark();
    try {
      if (!buffer.hasRemaining()) {
        throw new IllegalArgumentException("buffer.hasRemaining()==false");
      }
      while (!endOfRead && buffer.hasRemaining()) {
        Integer read = queue.poll();
        if (read != null) {
          buffer.put(read.byteValue());
          ++position;
        } else if (readNextLine() < 0) {
          endOfRead = true;
        } else if (position > chunkStart + buffer.position() - readOffset + 1) {
          endOfRead = (buffer.position() - readOffset) > 0;
          if (!endOfRead) {
            chunkStart = position;
          }
        }
      }
    } finally {
      readOffset = buffer.position() - readOffset;
      buffer.reset();
    }
    if (readOffset > 0) {
      return new DefaultMemoryChunk(chunkStart, buffer, readOffset);
    } else {
      return null;
    }
  }

  private int readNextLine() throws IOException
  {
    String line = reader.readLine();
    if (line != null) {
      return parseLine(line);
    }
    return -1;
  }

  private int parseLine(String pLine) throws IOException
  {
    try {
      checkLine(pLine);
      int byteCount = Integer.parseInt(pLine.substring(1, 3), 16);
      int recordType = Integer.parseInt(pLine.substring(7, 9), 16);
      switch (recordType) {
        case 0x00:// DataRecord
          position = Integer.parseInt(pLine.substring(3, 7), 16) + offset;
          processDataRecord(pLine.substring(9), byteCount);
          break;
        case 0x01://EOF
          recordType = -1;
          break;
        case 0x02://extendedsegementrecord
          processExtendedSegmentRecord(pLine.substring(9));
          break;
        case 0x03://segementrecord
          throw new IOException("Unexpected recordtype");
        case 0x04://extendendlinearaddress
          processLinearSegmentRecord(pLine.substring(9));
          break;
        case 0x05://startlinearaddress
          throw new IOException("Unexpected recordtype");
      }
      return recordType;
    } catch (NumberFormatException e) {
      throw new IOException("Invalid HexDigit at line " + reader.getLineNumber());
    }
  }

  private void processExtendedSegmentRecord(String pLine)
  {
    int lo = Integer.parseInt(pLine.substring(0, 2), 16);
    int high = Integer.parseInt(pLine.substring(2, 4), 16);
    offset = (lo | high << 8) << 3;
  }

  private void processLinearSegmentRecord(String pLine)
  {
    int lo = Integer.parseInt(pLine.substring(0, 2), 16);
    int high = Integer.parseInt(pLine.substring(2, 4), 16);
    offset = (lo | high << 8) << 15;
  }

  private void processDataRecord(String pLine, int byteCount)
  {
    for (int i = 0; i < 2 * byteCount; i += 2) {
      queue.offer(Integer.parseInt(pLine.substring(i, i + 2), 16));
    }
  }

  private boolean checkLine(String pLine) throws IOException
  {
    if (!pLine.startsWith(":")) {
      throw new IOException("Invalid record at line " + reader.getLineNumber());
    }
    if (pLine.length() % 2 == 0) {
      throw new IOException("Invalid line length at line " + reader.getLineNumber());
    }
    int sum = 0;
    for (int i = 1; i < pLine.length(); i += 2) {
      sum += Integer.parseInt(pLine.substring(i, i + 2), 16);
    }
    if ((sum & 0xff) != 0) {
      throw new IOException("Invalid checksum at line " + reader.getLineNumber());
    }
    return true;
  }

  @Override
  public void close() throws IOException
  {
    reader.close();
  }

  @Override
  public String toString()
  {
    return "IntelHexInputStream{" + "position=" + position + '}';
  }

}
