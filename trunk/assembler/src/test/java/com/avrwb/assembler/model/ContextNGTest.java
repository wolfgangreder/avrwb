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

  @Test(enabled = false, expectedExceptions = AssemblerException.class)
  public void testEquCollision() throws IOException, AssemblerException
  {
    TestContextListener tcl = new TestContextListener(assembler);
    tcl.parse(".equ	sreg	= 0x3f\n" + ".equ tmp=0x20\n" + ".equ sreg=0x20\n");
    fail();
  }

  @Test(enabled = false)
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
    AssemblerResult asr = tcl.parse(getIncludeLine()
                                    + "\nrjmp pushseq\nrjmp init\nadc r32,r31\nmov r0,r1\n\nmov r20,r0\npush r0 ; push hot gfeugelt\nnop\nnop\n.org 0x21\ninit:\nmov r22,r23\npop r22\npushseq:\npush r0\npush r1\npush r2\nin r0,spl\npush r0\nout spl,r1\nrjmp init\n");
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

}