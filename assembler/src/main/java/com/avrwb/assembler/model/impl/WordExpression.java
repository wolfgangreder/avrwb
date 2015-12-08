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

import com.avrwb.assembler.AssemblerError;
import com.avrwb.assembler.SourceContext;
import com.avrwb.assembler.model.Context;
import com.avrwb.assembler.model.Expression;
import com.avrwb.assembler.model.ExpressionType;
import com.avrwb.assembler.model.SegmentElement;
import java.nio.ByteOrder;

/**
 *
 * @author wolfi
 */
public class WordExpression implements Expression
{

  private final SourceContext sourceContext;
  private final Expression lo;
  private final Expression hi;

  public WordExpression(SourceContext sourceContext,
                        Expression lo,
                        Expression hi)
  {
    this.sourceContext = sourceContext;
    this.lo = lo;
    this.hi = hi;
  }

  @Override
  public ExpressionType getType()
  {
    return ExpressionType.INTEGER;
  }

  @Override
  public SourceContext getSourceContext()
  {
    return sourceContext;
  }

  @Override
  public int evaluate(Context ctx) throws AssemblerError
  {
    int result = 0;
    if (hi != null) {
      result = (hi.evaluate(ctx) & 0xff) << 8;
    }
    if (lo != null) {
      result += lo.evaluate(ctx) & 0xff;
    }
    return result;
  }

  @Override
  public SegmentElement toSegmentElement(Context ctx,
                                         int offset,
                                         int numBytes,
                                         ByteOrder byteOrder) throws AssemblerError
  {
    return SegmentElementImpl.getWordInstance(ctx.getCurrentSegment(),
                                              offset,
                                              (short) (evaluate(ctx) & 0xffff),
                                              byteOrder,
                                              getSourceContext());
  }

}
