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

/**
 *
 * @author wolfi
 */
public class AssemblerException extends Exception
{

  private final SourceContext sourceContext;

  public AssemblerException(String msg,
                            SourceContext sourceContext)
  {
    super(msg);
    this.sourceContext = sourceContext;
  }

  public AssemblerException(Throwable th,
                            SourceContext sourceContext)
  {
    super(th);
    this.sourceContext = sourceContext;
  }

  public AssemblerException(String msg,
                            Throwable th,
                            SourceContext sourceContext)
  {
    super(msg,
          th);
    this.sourceContext = sourceContext;
  }

  public SourceContext getSourceContext()
  {
    return sourceContext;
  }

  public AssemblerError toWrapper()
  {
    return new AssemblerError(this,
                              sourceContext);
  }

}
