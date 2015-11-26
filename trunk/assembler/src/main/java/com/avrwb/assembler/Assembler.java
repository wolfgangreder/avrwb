/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avrwb.assembler;

import com.avrwb.assembler.parser.AtmelasmBaseListener;
import com.avrwb.assembler.parser.AtmelasmLexer;
import com.avrwb.assembler.parser.AtmelasmParser;
import com.avrwb.io.IntelHexOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 * @author wolfi
 */
public final class Assembler
{

  private static final class MyListener extends AtmelasmBaseListener
  {

    @Override
    public void visitErrorNode(ErrorNode node)
    {
    }

    @Override
    public void visitTerminal(TerminalNode node)
    {
      Token token = node.getSymbol();

      System.out.println(token.getText());
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx)
    {
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx)
    {
//      System.out.println("enter " + ctx.toString());
    }

    @Override
    public void exitInit(AtmelasmParser.InitContext ctx)
    {
    }

    @Override
    public void enterInit(AtmelasmParser.InitContext ctx)
    {
    }

    @Override
    public void enterInstruction(AtmelasmParser.InstructionContext ctx)
    {

    }

    @Override
    public void exitInstruction(AtmelasmParser.InstructionContext ctx)
    {
      RuleContext rctx = ctx.getPayload();
    }

  }

  private static void visit(ParseTree tree,
                            String prefix)
  {
    System.out.print(prefix);
    System.out.println(tree.getText());
    for (int i = 0; i < tree.getChildCount(); ++i) {
      visit(tree.getChild(i),
            prefix + "  ");
    }
  }

  public static IntelHexOutputStream compile(InputStream asmStream,
                                             Function<String, InputStream> fileResolver) throws IOException
  {
    try (InputStream is = asmStream) {
      ANTLRInputStream ais = new ANTLRInputStream(is);
      AtmelasmLexer lexer = new AtmelasmLexer(ais);
      CommonTokenStream tokenStream = new CommonTokenStream(lexer);
      AtmelasmParser parser = new AtmelasmParser(tokenStream);
      parser.addParseListener(new MyListener());
      ParseTree tree = parser.init();
//      visit(tree,
//            "");
      System.out.println(tree.toStringTree(parser));
      return null;
    }
  }

}
