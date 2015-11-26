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
import com.avrwb.assembler.model.impl.BitAndOperation;
import com.avrwb.assembler.model.impl.BitNotOperation;
import com.avrwb.assembler.model.impl.BitOrOperation;
import com.avrwb.assembler.model.impl.BitXorOperation;
import com.avrwb.assembler.model.impl.Byte3Operation;
import com.avrwb.assembler.model.impl.Byte4Operation;
import com.avrwb.assembler.model.impl.DivisionOperation;
import com.avrwb.assembler.model.impl.EqualThanOperation;
import com.avrwb.assembler.model.impl.Exp2Operation;
import com.avrwb.assembler.model.impl.GreaterEqualThanOperation;
import com.avrwb.assembler.model.impl.GreaterThanOperation;
import com.avrwb.assembler.model.impl.HighOperation;
import com.avrwb.assembler.model.impl.HwrdOperation;
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
import com.avrwb.assembler.model.impl.UniMinusOperation;
import com.avrwb.assembler.parser.AtmelAsmBaseListener;
import com.avrwb.assembler.parser.AtmelAsmParser;

/**
 *
 * @author wolfi
 */
public class ContextListener extends AtmelAsmBaseListener
{

  private final Context context;

  public ContextListener(Context context)
  {
    this.context = context != null ? context : new Context();
  }

  public Context getContext()
  {
    return context;
  }

  @Override
  public void exitInt(AtmelAsmParser.IntContext ctx)
  {
    context.pushExpression(new IntExpression(ctx.getText()));
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
                                                        right));
        break;
      case ">=":
        context.pushExpression(new GreaterEqualThanOperation(left,
                                                             right));
        break;
      case "<":
        context.pushExpression(new LessThanOperation(left,
                                                     right));
        break;
      case "<=":
        context.pushExpression(new LessEqualOperation(left,
                                                      right));
        break;
      default:
        throw new AssemblerException("unknown compare " + text);

    }
  }

  @Override
  public void exitBitAnd(AtmelAsmParser.BitAndContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    context.pushExpression(new BitAndOperation(left,
                                               right));
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
                                                      right));
        break;
      case "!=":
        context.pushExpression(new NotEqualThanOperation(left,
                                                         right));
        break;
      default:
        throw new AssemblerException("unkown equal " + text);
    }
  }

  @Override
  public void exitBitXor(AtmelAsmParser.BitXorContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    context.pushExpression(new BitXorOperation(left,
                                               right));
  }

  @Override
  public void exitLogAnd(AtmelAsmParser.LogAndContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    context.pushExpression(new LogAndOperation(left,
                                               right));
  }

  @Override
  public void exitUnary(AtmelAsmParser.UnaryContext ctx)
  {
    Expression right = context.popExpression();
    String text = ctx.getChild(0).getText();
    switch (text) {
      case "!":
        context.pushExpression(new LogNotOperation(right));
        break;
      case "~":
        context.pushExpression(new BitNotOperation(right));
        break;
      case "-":
        context.pushExpression(new UniMinusOperation(right));
        break;
      default:
        throw new AssemblerException("unkown unary " + text);
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
                                                 right));
        break;
      case "-":
        context.pushExpression(new MinusOperation(left,
                                                  right));
        break;
      default:
        throw new AssemblerException("unkown operation " + text);
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
                                                    right));
        break;
      case "/":
        context.pushExpression(new DivisionOperation(left,
                                                     right));
        break;
      default:
        throw new AssemblerException("unkown operation " + text);
    }
  }

  @Override
  public void exitLogOr(AtmelAsmParser.LogOrContext ctx)
  {
    Expression right = context.popExpression();
    Expression left = context.popExpression();
    context.pushExpression(new LogOrOperation(left,
                                              right));
  }

  @Override
  public void exitFunc(AtmelAsmParser.FuncContext ctx)
  {
    Expression right = context.popExpression();
    String text = ctx.getChild(0).getText();
    switch (text) {
      case "byte3":
        context.pushExpression(new Byte3Operation(right));
        break;
      case "byte4":
        context.pushExpression(new Byte4Operation(right));
        break;
      case "exp2":
        context.pushExpression(new Exp2Operation(right));
        break;
      case "high":
      case "byte2":
        context.pushExpression(new HighOperation(right));
        break;
      case "hwrd":
        context.pushExpression(new HwrdOperation(right));
        break;
      case "log2":
        context.pushExpression(new Log2Operation(right));
        break;
      case "low":
        context.pushExpression(new LowOperation(right));
        break;
      case "lwrd":
        context.pushExpression(new LwrdOperation(right));
        break;
      case "page":
        context.pushExpression(new PageOperation(right));
        break;
      default:
        throw new AssemblerException("unkown func " + text);
    }
  }

  @Override
  public void exitBitOr(AtmelAsmParser.BitOrContext ctx)
  {
    Expression left = context.popExpression();
    Expression right = context.popExpression();
    context.pushExpression(new BitOrOperation(left,
                                              right));
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
                                                      right));
        break;
      case ">>":
        context.pushExpression(new RightShiftOperation(left,
                                                       right));
        break;
      default:
        throw new AssemblerException("unkonw operation " + text);
    }
  }

}
