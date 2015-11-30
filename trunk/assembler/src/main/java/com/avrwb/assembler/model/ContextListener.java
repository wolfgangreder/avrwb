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

import com.avrwb.annotations.NotNull;
import com.avrwb.assembler.AssemblerConfig;
import com.avrwb.assembler.AssemblerError;
import com.avrwb.assembler.AssemblerException;
import com.avrwb.assembler.model.impl.AliasImpl;
import com.avrwb.assembler.model.impl.BitAndOperation;
import com.avrwb.assembler.model.impl.BitNotOperation;
import com.avrwb.assembler.model.impl.BitOrOperation;
import com.avrwb.assembler.model.impl.BitXorOperation;
import com.avrwb.assembler.model.impl.Byte3Operation;
import com.avrwb.assembler.model.impl.Byte4Operation;
import com.avrwb.assembler.model.impl.DistanceExpression;
import com.avrwb.assembler.model.impl.DivisionOperation;
import com.avrwb.assembler.model.impl.EqualThanOperation;
import com.avrwb.assembler.model.impl.Exp2Operation;
import com.avrwb.assembler.model.impl.ForwardExpression;
import com.avrwb.assembler.model.impl.GreaterEqualThanOperation;
import com.avrwb.assembler.model.impl.GreaterThanOperation;
import com.avrwb.assembler.model.impl.HighOperation;
import com.avrwb.assembler.model.impl.HwrdOperation;
import com.avrwb.assembler.model.impl.InstructionSegmentElement;
import com.avrwb.assembler.model.impl.IntExpression;
import com.avrwb.assembler.model.impl.LeftShiftOperation;
import com.avrwb.assembler.model.impl.LessEqualOperation;
import com.avrwb.assembler.model.impl.LessThanOperation;
import com.avrwb.assembler.model.impl.Log2Operation;
import com.avrwb.assembler.model.impl.LogAndOperation;
import com.avrwb.assembler.model.impl.LogNotOperation;
import com.avrwb.assembler.model.impl.LogOrOperation;
import com.avrwb.assembler.model.impl.LowOperation;
import com.avrwb.assembler.model.impl.LwrdOperation;
import com.avrwb.assembler.model.impl.MinusOperation;
import com.avrwb.assembler.model.impl.NotEqualThanOperation;
import com.avrwb.assembler.model.impl.PageOperation;
import com.avrwb.assembler.model.impl.PlusOperation;
import com.avrwb.assembler.model.impl.ProductOperation;
import com.avrwb.assembler.model.impl.RightShiftOperation;
import com.avrwb.assembler.model.impl.StringExpression;
import com.avrwb.assembler.model.impl.UniMinusOperation;
import com.avrwb.assembler.parser.AtmelAsmBaseListener;
import com.avrwb.assembler.parser.AtmelAsmParser;
import com.avrwb.avr8.api.InstructionComposer;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;

/**
 *
 * @author wolfi
 */
public class ContextListener extends AtmelAsmBaseListener
{

  private final Context context;

  public ContextListener(@NotNull Context context,
                         int dummy)
  {
    Objects.requireNonNull(context,
                           "context==null");
    this.context = context;
  }

  public ContextListener(@NotNull InternalAssembler assembler,
                         AssemblerConfig config)
  {
    this.context = new Context(assembler,
                               config);
  }

  public Context getContext()
  {
    return context;
  }

  @Override
  public void visitErrorNode(ErrorNode node)
  {
    throw new AssemblerError(node.toString());
  }

  @Override
  public void exitInt(AtmelAsmParser.IntContext ctx)
  {
    context.pushExpression(new IntExpression(ctx.getText(),
                                             createFileContext(ctx)));
  }

  @Override
  public void exitName(AtmelAsmParser.NameContext ctx)
  {
    Alias alias = context.getAlias(ctx.getText());
    if (alias == null) {
      if (ctx.getParent().getStart().getType() == AtmelAsmParser.MNEMONIC) {
        context.pushExpression(new ForwardExpression(createFileContext(ctx),
                                                     ctx.getText()));
        return;
      } else {
        throw new AssemblerError("cannot find name " + ctx.getText());
      }
    }
    context.pushExpression(alias.getExpression());
  }

  @Override
  public void exitCompare(AtmelAsmParser.CompareContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    String text = ctx.getChild(1).getText();
    switch (text) {
      case ">":
        context.pushExpression(new GreaterThanOperation(left,
                                                        right,
                                                        createFileContext(ctx)));
        break;
      case ">=":
        context.pushExpression(new GreaterEqualThanOperation(left,
                                                             right,
                                                             createFileContext(ctx)));
        break;
      case "<":
        context.pushExpression(new LessThanOperation(left,
                                                     right,
                                                     createFileContext(ctx)));
        break;
      case "<=":
        context.pushExpression(new LessEqualOperation(left,
                                                      right,
                                                      createFileContext(ctx)));
        break;
      default:
        throw new AssemblerError("unknown compare " + text);

    }
  }

  @Override
  public void exitBitAnd(AtmelAsmParser.BitAndContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    context.pushExpression(new BitAndOperation(left,
                                               right,
                                               createFileContext(ctx)));
  }

  @Override
  public void exitEqual(AtmelAsmParser.EqualContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    String text = ctx.getChild(1).getText();
    switch (text) {
      case "==":
        context.pushExpression(new EqualThanOperation(left,
                                                      right,
                                                      createFileContext(ctx)));
        break;
      case "!=":
        context.pushExpression(new NotEqualThanOperation(left,
                                                         right,
                                                         createFileContext(ctx)));
        break;
      default:
        throw new AssemblerError("unkown equal " + text);
    }
  }

  @Override
  public void exitBitXor(AtmelAsmParser.BitXorContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    context.pushExpression(new BitXorOperation(left,
                                               right,
                                               createFileContext(ctx)));
  }

  @Override
  public void exitLogAnd(AtmelAsmParser.LogAndContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    context.pushExpression(new LogAndOperation(left,
                                               right,
                                               createFileContext(ctx)));
  }

  @Override
  public void exitUnary(AtmelAsmParser.UnaryContext ctx)
  {
    Expression right = context.popExpression();
    String text = ctx.getChild(0).getText();
    switch (text) {
      case "!":
        context.pushExpression(new LogNotOperation(right,
                                                   createFileContext(ctx)));
        break;
      case "~":
        context.pushExpression(new BitNotOperation(right,
                                                   createFileContext(ctx)));
        break;
      case "-":
        context.pushExpression(new UniMinusOperation(right,
                                                     createFileContext(ctx)));
        break;
      default:
        throw new AssemblerError("unkown unary " + text);
    }
  }

  @Override
  public void exitSum(AtmelAsmParser.SumContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    String text = ctx.getChild(1).getText();
    switch (text) {
      case "+":
        context.pushExpression(new PlusOperation(left,
                                                 right,
                                                 createFileContext(ctx)));
        break;
      case "-":
        context.pushExpression(new MinusOperation(left,
                                                  right,
                                                  createFileContext(ctx)));
        break;
      default:
        throw new AssemblerError("unkown operation " + text);
    }
  }

  @Override
  public void exitProduct(AtmelAsmParser.ProductContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    String text = ctx.getChild(1).getText();
    switch (text) {
      case "*":
        context.pushExpression(new ProductOperation(left,
                                                    right,
                                                    createFileContext(ctx)));
        break;
      case "/":
        context.pushExpression(new DivisionOperation(left,
                                                     right,
                                                     createFileContext(ctx)));
        break;
      default:
        throw new AssemblerError("unkown operation " + text);
    }
  }

  @Override
  public void exitLogOr(AtmelAsmParser.LogOrContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    context.pushExpression(new LogOrOperation(left,
                                              right,
                                              createFileContext(ctx)));
  }

  @Override
  public void exitFunc(AtmelAsmParser.FuncContext ctx)
  {
    Expression right = context.popExpression();
    String text = ctx.getChild(0).getText();
    switch (text) {
      case "byte3":
        context.pushExpression(new Byte3Operation(right,
                                                  createFileContext(ctx)));
        break;
      case "byte4":
        context.pushExpression(new Byte4Operation(right,
                                                  createFileContext(ctx)));
        break;
      case "exp2":
        context.pushExpression(new Exp2Operation(right,
                                                 createFileContext(ctx)));
        break;
      case "high":
      case "byte2":
        context.pushExpression(new HighOperation(right,
                                                 createFileContext(ctx)));
        break;
      case "hwrd":
        context.pushExpression(new HwrdOperation(right,
                                                 createFileContext(ctx)));
        break;
      case "log2":
        context.pushExpression(new Log2Operation(right,
                                                 createFileContext(ctx)));
        break;
      case "low":
        context.pushExpression(new LowOperation(right,
                                                createFileContext(ctx)));
        break;
      case "lwrd":
        context.pushExpression(new LwrdOperation(right,
                                                 createFileContext(ctx)));
        break;
      case "page":
        context.pushExpression(new PageOperation(right,
                                                 createFileContext(ctx)));
        break;
      default:
        throw new AssemblerError("unkown func " + text);
    }
  }

  @Override
  public void exitBitOr(AtmelAsmParser.BitOrContext ctx)
  {
    Expression left = context.popExpression();
    Expression right = context.popExpression();
    context.pushExpression(new BitOrOperation(left,
                                              right,
                                              createFileContext(ctx)));
  }

  @Override
  public void exitShift(AtmelAsmParser.ShiftContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    String text = ctx.getChild(1).getText();
    switch (text) {
      case "<<":
        context.pushExpression(new LeftShiftOperation(left,
                                                      right,
                                                      createFileContext(ctx)));
        break;
      case ">>":
        context.pushExpression(new RightShiftOperation(left,
                                                       right,
                                                       createFileContext(ctx)));
        break;
      default:
        throw new AssemblerError("unkonw operation " + text);
    }
  }

  private String removeQoutes(String strIn)
  {
    int beginIndex = strIn.startsWith("\"") ? 1 : 0;
    int endIndex = strIn.lastIndexOf('"');
    return strIn.substring(beginIndex,
                           endIndex);
  }

  @Override
  public void exitInclude_dir(AtmelAsmParser.Include_dirContext ctx)
  {
    String fileName = removeQoutes(ctx.getChild(1).getText());
    try {
      URL u = new URL(fileName);
      AssemblerSource source = context.getConfig().getFileResolver().resolveFile(u);
      context.addInline(new Inline(createFileContext(ctx),
                                   source));
      context.getAssembler().compile(source,
                                     this);
    } catch (IOException | AssemblerException ex) {
      throw new AssemblerError(ex.getMessage());
    }
  }

  @Override
  public void exitEqu_dir(AtmelAsmParser.Equ_dirContext ctx)
  {
    String name = ctx.getChild(1).getText();
    Expression exp = context.popExpression();
    context.addAlias(new AliasImpl(name,
                                   true,
                                   exp));
  }

  @Override
  public void exitSet_dir(AtmelAsmParser.Set_dirContext ctx)
  {
    String name = ctx.getChild(1).getText();
    Expression exp = context.popExpression();
    context.addAlias(new AliasImpl(name,
                                   false,
                                   exp));
  }

  @Override
  public void exitString(AtmelAsmParser.StringContext ctx)
  {
    String text = removeQoutes(ctx.getChild(0).getText());
    context.pushExpression(new StringExpression(text,
                                                context.getConfig().getTargetCharset(),
                                                createFileContext(ctx)));
  }

  @Override
  public void exitDw_dir(AtmelAsmParser.Dw_dirContext ctx)
  {
    context.forEachExpression((Expression ex) -> {
      SegmentElement se = ex.toSegmentElement(context,
                                              context.getCurrentPosition(),
                                              2,
                                              context.getConfig().getTargetByteOrder());
      context.addToSeg(se);
    },
                              true);
  }

  @Override
  public void exitDb_dir(AtmelAsmParser.Db_dirContext ctx)
  {
    context.forEachExpression((Expression ex) -> {
      SegmentElement se = ex.toSegmentElement(context,
                                              context.getCurrentPosition(),
                                              1,
                                              context.getConfig().getTargetByteOrder());
      context.addToSeg(se);
    },
                              true);
  }

  private abstract class Composer2 implements Supplier<Integer>
  {

    protected final Expression right;
    protected final Expression left;
    protected final int baseOpcode;

    public Composer2(int baseOpcode,
                     Expression left,
                     Expression right)
    {
      this.right = right.preEvaluate(context);
      this.left = left.preEvaluate(context);
      this.baseOpcode = baseOpcode;
    }

  }

  private final class Composer_Rd_Rr extends Composer2
  {

    public Composer_Rd_Rr(int baseOpcode,
                          Expression left,
                          Expression right)
    {
      super(baseOpcode,
            left,
            right);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_Rd_Rr(baseOpcode,
                                                     left.evaluate(context),
                                                     right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Rd_Rr(int baseOpcode)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    return new Composer_Rd_Rr(baseOpcode,
                              left,
                              right);
  }

  private Supplier<Integer> composeOpcode_Rd_Rd(int baseOpcode)
  {
    Expression left = context.popExpression();
    return new Composer_Rd_Rr(baseOpcode,
                              left,
                              left);
  }

  public final class Composer_Rdl_K6 extends Composer2
  {

    public Composer_Rdl_K6(int baseOpcode,
                           Expression left,
                           Expression right)
    {
      super(baseOpcode,
            left,
            right);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_Rdl_K6(baseOpcode,
                                                      left.evaluate(context),
                                                      right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Rdl_K6(int baseOpcode)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    return new Composer_Rdl_K6(baseOpcode,
                               left,
                               right);
  }

  private final class Composer_Rd_K8 extends Composer2
  {

    public Composer_Rd_K8(int baseOpcode,
                          Expression left,
                          Expression right)
    {
      super(baseOpcode,
            left,
            right);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_Rd_K8(baseOpcode,
                                                     left.evaluate(context),
                                                     right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Rd_K8(int baseOpcode)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    return new Composer_Rd_K8(baseOpcode,
                              left,
                              right);
  }

  private abstract class Composer1 implements Supplier<Integer>
  {

    protected final Expression left;
    protected final int baseOpcode;

    public Composer1(int baseOpcode,
                     Expression left)
    {
      this.left = left.preEvaluate(context);
      this.baseOpcode = baseOpcode;
    }

  }

  private final class Compose_Rd extends Composer1
  {

    public Compose_Rd(int baseOpcode,
                      Expression left)
    {
      super(baseOpcode,
            left);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_Rd(baseOpcode,
                                                  left.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Rd(int baseOpcode)
  {
    Expression left = context.popExpression();
    return new Compose_Rd(baseOpcode,
                          left);
  }

  private final class Compose_Bclr_Bset extends Composer1
  {

    private final boolean bset;

    public Compose_Bclr_Bset(int baseOpcode,
                             Expression left,
                             boolean bset)
    {
      super(baseOpcode,
            left);
      this.bset = bset;
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_Bclr_Bset(baseOpcode,
                                                         bset,
                                                         left.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Bclr_Bset(int baseOpcode,
                                                    boolean bset,
                                                    int bit,
                                                    ParserRuleContext ctx)
  {
    return new Compose_Bclr_Bset(baseOpcode,
                                 new IntExpression(bit,
                                                   createFileContext(ctx)),
                                 bset);
  }

  private Supplier<Integer> composeOpcode_Bclr_Bset(int baseOpcode,
                                                    boolean bset)
  {
    Expression left = context.popExpression();
    return new Compose_Bclr_Bset(baseOpcode,
                                 left,
                                 bset);
  }

  private final class Composer_Rd_b extends Composer2
  {

    public Composer_Rd_b(int baseOpcode,
                         Expression left,
                         Expression right)
    {
      super(baseOpcode,
            left,
            right);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_Rd_b(baseOpcode,
                                                    left.evaluate(context),
                                                    right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Rd_b(int baseOpcode)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    return new Composer_Rd_b(baseOpcode,
                             left,
                             right);
  }

  private final class Composer_b_k7 extends Composer2
  {

    Composer_b_k7(int baseOpocde,
                  Expression left,
                  Expression right)
    {
      super(baseOpocde,
            left,
            right);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_b_k7(baseOpcode,
                                                    left.evaluate(context),
                                                    right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_b_k7(int baseOpcode)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    return new Composer_b_k7(baseOpcode,
                             left,
                             right);
  }

  private Supplier<Integer> composeOpcode_b_k7(int baseOpcode,
                                               int bit,
                                               FileContext ctx)
  {
    Expression left = getDistanceExpression(context.popExpression(),
                                            ctx);
    return new Composer_b_k7(baseOpcode,
                             new IntExpression(bit,
                                               ctx),
                             left);
  }

  private final class Composer_K22 extends Composer1
  {

    Composer_K22(int baseOpcode,
                 Expression left)
    {
      super(baseOpcode,
            left);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_K22(baseOpcode,
                                                   left.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_K22(int baseOpcode)

  {
    Expression left = context.popExpression();
    return new Composer_K22(baseOpcode,
                            left);
  }

  private final class Composer_P_b extends Composer2
  {

    public Composer_P_b(int baseOpcode,
                        Expression left,
                        Expression right)
    {
      super(baseOpcode,
            left,
            right);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_P_b(baseOpcode,
                                                   left.evaluate(context),
                                                   right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_P_b(int baseOpcode)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    return new Composer_P_b(baseOpcode,
                            left,
                            right);
  }

  private final class Composer_K4 extends Composer1
  {

    public Composer_K4(int baseOpcode,
                       Expression left)
    {
      super(baseOpcode,
            left);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_K4(baseOpcode,
                                                  left.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_K4(int baseOpcode)
  {
    Expression left = context.popExpression();
    return new Composer_K4(baseOpcode,
                           left);
  }

  private Supplier<Integer> composeElpm(AtmelAsmParser.InstructionContext ctx)
  {
    throw new UnsupportedOperationException();
  }

  private final class Composer_Rdh23_Rrh23 extends Composer2
  {

    public Composer_Rdh23_Rrh23(int baseOpcode,
                                Expression left,
                                Expression right)
    {
      super(baseOpcode,
            left,
            right);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_Rdh23_Rrh23(baseOpcode,
                                                           left.evaluate(context),
                                                           right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Rdh23_Rrh23(int baseOpcode)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    return new Composer_Rdh23_Rrh23(baseOpcode,
                                    left,
                                    right);
  }

  private final class Composer_Rd_P extends Composer2
  {

    public Composer_Rd_P(int baseOpcode,
                         Expression left,
                         Expression right)
    {
      super(baseOpcode,
            left,
            right);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_Rd_P(baseOpcode,
                                                    left.evaluate(context),
                                                    right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Rd_P(int baseOpocde)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    return new Composer_Rd_P(baseOpocde,
                             left,
                             right);
  }

  private Supplier<Integer> composeOpcode_P_Rd(int baseOpocde)
  {
    Expression left = context.popExpression();
    Expression right = context.popExpression();
    return new Composer_Rd_P(baseOpocde,
                             left,
                             right);
  }

  private Supplier<Integer> composeOpcode_Ld(AtmelAsmParser.InstructionContext ctx)
  {
    throw new UnsupportedOperationException();
  }

  private Supplier<Integer> composeOpcode_Lpm(AtmelAsmParser.InstructionContext ctx)
  {
    throw new UnsupportedOperationException();
  }

  private Supplier<Integer> composeOpcode_St(AtmelAsmParser.InstructionContext ctx)
  {
    throw new UnsupportedOperationException();
  }

  private Supplier<Integer> composeOpcode_Spm(AtmelAsmParser.InstructionContext ctx)
  {
    throw new UnsupportedOperationException();
  }

  private final class Composer_Rd_K16 extends Composer2
  {

    public Composer_Rd_K16(int baseOpcode,
                           Expression left,
                           Expression right)
    {
      super(baseOpcode,
            left,
            right);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_Rd_K16(baseOpcode,
                                                      left.evaluate(context),
                                                      right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Rd_K16(int baseOpocde)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    return new Composer_Rd_K16(baseOpocde,
                               left,
                               right);
  }

  private Supplier<Integer> composeOpcode_K16_Rd(int baseOpocde)
  {
    Expression left = context.popExpression();
    Expression right = context.popExpression();
    return new Composer_Rd_K16(baseOpocde,
                               left,
                               right);
  }

  private final class Composer_Rdh_Rrh extends Composer2
  {

    public Composer_Rdh_Rrh(int baseOpcode,
                            Expression left,
                            Expression right)
    {
      super(baseOpcode,
            left,
            right);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_Rdh_Rrh(baseOpcode,
                                                       left.evaluate(context),
                                                       right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Rdh_Rrh(int baseOpcode)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    return new Composer_Rdh_Rrh(baseOpcode,
                                left,
                                right);
  }

  private final class Composer_k12 extends Composer1
  {

    public Composer_k12(int baseOpcode,
                        Expression left)
    {
      super(baseOpcode,
            left);
    }

    @Override
    public Integer get()
    {
      return InstructionComposer.composeOpcode_k12(baseOpcode,
                                                   left.evaluate(context));
    }

  }

  private Expression getDistanceExpression(Expression label,
                                           FileContext fileContext)
  {
    return new DistanceExpression(context.getCurrentPosition(),
                                  label,
                                  fileContext,
                                  Segment.CSEG);
  }

  private Supplier<Integer> composeOpcode_k12(int baseOpcode,
                                              FileContext fileContext)
  {
    Expression left = getDistanceExpression(context.popExpression(),
                                            fileContext);
    return new Composer_k12(baseOpcode,
                            left);
  }

  @Override
  public void exitInstruction(AtmelAsmParser.InstructionContext ctx)
  {
    String mnemonic = ctx.getChild(0).getText();
    Supplier<Integer> opcode = null;
    boolean bigInstruciton = false;
    final FileContext fileContext = createFileContext(ctx);

    switch (mnemonic) {
      case "adc":
        opcode = composeOpcode_Rd_Rr(0x1c00);
        break;
      case "add":
        opcode = composeOpcode_Rd_Rr(0x0c00);
        break;
      case "adiw":
        opcode = composeOpcode_Rdl_K6(0x9600);
        break;
      case "and":
        opcode = composeOpcode_Rd_Rr(0x2000);
        break;
      case "andi":
        opcode = composeOpcode_Rd_K8(0x7000);
        break;
      case "asr":
        opcode = composeOpcode_Rd(0x9405);
        break;
      case "bclr":
        opcode = composeOpcode_Bclr_Bset(0x9488,
                                         false);
        break;
      case "bld":
        opcode = composeOpcode_Rd_b(0xf800);
        break;
      case "brbc":
        opcode = composeOpcode_b_k7(0xf400);
        break;
      case "brbs":
        opcode = composeOpcode_b_k7(0xf000);
        break;
      case "brcc":
        opcode = composeOpcode_b_k7(0xf400,
                                    0,
                                    fileContext);
        break;
      case "brcs":
        opcode = composeOpcode_b_k7(0xf000,
                                    0,
                                    fileContext);
        break;
      case "break":
        opcode = () -> 0x9598;
        break;
      case "breq":
        opcode = composeOpcode_b_k7(0xf000,
                                    1,
                                    fileContext);
        break;
      case "brge":
        opcode = composeOpcode_b_k7(0xf400,
                                    4,
                                    fileContext);
        break;
      case "brhc":
        opcode = composeOpcode_b_k7(0xf000,
                                    5,
                                    fileContext);
        break;
      case "brhs":
        opcode = composeOpcode_b_k7(0xf400,
                                    5,
                                    fileContext);
        break;
      case "brid":
        opcode = composeOpcode_b_k7(0xf400,
                                    7,
                                    fileContext);
        break;
      case "brie":
        opcode = composeOpcode_b_k7(0xf000,
                                    7,
                                    fileContext);
        break;
      case "brlo":
        opcode = composeOpcode_b_k7(0xf000,
                                    0,
                                    fileContext);
        break;
      case "brlt":
        opcode = composeOpcode_b_k7(0xf000,
                                    7,
                                    fileContext);
        break;
      case "brmi":
        opcode = composeOpcode_b_k7(0xf000,
                                    2,
                                    fileContext);
        break;
      case "brne":
        opcode = composeOpcode_b_k7(0xf400,
                                    1,
                                    fileContext);
        break;
      case "brpl":
        opcode = composeOpcode_b_k7(0xf400,
                                    2,
                                    fileContext);
        break;
      case "brsh":
        opcode = composeOpcode_b_k7(0xf400,
                                    0,
                                    fileContext);
        break;
      case "brtc":
        opcode = composeOpcode_b_k7(0xf400,
                                    6,
                                    fileContext);
        break;
      case "brts":
        opcode = composeOpcode_b_k7(0xf000,
                                    6,
                                    fileContext);
        break;
      case "brvc":
        opcode = composeOpcode_b_k7(0xf400,
                                    3,
                                    fileContext);
        break;
      case "brvs":
        opcode = composeOpcode_b_k7(0xf000,
                                    3,
                                    fileContext);
        break;
      case "bset":
        opcode = composeOpcode_Bclr_Bset(0x9408,
                                         true);
        break;
      case "bst":
        opcode = composeOpcode_Rd_b(0xfa00);
        break;
      case "call":
        opcode = composeOpcode_K22(0x940e);
        bigInstruciton = true;
        break;
      case "cbi":
        opcode = composeOpcode_P_b(0x9800);
        break;
      case "cbr":
        opcode = composeOpcode_Rd_K8(0x7000);
        break;
      case "clc":
        opcode = composeOpcode_Bclr_Bset(0x9488,
                                         false,
                                         0,
                                         ctx);
        break;
      case "clh":
        opcode = composeOpcode_Bclr_Bset(0x9488,
                                         false,
                                         5,
                                         ctx);
        break;
      case "cli":
        opcode = composeOpcode_Bclr_Bset(0x9488,
                                         false,
                                         7,
                                         ctx);
        break;
      case "cln":
        opcode = composeOpcode_Bclr_Bset(0x9488,
                                         false,
                                         2,
                                         ctx);
        break;
      case "clr":
        opcode = composeOpcode_Rd_Rd(0x2400);
        break;
      case "cls":
        opcode = composeOpcode_Bclr_Bset(0x9488,
                                         false,
                                         4,
                                         ctx);
        break;
      case "clt":
        opcode = composeOpcode_Bclr_Bset(0x9488,
                                         false,
                                         6,
                                         ctx);
        break;
      case "clv":
        opcode = composeOpcode_Bclr_Bset(0x9488,
                                         false,
                                         3,
                                         ctx);
        break;
      case "clz":
        opcode = composeOpcode_Bclr_Bset(0x9488,
                                         false,
                                         1,
                                         ctx);
        break;
      case "com":
        opcode = composeOpcode_Rd(0x9400);
        break;
      case "cp":
        opcode = composeOpcode_Rd_Rr(0x1400);
        break;
      case "cpc":
        opcode = composeOpcode_Rd_Rr(0x0400);
        break;
      case "cpi":
        opcode = composeOpcode_Rd_K8(0x3000);
        break;
      case "cpse":
        opcode = composeOpcode_Rd_Rr(0x1000);
        break;
      case "dec":
        opcode = composeOpcode_Rd(0x940a);
        break;
      case "des":
        opcode = composeOpcode_K4(0x940b);
        break;
      case "eicall":
        opcode = () -> 0x9519;
        break;
      case "eijmp":
        opcode = () -> 0x9419;
        break;
      case "elpm":
        opcode = composeElpm(ctx);
        break;
      case "eor":
        opcode = composeOpcode_Rd_Rr(0x2400);
        break;
      case "fmul":
        opcode = composeOpcode_Rdh23_Rrh23(0x0308);
        break;
      case "fmuls":
        opcode = composeOpcode_Rdh23_Rrh23(0x0380);
        break;
      case "fmulsu":
        opcode = composeOpcode_Rdh23_Rrh23(0x0388);
        break;
      case "icall":
        opcode = () -> 0x9509;
        break;
      case "ijmp":
        opcode = () -> 0x9409;
        break;
      case "in":
        opcode = composeOpcode_Rd_P(0xb000);
        break;
      case "inc":
        opcode = composeOpcode_Rd(0x9403);
        break;
      case "jmp":
        opcode = composeOpcode_K22(0x940c);
        bigInstruciton = true;
        break;
      case "lac":
        opcode = composeOpcode_Rd(0x9206);
        break;
      case "las":
        opcode = composeOpcode_Rd(0x9205);
        break;
      case "lat":
        opcode = composeOpcode_Rd(0x9207);
        break;
      case "ld":
        opcode = composeOpcode_Ld(ctx);
        break;
      case "ldd":
        opcode = composeOpcode_Ld(ctx);
        break;
      case "ldi":
        opcode = composeOpcode_Rd_K8(0xe000);
        break;
      case "lds":
        bigInstruciton = true;
        opcode = composeOpcode_Rd_K16(0x9000);
        break;
      case "lpm":
        opcode = composeOpcode_Lpm(ctx);
        break;
      case "lsl":
        opcode = composeOpcode_Rd_Rd(0x0c00);
        break;
      case "lsr":
        opcode = composeOpcode_Rd(0x9406);
        break;
      case "mov":
        opcode = composeOpcode_Rd_Rr(0x2c00);
        break;
      case "movw":
        opcode = composeOpcode_Rdh_Rrh(0x0100);
        break;
      case "mul":
        opcode = composeOpcode_Rd_Rr(0x9c00);
        break;
      case "muls":
        opcode = composeOpcode_Rdh_Rrh(0x0200);
        break;
      case "mulsu":
        opcode = composeOpcode_Rdh_Rrh(0x0300);
        break;
      case "neg":
        opcode = composeOpcode_Rd(0x9401);
        break;
      case "nop":
        opcode = () -> 0x0000;
        break;
      case "or":
        opcode = composeOpcode_Rd_Rr(0x2800);
        break;
      case "ori":
        opcode = composeOpcode_Rd_K8(0x6000);
        break;
      case "out":
        opcode = composeOpcode_P_Rd(0xb800);
        break;
      case "pop":
        opcode = composeOpcode_Rd(0x900f);
        break;
      case "push":
        opcode = composeOpcode_Rd(0x920f);
        break;
      case "rcall":
        opcode = composeOpcode_k12(0xd000,
                                   fileContext);
        break;
      case "ret":
        opcode = () -> 0x9508;
        break;
      case "reti":
        opcode = () -> 0x9518;
        break;
      case "rjmp":
        opcode = composeOpcode_k12(0xc000,
                                   fileContext);
        break;
      case "rol":
        opcode = composeOpcode_Rd_Rd(0x1c00);
        break;
      case "ror":
        opcode = composeOpcode_Rd(0x9407);
        break;
      case "sbc":
        opcode = composeOpcode_Rd_Rr(0x0800);
        break;
      case "sbci":
        opcode = composeOpcode_Rd_K8(0x4000);
        break;
      case "sbi":
        opcode = composeOpcode_P_b(0x9a00);
        break;
      case "sbic":
        opcode = composeOpcode_P_b(0x9900);
        break;
      case "sbis":
        opcode = composeOpcode_P_b(0x9b00);
        break;
      case "sbiw":
        opcode = composeOpcode_Rdl_K6(0x9700);
        break;
      case "sbr":
        opcode = composeOpcode_Rd_K8(0x6000);
        break;
      case "sbrc":
        opcode = composeOpcode_Rd_b(0xfc00);
        break;
      case "sbrs":
        opcode = composeOpcode_Rd_b(0xfe00);
        break;
      case "sec":
        opcode = composeOpcode_Bclr_Bset(0x9408,
                                         true,
                                         0,
                                         ctx);
        break;
      case "seh":
        opcode = composeOpcode_Bclr_Bset(0x9408,
                                         true,
                                         5,
                                         ctx);
        break;
      case "sei":
        opcode = composeOpcode_Bclr_Bset(0x9408,
                                         true,
                                         7,
                                         ctx);
        break;
      case "sen":
        opcode = composeOpcode_Bclr_Bset(0x9408,
                                         true,
                                         2,
                                         ctx);
        break;
      case "ser":
      case "ses":
        opcode = composeOpcode_Bclr_Bset(0x9408,
                                         true,
                                         4,
                                         ctx);
        break;
      case "set":
        opcode = composeOpcode_Bclr_Bset(0x9408,
                                         true,
                                         6,
                                         ctx);
        break;
      case "sev":
        opcode = composeOpcode_Bclr_Bset(0x9408,
                                         true,
                                         3,
                                         ctx);
        break;
      case "sez":
        opcode = composeOpcode_Bclr_Bset(0x9408,
                                         true,
                                         1,
                                         ctx);
        break;
      case "sleep":
        opcode = () -> 0x9588;
        break;
      case "spm":
        opcode = composeOpcode_Spm(ctx);
        break;
      case "st":
        opcode = composeOpcode_St(ctx);
        break;
      case "std":
        opcode = composeOpcode_St(ctx);
        break;
      case "sts":
        bigInstruciton = true;
        opcode = composeOpcode_K16_Rd(0x9200);
        break;
      case "sub":
        opcode = composeOpcode_Rd_Rr(0x1800);
        break;
      case "subi":
        opcode = composeOpcode_Rd_K8(0x5000);
        break;
      case "swap":
        opcode = composeOpcode_Rd(0x9402);
        break;
      case "tst":
        opcode = composeOpcode_Rd_Rd(0x2000);
        break;
      case "wdr":
        opcode = () -> 0x95a8;
        break;
      case "xch":
        opcode = composeOpcode_Rd(0x9204);
        break;
    }
    if (opcode == null) {
      throw new AssemblerError("unknown mnemonic " + mnemonic);
    }
    if (bigInstruciton) {
      context.addToSeg(InstructionSegmentElement.getDWordInstance(context.getCurrentSegment(),
                                                                  context.getCurrentPosition(),
                                                                  opcode,
                                                                  context.getConfig().getTargetByteOrder(),
                                                                  fileContext));
    } else {
      context.addToSeg(InstructionSegmentElement.getWordInstance(context.getCurrentSegment(),
                                                                 context.getCurrentPosition(),
                                                                 opcode,
                                                                 context.getConfig().getTargetByteOrder(),
                                                                 fileContext));
    }
  }

  private FileContext createFileContext(ParserRuleContext ctx)
  {
    Token startToken = ctx.getStart();
    int lineNumber = startToken.getLine();
    return new FileContext(context.currentSource(),
                           lineNumber);
  }

  @Override
  public void exitOrg_dir(AtmelAsmParser.Org_dirContext ctx)
  {
    Expression ex = context.popExpression();
    context.setCurrentPosition(context.getCurrentSegment(),
                               ex.evaluate(context) * 2);
  }

  @Override
  public void exitLabel(AtmelAsmParser.LabelContext ctx)
  {
    String name = ctx.getChild(0).getText();
    context.addAlias(new AliasImpl(name,
                                   true,
                                   new IntExpression(context.getCurrentPosition(),
                                                     createFileContext(ctx))));
  }

  @Override
  public void exitEseg_dir(AtmelAsmParser.Eseg_dirContext ctx)
  {
    context.setCurrentSegment(Segment.ESEG);
  }

  @Override
  public void exitDseg_dir(AtmelAsmParser.Dseg_dirContext ctx)
  {
    context.setCurrentSegment(Segment.DSEG);
  }

  @Override
  public void exitCseg_dir(AtmelAsmParser.Cseg_dirContext ctx)
  {
    context.setCurrentSegment(Segment.CSEG);
  }

}
