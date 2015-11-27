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
import com.avrwb.assembler.AssemblerException;
import com.avrwb.assembler.model.impl.AssemblerImpl;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class ContextNGTest
{

  private static URL includeFile;
  private static Assembler assembler;

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    File iFile = File.createTempFile("ContextNGTest_inc",
                                     ".def");
    iFile.createNewFile();
    try (FileWriter writer = new FileWriter(iFile)) {
      writer.write(".device AT90S1200\n");
      writer.write(".equ	sreg	= 0x3f\n");
      writer.write(".equ	spl	= 0x3d\n");
      writer.write(".equ	sph	= 0x3e\n");
      writer.write(".set val1 = 0x20\n");
      writer.write(".set val2 = 0x30\n");
      writer.write(".set val1 = 0x40\n");
    }
    iFile.deleteOnExit();
    includeFile = iFile.toURI().toURL();
    assembler = new AssemblerImpl();
  }

  @Test
  public void testPlus() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".db 1+1\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 2);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void testMinus() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".db 1-1\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 0);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void test4() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".db -5+10-1\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 4);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void test5() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".db -5+7*2+1\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 10);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void test6() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".db (-5+7)*2+1\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 5);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void test7() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".db (-5+(7<<2))*2+(10/3)\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 49);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void testLogNotFalse() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".db !(1+1)\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 0);
    assertTrue(tcl.getContext().isExpressionStackEmpty());

  }

  @Test
  public void testLogNotTrue() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".db !(1-1)\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 1);
    assertTrue(tcl.getContext().isExpressionStackEmpty());

  }

  @Test
  public void testBinaryNot() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".db ~0x55\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 0xffffffaa);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void testEqu() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".equ	sreg	= 0x3f\n");
    assertTrue(tcl.getContext().isExpressionStackEmpty());
    Alias alias = tcl.getContext().getAlias("sreg");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "sreg");
    assertEquals(alias.getValue(),
                 0x3f);
  }

  @Test(expectedExceptions = AssemblerException.class)
  public void testEquCollision() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".equ	sreg	= 0x3f\n" + ".equ tmp=0x20\n" + ".equ sreg=0x20\n");
    fail();
  }

  @Test
  public void testInclude() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".include \"" + includeFile.toString() + "\"\n");
    Context context = tcl.getContext();
    assertTrue(context.isExpressionStackEmpty());
    Alias alias = context.getAlias("sreg");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "sreg");
    assertEquals(alias.getValue(),
                 0x3f);
    alias = context.getAlias("sph");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "sph");
    assertEquals(alias.getValue(),
                 0x3e);
    alias = context.getAlias("spl");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "spl");
    assertEquals(alias.getValue(),
                 0x3d);
    alias = context.getAlias("val1");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "val1");
    assertEquals(alias.getValue(),
                 0x40);
    alias = context.getAlias("val2");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "val2");
    assertEquals(alias.getValue(),
                 0x30);

  }

  @Test
  public void testResolveName() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".set VAL1 = 0xf0\n" + ".db VAL1 & 0x88\n");
    Context ctx = tcl.getContext();
    Alias alias = ctx.getAlias("VAL1");
    assertNotNull(alias);
    assertEquals(alias.getValue(),
                 0xf0);
    Expression exp = ctx.popExpression();
    assertTrue(ctx.isExpressionStackEmpty());
    assertEquals(exp.evaluate(),
                 0x80);
  }

  @Test
  public void testResolveName2() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".set VAL1 = 0xf0\n" + ".db VAL1 & 0x88, \"Wolfgang Reder\"\n");
    Context ctx = tcl.getContext();
    Alias alias = ctx.getAlias("VAL1");
    assertNotNull(alias);
    assertEquals(alias.getValue(),
                 0xf0);
    Expression exp = ctx.popExpression();
    assertTrue(ctx.isExpressionStackEmpty());
    assertEquals(exp.evaluate(),
                 0x80);
  }

}
