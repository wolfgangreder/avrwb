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
import com.avrwb.assembler.AssemblerResult;
import com.avrwb.assembler.NameAlreadyDefinedException;
import com.avrwb.assembler.StandardAssemblerSource;
import com.avrwb.assembler.model.impl.AssemblerImpl;
import com.avrwb.io.IntelHexOutputStream;
import com.avrwb.io.MemoryChunkOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class ContextNGTest
{

  private static Path includeFile;
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
      writer.write(".set val1 = 0x40\n\n");
    }
    iFile.deleteOnExit();
    includeFile = iFile.toPath();
    assembler = new AssemblerImpl();
  }

  private String getIncludeLine()
  {
    return ".include \"" + includeFile.toString() + "\"\n";
  }

  @Test(expectedExceptions = NameAlreadyDefinedException.class)
  public void testEquCollision() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".equ	sreg	= 0x3f\n" + ".equ tmp=0x20\n" + ".equ sReg=0x20");
    fail();
  }

  @Test
  public void testInclude() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(getIncludeLine());
    Context context = tcl.getContext();
    assertTrue(context.isExpressionStackEmpty());
    Alias alias = context.getAlias("sreg");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "sreg");
    assertEquals(alias.getValue(context),
                 0x3f);
    alias = context.getAlias("sph");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "sph");
    assertEquals(alias.getValue(context),
                 0x3e);
    alias = context.getAlias("spl");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "spl");
    assertEquals(alias.getValue(context),
                 0x3d);
    alias = context.getAlias("val1");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "val1");
    assertEquals(alias.getValue(context),
                 0x40);
    alias = context.getAlias("val2");
    assertNotNull(alias,
                  "alias==null");
    assertEquals(alias.getName(),
                 "val2");
    assertEquals(alias.getValue(context),
                 0x30);

  }

  @Test
  public void testAdc() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    AssemblerResult asr = tcl.parse("adc r30,r31");
    assertNotNull(asr);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
  }

  @Test
  public void testAdc1() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    AssemblerResult asr = tcl.parse(getIncludeLine()
                                            + "\nrjmp pushseq\nrjmp init\nadc r30,r31\nmov r0,r1\n\nmov r20,r0\npush r0 ; push hot gfeugelt\nnop\nnop\n.org 0x21\ninit:\nmov r22,r23\npop r22\npushseq:\npush r0\npush r1\npush r2\nin r0,spl\npush r0\nout spl,r1\nrjmp init\n");
    assertNotNull(asr);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
  }

  @Test
  public void testBranch() throws IOException, AssemblerException, URISyntaxException
  {
    URL u = getClass().getResource("/asm/br1.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }

  }

  @Test
  public void testMov1() throws IOException, AssemblerException, URISyntaxException
  {
    URL u = getClass().getResource("/asm/mov1.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }

  }

  @Test
  public void testBr2() throws Exception
  {
    URL u = getClass().getResource("/asm/br2.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
  }

  @Test
  public void testBr3() throws Exception
  {
    URL u = getClass().getResource("/asm/br3.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
  }

  @Test
  public void testBr4() throws Exception
  {
    URL u = getClass().getResource("/asm/br4.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
  }

  @Test
  public void testCall1() throws Exception
  {
    URL u = getClass().getResource("/asm/call1.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
  }

  @Test
  public void testPadding1() throws Exception
  {
    URL u = getClass().getResource("/asm/padding1.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
  }

  @Test
  public void testPadding2() throws Exception
  {
    URL u = getClass().getResource("/asm/padding2.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
  }

  @Test
  public void testElpm1() throws Exception
  {
    URL u = getClass().getResource("/asm/elpm1.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
    assertTrue(asr.isCSEGAvailable());
  }

  @Test
  public void testElpm2() throws Exception
  {
    URL u = getClass().getResource("/asm/elpm2.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
    assertTrue(asr.isCSEGAvailable());
  }

  @Test
  public void testElpm3() throws Exception
  {
    URL u = getClass().getResource("/asm/elpm3.asm");
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
    assertTrue(asr.isCSEGAvailable());
  }

  @DataProvider(name = "LDNakedProvider")
  public Object[][] getData()
  {
    return new Object[][]{
      {"/asm/ld_X.asm"},
      {"/asm/ld_Y.asm"},
      {"/asm/ld_Z.asm"}
    };
  }

  @Test(dataProvider = "LDNakedProvider")
  public void testLdNaked(String file) throws Exception
  {
    URL u = getClass().getResource(file);
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
    assertTrue(asr.isCSEGAvailable());
  }

  @DataProvider(name = "LDMinusProvider")
  public Object[][] getDataLDM()
  {
    return new Object[][]{
      {"/asm/ld_MX.asm"},
      {"/asm/ld_MY.asm"},
      {"/asm/ld_MZ.asm"}
    };
  }

  @Test(dataProvider = "LDMinusProvider")
  public void testLdMinus(String file) throws Exception
  {
    URL u = getClass().getResource(file);
    AssemblerSource source = new StandardAssemblerSource(Paths.get(u.toURI()));
    AssemblerResult asr = assembler.compile(source,
                                            null);
    try (Writer writer = new PrintWriter(System.out)) {
      asr.getList(writer);
    }
    try (MemoryChunkOutputStream os = new IntelHexOutputStream(System.out)) {
      asr.getCSEG(os);
    }
    assertTrue(asr.isCSEGAvailable());
  }

}
