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

import com.avrwb.assembler.AssemblerException;
import com.avrwb.assembler.parser.AtmelAsmLexer;
import com.avrwb.assembler.parser.AtmelAsmParser;
import java.io.IOException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 *
 * @author wolfi
 */
public class TestContextListener extends ContextListener
{

  public TestContextListener()
  {
    super(null);
  }

  void parse(String text) throws IOException, AssemblerException
  {
    try (StringInputStream is = new StringInputStream(text)) {
      ANTLRInputStream ais = new ANTLRInputStream(is);
      AtmelAsmLexer lexer = new AtmelAsmLexer(ais);
      CommonTokenStream tokenStream = new CommonTokenStream(lexer);
      AtmelAsmParser parser = new AtmelAsmParser(tokenStream);
      parser.addParseListener(this);
      parser.init();
    }
  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx)
  {
    super.exitEveryRule(ctx);
  }

}
