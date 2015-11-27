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
package com.avrwb.assembler.model.impl;

import com.avrwb.assembler.model.SegmentElement;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 *
 * @author wolfi
 */
public class SegmentElementImpl implements SegmentElement
{

  private final int offset;
  private final ByteBuffer data;

  public static SegmentElement getByteInstance(int offset,
                                               byte b)
  {
    return new SegmentElementImpl(offset,
                                  ByteBuffer.wrap(new byte[]{b}));
  }

  public static SegmentElement getWordInstance(int offset,
                                               int word,
                                               ByteOrder byteOrder)
  {
    return new SegmentElementImpl(offset,
                                  ByteBuffer.allocate(2).
                                  order(byteOrder).
                                  putShort((short) (word & 0xffff)));
  }

  public static SegmentElement getDWordInstance(int offset,
                                                int dword,
                                                ByteOrder byteOrder)
  {
    return new SegmentElementImpl(offset,
                                  ByteBuffer.allocate(4).
                                  order(byteOrder).
                                  putInt(dword));

  }

  public static SegmentElement getStringInstance(int offset,
                                                 String string,
                                                 ByteOrder byteOrder,
                                                 Charset charset) throws CharacterCodingException
  {
    CharsetEncoder encoder = charset.newEncoder();
    CharBuffer charBuffer = CharBuffer.allocate(string.length() + 1);
    charBuffer.append(string);
    charBuffer.append((char) 0);
    charBuffer.rewind();
    return new SegmentElementImpl(offset,
                                  encoder.encode(charBuffer).order(byteOrder));

  }

  public static SegmentElement getArrayInstance(int offset,
                                                byte[] data,
                                                ByteOrder byteOrder)
  {
    return new SegmentElementImpl(offset,
                                  ByteBuffer.allocate(data.length).
                                  order(byteOrder).
                                  put(data));
  }

  private SegmentElementImpl(int offset,
                             ByteBuffer data)
  {
    this.offset = offset;
    this.data = data;
  }

  @Override
  public int getOffset()
  {
    return offset;
  }

  @Override
  public int getSize()
  {
    return data.capacity();
  }

  @Override
  public ByteBuffer getData()
  {
    return data.asReadOnlyBuffer();
  }

  @Override
  public int compareTo(SegmentElement o)
  {
    return Integer.compare(offset,
                           o.getOffset());
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 47 * hash + this.offset;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SegmentElementImpl other = (SegmentElementImpl) obj;
    return this.offset == other.offset;
  }

}
