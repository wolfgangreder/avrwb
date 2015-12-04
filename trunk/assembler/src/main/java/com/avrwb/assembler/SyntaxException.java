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

import org.antlr.v4.runtime.RecognitionException;

/**
 *
 * @author wolfi
 */
public class SyntaxException extends AssemblerException
{

  public SyntaxException(RecognitionException err)
  {
    super(AssemblerError.constructMessage(err),
          err,
          AssemblerError.createSourceContext(err));
  }

  public SyntaxException(String msg,
                         SourceContext sourceContext)
  {
    super(msg,
          sourceContext);
  }

  public SyntaxException(Throwable th,
                         SourceContext sourceContext)
  {
    super(th,
          sourceContext);
  }

  public SyntaxException(String msg,
                         Throwable th,
                         SourceContext sourceContext)
  {
    super(msg,
          th,
          sourceContext);
  }

}
