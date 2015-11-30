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

import com.avrwb.assembler.Assembler;
import com.avrwb.assembler.AssemblerConfig;
import com.avrwb.assembler.AssemblerException;
import com.avrwb.assembler.AssemblerResult;
import com.avrwb.assembler.StandardAssemblerSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author wolfi
 */
public class TestContextListener
{

  private final Assembler assembler;
  private Context lastContext;

  public TestContextListener(Assembler assembler)
  {
    this.assembler = assembler;
  }

  public AssemblerResult parse(String toParse) throws IOException, AssemblerException
  {
    return parse(toParse,
                 null);
  }

  public AssemblerResult parse(String parse,
                               AssemblerConfig config) throws IOException, AssemblerException
  {
    AssemblerResult tmp = assembler.compile(constructFile(parse),
                                            config);
    lastContext = tmp.getLookup().lookup(Context.class);
    return tmp;
  }

  private AssemblerSource constructFile(String toParse) throws IOException
  {
    File result = File.createTempFile("TestContextListener",
                                      ".asm");
    result.deleteOnExit();
    try (Writer fw = new OutputStreamWriter(new FileOutputStream(result),
                                            StandardCharsets.UTF_8)) {
      fw.write(toParse);
    }
    return new StandardAssemblerSource(result.toPath());
  }

  public Context getContext()
  {
    return lastContext;
  }

}
