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
package com.avrwb.assembler.model;

import java.util.Objects;

/**
 *
 * @author wolfi
 */
public class FileContext
{

  private final AssemblerSource src;
  private final int lineNumber;

  public FileContext(AssemblerSource src,
                     int lineNumber)
  {
    this.src = src;
    this.lineNumber = lineNumber;
  }

  public AssemblerSource getSource()
  {
    return src;
  }

  public int getLineNumber()
  {
    return lineNumber;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.src);
    hash = 53 * hash + this.lineNumber;
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
    final FileContext other = (FileContext) obj;
    if (this.lineNumber != other.lineNumber) {
      return false;
    }
    return Objects.equals(this.src,
                          other.src);
  }

  @Override
  public String toString()
  {
    return "FileContext{" + "src=" + src + ", lineNumber=" + lineNumber + '}';
  }

}
