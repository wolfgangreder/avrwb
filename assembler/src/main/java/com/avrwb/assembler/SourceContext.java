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

import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class SourceContext
{

  private final String sourceFile;
  private final int line;
  private final int col;

  public SourceContext(String sourceFile,
                       int line,
                       int col)
  {
    this.sourceFile = sourceFile;
    this.line = line;
    this.col = col;
  }

  public String getSourceFile()
  {
    return sourceFile;
  }

  public int getLine()
  {
    return line;
  }

  public int getCol()
  {
    return col;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 13 * hash + Objects.hashCode(this.sourceFile);
    hash = 13 * hash + this.line;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SourceContext other = (SourceContext) obj;
    if (this.line != other.line) {
      return false;
    }
    return Objects.equals(this.sourceFile,
                          other.sourceFile);
  }

  @Override
  public String toString()
  {
    return "(" + line + "," + col + ")@" + sourceFile;
  }

}
