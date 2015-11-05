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
package at.reder.avrwb.avr8.impl;

import at.reder.avrwb.avr8.Memory;
import at.reder.avrwb.avr8.ResetSource;
import at.reder.avrwb.io.MemoryChunk;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

final class MemoryImpl implements Memory
{

  private final String id;
  private final String name;
  private final ByteOrder byteOrder;
  private final int size;
  private final int start;
  private final ByteBuffer data;
  private final int initValue;

  MemoryImpl(String id,
             String name,
             ByteOrder byteOrder,
             int size,
             int start,
             int initValue)
  {
    this.id = id;
    this.name = name;
    this.byteOrder = byteOrder;
    this.size = size;
    this.start = start;
    this.initValue = initValue;
    this.data = ByteBuffer.allocate(size).order(byteOrder);
    for (int i = 0; i < size; ++i) {
      data.put(i, (byte) initValue);
    }
  }

  @Override
  public void reset(ResetSource source)
  {
    if (source == ResetSource.POWER_UP) {
      for (int i = 0; i < size; ++i) {
        data.put(i, (byte) initValue);
      }
    }
  }

  @Override
  public boolean initialize(MemoryChunk chunk)
  {
    Objects.requireNonNull(chunk, "chunk==null");
    ByteBuffer byteBuffer = chunk.getData();
    data.position(chunk.getStartAddress());
    int limit = Math.min(chunk.getSize(), Math.min(data.remaining(), byteBuffer.remaining()));
    byteBuffer.mark();
    try {
      for (int i = 0; i < limit; ++i) {
        data.put(byteBuffer.get());
      }
    } finally {
      data.rewind();
      byteBuffer.reset();
    }
    return limit >= chunk.getSize();
  }

  @Override
  public String getId()
  {
    return id;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public ByteOrder getByteOrder()
  {
    return byteOrder;
  }

  @Override
  public int getSize()
  {
    return size;
  }

  @Override
  public int getStart()
  {
    return start;
  }

  protected int checkAddress(int address) throws IndexOutOfBoundsException
  {
    int realAddress = address - start;
    if (realAddress < 0 || realAddress >= size) {
      throw new IndexOutOfBoundsException("address out of range");
    }
    return realAddress;
  }

  private ByteBuffer getBufferAtAddress(int address) throws IndexOutOfBoundsException
  {
    ByteBuffer buffer = data.duplicate();
    buffer.position(checkAddress(address));
    return buffer;
  }

  @Override
  public int getByteAt(int address)
  {
    return data.get(checkAddress(address));
  }

  @Override
  public void setByteAt(int address,
                        int value)
  {
    final int ra = checkAddress(address);
    data.put(ra, (byte) value);
  }

  @Override
  public int getWordAt(int address)
  {
    return data.getShort(checkAddress(address));
  }

  @Override
  public void setWordAt(int address,
                        int value)
  {
    data.putShort(checkAddress(address), (short) value);
  }

  @Override
  public long getDWordAt(int address)
  {
    return data.getInt(checkAddress(address));
  }

  @Override
  public void setDWordAt(int address,
                         long value)
  {
    data.putInt(checkAddress(address), (int) value);
  }

  @Override
  public long getQWordAt(int address)
  {
    return data.getLong(checkAddress(address));
  }

  @Override
  public void setQWordAt(int address,
                         long value)
  {
    data.putLong(checkAddress(address), value);
  }

  @Override
  public float getFloatAt(int address)
  {
    return data.getFloat(checkAddress(address));
  }

  @Override
  public void setFloatAt(int address,
                         float f)
  {
    data.putFloat(checkAddress(address), f);
  }

  private int readCharacters(char[] buffer,
                             int offset,
                             int numCharacters,
                             Charset charSet,
                             ByteBuffer dataView)
  {
    Objects.requireNonNull(buffer, "buffer==null");
    if (charSet == null) {
      charSet = StandardCharsets.US_ASCII;
    }
    CharsetDecoder decoder = charSet.newDecoder().
            onMalformedInput(CodingErrorAction.REPLACE).
            onUnmappableCharacter(CodingErrorAction.REPLACE).
            replaceWith("?");
    int maxLen = numCharacters >= 0 ? Math.min(numCharacters, buffer.length - offset) : buffer.length - offset;
    CharBuffer charBuffer = CharBuffer.wrap(buffer, offset, maxLen);
    decoder.decode(dataView, charBuffer, true);
    int read = charBuffer.position();
    if (numCharacters == -1) {
      int zeroPos = -1;
      charBuffer.rewind();
      while (zeroPos == -1 && charBuffer.hasRemaining()) {
        char ch = charBuffer.get();
        if (ch == (char) 0) {
          zeroPos = charBuffer.position() - 1;
        }
      }
      if (zeroPos >= 0) {
        read = zeroPos;
      }
    }
    return read;
  }

  @Override
  public int readCharacters(int address,
                            char[] buffer,
                            int offset,
                            int numCharacters,
                            Charset charSet)
  {
    return readCharacters(buffer, offset, numCharacters, charSet, getBufferAtAddress(address));
  }

  StringBuilder readString(int address,
                           Charset charSet,
                           int bufSize)
  {
    if (charSet == null) {
      charSet = StandardCharsets.US_ASCII;
    }
    Objects.requireNonNull(charSet, "charSet==null");
    StringBuilder result = new StringBuilder();
    char[] buffer = new char[bufSize];
    int read = -1;
    ByteBuffer dataView = getBufferAtAddress(address);
    while ((dataView.hasRemaining() && (read = readCharacters(buffer, 0, -1, charSet, dataView)) == buffer.length)) {
      result.append(buffer, 0, read);
      read = -1;
    }
    if (read > 0) {
      result.append(buffer, 0, read);
    }
    return result;
  }

  @Override
  public StringBuilder readString(int address,
                                  Charset charSet)
  {
    return readString(address, charSet, 1024);
  }

  @Override
  public int writeCharacters(int address,
                             char[] buffer,
                             int offset,
                             int toWrite,
                             Charset charSet)
  {
    Objects.requireNonNull(buffer, "buffer==null");
    if (charSet == null) {
      charSet = StandardCharsets.US_ASCII;
    }
    final int ra = checkAddress(address);
    final ByteBuffer dataView = getBufferAtAddress(address);
    CharsetEncoder encoder = charSet.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(
            CodingErrorAction.REPLACE).replaceWith(new byte[]{0});
    int maxLen = toWrite >= 0 ? Math.min(toWrite, buffer.length - offset) : buffer.length - offset;
    if (toWrite == -1) {
      for (int i = offset; i < buffer.length; ++i) {
        if (buffer[i] == 0) {
          maxLen = i;
          break;
        }
      }
    }
    CharBuffer charBuffer = CharBuffer.wrap(buffer, offset, maxLen);
    if (encoder.encode(charBuffer, dataView, true) == CoderResult.UNDERFLOW) {
      return dataView.position() - ra;
    } else {
      return size - address;
    }
  }

}
