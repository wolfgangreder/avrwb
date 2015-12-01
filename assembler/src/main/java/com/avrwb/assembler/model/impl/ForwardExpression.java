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
import com.avrwb.assembler.model.Alias;
import com.avrwb.assembler.model.Context;
import com.avrwb.assembler.model.Expression;
import com.avrwb.assembler.model.ExpressionType;

public class ForwardExpression implements Expression
{

  private final SourceContext fileContext;
  private final String name;

  public ForwardExpression(String name,
                           SourceContext sourceContext)
  {
    this.fileContext = sourceContext;
    this.name = name;
  }

  @Override
  public ExpressionType getType()
  {
    return ExpressionType.FORWARD;
  }

  public Expression resolveForward(Context ctx)
  {
    Alias exp = ctx.getAlias(name);
    if (exp == null) {
      return null;
    }
    return exp.getExpression();
  }

  @Override
  public SourceContext getSourceContext()
  {
    return fileContext;
  }

  @Override
  public int evaluate(Context ctx) throws AssemblerError
  {
    Alias exp = ctx.getAlias(name);
    if (exp == null) {
      throw new AssemblerError("cannot find " + name,
                               fileContext);
    }
    return exp.getValue(ctx);
  }

}
