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
import com.avrwb.assembler.model.Context;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 *
 * @author wolfi
 */
public class StandardFileResolver implements FileResolver
{

  private final Charset charset;

  public StandardFileResolver()
  {
    this(null);
  }

  public StandardFileResolver(Charset charset)
  {
    this.charset = charset != null ? charset : StandardCharsets.UTF_8;
  }

  private Path resolveWithPaths(Path path,
                                Collection<Path> paths)
  {
    for (Path currentPath : paths) {
      Path result = Paths.get(currentPath.toString(),
                              path.toString());
      if (Files.isReadable(result)) {
        return result;
      }
    }
    return null;
  }

  @Override
  public AssemblerSource resolveFile(Context context,
                                     String path) throws IOException
  {
    Path result;
    Path tmp = Paths.get(path);
    if (tmp.isAbsolute()) {
      result = tmp;
    } else {
      Path parent = context.getCurrentSource().getSourcePath().getParent();
      result = Paths.get(parent.toString(),
                         path);
      if (!Files.isReadable(result)) {
        result = resolveWithPaths(tmp,
                                  context.getConfig().getIncludePaths());
      }
    }
    return new StandardAssemblerSource(result,
                                       charset);
  }

}
