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

import com.avrwb.assembler.model.AssemblerSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class StandardAssemblerSource implements AssemblerSource
{

  private final Path path;
  private final Charset charset;

  public StandardAssemblerSource(Path path)
  {
    this(path,
         StandardCharsets.UTF_8);
  }

  public StandardAssemblerSource(Path path,
                                 Charset charset)
  {
    this.path = path;
    this.charset = charset != null ? charset : StandardCharsets.UTF_8;
  }

  @Override
  public Reader getReader() throws IOException
  {
    return new InputStreamReader(Files.newInputStream(path,
                                                      StandardOpenOption.READ),
                                 charset);
  }

  @Override
  public Path getSourcePath()
  {
    return path;
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 67 * hash + Objects.hashCode(this.path);
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
    final StandardAssemblerSource other = (StandardAssemblerSource) obj;
    return Objects.equals(this.path,
                          other.path);
  }

  @Override
  public String toString()
  {
    return "StandardAssemblerSource{" + "path=" + path + '}';
  }

}
