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
package at.reder.avrwb.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
public final class IntelHexOutputStream extends OutputStream implements MemoryChunkOutputStream
{

  public static final Charset CHARSET = StandardCharsets.US_ASCII;
  private static final byte[] EOL = "\r\n".getBytes(CHARSET);
  private static final byte[] EOF = ":00000001FF\r\n".getBytes(CHARSET);
  private final OutputStream wrapped;
  private final StringBuilder currentLine = new StringBuilder();
  private int bytesInRecord;
  private int checkSum;
  private int address;
  private int lineStartAddress;
  private final int maxBytesInLine;

  public IntelHexOutputStream(OutputStream wrapped)
  {
    this(wrapped, 16);
  }

  public IntelHexOutputStream(OutputStream wrapped, int maxBytesInLine)
  {
    this.wrapped = wrapped;
    checkSum = 0;
    address = 0;
    lineStartAddress = 0;
    this.maxBytesInLine = maxBytesInLine;
  }

  public final int getMaxBytesInLine()
  {
    return maxBytesInLine;
  }

  public int getAddress()
  {
    return address;
  }

  private int getHighAddress()
  {
    return (address & 0xffff0000) >> 16;
  }

  private int getLowAddress()
  {
    return address & 0xffff;
  }

  public void setAddress(int newAddress) throws IOException
  {
    int oldAddress = getAddress();
    if (oldAddress != newAddress) {
      int oldHighAddress = getHighAddress();
      address = newAddress;
      if (oldAddress + 1 != newAddress) {
        flushLine();
      }
      if (getHighAddress() != oldHighAddress) {
        flushLine();
        writeHighAddressLine();
      }
    }
  }

  private void incrementAddress() throws IOException
  {
    if (getLowAddress() == 0xffff) {
      flushLine();
      writeHighAddressLine();
    }
    ++address;
  }

  private String byteToHex(int b)
  {
    return Integer.toHexString((b | 0x100) & 0x1ff).substring(1).toUpperCase();
  }

  private String wordToHex(int b)
  {
    return Integer.toHexString((b | 0x10000) & 0x1ffff).substring(1).toUpperCase();
  }

  @Override
  public void write(int b) throws IOException
  {
    currentLine.append(byteToHex(b));
    checkSum += b & 0xff;
    ++bytesInRecord;
    incrementAddress();
    if (bytesInRecord == maxBytesInLine) {
      flushLine();
    }
  }

  private void writeHighAddressLine() throws IOException
  {
    StringBuilder line = new StringBuilder(":02000004");
    int highAddress = getHighAddress();
    line.append(wordToHex(highAddress));
    int cs = (-(6 + highAddress)) & 0xff;
    line.append(byteToHex(cs));
    wrapped.write(line.toString().getBytes(CHARSET));
    wrapped.write(EOL);
  }

  @Override
  public void flush() throws IOException
  {
    flushLine();
    wrapped.flush();
  }

  public void flushLine() throws IOException
  {
    if (bytesInRecord > 0) {
      StringBuilder line = new StringBuilder(":");
      checkSum += bytesInRecord;
      checkSum += lineStartAddress & 0xff;
      checkSum += (lineStartAddress & 0xff00) >> 8;
      line.append(byteToHex(bytesInRecord));
      line.append(wordToHex(lineStartAddress));
      line.append("00");
      line.append(currentLine);
      line.append(byteToHex((-checkSum) & 0xff));
      wrapped.write(line.toString().getBytes(CHARSET));
      wrapped.write(EOL);
      bytesInRecord = 0;
      checkSum = 0;
      currentLine.setLength(0);
    }
    lineStartAddress = getLowAddress();
  }

  private void writeEndRecord() throws IOException
  {
    wrapped.write(EOF);
  }

  @Override
  public void write(MemoryChunk chunk) throws IOException
  {
    Objects.requireNonNull(chunk, "chunk==null");
    setAddress(chunk.getStartAddress());
    ByteBuffer buffer = chunk.getData();
    if (buffer.hasArray()) {
      write(buffer.array(), 0, chunk.getSize());
    } else {
      byte[] tmp = new byte[4096];
      int written = 0;
      while (buffer.hasRemaining() && written < chunk.getSize()) {
        int len = Math.min(chunk.getSize(), Math.min(tmp.length, buffer.remaining()));
        buffer.get(tmp, 0, len);
        write(tmp, 0, len);
        written += len;
      }
    }
  }

  @Override
  public void close() throws IOException
  {
    flushLine();
    writeEndRecord();
    wrapped.close();
  }

  @Override
  public String toString()
  {
    return "IntelHexOutputStream{" + "address=" + getAddress() + ", lineStartAddress=" + lineStartAddress + '}';
  }

}
