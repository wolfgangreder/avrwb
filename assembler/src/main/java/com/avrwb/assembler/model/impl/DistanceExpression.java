/*
 * $Id$
 *
 * Author Wolfgang Reder (w.reder@mountain-sd.at)
 *
 * Copyright (c) 2015 Mountain Software Design KG
 *
 * Diese Datei unterliegt der Mountain Software Design Sourcecode Lizenz.
 */
package com.avrwb.assembler.model.impl;

import com.avrwb.assembler.AssemblerError;
import com.avrwb.assembler.SourceContext;
import com.avrwb.assembler.model.Context;
import com.avrwb.assembler.model.Expression;
import com.avrwb.assembler.model.ExpressionType;
import com.avrwb.assembler.model.Segment;

public class DistanceExpression implements Expression
{

  private final int position;
  private final Expression labelExpression;
  private final SourceContext fileContext;
  private final Segment segment;

  public DistanceExpression(int position,
                            Expression labelExpression,
                            SourceContext fileContext,
                            Segment segment)
  {
    this.position = position;
    this.labelExpression = labelExpression;
    this.fileContext = fileContext;
    this.segment = segment;
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
    int labelVal = labelExpression.evaluate(ctx);
    int tmp = labelVal - position - 1;
    if (segment == Segment.CSEG) {
      tmp >>= 1;
    }
    return tmp;
  }

}
