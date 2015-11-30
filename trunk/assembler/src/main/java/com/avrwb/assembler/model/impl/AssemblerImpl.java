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
package com.avrwb.assembler.model.impl;

import com.avrwb.assembler.Assembler;
import com.avrwb.assembler.AssemblerConfig;
import com.avrwb.assembler.AssemblerError;
import com.avrwb.assembler.AssemblerException;
import com.avrwb.assembler.AssemblerResult;
import com.avrwb.assembler.model.AssemblerSource;
import com.avrwb.assembler.model.ContextListener;
import com.avrwb.assembler.model.InternalAssembler;
import com.avrwb.assembler.parser.AtmelAsmLexer;
import com.avrwb.assembler.parser.AtmelAsmParser;
import java.io.IOException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author wolfi
 */
@ServiceProvider(service = Assembler.class)
public class AssemblerImpl implements InternalAssembler
{

  @Override
  public AssemblerResult compile(AssemblerSource source,
                                 ContextListener contextListener) throws IOException, AssemblerException
  {
    try {
      ANTLRInputStream ais = new ANTLRInputStream(source.getReader());
      ais.name = source.getSourcePath().toString();
      contextListener.getContext().pushSource(source);
      AtmelAsmLexer lexer = new AtmelAsmLexer(ais);
      CommonTokenStream tokenStream = new CommonTokenStream(lexer);
      AtmelAsmParser parser = new AtmelAsmParser(tokenStream);
      parser.addParseListener(contextListener);
      parser.init();
      contextListener.getContext().popSource();
      return new AssemblerResultImpl(contextListener.getContext());
    } catch (AssemblerError ae) {
      throw new AssemblerException(ae,
                                   ae.getSourceContext());
    }
  }

  @Override
  public AssemblerResult compile(AssemblerSource source,
                                 AssemblerConfig config) throws IOException, AssemblerException
  {
    return compile(source,
                   new ContextListener(this,
                                       config));
  }

}
