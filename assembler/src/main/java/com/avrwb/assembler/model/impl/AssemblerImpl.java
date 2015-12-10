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
import com.avrwb.assembler.SyntaxException;
import com.avrwb.assembler.model.AssemblerSource;
import com.avrwb.assembler.model.ContextListener;
import com.avrwb.assembler.model.InternalAssembler;
import com.avrwb.assembler.model.ParseListener;
import com.avrwb.assembler.parser.AtmelAsmLexer;
import com.avrwb.assembler.parser.AtmelAsmParser;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = Assembler.class)
public class AssemblerImpl implements InternalAssembler
{

  private static final class AppendNewLineReader extends Reader
  {

    private final Reader wrapped;

    public AppendNewLineReader(Reader wrapped)
    {
      this.wrapped = wrapped;
    }

    @Override
    public int read(char[] cbuf,
                    int off,
                    int len) throws IOException
    {
      int read = wrapped.read(cbuf,
                              off,
                              len);
      if (read >= 0 && read < len) {
        cbuf[off + read] = '\n';
        ++read;
      }
      return read;
    }

    @Override
    public void close() throws IOException
    {
      wrapped.close();
    }

  }

  @Override
  public AssemblerResult compile(AssemblerSource source,
                                 ContextListener contextListener) throws IOException, AssemblerException
  {
    try {
      AssemblerConfig cfg = contextListener.getContext().getConfig();
      ANTLRInputStream ais = new ANTLRInputStream(new AppendNewLineReader(source.getReader()));
      ais.name = source.getSourceName();
      contextListener.getContext().pushSource(source);
      AtmelAsmLexer lexer = new AtmelAsmLexer(ais);

      CommonTokenStream tokenStream = new CommonTokenStream(lexer);
      AtmelAsmParser parser = new AtmelAsmParser(tokenStream);
      ParseTreeWalker walker = new ParseTreeWalker();
      ParseListener pl = new ParseListener(lexer,
                                           parser,
                                           cfg.isTracingEnabled());
//      parser.setErrorHandler(pl);
      parser.addParseListener(pl);
      parser.addErrorListener(pl);
      if (cfg.isTracingEnabled()) {
        parser.setTrace(true);
      }
      ParserRuleContext tree = parser.init();
      Collection<RecognitionException> errors = pl.getErrors();
      if (!errors.isEmpty()) {
        throw new SyntaxException(errors);
      }
      walker.walk(contextListener,
                  tree);
      contextListener.getContext().popSource();
      return new AssemblerResultImpl(contextListener.getContext());
    } catch (RecognitionException e) {
      throw new SyntaxException(e);
    } catch (ParseCancellationException e) {
      if (e.getCause() instanceof RecognitionException) {
        throw new SyntaxException((RecognitionException) e.getCause());
      } else {
        throw new AssemblerException(e.getCause(),
                                     null);
      }
    } catch (AssemblerError ae) {
      if (ae.getCause() instanceof AssemblerException) {
        throw ((AssemblerException) ae.getCause());
      } else {
        throw new AssemblerException(ae,
                                     ae.getSourceContext());
      }
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
