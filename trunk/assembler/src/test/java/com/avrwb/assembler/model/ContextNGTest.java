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

import java.io.IOException;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class ContextNGTest
{

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @Test
  public void testPlus() throws IOException
  {
    TestContextListener tcl = new TestContextListener();
    tcl.parse(".db 1+1\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 2);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void testMinus() throws IOException
  {
    TestContextListener tcl = new TestContextListener();
    tcl.parse(".db 1-1\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 0);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void test4() throws IOException
  {
    TestContextListener tcl = new TestContextListener();
    tcl.parse(".db -5+10-1\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 4);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void test5() throws IOException
  {
    TestContextListener tcl = new TestContextListener();
    tcl.parse(".db -5+7*2+1\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 10);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void test6() throws IOException
  {
    TestContextListener tcl = new TestContextListener();
    tcl.parse(".db (-5+7)*2+1\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 5);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

  @Test
  public void test7() throws IOException
  {
    TestContextListener tcl = new TestContextListener();
    tcl.parse(".db (-5+(7<<2))*2+(10/3)\n");
    Expression exp = tcl.getContext().popExpression();
    assertEquals(exp.evaluate(),
                 49);
    assertTrue(tcl.getContext().isExpressionStackEmpty());
  }

}
