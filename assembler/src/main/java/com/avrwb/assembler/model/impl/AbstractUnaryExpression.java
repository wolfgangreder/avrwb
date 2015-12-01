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
import com.avrwb.assembler.SourceContext;
import com.avrwb.assembler.model.Expression;
import com.avrwb.assembler.model.ExpressionType;
import com.avrwb.assembler.model.OperatorExpression;
import com.avrwb.assembler.model.UnaryExpression;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public abstract class AbstractUnaryExpression implements OperatorExpression, UnaryExpression
{

  private final Expression subExpression;
  private final String operator;
  private final int level;
  private final String text;
  private final SourceContext fileContext;

  public AbstractUnaryExpression(@NotNull Expression subExpression,
                                 @NotNull String operator,
                                 int level,
                                 SourceContext fileContext)
  {
    this(subExpression,
         operator,
         level,
         null,
         null,
         fileContext);
  }

  public AbstractUnaryExpression(@NotNull Expression subExpression,
                                 @NotNull String operator,
                                 int level,
                                 String preExpression,
                                 String postExpression,
                                 SourceContext fileContext)
  {
    Objects.requireNonNull(subExpression,
                           "subExpression==null");
    Objects.requireNonNull(operator,
                           "operator==null");
    this.subExpression = subExpression;
    this.operator = operator;
    this.level = level;
    StringBuilder tmp = new StringBuilder(operator);
    if (preExpression != null) {
      tmp.append(preExpression);
    }
    tmp.append(subExpression.toString());
    if (postExpression != null) {
      tmp.append(postExpression);
    }
    text = tmp.toString();
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
  public final Expression getSubExpression()
  {
    return subExpression;
  }

  @Override
  public String toString()
  {
    return text;
  }

}
