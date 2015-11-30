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

import com.avrwb.assembler.SourceContext;

/**
 *
 * @author wolfi
 */
public final class Inline
{

  private final SourceContext fileContext;
  private final AssemblerSource src;

  public Inline(SourceContext fileContext,
                AssemblerSource src)
  {
    this.fileContext = fileContext;
    this.src = src;
  }

  public SourceContext getSourceContext()
  {
    return fileContext;
  }

  public AssemblerSource getSrc()
  {
    return src;
  }

}
