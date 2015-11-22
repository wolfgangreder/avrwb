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
////  @Test
////  public void testDecodeInstructionNop()
////  {
////    int opcode = 0x0;
////    int nextOpcode = 0x0;
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    nextOpcode);
////    assertTrue(result instanceof Nop);
////  }
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
//  @Test
//  public void testDecodeBitClearSet()
//  {
//    Map<Integer, String> expected = new HashMap();
//    expected.put(0x9408,
//                 "sec");
//    expected.put(0x9418,
//                 "sez");
//    expected.put(0x9428,
//                 "sen");
//    expected.put(0x9438,
//                 "sev");
//    expected.put(0x9448,
//                 "ses");
//    expected.put(0x9458,
//                 "seh");
//    expected.put(0x9468,
//                 "set");
//    expected.put(0x9478,
//                 "sei");
//    expected.put(0x9488,
//                 "clc");
//    expected.put(0x9498,
//                 "clz");
//    expected.put(0x94a8,
//                 "cln");
//    expected.put(0x94b8,
//                 "clv");
//    expected.put(0x94c8,
//                 "cls");
//    expected.put(0x94d8,
//                 "clh");
//    expected.put(0x94e8,
//                 "clt");
//    expected.put(0x94f8,
//                 "cli");
//    assertEquals(16,
//                 expected.size());
//    ProtocollDecoder instance = new ProtocollDecoder();
//    for (int opcode = 0x9407; opcode < 0x94fa; ++opcode) {
//      String context = Integer.toHexString(opcode) + " | ";
//      Instruction instruction = instance.decodeInstruction(mega8,
//                                                           opcode,
//                                                           0);
//      if (!expected.containsKey(opcode)) {
//        assertFalse(context + "class",
//                    instruction instanceof BitClearSet);
//      } else {
//        assertTrue(context + "class",
//                   instruction instanceof BitClearSet);
//        assertEquals(context + "mnemoic",
//                     expected.get(opcode),
//                     instruction.getMnemonic());
//      }
//    }
//  }
//
////  @Test
////  public void testAdiw()
////  {
////    int opcode = constructOpcodeRdlK6(Adiw.OPCODE,
////                                      24,
////                                      0x1f);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Adiw);
////    assertEquals("adiw r25:r24, 0x1f",
////                 result.toString());
////    assertRdlK6(instance,
////                Adiw.OPCODE,
////                Adiw.class,
////                "adiw");
////  }
////  @Test
////  public void testSbiw()
////  {
////    int opcode = constructOpcodeRdlK6(Sbiw.OPCODE,
////                                      24,
////                                      0x1f);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Sbiw);
////    assertEquals("sbiw r25:r24, 0x1f",
////                 result.toString());
////    assertRdlK6(instance,
////                Sbiw.OPCODE,
////                Sbiw.class,
////                "sbiw");
////  }
//  private void assertRdlK6(ProtocollDecoder decoder,
//                           int baseOpcode,
//                           Class<? extends Instruction_Rdl_K6> clazz,
//                           String mnemonic)
//  {
//    for (int rdl = 26; rdl < 32; rdl += 2) {
//      for (int k6 = 0; k6 < 64; ++k6) {
//        String context = mnemonic + " r" + (rdl + 1) + ":r" + rdl + ", " + k6 + " | ";
//        int opcode = constructOpcodeRdlK6(baseOpcode,
//                                          rdl,
//                                          k6);
//        Instruction instr = decoder.decodeInstruction(mega8,
//                                                      opcode,
//                                                      0);
//        assertTrue(context + "class",
//                   clazz.isInstance(instr));
//        Instruction_Rdl_K6 rdlk6 = clazz.cast(instr);
//        assertEquals(context + "rdl",
//                     rdl,
//                     rdlk6.getRdlAddress());
//        assertEquals(context + "k6",
//                     k6,
//                     rdlk6.getK6());
//        assertEquals(context + "mnemoic",
//                     mnemonic,
//                     rdlk6.getMnemonic());
//      }
//    }
//  }
//
//  private void assertRdb(ProtocollDecoder decoder,
//                         int baseOpcode,
//                         Class<? extends Instruction_Rd_b> clazz,
//                         String mnemonic)
//  {
//    for (int rd = 0; rd < 32; ++rd) {
//      for (int b = 0; b < 8; ++b) {
//        int opcode = constructOpcodeRdb(baseOpcode,
//                                        rd,
//                                        b);
//        String context = mnemonic + " r" + rd + "," + b;
//        Instruction instr = decoder.decodeInstruction(mega8,
//                                                      opcode,
//                                                      0);
//        assertTrue(context + " class",
//                   clazz.isInstance(instr));
//        Instruction_Rd_b rdb = clazz.cast(instr);
//        assertEquals(context + " rd",
//                     rd,
//                     rdb.getRdAddress());
//        assertEquals(context + " b",
//                     b,
//                     rdb.getBit());
//        assertEquals(context + " mnemonic",
//                     mnemonic,
//                     rdb.getMnemonic());
//      }
//    }
//  }
//
////  @Test
////  public void testBld()
////  {
////    int opcode = constructOpcodeRdb(Bld.OPCODE,
////                                    19,
////                                    5);//0xf935;
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Bld);
////    assertEquals("bld r19, 5",
////                 result.toString());
////    assertRdb(instance,
////              Bld.OPCODE,
////              Bld.class,
////              "bld");
////  }
////
////  @Test
////  public void testBranchInstruction()
////  {
////    Map<Integer, String> expected = new HashMap<>();
////    expected.put(constructOpcodeBranch(BranchInstruction.OPCODE_SET,
////                                       SREG.C,
////                                       -1),
////                 "brcs -1");
////    expected.put(constructOpcodeBranch(BranchInstruction.OPCODE_CLR,
////                                       SREG.Z,
////                                       0),
////                 "brzc 0");
////    expected.put(constructOpcodeBranch(BranchInstruction.OPCODE_SET,
////                                       SREG.N,
////                                       -37),
////                 "brns -37");
////    expected.put(constructOpcodeBranch(BranchInstruction.OPCODE_CLR,
////                                       SREG.V,
////                                       15),
////                 "brvc 15");
////    expected.put(constructOpcodeBranch(BranchInstruction.OPCODE_SET,
////                                       SREG.S,
////                                       -5),
////                 "brss -5");
////    expected.put(constructOpcodeBranch(BranchInstruction.OPCODE_CLR,
////                                       SREG.H,
////                                       63),
////                 "brhc 63");
////    expected.put(constructOpcodeBranch(BranchInstruction.OPCODE_SET,
////                                       SREG.T,
////                                       -64),
////                 "brts -64");
////    expected.put(constructOpcodeBranch(BranchInstruction.OPCODE_CLR,
////                                       SREG.I,
////                                       61),
////                 "brid 61");
////    expected.put(constructOpcodeBranch(BranchInstruction.OPCODE_SET,
////                                       SREG.I,
////                                       1),
////                 "brie 1");
////    ProtocollDecoder instance = new ProtocollDecoder();
////    for (Map.Entry<Integer, String> e : expected.entrySet()) {
////      Instruction result = instance.decodeInstruction(mega8,
////                                                      e.getKey(),
////                                                      0);
////      assertTrue(result instanceof BranchInstruction);
////      assertEquals(MessageFormat.format("opcode {0}",
////                                        Converter.printHexString(e.getKey(),
////                                                                 4)),
////                   e.getValue(),
////                   result.toString());
////    }
////    for (int b = 0; b < 8; ++b) {
////      for (int d = -64; d < 64; ++d) {
////        int opcode = constructOpcodeBranch(BranchInstruction.OPCODE_CLR,
////                                           b,
////                                           d);
////        String context = "clr b " + b + " d " + d + " | ";
////        Instruction i = instance.decodeInstruction(mega8,
////                                                   opcode,
////                                                   0);
////        assertTrue(context + "class",
////                   i instanceof BranchInstruction);
////        BranchInstruction bi = (BranchInstruction) i;
////        assertEquals(context + "b",
////                     b,
////                     bi.getBitIndex());
////        assertEquals(context + "d",
////                     d,
////                     bi.getOffset());
////        assertFalse(context + "clr",
////                    bi.isBitSet());
////      }
////    }
////    for (int b = 0; b < 8; ++b) {
////      for (int d = -64; d < 64; ++d) {
////        int opcode = constructOpcodeBranch(BranchInstruction.OPCODE_SET,
////                                           b,
////                                           d);
////        String context = "set b " + b + " d " + d + " | ";
////        Instruction i = instance.decodeInstruction(mega8,
////                                                   opcode,
////                                                   0);
////        assertTrue(context + "class",
////                   i instanceof BranchInstruction);
////        BranchInstruction bi = (BranchInstruction) i;
////        assertEquals(context + "b",
////                     b,
////                     bi.getBitIndex());
////        assertEquals(context + "d",
////                     d,
////                     bi.getOffset());
////        assertTrue(context + "set",
////                   bi.isBitSet());
////      }
////    }
////  }
////
////  @Test
////  public void testBst()
////  {
////    int opcode = constructOpcodeRdb(Bst.OPCODE,
////                                    19,
////                                    5);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Bst);
////    assertEquals("bst r19, 5",
////                 result.toString());
////    assertRdb(instance,
////              Bst.OPCODE,
////              Bst.class,
////              "bst");
////  }
////
////  @Test
////  public void testBreak()
////  {
////    int opcode = Break.OPCODE;
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Break);
////    assertEquals("break",
////                 result.toString());
////  }
////  @Test
////  public void testCall()
////  {
////    int opcode = constructOpcodeCallJmp(Call.OPCODE,
////                                        0x3cafeb);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    (opcode >> 16) & 0xffff,
////                                                    opcode & 0xffff);
////    assertTrue(result instanceof Call);
////    assertEquals("call 0x3cafeb",
////                 result.toString());
////    for (int offset = 0xffff; offset < 0x40ffff; offset += 0x10000) {
////      String context = "call 0x" + Integer.toHexString(offset) + " | ";
////      opcode = constructOpcodeCallJmp(Call.OPCODE,
////                                      offset);
////      result = instance.decodeInstruction(mega8,
////                                          (opcode >> 16) & 0xffff,
////                                          opcode & 0xffff);
////      assertTrue(context + "class",
////                 result instanceof Call);
////      Call call = (Call) result;
////      assertEquals(context + "target",
////                   offset,
////                   call.getCallTarget());
////      assertEquals(context + "mnemonic",
////                   "call",
////                   call.getMnemonic());
////    }
////  }
//  private void assertInstruction_P_b(ProtocollDecoder decoder,
//                                     int baseOpcode,
//                                     Class<? extends Instruction_P_b> clazz,
//                                     String mnemonic)
//  {
//    for (int io = 0; io < 32; ++io) {
//      for (int b = 0; b < 8; ++b) {
//        int opcode = constructOpcodePb(baseOpcode,
//                                       io,
//                                       b);
//        String context = mnemonic + " 0x" + Integer.toHexString(io) + ", " + b + " | ";
//        Instruction i = decoder.decodeInstruction(mega8,
//                                                  opcode,
//                                                  0);
//        assertTrue(context + "class",
//                   clazz.isInstance(i));
//        Instruction_P_b pb = clazz.cast(i);
//        assertEquals(context + "io",
//                     io,
//                     pb.getPortAddress());
//        assertEquals(context + "b",
//                     b,
//                     pb.getBitOffset());
//        assertEquals(context + "mnemonic",
//                     mnemonic,
//                     pb.getMnemonic());
//      }
//    }
//  }
//
////  @Test
////  public void testCBI()
////  {
////    int opcode = constructOpcodePb(SetClearIOBit.OPCODE_CBI,
////                                   0x18,
////                                   4);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof SetClearIOBit);
////    assertEquals("cbi 0x18, 4",
////                 result.toString());
////    assertInstruction_P_b(instance,
////                          SetClearIOBit.OPCODE_CBI,
////                          SetClearIOBit.class,
////                          "cbi");
////  }
////
////  @Test
////  public void testSBI()
////  {
////    int opcode = constructOpcodePb(SetClearIOBit.OPCODE_SBI,
////                                   0x1f,
////                                   7);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof SetClearIOBit);
////    assertEquals("sbi 0x1f, 7",
////                 result.toString());
////    assertInstruction_P_b(instance,
////                          SetClearIOBit.OPCODE_SBI,
////                          SetClearIOBit.class,
////                          "sbi");
////  }
//
//  @Test
//  public void testCPI()
//  {
//    int opcode = constructOpcodeRdK8(Cpi.OPCODE,
//                                     21,
//                                     0x8f);
//    ProtocollDecoder instance = new ProtocollDecoder();
//    Instruction result = instance.decodeInstruction(mega8,
//                                                    opcode,
//                                                    0);
//    assertTrue(result instanceof Cpi);
//    assertEquals("cpi r21, 0x8f",
//                 result.toString());
//    assertRdK8(instance,
//               Cpi.OPCODE,
//               Cpi.class,
//               "cpi");
//  }
//
//  @Test
//  public void testRet()
//  {
//    int opcode = 0x9508;
//    ProtocollDecoder instance = new ProtocollDecoder();
//    Instruction result = instance.decodeInstruction(mega8,
//                                                    opcode,
//                                                    0);
//    assertTrue(result instanceof Ret_i);
//    assertEquals("ret",
//                 result.toString());
//  }
//
//  @Test
//  public void testReti()
//  {
//    int opcode = 0x9518;
//    ProtocollDecoder instance = new ProtocollDecoder();
//    Instruction result = instance.decodeInstruction(mega8,
//                                                    opcode,
//                                                    0);
//    assertTrue(result instanceof Ret_i);
//    assertEquals("reti",
//                 result.toString());
//  }
//
//  @Test
//  public void testICall()
//  {
//    int opcode = 0x9509;
//    ProtocollDecoder instance = new ProtocollDecoder();
//    Instruction result = instance.decodeInstruction(mega8,
//                                                    opcode,
//                                                    0);
//    assertTrue(result instanceof ICall);
//    assertEquals("icall",
//                 result.toString());
//
//  }
//
//  @Test
//  public void testIJump()
//  {
//    int opcode = 0x9409;
//    ProtocollDecoder instance = new ProtocollDecoder();
//    Instruction result = instance.decodeInstruction(mega8,
//                                                    opcode,
//                                                    0);
//    assertTrue(result instanceof IJmp);
//    assertEquals("ijmp",
//                 result.toString());
//
//  }
//
////  @Test
////  public void testJmp()
////  {
////    int opcode = constructOpcodeCallJmp(Jmp.OPCODE,
////                                        0x3cafeb);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    (opcode >> 16) & 0xffff,
////                                                    opcode & 0xffff);
////    assertTrue(result instanceof Jmp);
////    assertEquals("jmp 0x3cafeb",
////                 result.toString());
////    for (int offset = 0xffff; offset < 0x40ffff; offset += 0x10000) {
////      String context = "jmp 0x" + Integer.toHexString(offset) + " | ";
////      opcode = constructOpcodeCallJmp(Jmp.OPCODE,
////                                      offset);
////      result = instance.decodeInstruction(mega8,
////                                          (opcode >> 16) & 0xffff,
////                                          opcode & 0xffff);
////      assertTrue(context + "class",
////                 result instanceof Jmp);
////      Jmp jmp = (Jmp) result;
////      assertEquals(context + "target",
////                   offset,
////                   jmp.getJumpTarget());
////      assertEquals(context + "mnemonic",
////                   "jmp",
////                   jmp.getMnemonic());
////    }
////  }
////  @Test
////  public void testAdd()
////  {
////    int opcode = constructOpcodeRdRr(Add.OPCODE,
////                                     28,
////                                     15);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Add);
////    assertEquals("add r28, r15",
////                 result.toString());
////    assertRdRr(instance,
////               Add.OPCODE,
////               Add.class,
////               "add");
////  }
////  @Test
////  public void testCpc()
////  {
////    int opcode = constructOpcodeRdRr(Cpc.OPCODE,
////                                     18,
////                                     19);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Cpc);
////    assertEquals("cpc r18, r19",
////                 result.toString());
////    assertRdRr(instance,
////               Cpc.OPCODE,
////               Cpc.class,
////               "cpc");
////  }
////  @Test
////  public void testSbc()
////  {
////    int opcode = constructOpcodeRdRr(Sbc.OPCODE,
////                                     31,
////                                     0);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Sbc);
////    assertEquals("sbc r31, r0",
////                 result.toString());
////    assertRdRr(instance,
////               Sbc.OPCODE,
////               Sbc.class,
////               "sbc");
////  }
////  @Test
////  public void testCpse()
////  {
////    int opcode = constructOpcodeRdRr(Cpse.OPCODE,
////                                     31,
////                                     0);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Cpse);
////    assertEquals("cpse r31, r0",
////                 result.toString());
////    assertRdRr(instance,
////               Cpse.OPCODE,
////               Cpse.class,
////               "cpse");
////
////  }
////  @Test
////  public void testAnd()
////  {
////    int opcode = constructOpcodeRdRr(And.OPCODE,
////                                     31,
////                                     0);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof And);
////    assertEquals("and r31, r0",
////                 result.toString());
////    assertRdRr(instance,
////               And.OPCODE,
////               And.class,
////               "and");
////  }
//  @Test
//  public void testEor()
//  {
//    int opcode = constructOpcodeRdRr(Eor.OPCODE,
//                                     31,
//                                     0);
//    ProtocollDecoder instance = new ProtocollDecoder();
//    Instruction result = instance.decodeInstruction(mega8,
//                                                    opcode,
//                                                    0);
//    assertTrue(result instanceof Eor);
//    assertEquals("eor r31, r0",
//                 result.toString());
//    assertRdRr(instance,
//               Eor.OPCODE,
//               Eor.class,
//               "eor");
//  }
//
////  @Test
////  public void testOr()
////  {
////    int opcode = constructOpcodeRdRr(Or.OPCODE,
////                                     31,
////                                     0);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Or);
////    assertEquals("or r31, r0",
////                 result.toString());
////    assertRdRr(instance,
////               Or.OPCODE,
////               Or.class,
////               "or");
////  }
////  @Test
////  public void testMov()
////  {
////    int opcode = constructOpcodeRdRr(Mov.OPCODE,
////                                     31,
////                                     0);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Mov);
////    assertEquals("mov r31, r0",
////                 result.toString());
////    assertRdRr(instance,
////               Mov.OPCODE,
////               Mov.class,
////               "mov");
////  }
////  @Test
////  public void testAdc()
////  {
////    int opcode = constructOpcodeRdRr(Adc.OPCODE,
////                                     31,
////                                     0);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Adc);
////    assertEquals("adc r31, r0",
////                 result.toString());
////    assertRdRr(instance,
////               Adc.OPCODE,
////               Adc.class,
////               "adc");
////
////  }
////  @Test
////  public void testCp()
////  {
////    int opcode = constructOpcodeRdRr(Cp.OPCODE,
////                                     31,
////                                     0);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Cp);
////    assertEquals("cp r31, r0",
////                 result.toString());
////    assertRdRr(instance,
////               Cp.OPCODE,
////               Cp.class,
////               "cp");
////  }
////  @Test
////  public void testSub()
////  {
////    int opcode = constructOpcodeRdRr(Sub.OPCODE,
////                                     31,
////                                     0);
////    ProtocollDecoder instance = new ProtocollDecoder();
////    Instruction result = instance.decodeInstruction(mega8,
////                                                    opcode,
////                                                    0);
////    assertTrue(result instanceof Sub);
////    assertEquals("sub r31, r0",
////                 result.toString());
////    assertRdRr(instance,
////               Sub.OPCODE,
////               Sub.class,
////               "sub");
////
////  }
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
