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
import com.avrwb.assembler.model.Context;
import com.avrwb.assembler.model.Expression;
import com.avrwb.assembler.model.FileContext;

/**
 *
 * @author wolfi
 */
public final class LogOrOperation extends AbstractBinaryOperation
{

  public LogOrOperation(Expression left,
                        Expression right,
                        FileContext fileContext)
  {
    super(left,
          right,
          "||",
          4,
          fileContext);
  }

  @Override
  public int evaluate(Context ctx) throws AssemblerError
  {
    int leftValue = getLeft().evaluate(ctx);
    int rightValue = getRight().evaluate(ctx);
    return leftValue != 0 || rightValue != 0 ? 1 : 0;
  }

}
