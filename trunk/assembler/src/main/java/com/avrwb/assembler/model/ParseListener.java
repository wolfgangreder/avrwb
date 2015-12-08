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

import com.avrwb.assembler.parser.AtmelAsmBaseListener;
import com.avrwb.assembler.parser.AtmelAsmLexer;
import com.avrwb.assembler.parser.AtmelAsmParser;
import java.util.BitSet;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 * @author wolfi
 */
public class ParseListener extends AtmelAsmBaseListener implements ANTLRErrorListener, ANTLRErrorStrategy
{

  private final AtmelAsmLexer lexer;
  private final AtmelAsmParser parser;
  private final ANTLRErrorStrategy stat = new DefaultErrorStrategy();

  public ParseListener(AtmelAsmLexer lexer,
                       AtmelAsmParser parser)
  {
    this.lexer = lexer;
    this.parser = parser;
  }

  @Override
  public void visitTerminal(TerminalNode node)
  {
    System.out.println("Visit terminal " + node.getText());
  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx)
  {
    System.out.println("Exit rule " + ctx.toInfoString(parser));
  }

  @Override
  public void enterEveryRule(ParserRuleContext ctx)
  {
    System.out.println("Enter rule" + ctx.toInfoString(parser));
  }

  @Override
  public void visitErrorNode(ErrorNode node)
  {
    System.out.println(node.toStringTree(parser));
  }

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer,
                          Object offendingSymbol,
                          int line,
                          int charPositionInLine,
                          String msg,
                          RecognitionException e)
  {
  }

  @Override
  public void reportAmbiguity(Parser recognizer,
                              DFA dfa,
                              int startIndex,
                              int stopIndex,
                              boolean exact,
                              BitSet ambigAlts,
                              ATNConfigSet configs)
  {
  }

  @Override
  public void reportAttemptingFullContext(Parser recognizer,
                                          DFA dfa,
                                          int startIndex,
                                          int stopIndex,
                                          BitSet conflictingAlts,
                                          ATNConfigSet configs)
  {
  }

  @Override
  public void reportContextSensitivity(Parser recognizer,
                                       DFA dfa,
                                       int startIndex,
                                       int stopIndex,
                                       int prediction,
                                       ATNConfigSet configs)
  {
  }

  @Override
  public void reset(Parser recognizer)
  {
    stat.reset(recognizer);
  }

  @Override
  public Token recoverInline(Parser recognizer) throws RecognitionException
  {
    return stat.recoverInline(recognizer);
  }

  @Override
  public void recover(Parser recognizer,
                      RecognitionException e) throws RecognitionException
  {
    stat.recover(recognizer,
                 e);
  }

  @Override
  public void sync(Parser recognizer) throws RecognitionException
  {
    stat.sync(recognizer);
  }

  @Override
  public boolean inErrorRecoveryMode(Parser recognizer)
  {
    return stat.inErrorRecoveryMode(recognizer);
  }

  @Override
  public void reportMatch(Parser recognizer)
  {
    stat.reportMatch(recognizer);
  }

  @Override
  public void reportError(Parser recognizer,
                          RecognitionException e)
  {
    stat.reportError(recognizer,
                     e);
  }

}
