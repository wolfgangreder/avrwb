/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avrwb.assembler;

import java.net.URL;
import static org.testng.Assert.assertNotNull;

/**
 *
 * @author wolfi
 */
public class AssemblerNGTest
{

  public AssemblerNGTest()
  {
  }

  @org.testng.annotations.BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @org.testng.annotations.Test
  public void testCompile() throws Exception
  {
    URL u = Assembler.class.getResource("/asm/mov1.asm");
    assertNotNull(u,
                  "cannot find file");
    Assembler.compile(u.openStream(),
                      null);
  }

}
