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

import com.avrwb.annotations.NotNull;
import com.avrwb.assembler.model.BinaryExpression;
import com.avrwb.assembler.model.Expression;
import com.avrwb.assembler.model.FileContext;
import com.avrwb.assembler.model.OperatorExpression;

/**
 *
 * @author wolfi
 */
public abstract class AbstractBinaryOperation implements OperatorExpression, BinaryExpression
{

  private final Expression left;
  private final Expression right;
  private final String operator;
  private final String text;
  private final int level;
  private final FileContext fileContext;

  public AbstractBinaryOperation(@NotNull Expression left,
                                 @NotNull Expression right,
                                 @NotNull String operator,
                                 int level,
                                 FileContext fileContext)
  {
    this.left = left;
    this.right = right;
    this.operator = operator;
    StringBuilder tmp = new StringBuilder(left.toString());
    tmp.append(operator);
    tmp.append(right.toString());
    text = tmp.toString();
    this.level = level;
    this.fileContext = fileContext;
  }

  @Override
  public FileContext getFileContext()
  {
    return fileContext;
  }

  @Override
  public final String getOperator()
  {
    return operator;
  }

  @Override
  public final int getLevel()
  {
    return level;
  }

  @Override
  public Expression getLeft()
  {
    return left;
  }

  @Override
  public Expression getRight()
  {
    return right;
  }

  @Override
  public String toString()
  {
    return text;
  }

}
