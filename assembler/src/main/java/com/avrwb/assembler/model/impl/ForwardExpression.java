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
import com.avrwb.assembler.model.Alias;
import com.avrwb.assembler.model.Context;
import com.avrwb.assembler.model.Expression;
import com.avrwb.assembler.model.FileContext;

public class ForwardExpression implements Expression
{

  private final FileContext fileContext;
  private final String name;

  public ForwardExpression(FileContext fileContext,
                           String name)
  {
    this.fileContext = fileContext;
    this.name = name;
  }

  @Override
  public FileContext getFileContext()
  {
    return fileContext;
  }

  @Override
  public int evaluate(Context ctx) throws AssemblerError
  {
    Alias exp = ctx.getAlias(name);
    if (exp == null) {
      throw new AssemblerError("cannot find " + name);
    }
    return exp.getValue(ctx);
  }

}
