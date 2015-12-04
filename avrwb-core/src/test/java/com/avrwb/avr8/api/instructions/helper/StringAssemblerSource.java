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
package com.avrwb.avr8.api.instructions.helper;

import com.avrwb.annotations.NotNull;
import com.avrwb.assembler.model.AssemblerSource;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class StringAssemblerSource implements AssemblerSource
{

  private final String source;
  private final String sourceName;

  public StringAssemblerSource(@NotNull String source,
                               @NotNull String sourceName)
  {
    Objects.requireNonNull(source,
                           "source==null");
    Objects.requireNonNull(sourceName,
                           "sourceName==null");
    this.source = source;
    this.sourceName = sourceName;
  }

  @Override
  public Reader getReader() throws IOException
  {
    return new StringReader(source);
  }

  @Override
  public Path getSourcePath()
  {
    return null;
  }

  @Override
  public String getSourceName()
  {
    return sourceName;
  }

}
