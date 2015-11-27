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
package com.avrwb.assembler.model;

import com.avrwb.assembler.AssemblerError;
import com.avrwb.assembler.model.impl.SegmentElementImpl;
import java.nio.ByteOrder;

/**
 *
 * @author wolfi
 */
public interface Expression
{

  public default boolean isStringExpression()
  {
    return false;
  }

  public int evaluate() throws AssemblerError;

  public default SegmentElement toSegmentElement(int offset,
                                                 int numBytes,
                                                 ByteOrder byteOrder) throws AssemblerError
  {
    int tmp = evaluate();
    switch (numBytes) {
      case 1:
        return SegmentElementImpl.getByteInstance(offset,
                                                  (byte) (tmp & 0xff));
      case 2:
        return SegmentElementImpl.getWordInstance(offset,
                                                  (short) (tmp & 0xffff),
                                                  byteOrder);
      case 4:
        return SegmentElementImpl.getDWordInstance(offset,
                                                   tmp,
                                                   byteOrder);
      default:
        throw new AssemblerError("illegal wordsize " + numBytes);
    }
  }

}
