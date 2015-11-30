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

import com.avrwb.assembler.model.FileContext;
import com.avrwb.assembler.model.Segment;
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
public class SegmentElementImpl extends AbstractSegmentElement
{

  private final ByteBuffer data;

  public static SegmentElement getByteInstance(Segment segment,
                                               int offset,
                                               byte b,
                                               FileContext fctx)
  {
    return new SegmentElementImpl(segment,
                                  offset,
                                  ByteBuffer.wrap(new byte[]{b}),
                                  fctx);
  }

  public static SegmentElement getWordInstance(Segment segment,
                                               int offset,
                                               int word,
                                               ByteOrder byteOrder,
                                               FileContext fctx)
  {
    return new SegmentElementImpl(segment,
                                  offset,
                                  ByteBuffer.allocate(2).
                                  order(byteOrder).
                                  putShort((short) (word & 0xffff)),
                                  fctx);
  }

  public static SegmentElement getDWordInstance(Segment segment,
                                                int offset,
                                                int dword,
                                                ByteOrder byteOrder,
                                                FileContext fctx)
  {
    return new SegmentElementImpl(segment,
                                  offset,
                                  ByteBuffer.allocate(4).
                                  order(byteOrder).
                                  putInt(dword),
                                  fctx);

  }

  public static SegmentElement getStringInstance(Segment segment,
                                                 int offset,
                                                 String string,
                                                 ByteOrder byteOrder,
                                                 Charset charset,
                                                 FileContext fctx) throws CharacterCodingException
  {
    CharsetEncoder encoder = charset.newEncoder();
    CharBuffer charBuffer = CharBuffer.allocate(string.length() + 1);
    charBuffer.append(string);
    charBuffer.append((char) 0);
    charBuffer.rewind();
    return new SegmentElementImpl(segment,
                                  offset,
                                  encoder.encode(charBuffer).order(byteOrder),
                                  fctx);

  }

  public static SegmentElement getArrayInstance(Segment segment,
                                                int offset,
                                                byte[] data,
                                                ByteOrder byteOrder,
                                                FileContext fctx)
  {
    return new SegmentElementImpl(segment,
                                  offset,
                                  ByteBuffer.allocate(data.length).
                                  order(byteOrder).
                                  put(data),
                                  fctx);
  }

  private SegmentElementImpl(Segment segment,
                             int offset,
                             ByteBuffer data,
                             FileContext fctx)
  {
    super(segment,
          offset,
          fctx);
    this.data = data;
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

}
