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

/**
 *
 * @author wolfi
 */
public final class IntExpression implements Expression
{

  private final int i;
  private final String text;
  private final SourceContext fileContext;

  public IntExpression(String str,
                       SourceContext fileContext)
  {
    if (str.startsWith("$")) {
      i = Integer.parseInt(str.substring(1),
                           16);
    } else if (str.startsWith("0b")) {
      i = Integer.parseInt(str.substring(2),
                           2);
    } else {
      i = Integer.decode(str);
    }
    text = str;
    this.fileContext = fileContext;
  }

  public IntExpression(int i,
                       SourceContext fileContext)
  {
    this.i = i;
    text = Integer.toString(i);
    this.fileContext = fileContext;
  }

  @Override
  public ExpressionType getType()
  {
    return ExpressionType.INTEGER;
  }

  @Override
  public SourceContext getSourceContext()
  {
    return fileContext;
  }

  @Override
  public int evaluate(Context ctx) throws AssemblerError
  {
    return i;
  }

  @Override
  public String toString()
  {
    return text;
  }

}
