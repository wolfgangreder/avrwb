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
import com.avrwb.assembler.InvalidParameterException;
import com.avrwb.assembler.InvalidTypeException;
import com.avrwb.assembler.SourceContext;
import com.avrwb.assembler.SyntaxException;
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
import com.avrwb.assembler.model.impl.WordExpression;
import com.avrwb.assembler.parser.AtmelAsmBaseListener;
import com.avrwb.assembler.parser.AtmelAsmParser;
import com.avrwb.avr8.api.InstructionComposer;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;

public class ContextListener extends AtmelAsmBaseListener
{

  private final Context context;
  private final LinkedList<SourceContext> sourceContext = new LinkedList<>();

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

  private SourceContext getSourceContext(RuleContext ctx)
  {
    return sourceContext.getFirst();
  }

  @Override
  public void enterEveryRule(ParserRuleContext ctx)
  {
    sourceContext.push(createSourceContext(ctx));
  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx)
  {
    sourceContext.pop();
  }

  private SourceContext createSourceContext(ParserRuleContext ctx)
  {
    Token startToken = ctx.getStart();
    int line = startToken.getLine();
    int col = startToken.getCharPositionInLine();
    return new SourceContext(context.getCurrentSource().getSourceName(),
                             line,
                             col);
  }

  private String removeQoutes(String strIn)
  {
    int beginIndex = strIn.startsWith("\"") ? 1 : 0;
    int endIndex = strIn.lastIndexOf('"');
    return strIn.substring(beginIndex,
                           endIndex);
  }

  //*********************************************************************************************************
  //*
  //* Directives
  //*
  //*********************************************************************************************************
  @Override
  public void exitInclude_dir(AtmelAsmParser.Include_dirContext ctx)
  {
    String fileName = removeQoutes(ctx.STRING().getText());
    try {
      AssemblerSource source = context.getConfig().getFileResolver().resolveFile(context,
                                                                                 fileName);
      context.addInline(new Inline(getSourceContext(ctx),
                                   source));
      context.getAssembler().compile(source,
                                     this);
    } catch (IOException | AssemblerException ex) {
      throw new AssemblerError(ex.getMessage(),
                               getSourceContext(ctx));
    }
  }

  @Override
  public void exitSet_dir(AtmelAsmParser.Set_dirContext ctx)
  {
    String name = ctx.NAME().getText();
    Expression exp = context.popExpression(getSourceContext(ctx));
    context.addAlias(new AliasImpl(name,
                                   false,
                                   exp),
                     getSourceContext(ctx));
  }

  @Override
  public void exitOrg_dir(AtmelAsmParser.Org_dirContext ctx)
  {
    Expression ex = context.popExpression(getSourceContext(ctx));
    context.setCurrentPosition(context.getCurrentSegment(),
                               ex.evaluate(context) * 2);
  }

  @Override
  public void exitDw_dir(AtmelAsmParser.Dw_dirContext ctx)
  {
    context.forEachExpression((Expression ex) -> {
      SegmentElement se = ex.toSegmentElement(context,
                                              context.getCurrentPosition(),
                                              2,
                                              context.getConfig().getTargetByteOrder());
      context.addToSeg(se,
                       getSourceContext(ctx));
    },
                              true);
  }

  @Override
  public void exitDseg_dir(AtmelAsmParser.Dseg_dirContext ctx)
  {
    context.setCurrentSegment(Segment.DSEG);
  }

  @Override
  public void exitDevice_dir(AtmelAsmParser.Device_dirContext ctx)
  {
    super.exitDevice_dir(ctx); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void exitDef_dir(AtmelAsmParser.Def_dirContext ctx)
  {
    String name = ctx.NAME().getText();
    String reg = ctx.REG_NAME().getText();
    Alias alias = context.getAlias(reg);
    if (alias != null) {
      Expression exp = alias.getExpression();
      if (exp.getType() == ExpressionType.REGISTER) {
        context.addAlias(new AliasImpl(name,
                                       true,
                                       exp),
                         getSourceContext(ctx));
        return;
      }
    }
    throw new InvalidTypeException(reg + " is not a register",
                                   getSourceContext(ctx)).toWrapper();
  }

  @Override
  public void exitDb_dir(AtmelAsmParser.Db_dirContext ctx)
  {
    if (context.getCurrentSegment() == Segment.CSEG) {
      final SourceContext sctx = getSourceContext(ctx);
      List<Expression> tmpList = new LinkedList<>();
      context.forEachExpression((Expression ex) -> {
        if (tmpList.size() == 2) {
          WordExpression we = new WordExpression(sctx,
                                                 tmpList.get(0),
                                                 tmpList.get(1));
          SegmentElement se = we.toSegmentElement(context,
                                                  context.getCurrentPosition(),
                                                  2,
                                                  context.getConfig().getTargetByteOrder());
          context.addToSeg(se,
                           getSourceContext(ctx));
          tmpList.clear();
        }
        tmpList.add(ex);
      },
                                true);
      if (tmpList.size() == 1) {
        tmpList.add(new IntExpression(0,
                                      sctx));
      }
      if (tmpList.size() == 2) {
        WordExpression we = new WordExpression(sctx,
                                               tmpList.get(0),
                                               tmpList.get(1));
        SegmentElement se = we.toSegmentElement(context,
                                                context.getCurrentPosition(),
                                                2,
                                                context.getConfig().getTargetByteOrder());
        context.addToSeg(se,
                         getSourceContext(ctx));
      }
    } else {
      context.forEachExpression((Expression ex) -> {
        SegmentElement se = ex.toSegmentElement(context,
                                                context.getCurrentPosition(),
                                                1,
                                                context.getConfig().getTargetByteOrder());
        context.addToSeg(se,
                         getSourceContext(ctx));
      },
                                true);
    }
  }

  @Override
  public void exitCseg_dir(AtmelAsmParser.Cseg_dirContext ctx)
  {
    context.setCurrentSegment(Segment.CSEG);
  }

  @Override
  public void exitEseg_dir(AtmelAsmParser.Eseg_dirContext ctx)
  {
    context.setCurrentSegment(Segment.ESEG);
  }

  @Override
  public void exitEqu_dir(AtmelAsmParser.Equ_dirContext ctx)
  {
    String name = ctx.NAME().getText();
    Expression exp = context.popExpression(getSourceContext(ctx));
    if (exp.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(ctx.DIR_EQU().getText() + "is no integer",
                                     getSourceContext(ctx)).toWrapper();
    }
    context.addAlias(new AliasImpl(name,
                                   true,
                                   exp),
                     getSourceContext(ctx));
  }

  //*********************************************************************************************************
  //*
  //* Instructions
  //*
  //*********************************************************************************************************
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

  private Supplier<Integer> composeOpcode_Rd_Rr(int baseOpcode,
                                                SourceContext sctx)
  {
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    if (right.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(right.toString() + " is no register",
                                     sctx).toWrapper();
    }
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    return new Composer_Rd_Rr(baseOpcode,
                              left,
                              right);
  }

  private Supplier<Integer> composeOpcode_Rd_Rd(int baseOpcode,
                                                SourceContext sctx)
  {
    Expression left = context.popExpression(sctx);
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
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

  private Supplier<Integer> composeOpcode_Rdl_K6(int baseOpcode,
                                                 SourceContext sctx)
  {
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    if (right.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(right.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
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

  private Supplier<Integer> composeOpcode_Rd_K8(int baseOpcode,
                                                SourceContext sctx)
  {
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    if (right.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(right.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    return new Composer_Rd_K8(baseOpcode,
                              left,
                              right);
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

  private Supplier<Integer> composeOpcode_Rd(int baseOpcode,
                                             SourceContext sctx)
  {
    Expression left = context.popExpression(sctx);
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
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
                                                    SourceContext sctx)
  {
    return new Compose_Bclr_Bset(baseOpcode,
                                 new IntExpression(bit,
                                                   sctx),
                                 bset);
  }

  private Supplier<Integer> composeOpcode_Bclr_Bset(int baseOpcode,
                                                    boolean bset,
                                                    SourceContext sctx)
  {
    Expression left = context.popExpression(sctx);
    if (left.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(left.toString() + " is no integer",
                                     sctx).toWrapper();
    }
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

  private Supplier<Integer> composeOpcode_Rd_b(int baseOpcode,
                                               SourceContext sctx)
  {
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    if (right.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(right.toString() + " is no register",
                                     sctx).toWrapper();
    }
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
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

  private Expression getDistanceExpression(Expression label,
                                           SourceContext sctx)
  {
    return new DistanceExpression(context.getCurrentPosition(),
                                  label,
                                  sctx,
                                  Segment.CSEG);
  }

  private Supplier<Integer> composeOpcode_b_k7(int baseOpcode,
                                               SourceContext sctx)
  {
    Expression right = getDistanceExpression(context.popExpression(sctx),
                                             sctx);
    Expression left = context.popExpression(sctx);
    if (right.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(right.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    if (left.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(left.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    return new Composer_b_k7(baseOpcode,
                             left,
                             right);
  }

  private Supplier<Integer> composeOpcode_b_k7(int baseOpcode,
                                               int bit,
                                               SourceContext sctx)
  {
    Expression left = getDistanceExpression(context.popExpression(sctx),
                                            sctx);
    if (left.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(left.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    return new Composer_b_k7(baseOpcode,
                             new IntExpression(bit,
                                               sctx),
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
                                                   left.evaluate(context) >> 1);
    }

  }

  private Supplier<Integer> composeOpcode_K22(int baseOpcode,
                                              SourceContext sctx)

  {
    Expression left = context.popExpression(sctx);
    if (left.getType() != ExpressionType.INTEGER && left.getType() != ExpressionType.FORWARD) {
      throw new InvalidTypeException(left.toString() + " is no integer",
                                     sctx).toWrapper();
    }
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

  private Supplier<Integer> composeOpcode_P_b(int baseOpcode,
                                              SourceContext sctx)
  {
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    if (right.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(right.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    if (left.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(left.toString() + " is no integer",
                                     sctx).toWrapper();
    }
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

  private Supplier<Integer> composeOpcode_K4(int baseOpcode,
                                             SourceContext sctx)
  {
    Expression left = context.popExpression(sctx);
    if (left.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(left.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    return new Composer_K4(baseOpcode,
                           left);
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

  private Supplier<Integer> composeOpcode_Rdh23_Rrh23(int baseOpcode,
                                                      SourceContext sctx)
  {
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    if (right.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(right.toString() + " is no register",
                                     sctx).toWrapper();
    }
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
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

  private Supplier<Integer> composeOpcode_Rd_P(int baseOpocde,
                                               SourceContext sctx)
  {
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    if (right.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(right.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    return new Composer_Rd_P(baseOpocde,
                             left,
                             right);
  }

  private Supplier<Integer> composeOpcode_P_Rd(int baseOpocde,
                                               SourceContext sctx)
  {
    Expression left = context.popExpression(sctx);
    Expression right = context.popExpression(sctx);
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    if (right.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(right.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    return new Composer_Rd_P(baseOpocde,
                             left,
                             right);
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

  private Supplier<Integer> composeOpcode_Rd_K16(int baseOpocde,
                                                 SourceContext sctx)
  {
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    if (right.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(right.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    return new Composer_Rd_K16(baseOpocde,
                               left,
                               right);
  }

  private Supplier<Integer> composeOpcode_K16_Rd(int baseOpocde,
                                                 SourceContext sctx)
  {
    Expression left = context.popExpression(sctx);
    Expression right = context.popExpression(sctx);
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    if (right.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(right.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    return new Composer_Rd_K16(baseOpocde,
                               left,
                               right);
  }

  private final class Composer_Movw extends Composer2
  {

    public Composer_Movw(int baseOpcode,
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
      return InstructionComposer.composeOpcode_Movw(baseOpcode,
                                                    left.evaluate(context),
                                                    right.evaluate(context));
    }

  }

  private final class Composer_Muls extends Composer2
  {

    public Composer_Muls(int baseOpcode,
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
      return InstructionComposer.composeOpcode_Muls(baseOpcode,
                                                    left.evaluate(context),
                                                    right.evaluate(context));
    }

  }

  private Supplier<Integer> composeOpcode_Movw(int baseOpcode,
                                               SourceContext sctx)
  {
    Expression rightLo = context.popExpression(sctx);
    Expression rightHi = context.popExpression(sctx);
    Expression leftLo = context.popExpression(sctx);
    Expression leftHi = context.popExpression(sctx);
    if (rightLo.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(rightLo.toString() + " is no register",
                                     sctx).toWrapper();
    }
    if (leftLo.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(leftLo.toString() + " is no register",
                                     sctx).toWrapper();
    }
    return new Composer_Movw(baseOpcode,
                             leftLo,
                             rightLo);
  }

  private Supplier<Integer> composeOpcode_Muls(int baseOpcode,
                                               SourceContext sctx)
  {
    Expression rightLo = context.popExpression(sctx);
    Expression rightHi = context.popExpression(sctx);
    Expression leftLo = context.popExpression(sctx);
    Expression leftHi = context.popExpression(sctx);
    if (rightLo.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(rightLo.toString() + " is no register",
                                     sctx).toWrapper();
    }
    if (leftLo.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(leftLo.toString() + " is no register",
                                     sctx).toWrapper();
    }
    return new Composer_Muls(baseOpcode,
                             leftLo,
                             rightLo);
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

  private Supplier<Integer> composeOpcode_k12(int baseOpcode,
                                              SourceContext sctx)
  {
    Expression left = getDistanceExpression(context.popExpression(sctx),
                                            sctx);
    if (left.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(left.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    return new Composer_k12(baseOpcode,
                            left);
  }

  private void addOpcodeToSegment(Supplier<Integer> opcode,
                                  boolean bigInstruction,
                                  SourceContext sctx)
  {
    if (bigInstruction) {
      context.addToSeg(InstructionSegmentElement.getDWordInstance(context.getCurrentSegment(),
                                                                  context.getCurrentPosition(),
                                                                  opcode,
                                                                  context.getConfig().getTargetByteOrder(),
                                                                  sctx),
                       sctx);
    } else {
      context.addToSeg(InstructionSegmentElement.getWordInstance(context.getCurrentSegment(),
                                                                 context.getCurrentPosition(),
                                                                 opcode,
                                                                 context.getConfig().getTargetByteOrder(),
                                                                 sctx),
                       sctx);
    }
  }

  @Override
  public void exitAsm_break(AtmelAsmParser.Asm_breakContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x9598;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitAsr(AtmelAsmParser.AsrContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9405,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitAndi(AtmelAsmParser.AndiContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_K8(0x7000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitAnd(AtmelAsmParser.AndContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x2000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitAdiw(AtmelAsmParser.AdiwContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rdl_K6(0x9600,
                                                    sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitAdd(AtmelAsmParser.AddContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x0c00,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitAdc(AtmelAsmParser.AdcContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x1c00,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitXch(AtmelAsmParser.XchContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9204,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitWdr(AtmelAsmParser.WdrContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x95a8;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitTst(AtmelAsmParser.TstContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rd(0x2000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSwap(AtmelAsmParser.SwapContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9402,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSubi(AtmelAsmParser.SubiContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_K8(0x5000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSub(AtmelAsmParser.SubContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x1800,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSts(AtmelAsmParser.StsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_K16_Rd(0x9200,
                                                    sctx);
    addOpcodeToSegment(opcode,
                       true,
                       sctx);
  }

  @Override
  public void exitSt_ALL(AtmelAsmParser.St_ALLContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    final AtmelAsmParser.Ptr_nakedContext ptrCtx = ctx.ptr_naked();
    final int ptr = ptrCtx.getStart().getType();
    int baseOpcode;
    switch (ptr) {
      case AtmelAsmParser.X_PTR:
        baseOpcode = 0x920c;
        break;
      case AtmelAsmParser.Y_PTR:
        baseOpcode = 0x8208;
        break;
      case AtmelAsmParser.Z_PTR:
        baseOpcode = 0x8200;
        break;
      default:
        throw new InvalidTypeException("Invalid PTR " + ptrCtx.getText(),
                                       sctx).toWrapper();
    }
    processLdSt(baseOpcode,
                sctx);
  }

  @Override
  public void exitStd(AtmelAsmParser.StdContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    final AtmelAsmParser.Ptr_qContext ptrCtx = ctx.ptr_q();
    int ptr = ptrCtx.getStart().getType();
    int baseOpcode;
    switch (ptr) {
      case AtmelAsmParser.Y_PTR:
        baseOpcode = 0x8008;
        break;
      case AtmelAsmParser.Z_PTR:
        baseOpcode = 0x8000;
        break;
      default:
        throw new InvalidTypeException("Invalid PTR " + ptrCtx.getText(),
                                       sctx).toWrapper();
    }
    processLddStd(baseOpcode,
                  sctx);
  }

  @Override
  public void exitSt_M_ALL(AtmelAsmParser.St_M_ALLContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    final AtmelAsmParser.Ptr_minusContext ptrCtx = ctx.ptr_minus();
    final int ptr = ptrCtx.getStart().getType();
    final int baseOpcode;
    switch (ptr) {
      case AtmelAsmParser.X_PTR:
        baseOpcode = 0x920e;
        break;
      case AtmelAsmParser.Y_PTR:
        baseOpcode = 0x920a;
        break;
      case AtmelAsmParser.Z_PTR:
        baseOpcode = 0x9202;
        break;
      default:
        throw new InvalidTypeException("Invalid PTR " + ptrCtx.getText(),
                                       sctx).toWrapper();
    }
    processLdSt(baseOpcode,
                sctx);
  }

  @Override
  public void exitSt_ALL_P(AtmelAsmParser.St_ALL_PContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    int ptr = ctx.ptr_plus().getRuleIndex();
    int baseOpcode;
    switch (ptr) {
      case AtmelAsmParser.X_PTR:
        baseOpcode = 0x920d;
        break;
      case AtmelAsmParser.Y_PTR:
        baseOpcode = 0x9209;
        break;
      case AtmelAsmParser.Z_PTR:
        baseOpcode = 0x9201;
        break;
      default:
        throw new InvalidTypeException("Invalid PTR " + ctx.ptr_plus().getText(),
                                       sctx).toWrapper();
    }
    processLdSt(baseOpcode,
                sctx);
  }

  @Override
  public void exitSpm_naked(AtmelAsmParser.Spm_nakedContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x95e8;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSpm_ZP(AtmelAsmParser.Spm_ZPContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x95f8;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSleep(AtmelAsmParser.SleepContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x9588;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSez(AtmelAsmParser.SezContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9408,
                                                       true,
                                                       1,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSev(AtmelAsmParser.SevContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9408,
                                                       true,
                                                       3,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSet(AtmelAsmParser.SetContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9408,
                                                       true,
                                                       6,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSes(AtmelAsmParser.SesContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9408,
                                                       true,
                                                       4,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSer(AtmelAsmParser.SerContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    final Expression left = context.popExpression(sctx);
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    Supplier<Integer> opcode = () -> {
      int rd = left.evaluate(context);
      if (rd < 16 || rd > 31) {
        throw new InvalidParameterException("invalid rd " + rd,
                                            sctx).toWrapper();
      }
      return 0xef0f | ((rd & 0xf) << 4);
    };
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSen(AtmelAsmParser.SenContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9408,
                                                       true,
                                                       2,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSei(AtmelAsmParser.SeiContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9408,
                                                       true,
                                                       7,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSeh(AtmelAsmParser.SehContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9408,
                                                       true,
                                                       5,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSec(AtmelAsmParser.SecContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9408,
                                                       true,
                                                       0,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSbrs(AtmelAsmParser.SbrsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_b(0xfe00,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSbrc(AtmelAsmParser.SbrcContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_b(0xfc00,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSbr(AtmelAsmParser.SbrContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_K8(0x6000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSbiw(AtmelAsmParser.SbiwContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rdl_K6(0x9700,
                                                    sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSbis(AtmelAsmParser.SbisContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_P_b(0x9b00,
                                                 sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSbic(AtmelAsmParser.SbicContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_P_b(0x9900,
                                                 sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSbi(AtmelAsmParser.SbiContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_P_b(0x9a00,
                                                 sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSbci(AtmelAsmParser.SbciContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_K8(0x4000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitSbc(AtmelAsmParser.SbcContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x0800,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitRor(AtmelAsmParser.RorContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9407,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitRol(AtmelAsmParser.RolContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rd(0x1c00,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitRjmp(AtmelAsmParser.RjmpContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_k12(0xc000,
                                                 sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitReti(AtmelAsmParser.RetiContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x9518;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitRet(AtmelAsmParser.RetContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x9508;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitRcall(AtmelAsmParser.RcallContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_k12(0xd000,
                                                 sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitPush(AtmelAsmParser.PushContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x920f,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitPop(AtmelAsmParser.PopContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x900f,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitOut(AtmelAsmParser.OutContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_P_Rd(0xb800,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitOri(AtmelAsmParser.OriContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_K8(0x6000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitOr(AtmelAsmParser.OrContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x2800,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitNop(AtmelAsmParser.NopContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x0000;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitNeg(AtmelAsmParser.NegContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9401,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitMulsu(AtmelAsmParser.MulsuContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Muls(0x0300,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitMuls(AtmelAsmParser.MulsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Muls(0x0200,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitMul(AtmelAsmParser.MulContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x9c00,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitMovw(AtmelAsmParser.MovwContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Movw(0x0100,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitMov(AtmelAsmParser.MovContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x2c00,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitLsr(AtmelAsmParser.LsrContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9406,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitLsl(AtmelAsmParser.LslContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rd(0x0c00,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitLpm_naked(AtmelAsmParser.Lpm_nakedContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x95c8;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitLpm_Z(AtmelAsmParser.Lpm_ZContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    final Expression left = context.popExpression(sctx);
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    Supplier<Integer> opcode = () -> {
      int rd = left.evaluate(context);
      if (rd < 0 || rd > 31) {
        throw new InvalidParameterException("invalid rd " + rd,
                                            sctx).toWrapper();
      }
      return 0x9004 | (rd << 4);
    };
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitLpm_ZP(AtmelAsmParser.Lpm_ZPContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    final Expression left = context.popExpression(sctx);
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    Supplier<Integer> opcode = () -> {
      int rd = left.evaluate(context);
      if (rd < 0 || rd > 31) {
        throw new InvalidParameterException("invalid rd " + rd,
                                            sctx).toWrapper();
      }
      return 0x9005 | (rd << 4);
    };
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitLds(AtmelAsmParser.LdsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_K16(0x9000,
                                                    sctx);
    addOpcodeToSegment(opcode,
                       true,
                       sctx);
  }

  @Override
  public void exitLdi(AtmelAsmParser.LdiContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_K8(0xe000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  private void processLddStd(int baseOpcode,
                             SourceContext sctx)
  {
    final Expression right = context.popExpression(sctx);
    final Expression left = context.popExpression(sctx);
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    if (right.getType() != ExpressionType.INTEGER) {
      throw new InvalidTypeException(right.toString() + " is no integer",
                                     sctx).toWrapper();
    }
    Supplier<Integer> opcode = () -> {
      int rd = left.evaluate(context);
      int displacement = right.evaluate(context);
      if (rd < 0 || rd > 31) {
        throw new InvalidParameterException("invalid rd " + rd,
                                            sctx).toWrapper();
      }
      return baseOpcode | rd << 4 | ((displacement & 0x20) << 0x8) | ((displacement & 0x18) << 7) | (displacement & 0x7);
    };
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitLd_naked(AtmelAsmParser.Ld_nakedContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    AtmelAsmParser.Ptr_nakedContext ptrCtx = ctx.ptr_naked();
    int type = ptrCtx.getStart().getType();
    final int baseOpcode;
    switch (type) {
      case AtmelAsmParser.X_PTR:
        baseOpcode = 0x900c;
        break;
      case AtmelAsmParser.Y_PTR:
        baseOpcode = 0x8008;
        break;
      case AtmelAsmParser.Z_PTR:
        baseOpcode = 0x8000;
        break;
      default:
        throw new InvalidTypeException("Invalid PTR " + ptrCtx.getText(),
                                       sctx).toWrapper();
    }
    processLdSt(baseOpcode,
                sctx);
  }

  @Override
  public void exitLdd(AtmelAsmParser.LddContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    final AtmelAsmParser.Ptr_qContext ptrCtx = ctx.ptr_q();
    final int ptr = ptrCtx.getStart().getType();
    final int baseOpcode;
    switch (ptr) {
      case AtmelAsmParser.Y_PTR:
        baseOpcode = 0x8008;
        break;
      case AtmelAsmParser.Z_PTR:
        baseOpcode = 0x8000;
        break;
      default:
        throw new InvalidTypeException("Invalid PTR " + ptrCtx.getText(),
                                       sctx).toWrapper();
    }
    processLddStd(baseOpcode,
                  sctx);
  }

  @Override
  public void exitLd_plus(AtmelAsmParser.Ld_plusContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    final AtmelAsmParser.Ptr_plusContext ptrCtx = ctx.ptr_plus();
    final int ptr = ptrCtx.getStart().getType();
    final int baseOpcode;
    switch (ptr) {
      case AtmelAsmParser.X_PTR:
        baseOpcode = 0x900d;
        break;
      case AtmelAsmParser.Y_PTR:
        baseOpcode = 0x9009;
        break;
      case AtmelAsmParser.Z_PTR:
        baseOpcode = 0x9001;
        break;
      default:
        throw new InvalidTypeException("Invalid PTR " + ptrCtx.getText(),
                                       sctx).toWrapper();
    }
    processLdSt(baseOpcode,
                sctx);
  }

  @Override
  public void exitLd_minus(AtmelAsmParser.Ld_minusContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    final AtmelAsmParser.Ptr_minusContext ptrCtx = ctx.ptr_minus();
    final int ptr = ptrCtx.getStop().getType();
    final int baseOpcode;
    switch (ptr) {
      case AtmelAsmParser.X_PTR:
        baseOpcode = 0x900e;
        break;
      case AtmelAsmParser.Y_PTR:
        baseOpcode = 0x900a;
        break;
      case AtmelAsmParser.Z_PTR:
        baseOpcode = 0x9002;
        break;
      default:
        throw new InvalidTypeException("Invalid PTR " + ptrCtx.getText(),
                                       sctx).toWrapper();
    }
    processLdSt(baseOpcode,
                sctx);
  }

  public void processLdSt(int baseOpcode,
                          SourceContext sctx)
  {
    Expression left = context.popExpression(sctx);
    if (left.getType() != ExpressionType.REGISTER) {
      throw new InvalidTypeException(left.toString() + " is no register",
                                     sctx).toWrapper();
    }
    Supplier<Integer> opcode = () -> {
      int rd = left.evaluate(context);
      if (rd < 0 || rd > 31) {
        throw new InvalidParameterException("invalid rd " + rd,
                                            sctx).toWrapper();
      }
      return baseOpcode | rd << 4;
    };
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitLat(AtmelAsmParser.LatContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9207,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitLas(AtmelAsmParser.LasContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9205,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitLac(AtmelAsmParser.LacContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9206,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitJmp(AtmelAsmParser.JmpContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_K22(0x940c,
                                                 sctx);
    addOpcodeToSegment(opcode,
                       true,
                       sctx);
  }

  @Override
  public void exitInc(AtmelAsmParser.IncContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9403,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitIn(AtmelAsmParser.InContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_P(0xb000,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitIjmp(AtmelAsmParser.IjmpContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x9409;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitIcall(AtmelAsmParser.IcallContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x9509;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitFmulsu(AtmelAsmParser.FmulsuContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rdh23_Rrh23(0x0388,
                                                         sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitFmuls(AtmelAsmParser.FmulsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rdh23_Rrh23(0x0380,
                                                         sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitFmul(AtmelAsmParser.FmulContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rdh23_Rrh23(0x0308,
                                                         sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitEor(AtmelAsmParser.EorContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x2400,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitElpm_ZP(AtmelAsmParser.Elpm_ZPContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);

    Supplier<Integer> opcode = composeOpcode_Rd(0x9007,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitElpm_Z(AtmelAsmParser.Elpm_ZContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9006,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitElpm_naked(AtmelAsmParser.Elpm_nakedContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x95d8;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitEijmp(AtmelAsmParser.EijmpContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x9419;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitEicall(AtmelAsmParser.EicallContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = () -> 0x9519;
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitDes(AtmelAsmParser.DesContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_K4(0x940b,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitDec(AtmelAsmParser.DecContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x940a,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCpse(AtmelAsmParser.CpseContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x1000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCpi(AtmelAsmParser.CpiContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_K8(0x3000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCpc(AtmelAsmParser.CpcContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x0400,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCp(AtmelAsmParser.CpContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rr(0x1400,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCom(AtmelAsmParser.ComContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd(0x9400,
                                                sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitClz(AtmelAsmParser.ClzContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9488,
                                                       false,
                                                       1,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitClv(AtmelAsmParser.ClvContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9488,
                                                       false,
                                                       3,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitClt(AtmelAsmParser.CltContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9488,
                                                       false,
                                                       6,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCls(AtmelAsmParser.ClsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9488,
                                                       false,
                                                       4,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitClr(AtmelAsmParser.ClrContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_Rd(0x2400,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCln(AtmelAsmParser.ClnContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9488,
                                                       false,
                                                       2,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCli(AtmelAsmParser.CliContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9488,
                                                       false,
                                                       7,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitClh(AtmelAsmParser.ClhContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9488,
                                                       false,
                                                       5,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitClc(AtmelAsmParser.ClcContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9488,
                                                       false,
                                                       0,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCbr(AtmelAsmParser.CbrContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_K8(0x7000,
                                                   sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCbi(AtmelAsmParser.CbiContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_P_b(0x9800,
                                                 sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitCall(AtmelAsmParser.CallContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_K22(0x940e,
                                                 sctx);
    addOpcodeToSegment(opcode,
                       true,
                       sctx);
  }

  @Override
  public void exitBst(AtmelAsmParser.BstContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_b(0xfa00,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBset(AtmelAsmParser.BsetContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9408,
                                                       true,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrvs(AtmelAsmParser.BrvsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf000,
                                                  3,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrvc(AtmelAsmParser.BrvcContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf400,
                                                  3,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrts(AtmelAsmParser.BrtsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf000,
                                                  6,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrtc(AtmelAsmParser.BrtcContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf400,
                                                  6,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrsh(AtmelAsmParser.BrshContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf400,
                                                  0,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrpl(AtmelAsmParser.BrplContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf400,
                                                  2,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrne(AtmelAsmParser.BrneContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf400,
                                                  1,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrmi(AtmelAsmParser.BrmiContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf000,
                                                  2,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrlt(AtmelAsmParser.BrltContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf000,
                                                  7,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrlo(AtmelAsmParser.BrloContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf000,
                                                  0,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrie(AtmelAsmParser.BrieContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf000,
                                                  7,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrid(AtmelAsmParser.BridContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf400,
                                                  7,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrhs(AtmelAsmParser.BrhsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf400,
                                                  5,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrhc(AtmelAsmParser.BrhcContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf000,
                                                  5,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrge(AtmelAsmParser.BrgeContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf400,
                                                  4,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBreq(AtmelAsmParser.BreqContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf000,
                                                  1,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrcs(AtmelAsmParser.BrcsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf000,
                                                  0,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrcc(AtmelAsmParser.BrccContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf400,
                                                  0,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrbs(AtmelAsmParser.BrbsContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf000,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBrbc(AtmelAsmParser.BrbcContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_b_k7(0xf400,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBld(AtmelAsmParser.BldContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Rd_b(0xf800,
                                                  sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  @Override
  public void exitBclr(AtmelAsmParser.BclrContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Supplier<Integer> opcode = composeOpcode_Bclr_Bset(0x9488,
                                                       false,
                                                       sctx);
    addOpcodeToSegment(opcode,
                       false,
                       sctx);
  }

  //*********************************************************************************************************
  //*
  //* Functions and Operators
  //*
  //*********************************************************************************************************
  @Override
  public void exitFunc_log2(AtmelAsmParser.Func_log2Context ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    context.pushExpression(new Log2Operation(right,
                                             sctx));
  }

  @Override
  public void exitFunc_high(AtmelAsmParser.Func_highContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    context.pushExpression(new HighOperation(right,
                                             sctx));
  }

  @Override
  public void exitFunc_page(AtmelAsmParser.Func_pageContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    context.pushExpression(new PageOperation(right,
                                             sctx));
  }

  @Override
  public void exitFunc_low(AtmelAsmParser.Func_lowContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    context.pushExpression(new LowOperation(right,
                                            sctx));
  }

  @Override
  public void exitFunc_exp2(AtmelAsmParser.Func_exp2Context ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    context.pushExpression(new Exp2Operation(right,
                                             sctx));
  }

  @Override
  public void exitFunc_hwrd(AtmelAsmParser.Func_hwrdContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    context.pushExpression(new HwrdOperation(right,
                                             sctx));
  }

  @Override
  public void exitFunc_lwrd(AtmelAsmParser.Func_lwrdContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    context.pushExpression(new LwrdOperation(right,
                                             sctx));
  }

  @Override
  public void exitFunc_byte3(AtmelAsmParser.Func_byte3Context ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    context.pushExpression(new Byte3Operation(right,
                                              sctx));
  }

  @Override
  public void exitFunc_byte4(AtmelAsmParser.Func_byte4Context ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    context.pushExpression(new Byte4Operation(right,
                                              sctx));
  }

  @Override
  public void exitBitAnd(AtmelAsmParser.BitAndContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    context.pushExpression(new BitAndOperation(left,
                                               right,
                                               sctx));
  }

  @Override
  public void exitBitXor(AtmelAsmParser.BitXorContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    context.pushExpression(new BitXorOperation(left,
                                               right,
                                               sctx));
  }

  @Override
  public void exitBitOr(AtmelAsmParser.BitOrContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    context.pushExpression(new BitOrOperation(left,
                                              right,
                                              sctx));
  }

  @Override
  public void exitCompare(AtmelAsmParser.CompareContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    final String text = ctx.getChild(1).getText();
    switch (text) {
      case ">":
        context.pushExpression(new GreaterThanOperation(left,
                                                        right,
                                                        sctx));
        break;
      case ">=":
        context.pushExpression(new GreaterEqualThanOperation(left,
                                                             right,
                                                             sctx));
        break;
      case "<":
        context.pushExpression(new LessThanOperation(left,
                                                     right,
                                                     sctx));
        break;
      case "<=":
        context.pushExpression(new LessEqualOperation(left,
                                                      right,
                                                      sctx));
        break;
      default:
        throw new SyntaxException("unknown compare " + text,
                                  sctx).toWrapper();

    }
  }

  @Override
  public void exitProduct(AtmelAsmParser.ProductContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    String text = ctx.getChild(1).getText();
    switch (text) {
      case "*":
        context.pushExpression(new ProductOperation(left,
                                                    right,
                                                    sctx));
        break;
      case "/":
        context.pushExpression(new DivisionOperation(left,
                                                     right,
                                                     sctx));
        break;
      default:
        throw new SyntaxException("unkown operation " + text,
                                  sctx).toWrapper();
    }
  }

  @Override
  public void exitEqual(AtmelAsmParser.EqualContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    String text = ctx.getChild(1).getText();
    switch (text) {
      case "==":
        context.pushExpression(new EqualThanOperation(left,
                                                      right,
                                                      sctx));
        break;
      case "!=":
        context.pushExpression(new NotEqualThanOperation(left,
                                                         right,
                                                         sctx));
        break;
      default:
        throw new SyntaxException("unkown equal " + text,
                                  sctx).toWrapper();
    }
  }

  @Override
  public void exitLogAnd(AtmelAsmParser.LogAndContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    context.pushExpression(new LogAndOperation(left,
                                               right,
                                               sctx));
  }

  @Override
  public void exitUnary(AtmelAsmParser.UnaryContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    String text = ctx.getChild(0).getText();
    switch (text) {
      case "!":
        context.pushExpression(new LogNotOperation(right,
                                                   sctx));
        break;
      case "~":
        context.pushExpression(new BitNotOperation(right,
                                                   sctx));
        break;
      case "-":
        context.pushExpression(new UniMinusOperation(right,
                                                     sctx));
        break;
      default:
        throw new SyntaxException("unkown unary " + text,
                                  sctx).toWrapper();
    }
  }

  @Override
  public void exitSum(AtmelAsmParser.SumContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    String text = ctx.getChild(1).getText();
    switch (text) {
      case "+":
        context.pushExpression(new PlusOperation(left,
                                                 right,
                                                 sctx));
        break;
      case "-":
        context.pushExpression(new MinusOperation(left,
                                                  right,
                                                  sctx));
        break;
      default:
        throw new SyntaxException("unkown operation " + text,
                                  sctx).toWrapper();
    }
  }

  @Override
  public void exitLogOr(AtmelAsmParser.LogOrContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    context.pushExpression(new LogOrOperation(left,
                                              right,
                                              sctx));
  }

  @Override
  public void exitShift(AtmelAsmParser.ShiftContext ctx)
  {
    final SourceContext sctx = getSourceContext(ctx);
    Expression right = context.popExpression(sctx);
    Expression left = context.popExpression(sctx);
    String text = ctx.getChild(1).getText();
    switch (text) {
      case "<<":
        context.pushExpression(new LeftShiftOperation(left,
                                                      right,
                                                      sctx));
        break;
      case ">>":
        context.pushExpression(new RightShiftOperation(left,
                                                       right,
                                                       sctx));
        break;
      default:
        throw new SyntaxException("unkonw operation " + text,
                                  sctx).toWrapper();
    }
  }

  //*********************************************************************************************************
  //*
  //* Terminals
  //*
  //*********************************************************************************************************
  @Override
  public void exitLabel(AtmelAsmParser.LabelContext ctx)
  {
    String name = ctx.NAME().getText();
    context.addAlias(new AliasImpl(name,
                                   true,
                                   new IntExpression(context.getCurrentPosition(),
                                                     getSourceContext(ctx))),
                     getSourceContext(ctx));
  }

  @Override
  public void exitString(AtmelAsmParser.StringContext ctx)
  {
    String text = removeQoutes(ctx.STRING().getText());
    context.pushExpression(new StringExpression(text,
                                                context.getConfig().getTargetCharset(),
                                                getSourceContext(ctx)));
  }

  @Override
  public void exitInt(AtmelAsmParser.IntContext ctx)
  {
    context.pushExpression(new IntExpression(ctx.getText(),
                                             getSourceContext(ctx)));
  }

  @Override
  public void exitName(AtmelAsmParser.NameContext ctx)
  {
    Alias alias = context.getAlias(ctx.getText());
    if (alias == null) {
      context.pushExpression(new ForwardExpression(ctx.getText(),
                                                   getSourceContext(ctx)));
      return;
    }
    context.pushExpression(alias.getExpression());
  }

  @Override
  public void exitRegName(AtmelAsmParser.RegNameContext ctx)
  {
    Alias alias = context.getAlias(ctx.getText());
    if (alias != null) {
      context.pushExpression(alias.getExpression());
    }
  }

}
