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
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

/**
 *
 * @author wolfi
 */
public class StringExpression implements Expression
{

  private final String str;
  private final Charset charset;
  private final SourceContext fileContext;

  public StringExpression(String str,
                          Charset charset,
                          SourceContext fileContext)
  {
    this.str = str;
    this.charset = charset;
    this.fileContext = fileContext;
  }

  @Override
  public ExpressionType getType()
  {
    return ExpressionType.STRING;
  }

  @Override
  public SourceContext getSourceContext()
  {
    return fileContext;
  }

  @Override
  public int evaluate(Context ctx) throws AssemblerError
  {
    throw new AssemblerError("cannot convert string to number",
                             getSourceContext());
  }

  @Override
  public SegmentElement toSegmentElement(Context ctx,
                                         int offset,
                                         int numBytes,
                                         ByteOrder byteOrder) throws AssemblerError
  {
    try {
      return SegmentElementImpl.getStringInstance(ctx.getCurrentSegment(),
                                                  offset,
                                                  str,
                                                  byteOrder,
                                                  charset,
                                                  fileContext);
    } catch (CharacterCodingException ex) {
      throw new AssemblerError(ex.getMessage(),
                               getSourceContext());
    }
  }

}
