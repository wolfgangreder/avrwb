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

import com.avrwb.assembler.AssemblerException;
import com.avrwb.assembler.model.Expression;

/**
 *
 * @author wolfi
 */
public final class LessEqualOperation extends AbstractBinaryOperation
{

  public LessEqualOperation(Expression left,
                            Expression right)
  {
    super(left,
          right,
          "<=",
          10);
  }

  @Override
  public int evaluate() throws AssemblerException
  {
    int leftValue = getLeft().evaluate();
    int rightValue = getRight().evaluate();
    return leftValue <= rightValue ? 1 : 0;
  }

}
