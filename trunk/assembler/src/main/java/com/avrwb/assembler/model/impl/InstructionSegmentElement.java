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

import com.avrwb.annotations.Invariants;
import com.avrwb.annotations.NotNull;
import com.avrwb.assembler.SourceContext;
import com.avrwb.assembler.model.Segment;
import com.avrwb.assembler.model.SegmentElement;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * @author wolfi
 */
public class InstructionSegmentElement extends AbstractSegmentElement
{

  private final Supplier<Integer> opcodeGenerator;
  private final int size;
  private ByteBuffer data;
  private final ByteOrder byteOrder;

  public static SegmentElement getWordInstance(Segment segment,
                                               int offset,
                                               @NotNull Supplier<Integer> opcodeGenerator,
                                               @NotNull ByteOrder byteOrder,
                                               SourceContext fileContext)
  {
    return new InstructionSegmentElement(segment,
                                         offset,
                                         2,
                                         opcodeGenerator,
                                         byteOrder,
                                         fileContext);
  }

  public static SegmentElement getDWordInstance(Segment segment,
                                                int offset,
                                                @NotNull Supplier<Integer> opcodeGenerator,
                                                @NotNull ByteOrder byteOrder,
                                                SourceContext fileContext)
  {
    return new InstructionSegmentElement(segment,
                                         offset,
                                         4,
                                         opcodeGenerator,
                                         byteOrder,
                                         fileContext);
  }

  public InstructionSegmentElement(Segment segment,
                                   int offset,
                                   @Invariants(allowedValues = {"2", "4"}) int size,
                                   @NotNull Supplier<Integer> opcodeGenerator,
                                   @NotNull ByteOrder byteOrder,
                                   SourceContext fileContext)
  {
    super(segment,
          offset,
          fileContext);
    Objects.requireNonNull(opcodeGenerator,
                           "opcodeGenerator==null");
    if (size != 2 && size != 4) {
      throw new IllegalArgumentException("size not 2 or 4");
    }
    this.opcodeGenerator = opcodeGenerator;
    this.size = size;
    this.byteOrder = byteOrder;
  }

  @Override
  public int getSize()
  {
    return size;
  }

  @Override
  public ByteBuffer getData()
  {
    if (data == null) {
      data = ByteBuffer.allocate(size).order(byteOrder);
      if (size == 2) {
        data.putShort((short) (opcodeGenerator.get() & 0xffff));
      } else {
        int opc = opcodeGenerator.get();
        data.putShort((short) ((opc >> 16) & 0xffff));
        data.putShort((short) (opc & 0xffff));
      }
    }
    ByteBuffer result = data.asReadOnlyBuffer().order(byteOrder);
    result.position(0);
    return result;
  }

}
