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
package at.reder.avrwb.avr8.impl;

import at.reder.atmelschema.util.HexIntAdapter;
import at.reder.avrwb.avr8.AVRCoreVersion;
import at.reder.avrwb.avr8.AVRDeviceKey;
import at.reder.avrwb.avr8.Architecture;
import at.reder.avrwb.avr8.Family;
import at.reder.avrwb.avr8.api.Instruction;
import at.reder.avrwb.avr8.api.instructions.AbstractInstructionTest;
import at.reder.avrwb.avr8.api.instructions.Adc;
import at.reder.avrwb.avr8.api.instructions.Add;
import at.reder.avrwb.avr8.api.instructions.Adiw;
import at.reder.avrwb.avr8.api.instructions.And;
import at.reder.avrwb.avr8.api.instructions.Andi;
import at.reder.avrwb.avr8.api.instructions.Asr;
import at.reder.avrwb.avr8.api.instructions.BitClearSet;
import at.reder.avrwb.avr8.api.instructions.Bld;
import at.reder.avrwb.avr8.api.instructions.BranchInstruction;
import at.reder.avrwb.avr8.api.instructions.Break;
import at.reder.avrwb.avr8.api.instructions.Bst;
import at.reder.avrwb.avr8.api.instructions.Call;
import at.reder.avrwb.avr8.api.instructions.Com;
import at.reder.avrwb.avr8.api.instructions.Cp;
import at.reder.avrwb.avr8.api.instructions.Cpc;
import at.reder.avrwb.avr8.api.instructions.Cpi;
import at.reder.avrwb.avr8.api.instructions.Cpse;
import at.reder.avrwb.avr8.api.instructions.Dec;
import at.reder.avrwb.avr8.api.instructions.Eor;
import at.reder.avrwb.avr8.api.instructions.ICall;
import at.reder.avrwb.avr8.api.instructions.IJump;
import at.reder.avrwb.avr8.api.instructions.InOut;
import at.reder.avrwb.avr8.api.instructions.Inc;
import at.reder.avrwb.avr8.api.instructions.Instruction_Rd_K8;
import at.reder.avrwb.avr8.api.instructions.Instruction_Rd_Rr;
import at.reder.avrwb.avr8.api.instructions.Instruction_Rd_b;
import at.reder.avrwb.avr8.api.instructions.Jmp;
import at.reder.avrwb.avr8.api.instructions.Ld;
import at.reder.avrwb.avr8.api.instructions.Ldi;
import at.reder.avrwb.avr8.api.instructions.Mov;
import at.reder.avrwb.avr8.api.instructions.Mul;
import at.reder.avrwb.avr8.api.instructions.Neg;
import at.reder.avrwb.avr8.api.instructions.Nop;
import at.reder.avrwb.avr8.api.instructions.Or;
import at.reder.avrwb.avr8.api.instructions.Pop;
import at.reder.avrwb.avr8.api.instructions.Push;
import at.reder.avrwb.avr8.api.instructions.Rcall;
import at.reder.avrwb.avr8.api.instructions.Ret_i;
import at.reder.avrwb.avr8.api.instructions.Ror;
import at.reder.avrwb.avr8.api.instructions.Sbc;
import at.reder.avrwb.avr8.api.instructions.Sbci_Subi;
import at.reder.avrwb.avr8.api.instructions.Sbiw;
import at.reder.avrwb.avr8.api.instructions.Sbr;
import at.reder.avrwb.avr8.api.instructions.SetClearIOBit;
import at.reder.avrwb.avr8.api.instructions.Sub;
import at.reder.avrwb.avr8.api.instructions.Swap;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class DefaultInstructionDecoderNGTest extends AbstractInstructionTest
{

  private final AVRDeviceKey mega8 = new AVRDeviceKey(Family.megaAVR,
                                                      Architecture.AVR8,
                                                      AVRCoreVersion.V2E,
                                                      "ATmega8");

  public DefaultInstructionDecoderNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @Test
  public void testDecodeInstructionNop()
  {
    int opcode = 0x0;
    int nextOpcode = 0x0;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    nextOpcode);
    assertTrue(result instanceof Nop);
  }

  @Test(enabled = false)
  public void testDecodeInstructionMul()
  {
    int opcode = 0x9c00;
    int nextOpcode = 0x0;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    nextOpcode);
    assertTrue(result instanceof Mul);
  }

  @Test(enabled = false)
  public void setDecodeBitClearSet()
  {
    Map<Integer, String> expected = new HashMap();
    expected.put(0x9408,
                 "sec");
    expected.put(0x9418,
                 "sez");
    expected.put(0x9428,
                 "sen");
    expected.put(0x9438,
                 "sev");
    expected.put(0x9448,
                 "ses");
    expected.put(0x9458,
                 "seh");
    expected.put(0x9468,
                 "set");
    expected.put(0x9478,
                 "sei");
    expected.put(0x9488,
                 "clc");
    expected.put(0x9498,
                 "clz");
    expected.put(0x94a8,
                 "cln");
    expected.put(0x94b8,
                 "clv");
    expected.put(0x94c8,
                 "cls");
    expected.put(0x94d8,
                 "clh");
    expected.put(0x94e8,
                 "clt");
    expected.put(0x94f8,
                 "cli");
    assertEquals(16,
                 expected.size());
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    for (int opcode = 0x9407; opcode < 0x94fa; ++opcode) {
      Instruction instruction = instance.decodeInstruction(mega8,
                                                           opcode,
                                                           0);
      if (!expected.containsKey(opcode)) {
        assertFalse(instruction instanceof BitClearSet);
      } else {
        assertTrue(instruction instanceof BitClearSet);
        assertEquals(expected.get(opcode),
                     instruction.getMnemonic());
      }
    }
  }

  @Test(enabled = false)
  public void testAsr()
  {
    int opcode = 0x9505;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Asr);
    assertEquals("asr r16",
                 result.toString());
  }

  @Test(enabled = false)
  public void testCom()
  {
    int opcode = 0x95f0;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Com);
    assertEquals("com r31",
                 result.toString());

  }

  @Test(enabled = false)
  public void testDec()
  {
    int opcode = 0x947a;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Dec);
    assertEquals("dec r7",
                 result.toString());
  }

  @Test(enabled = false)
  public void testInc()
  {
    int opcode = 0x9583;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Inc);
    assertEquals("inc r24",
                 result.toString());
  }

  @Test(enabled = false)
  public void testNeg()
  {
    int opcode = 0x9431;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Neg);
    assertEquals("neg r3",
                 result.toString());
  }

  @Test(enabled = false)
  public void testPop()
  {
    int opcode = 0x91ef;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Pop);
    assertEquals("pop r30",
                 result.toString());

  }

  @Test(enabled = false)
  public void testPush()
  {
    int opcode = 0x93ef;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Push);
    assertEquals("push r30",
                 result.toString());

  }

  @Test(enabled = false)
  public void testRor()
  {
    int opcode = 0x9467;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Ror);
    assertEquals("ror r6",
                 result.toString());
  }

  @Test
  public void testLdi()
  {
    int opcode = constructOpcodeRdK8(Ldi.OPCODE,
                                     16,
                                     0xff);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Ldi);
    assertEquals("ldi r16, 0xff",
                 result.toString());
    assertRdK8(instance,
               Ldi.OPCODE,
               Ldi.class,
               "ldi");

  }

  @Test(enabled = false)
  public void testSwap()
  {
    int opcode = 0x9432;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Swap);
    assertEquals("swap r3",
                 result.toString());
  }

  @Test(enabled = false)
  public void testAdiw()
  {
    int opcode = 0x964f;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Adiw);
    assertEquals("adiw r25:r24, 0x1f",
                 result.toString());
  }

  @Test(enabled = false)
  public void testSbiw()
  {
    int opcode = 0x976f;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Sbiw);
    assertEquals("sbiw r29:r28, 0x1f",
                 result.toString());
  }

  @Test
  public void testAndi()
  {
    int opcode = constructOpcodeRdK8(Andi.OPCODE,
                                     19,
                                     0xc5);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Andi);
    assertEquals("andi r19, 0xc5",
                 result.toString());
    assertRdK8(instance,
               Andi.OPCODE,
               Andi.class,
               "andi");
  }

  @Test
  public void testSbci()
  {
    int opcode = constructOpcodeRdK8(Sbci_Subi.OPCODE_SBCI,
                                     19,
                                     0xc5);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Sbci_Subi);
    assertEquals("sbci r19, 0xc5",
                 result.toString());
    assertRdK8(instance,
               Sbci_Subi.OPCODE_SBCI,
               Sbci_Subi.class,
               "sbci");
  }

  @Test
  public void testSubi()
  {
    int opcode = constructOpcodeRdK8(Sbci_Subi.OPCODE_SUBI,
                                     19,
                                     0xc5);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Sbci_Subi);
    assertEquals("subi r19, 0xc5",
                 result.toString());
    assertRdK8(instance,
               Sbci_Subi.OPCODE_SUBI,
               Sbci_Subi.class,
               "subi");
  }

  @Test
  public void testSbr()
  {
    int opcode = constructOpcodeRdK8(Sbr.OPCODE,
                                     19,
                                     0xc5);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Sbr);
    assertEquals("sbr r19, 0xc5",
                 result.toString());
    assertRdK8(instance,
               Sbr.OPCODE,
               Sbr.class,
               "sbr");
  }

  protected void assertRdb(DefaultInstructionDecoder decoder,
                           int baseOpcode,
                           Class<? extends Instruction_Rd_b> clazz,
                           String mnemonic)
  {
    for (int rd = 0; rd < 32; ++rd) {
      for (int b = 0; b < 8; ++b) {
        int opcode = constructOpcodeRdb(baseOpcode,
                                        rd,
                                        b);
        String context = mnemonic + " r" + rd + "," + b;
        Instruction instr = decoder.decodeInstruction(mega8,
                                                      opcode,
                                                      0);
        assertTrue(context + " class",
                   clazz.isInstance(instr));
        Instruction_Rd_b rdb = clazz.cast(instr);
        assertEquals(context + " rd",
                     rd,
                     rdb.getRdAddress());
        assertEquals(context + " b",
                     b,
                     rdb.getBit());
        assertEquals(context + " mnemonic",
                     mnemonic,
                     rdb.getMnemonic());
      }
    }
  }

  @Test
  public void testBld()
  {
    int opcode = constructOpcodeRdb(Bld.OPCODE,
                                    19,
                                    5);//0xf935;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Bld);
    assertEquals("bld r19, 5",
                 result.toString());
    assertRdb(instance,
              Bld.OPCODE,
              Bld.class,
              "bld");
  }

  @Test(enabled = false)
  public void testBranchInstruction()
  {
    Map<Integer, String> expected = new HashMap<>();
    expected.put(0xf3f8,
                 "brcs -1");
    expected.put(0xf401,
                 "brzc 0");
    expected.put(0xf2da,
                 "brns -37");
    expected.put(0xf47b,
                 "brvc 15");
    expected.put(0xf3dc,
                 "brss -5");
    expected.put(0xf515,
                 "brhc 34");
    expected.put(0xf206,
                 "brts -64");
    expected.put(0xf5ef,
                 "brid 61");
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    for (Map.Entry<Integer, String> e : expected.entrySet()) {
      Instruction result = instance.decodeInstruction(mega8,
                                                      e.getKey(),
                                                      0);
      assertTrue(result instanceof BranchInstruction);
      assertEquals(MessageFormat.format("opcode {0}",
                                        HexIntAdapter.toHexString(e.getKey(),
                                                                  4)),
                   e.getValue(),
                   result.toString());
    }
  }

  @Test
  public void testBst()
  {
    int opcode = constructOpcodeRdb(Bst.OPCODE,
                                    19,
                                    5);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Bst);
    assertEquals("bst r19, 5",
                 result.toString());
    assertRdb(instance,
              Bst.OPCODE,
              Bst.class,
              "bst");
  }

  @Test(enabled = false)
  public void testBreak()
  {
    int opcode = Break.OPCODE;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Break);
    assertEquals("break",
                 result.toString());
  }

  @Test(enabled = false)
  public void testCall()
  {
    int opcode = 0x95eeafeb;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    (opcode >> 16) & 0xffff,
                                                    opcode & 0xffff);
    assertTrue(result instanceof Call);
    assertEquals("call 0x3cafeb",
                 result.toString());

  }

  @Test(enabled = false)
  public void testCBI()
  {
    int opcode = 0x98c4;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof SetClearIOBit);
    assertEquals("cbi 0x18, 4",
                 result.toString());
  }

  @Test(enabled = false)
  public void testSBI()
  {
    int opcode = 0x9aff;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof SetClearIOBit);
    assertEquals("sbi 0x1f, 7",
                 result.toString());
  }

  private void assertRdK8(DefaultInstructionDecoder decoder,
                          int baseOpcode,
                          Class<? extends Instruction_Rd_K8> clazz,
                          String mnemonic)
  {
    for (int rd = 16; rd < 32; ++rd) {
      for (int k8 = 0; k8 < 0xff; ++k8) {
        final String context = MessageFormat.format("cpi r{0,number,0},{1,number,0}",
                                                    rd,
                                                    k8);
        int opcode = constructOpcodeRdK8(baseOpcode,
                                         rd,
                                         k8);
        Instruction instruction = decoder.decodeInstruction(mega8,
                                                            opcode,
                                                            -1);
        assertTrue(context + " clazz",
                   clazz.isInstance(instruction));
        Instruction_Rd_K8 rdK8 = clazz.cast(instruction);
        assertEquals(context + " mnemonic",
                     mnemonic,
                     rdK8.getMnemonic());
        assertEquals(context + " rd",
                     rd,
                     rdK8.getRdAddress());
        assertEquals(context + " k8",
                     k8,
                     rdK8.getK8());
      }
    }
  }

  @Test
  public void testCPI()
  {
    int opcode = constructOpcodeRdK8(Cpi.OPCODE,
                                     21,
                                     0x8f);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Cpi);
    assertEquals("cpi r21, 0x8f",
                 result.toString());
    assertRdK8(instance,
               Cpi.OPCODE,
               Cpi.class,
               "cpi");
  }

  @Test(enabled = false)
  public void testIn()
  {
    int opcode = 0xb777;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof InOut);
    assertEquals("in r23, 0x37",
                 result.toString());
  }

  @Test(enabled = false)
  public void testOut()
  {
    int opcode = 0xbf77;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof InOut);
    assertEquals("out 0x37, r23",
                 result.toString());
  }

  @Test
  public void testRcall()
  {
    int opcode = 0xd800;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Rcall);
    assertEquals("rcall -2048",
                 result.toString());
    opcode = 0xdfff;
    result = instance.decodeInstruction(mega8,
                                        opcode,
                                        0);
    assertTrue(result instanceof Rcall);
    assertEquals("rcall -1",
                 result.toString());
    opcode = 0xd7ff;
    result = instance.decodeInstruction(mega8,
                                        opcode,
                                        0);
    assertTrue(result instanceof Rcall);
    assertEquals("rcall 2047",
                 result.toString());
    opcode = 0xd000;
    result = instance.decodeInstruction(mega8,
                                        opcode,
                                        0);
    assertTrue(result instanceof Rcall);
    assertEquals("rcall 0",
                 result.toString());
  }

  @Test(enabled = false)
  public void testRet()
  {
    int opcode = 0x9508;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Ret_i);
    assertEquals("ret",
                 result.toString());
  }

  @Test(enabled = false)
  public void testReti()
  {
    int opcode = 0x9518;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Ret_i);
    assertEquals("reti",
                 result.toString());
  }

  @Test(enabled = false)
  public void testICall()
  {
    int opcode = 0x9509;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof ICall);
    assertEquals("icall",
                 result.toString());

  }

  @Test(enabled = false)
  public void testIJump()
  {
    int opcode = 0x9409;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof IJump);
    assertEquals("ijump",
                 result.toString());

  }

  @Test(enabled = false)
  public void testJmp()
  {
    int opcode = 0x95ecafeb;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    (opcode >> 16) & 0xffff,
                                                    opcode & 0xffff);
    assertTrue(result instanceof Jmp);
    assertEquals("jmp 0x3cafeb",
                 result.toString());

  }

  @Test(enabled = false)
  public void testLd_X()
  {
    int opcode = 0x911c;
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Ld);
    assertEquals("ld r17, X",
                 result.toString());

  }

  @Test
  public void testLd_Y()
  {
    fail("noch nicht implementiert");
  }

  private void assertRdRr(DefaultInstructionDecoder instance,
                          int baseOpcode,
                          Class<? extends Instruction_Rd_Rr> clazz,
                          String menmonic)
  {
    for (int rd = 0; rd < 32; ++rd) {
      for (int rr = 0; rr < 32; ++rr) {
        int opcode = constructOpcodeRdRr(baseOpcode,
                                         rd,
                                         rr);
        Instruction instruction = instance.decodeInstruction(mega8,
                                                             opcode,
                                                             0);
        assertTrue(clazz.getSimpleName(),
                   clazz.isInstance(instruction));
        Instruction_Rd_Rr rdrr = clazz.cast(instruction);
        assertEquals(clazz.getSimpleName(),
                     rd,
                     rdrr.getRdAddress());
        assertEquals(clazz.getSimpleName(),
                     rr,
                     rdrr.getRrAddress());
        assertEquals(clazz.getSimpleName(),
                     menmonic,
                     rdrr.getMnemonic());
      }
    }
  }

  @Test
  public void testAdd()
  {
    int opcode = constructOpcodeRdRr(Add.OPCODE,
                                     28,
                                     15);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Add);
    assertEquals("add r28, r15",
                 result.toString());
    assertRdRr(instance,
               Add.OPCODE,
               Add.class,
               "add");
  }

  @Test
  public void testCpc()
  {
    int opcode = constructOpcodeRdRr(Cpc.OPCODE,
                                     18,
                                     19);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Cpc);
    assertEquals("cpc r18, r19",
                 result.toString());
    assertRdRr(instance,
               Cpc.OPCODE,
               Cpc.class,
               "cpc");
  }

  @Test
  public void testSbc()
  {
    int opcode = constructOpcodeRdRr(Sbc.OPCODE,
                                     31,
                                     0);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Sbc);
    assertEquals("sbc r31, r0",
                 result.toString());
    assertRdRr(instance,
               Sbc.OPCODE,
               Sbc.class,
               "sbc");
  }

  @Test
  public void testCpse()
  {
    int opcode = constructOpcodeRdRr(Cpse.OPCODE,
                                     31,
                                     0);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Cpse);
    assertEquals("cpse r31, r0",
                 result.toString());
    assertRdRr(instance,
               Cpse.OPCODE,
               Cpse.class,
               "cpse");

  }

  @Test
  public void testAnd()
  {
    int opcode = constructOpcodeRdRr(And.OPCODE,
                                     31,
                                     0);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof And);
    assertEquals("and r31, r0",
                 result.toString());
    assertRdRr(instance,
               And.OPCODE,
               And.class,
               "and");
  }

  @Test
  public void testEor()
  {
    int opcode = constructOpcodeRdRr(Eor.OPCODE,
                                     31,
                                     0);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Eor);
    assertEquals("eor r31, r0",
                 result.toString());
    assertRdRr(instance,
               Eor.OPCODE,
               Eor.class,
               "eor");
  }

  @Test
  public void testOr()
  {
    int opcode = constructOpcodeRdRr(Or.OPCODE,
                                     31,
                                     0);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Or);
    assertEquals("or r31, r0",
                 result.toString());
    assertRdRr(instance,
               Or.OPCODE,
               Or.class,
               "or");
  }

  @Test
  public void testMov()
  {
    int opcode = constructOpcodeRdRr(Mov.OPCODE,
                                     31,
                                     0);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Mov);
    assertEquals("mov r31, r0",
                 result.toString());
    assertRdRr(instance,
               Mov.OPCODE,
               Mov.class,
               "mov");
  }

  @Test
  public void testAdc()
  {
    int opcode = constructOpcodeRdRr(Adc.OPCODE,
                                     31,
                                     0);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Adc);
    assertEquals("adc r31, r0",
                 result.toString());
    assertRdRr(instance,
               Adc.OPCODE,
               Adc.class,
               "adc");

  }

  @Test
  public void testCp()
  {
    int opcode = constructOpcodeRdRr(Cp.OPCODE,
                                     31,
                                     0);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Cp);
    assertEquals("cp r31, r0",
                 result.toString());
    assertRdRr(instance,
               Cp.OPCODE,
               Cp.class,
               "cp");
  }

  @Test
  public void testSub()
  {
    int opcode = constructOpcodeRdRr(Sub.OPCODE,
                                     31,
                                     0);
    DefaultInstructionDecoder instance = new DefaultInstructionDecoder();
    Instruction result = instance.decodeInstruction(mega8,
                                                    opcode,
                                                    0);
    assertTrue(result instanceof Sub);
    assertEquals("sub r31, r0",
                 result.toString());
    assertRdRr(instance,
               Sub.OPCODE,
               Sub.class,
               "sub");

  }

}
