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
package com.avrwb.avr8.impl;

import com.avrwb.avr8.api.instructions.AbstractInstructionTest;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
@Test(enabled = false)
public class DefaultInstructionDecoderNGTest extends AbstractInstructionTest
{
//
//
//  @Test
//  public void testDecodeInstructionMul()
//  {
////    int opcode = constructOpcodeRdRr(Mul.OPCODE,
////                                     5,
////                                     31);
////    int nextOpcode = 0x0;
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    nextOpcode);
////    assertTrue(result instanceof Mul);
////    assertEquals("mul r5, r31",
////                 result.toString());
////    assertRdRr(instance,
////               Mul.OPCODE,
////               Mul.class,
////               "mul");
//  }
//
//  private void assertLdPtr(ProtocollDecoder decoder,
//                           Pointer pointer)
//  {
//    Instruction instruction;
//    for (int rd = 0; rd < 32; ++rd) {
//      String context = "ld r" + rd + ", " + pointer.name() + " | ";
//      int opcode = constructOpcodeLd(pointer,
//                                     rd,
//                                     Ld.Mode.UNMODIFIED,
//                                     0);
//      instruction = decoder.decodeInstruction(mega8,
//                                              opcode,
//                                              0);
//      assertTrue(context + "class",
//                 instruction instanceof Ld);
//      Ld ld = (Ld) instruction;
//      assertEquals(context + "mnemonic",
//                   "ld",
//                   ld.getMnemonic());
//      assertEquals(context + "rd",
//                   rd,
//                   ld.getRdAddress());
//      assertEquals(context + "ptr",
//                   pointer,
//                   ld.getPointer());
//      assertEquals(context + "disp",
//                   0,
//                   ld.getDisplacement());
//      assertEquals(context + "mode",
//                   Ld.Mode.UNMODIFIED,
//                   ld.getMode());
//    }
//    for (int rd = 0; rd < 32; ++rd) {
//      String context = "ld r" + rd + ", " + pointer.name() + "+ | ";
//      int opcode = constructOpcodeLd(pointer,
//                                     rd,
//                                     Ld.Mode.POST_INCREMENT,
//                                     0);
//      instruction = decoder.decodeInstruction(mega8,
//                                              opcode,
//                                              0);
//      assertTrue(context + "class",
//                 instruction instanceof Ld);
//      Ld ld = (Ld) instruction;
//      assertEquals(context + "mnemonic",
//                   "ld",
//                   ld.getMnemonic());
//      assertEquals(context + "rd",
//                   rd,
//                   ld.getRdAddress());
//      assertEquals(context + "ptr",
//                   pointer,
//                   ld.getPointer());
//      assertEquals(context + "disp",
//                   0,
//                   ld.getDisplacement());
//      assertEquals(context + "mode",
//                   Ld.Mode.POST_INCREMENT,
//                   ld.getMode());
//    }
//    for (int rd = 0; rd < 32; ++rd) {
//      String context = "ld r" + rd + ", -" + pointer.name() + " | ";
//      int opcode = constructOpcodeLd(pointer,
//                                     rd,
//                                     Ld.Mode.PRE_DECREMENT,
//                                     0);
//      instruction = decoder.decodeInstruction(mega8,
//                                              opcode,
//                                              0);
//      assertTrue(context + "class",
//                 instruction instanceof Ld);
//      Ld ld = (Ld) instruction;
//      assertEquals(context + "mnemonic",
//                   "ld",
//                   ld.getMnemonic());
//      assertEquals(context + "rd",
//                   rd,
//                   ld.getRdAddress());
//      assertEquals(context + "ptr",
//                   pointer,
//                   ld.getPointer());
//      assertEquals(context + "disp",
//                   0,
//                   ld.getDisplacement());
//      assertEquals(context + "mode",
//                   Ld.Mode.PRE_DECREMENT,
//                   ld.getMode());
//    }
//    if (pointer != Pointer.X) {
//      for (int rd = 0; rd < 32; ++rd) {
//        for (int q = 0; q < 64; ++q) {
//          String context = "ldd r" + rd + ", " + pointer.name() + "+" + q + " | ";
//          int opcode = constructOpcodeLd(pointer,
//                                         rd,
//                                         Ld.Mode.DISPLACEMENT,
//                                         q);
//          instruction = decoder.decodeInstruction(mega8,
//                                                  opcode,
//                                                  0);
//          if (q != 0) {
//            assertNull(context + "xmega",
//                       instruction);
//            instruction = decoder.decodeInstruction(xmega,
//                                                    opcode,
//                                                    0);
//          }
//          assertTrue(context + "class",
//                     instruction instanceof Ld);
//          Ld ld = (Ld) instruction;
//          assertEquals(context + "disp",
//                       q,
//                       ld.getDisplacement());
//          assertEquals(context + "rd",
//                       rd,
//                       ld.getRdAddress());
//          assertEquals(context + "ptr",
//                       pointer,
//                       ld.getPointer());
//          if (q != 0) {
//            assertEquals(context + "mnemonic",
//                         "ldd",
//                         ld.getMnemonic());
//            assertEquals(context + "mode",
//                         Ld.Mode.DISPLACEMENT,
//                         ld.getMode());
//          } else {
//            assertEquals(context + "mnemonic",
//                         "ld",
//                         ld.getMnemonic());
//            assertEquals(context + "mode",
//                         Ld.Mode.UNMODIFIED,
//                         ld.getMode());
//          }
//        }
//      }
//
//    }
//  }
//
//  @Test
//  public void testLd_X()
//  {
//    int opcode = constructOpcodeLd(Pointer.X,
//                                   17,
//                                   Ld.Mode.UNMODIFIED,
//                                   0);//0x911c;
//    ProtocollDecoder instance = new ProtocollDecoder();
//    Instruction result = instance.decodeInstruction(mega8,
//                                                    opcode,
//                                                    0);
//    assertTrue(result instanceof Ld);
//    assertEquals("ld r17, X",
//                 result.toString());
//    assertLdPtr(instance,
//                Pointer.X);
//  }
//
//  @Test
//  public void testLd_Y()
//  {
//    int opcode = constructOpcodeLd(Pointer.Y,
//                                   17,
//                                   Ld.Mode.DISPLACEMENT,
//                                   5);
//    ProtocollDecoder instance = new ProtocollDecoder();
//    Instruction result = instance.decodeInstruction(mega8,
//                                                    opcode,
//                                                    0);
//    assertNull(result);
//    result = instance.decodeInstruction(xmega,
//                                        opcode,
//                                        0);
//    assertTrue(result instanceof Ld);
//    assertEquals("ldd r17, Y+5",
//                 result.toString());
//    assertLdPtr(instance,
//                Pointer.Y);
//  }
//
//  @Test
//  public void testLd_Z()
//  {
//    int opcode = constructOpcodeLd(Pointer.Z,
//                                   1,
//                                   Ld.Mode.PRE_DECREMENT,
//                                   0);
//    ProtocollDecoder instance = new ProtocollDecoder();
//    Instruction result = instance.decodeInstruction(mega8,
//                                                    opcode,
//                                                    0);
//    assertTrue(result instanceof Ld);
//    assertEquals("ld r1, -Z",
//                 result.toString());
//    assertLdPtr(instance,
//                Pointer.Z);
//  }
//
//  @Test
//  public void testDES()
//  {
//    ProtocollDecoder instance = new ProtocollDecoder();
//    for (int i = 0; i < 16; ++i) {
//      int opcode = constructOpcodeK4(Des.OPCODE,
//                                     i);
//      String context = "des " + i + " | ";
//      Instruction result = instance.decodeInstruction(mega8,
//                                                      opcode,
//                                                      0);
//      assertTrue(context + "class",
//                 result instanceof Des);
//      assertEquals("des " + i,
//                   result.toString());
//      assertEquals(context + "k4",
//                   i,
//                   ((Des) result).getK4());
//    }
//  }
//
////  @Test
////  public void testEICall()
////  {
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    EICall.OPCODE,
////                                                    0);
////    assertTrue(result instanceof EICall);
////    assertEquals("eicall",
////                 result.toString());
////  }
////  @Test
////  public void testEIJmp()
////  {
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    EIJmp.OPCODE,
////                                                    0);
////    assertTrue(result instanceof EIJmp);
////    assertEquals("eijmp",
////                 result.toString());
////  }
//  @Test
//  public void testElmp()
//  {
//    ProtocollDecoder instance = new ProtocollDecoder();
//    Instruction result = instance.decodeInstruction(mega8,
//                                                    Elpm.OPCODE_ELPM,
//                                                    0);
//    assertTrue(result instanceof Elpm);
//    assertEquals("elpm",
//                 result.toString());
//    Elpm el = (Elpm) result;
//    assertEquals(0,
//                 el.getRdAddress());
//    assertFalse(el.isPostIncrement());
//    for (int rd = 0; rd < 32; ++rd) {
//      String context = "elpm r" + rd + ", Z | ";
//      int opcode = Elpm.OPCODE_ELPM_RD | (rd << 4);
//      result = instance.decodeInstruction(mega8,
//                                          opcode,
//                                          0);
//      assertTrue(context + "class",
//                 result instanceof Elpm);
//      el = (Elpm) result;
//      assertEquals(context + "rd",
//                   rd,
//                   el.getRdAddress());
//      assertFalse(context + "pi",
//                  el.isPostIncrement());
//    }
//    for (int rd = 0; rd < 32; ++rd) {
//      String context = "elpm r" + rd + ", Z+ | ";
//      int opcode = Elpm.OPCODE_ELPM_RDP | (rd << 4);
//      result = instance.decodeInstruction(mega8,
//                                          opcode,
//                                          0);
//      assertTrue(context + "class",
//                 result instanceof Elpm);
//      el = (Elpm) result;
//      assertEquals(context + "rd",
//                   rd,
//                   el.getRdAddress());
//      assertTrue(context + "pi",
//                 el.isPostIncrement());
//    }
//  }
//
////  @Test
////  public void testFmul()
////  {
////    final ProtocollDecoder decoder = new ProtocollDecoder();
////    for (int rd = 16; rd < 24; ++rd) {
////      for (int rr = 16; rr < 24; ++rr) {
////        final String context = "fmul r" + rd + ",r" + rr + " | ";
////        final int opcode = Fmul.composeOpcode(Fmul.OPCODE,
////                                              rd,
////                                              rr);
////        final Instruction i = decoder.decodeInstruction(mega8,
////                                                        opcode,
////                                                        0);
////        assertTrue(context + "class",
////                   i instanceof Fmul);
////        Fmul fmul = (Fmul) i;
////        assertEquals(context + "rd",
////                     rd,
////                     fmul.getRdhAddress());
////        assertEquals(context + "rr",
////                     rr,
////                     fmul.getRrhAddress());
////        assertEquals(context + "toString",
////                     "fmul r" + rd + ", r" + rr,
////                     fmul.toString());
////      }
////    }
////  }
//  @Test
//  public void testFmuls()
//  {
//    final ProtocollDecoder decoder = new ProtocollDecoder();
//    for (int rd = 16; rd < 24; ++rd) {
//      for (int rr = 16; rr < 24; ++rr) {
//        final String context = "fmuls r" + rd + ",r" + rr + " | ";
//        final int opcode = Fmuls.composeOpcode(Fmuls.OPCODE,
//                                               rd,
//                                               rr);
//        final Instruction i = decoder.decodeInstruction(mega8,
//                                                        opcode,
//                                                        0);
//        assertTrue(context + "class",
//                   i instanceof Fmuls);
//        Fmuls fmul = (Fmuls) i;
//        assertEquals(context + "rd",
//                     rd,
//                     fmul.getRdhAddress());
//        assertEquals(context + "rr",
//                     rr,
//                     fmul.getRrhAddress());
//        assertEquals(context + "toString",
//                     "fmuls r" + rd + ", r" + rr,
//                     fmul.toString());
//      }
//    }
//  }
//
////  @Test
////  public void testFmulsu()
////  {
////    final ProtocollDecoder decoder = new ProtocollDecoder();
////    for (int rd = 16; rd < 24; ++rd) {
////      for (int rr = 16; rr < 24; ++rr) {
////        final String context = "fmulsu r" + rd + ",r" + rr + " | ";
////        final int opcode = Fmulsu.composeOpcode(Fmulsu.OPCODE,
////                                                rd,
////                                                rr);
////        final Instruction i = decoder.decodeInstruction(mega8,
////                                                        opcode,
////                                                        0);
////        assertTrue(context + "class",
////                   i instanceof Fmulsu);
////        Fmulsu fmul = (Fmulsu) i;
////        assertEquals(context + "rd",
////                     rd,
////                     fmul.getRdhAddress());
////        assertEquals(context + "rr",
////                     rr,
////                     fmul.getRrhAddress());
////        assertEquals(context + "toString",
////                     "fmulsu r" + rd + ", r" + rr,
////                     fmul.toString());
////      }
////    }
////  }
////  @Test
////  public void testLac()
////  {
////    final ProtocollDecoder decoder = new ProtocollDecoder();
////    assertRd(decoder,
////             Lac.OPCODE,
////             Lac.class,
////             "lac");
////  }
////
////  @Test
////  public void testLas()
////  {
////    final ProtocollDecoder decoder = new ProtocollDecoder();
////    assertRd(decoder,
////             Las.OPCODE,
////             Las.class,
////             "las");
////  }
////
////  @Test
////  public void testLat()
////  {
////    final ProtocollDecoder decoder = new ProtocollDecoder();
////    assertRd(decoder,
////             Lat.OPCODE,
////             Lat.class,
////             "lat");
////  }
////  @Test
////  public void testLds()
////  {
////    final ProtocollDecoder decoder = new ProtocollDecoder();
////    final int k = 0xffff;
////    for (int rd = 0; rd < 32; ++rd) {
////      String context = "lds r" + rd + " " + Converter.printHexString(k,
////                                                                     4) + " | ";
////      final int opcode = Lds.composeOpcode(Lds.OPCODE,
////                                           rd,
////                                           k);
////      Instruction i = decoder.decodeInstruction(mega8,
////                                                (opcode >> 16),
////                                                opcode & 0xffff);
////      assertTrue(context + "class",
////                 i instanceof Lds);
////      Lds l = (Lds) i;
////      assertEquals(context + "rd",
////                   rd,
////                   l.getRdAddress());
////      assertEquals(context + "k16",
////                   k,
////                   l.getK16());
////      assertEquals(context + "mnemonic",
////                   "lds",
////                   l.getMnemonic());
////    }
////  }
////  @Test
////  public void testLds26()
////  {
////    final ProtocollDecoder decoder = new ProtocollDecoder();
////    for (int k = 0; k < 0x80; ++k) {
////      for (int rd = 16; rd < 32; ++rd) {
////        String context = "lds r" + rd + " " + Converter.printHexString(k,
////                                                                       4) + " | ";
////        final int opcode = Lds16.composeOpcode(Lds16.OPCODE,
////                                               rd,
////                                               k);
////        Instruction i = decoder.decodeInstruction(mega8,
////                                                  opcode,
////                                                  0);
////        assertTrue(context + "class",
////                   i instanceof Lds16);
////        Lds16 l = (Lds16) i;
////        assertEquals(context + "rd",
////                     rd,
////                     l.getRdhAddress());
////        assertEquals(context + "k7",
////                     k,
////                     l.getK7());
////        assertEquals(context + "mnemonic",
////                     "lds",
////                     l.getMnemonic());
////      }
////    }
////  }
}
