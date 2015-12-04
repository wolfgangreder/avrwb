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
package com.avrwb.assembler;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

/**
 *
 * @author wolfi
 */
public class AssemblerError extends Error
{

  private final SourceContext sourceContext;

  public AssemblerError(String message,
                        SourceContext sourceContext)
  {
    super(message);
    this.sourceContext = sourceContext;
  }

  public AssemblerError(Throwable th,
                        SourceContext sourceContext)
  {
    super(th);
    this.sourceContext = sourceContext;
  }

  public AssemblerError(String message,
                        Throwable th,
                        SourceContext sourceContext)
  {
    super(message,
          th);
    this.sourceContext = sourceContext;
  }

  public SourceContext getSourceContext()
  {
    return sourceContext;
  }

  public static SourceContext createSourceContext(RecognitionException e)
  {
    Token offending = e.getOffendingToken();
    int line = offending.getLine();
    int posInLine = offending.getCharPositionInLine();
    return new SourceContext(e.getInputStream().getSourceName(),
                             line,
                             posInLine);
  }

  public static String constructMessage(RecognitionException e)
  {
    Recognizer<?, ?> rec = e.getRecognizer();
    Token offending = e.getOffendingToken();
    int line = offending.getLine();
    int posInLine = offending.getCharPositionInLine();
    String msg = e.getMessage();
    return constructMessage(rec,
                            offending,
                            line,
                            posInLine,
                            msg,
                            e);
  }

  public static String constructMessage(Recognizer<?, ?> recognizer,
                                        Token offendingSymbol,
                                        int line,
                                        int charPositionInLine,
                                        String msg,
                                        RecognitionException e)
  {
    final String newLine = System.getProperty("line.separator");
    StringBuilder builder = new StringBuilder("Sytax error at line ");
    String file = e.getInputStream().getSourceName();
    builder.append(line);
    builder.append(':');
    builder.append(charPositionInLine);
    if (file != null) {
      builder.append('@');
      builder.append(file);
    }
    builder.append(": ");
    if (msg != null) {
      builder.append(msg);
    }
    builder.append(newLine);
    builder.append(getLine(recognizer,
                           line));
    builder.append(newLine);
    for (int i = 0; i < charPositionInLine; ++i) {
      builder.append(' ');
    }
    int start = offendingSymbol.getStartIndex();
    int stop = offendingSymbol.getStopIndex();
    if (start >= 0 && stop >= 0) {
      for (int i = start; i <= stop; ++i) {
        builder.append('^');
      }
    }
    return builder.toString();
  }

  protected static String getLine(Recognizer<?, ?> recognizer,
                                  int line)
  {
    CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
    String input = tokens.getTokenSource().getInputStream().toString();
    String[] lines = input.split("\n");
    return lines[line - 1];
  }

}
