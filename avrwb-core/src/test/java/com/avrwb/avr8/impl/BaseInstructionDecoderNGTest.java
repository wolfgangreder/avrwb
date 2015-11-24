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

import com.avrwb.avr8.Device;
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.InstructionDecoder;
import com.avrwb.avr8.api.instructions.Bclr_Bset;
import com.avrwb.avr8.api.instructions.Brbs_Brbc;
import com.avrwb.avr8.api.instructions.Elpm;
import com.avrwb.avr8.api.instructions.InOut;
import com.avrwb.avr8.api.instructions.Instruction_K22;
import com.avrwb.avr8.api.instructions.Instruction_K4;
import com.avrwb.avr8.api.instructions.Instruction_LdSt;
import com.avrwb.avr8.api.instructions.Instruction_P_b;
import com.avrwb.avr8.api.instructions.Instruction_Rd;
import com.avrwb.avr8.api.instructions.Instruction_Rd_K16;
import com.avrwb.avr8.api.instructions.Instruction_Rd_K8;
import com.avrwb.avr8.api.instructions.Instruction_Rd_Rr;
import com.avrwb.avr8.api.instructions.Instruction_Rd_b;
import com.avrwb.avr8.api.instructions.Instruction_Rdh23_Rrh23;
import com.avrwb.avr8.api.instructions.Instruction_Rdh_K7;
import com.avrwb.avr8.api.instructions.Instruction_Rdh_Rrh;
import com.avrwb.avr8.api.instructions.Instruction_Rdl_K6;
import com.avrwb.avr8.api.instructions.Instruction_k12;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.InstructionNotAvailableException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 *
 * @author wolfi
 */
public class BaseInstructionDecoderNGTest
{

  public BaseInstructionDecoderNGTest()
  {
  }

  private static final Set<Integer> TESTED_OPCODES = new HashSet<>();
  protected static InstructionDecoder decoder;
  protected static AvrDeviceKey deviceKey;

  protected static final class ProtocollDecoder implements InstructionDecoder
  {

    private final InstructionDecoder decoder;

    public ProtocollDecoder(InstructionDecoder decoder)
    {
      this.decoder = decoder;
    }

    @Override
    public Instruction getInstruction(Device device,
                                      int address) throws NullPointerException, IllegalArgumentException,
                                                          InstructionNotAvailableException
    {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Instruction decodeInstruction(AvrDeviceKey deviceKey,
                                         int opcode,
                                         int nextOpcode)
    {
      Instruction result = this.decoder.decodeInstruction(deviceKey,
                                                          opcode,
                                                          nextOpcode);
      if (result != null) {
        TESTED_OPCODES.add(opcode);
      }
      return result;
    }

  }

  protected static void afterClass()
  {
    double coverage = TESTED_OPCODES.size();
    coverage /= 65535d;
    System.err.println(MessageFormat.format("Tested {0,number,0,000} ({1,number,0.0}%) opcodes.",
                                            TESTED_OPCODES.size(),
                                            coverage * 100));
  }

  protected void test_Rd_Rr(int baseOpcode,
                            Class<? extends Instruction_Rd_Rr> clazz,
                            String menmonic,
                            boolean expectToFind)
  {
    String context = clazz.getName() + "@" + deviceKey.toString() + " ";
    for (int rd = 0; rd < 32; ++rd) {
      for (int rr = 0; rr < 32; ++rr) {
        int opcode = Instruction_Rd_Rr.composeOpcode(baseOpcode,
                                                     rd,
                                                     rr);
        Instruction instruction = decoder.decodeInstruction(deviceKey,
                                                            opcode,
                                                            0);
        if (expectToFind) {
          assertTrue(clazz.isInstance(instruction),
                     context);
          Instruction_Rd_Rr rdrr = clazz.cast(instruction);
          assertEquals(rd,
                       rdrr.getRdAddress(),
                       context);
          assertEquals(rr,
                       rdrr.getRrAddress(),
                       context);
          assertEquals(menmonic,
                       rdrr.getMnemonic(),
                       context);
        } else {
          assertFalse(clazz.isInstance(instruction),
                      context);
        }
      }
    }
  }

  protected void test_Rd(int baseOpcode,
                         Class<? extends Instruction_Rd> clazz,
                         String mnemonic,
                         boolean expectToFind)
  {
    for (int rd = 0; rd < 32; ++rd) {
      String context = mnemonic + " r" + rd + " | ";
      int opcode = Instruction_Rd.composeOpcode(baseOpcode,
                                                rd);
      Instruction inst = decoder.decodeInstruction(deviceKey,
                                                   opcode,
                                                   0);
      if (expectToFind) {
        assertTrue(clazz.isInstance(inst),
                   context + "class");
        Instruction_Rd ird = clazz.cast(inst);
        assertEquals(rd,
                     ird.getRdAddress(),
                     context + "rd");
        assertEquals(mnemonic,
                     ird.getMnemonic(),
                     context + "mnemonic");
      } else {
        assertFalse(clazz.isInstance(inst),
                    context + "class");
      }
    }
  }

  protected void test_Rd_K8(int baseOpcode,
                            Class<? extends Instruction_Rd_K8> clazz,
                            String mnemonic,
                            boolean expectToFind)
  {
    for (int rd = 16; rd < 32; ++rd) {
      for (int k8 = 0; k8 < 0xff; ++k8) {
        final String context = MessageFormat.format("{2} r{0,number,0},{1,number,0} | ",
                                                    rd,
                                                    k8,
                                                    mnemonic);
        int opcode = Instruction_Rd_K8.composeOpcode(baseOpcode,
                                                     rd,
                                                     k8);
        Instruction instruction = decoder.decodeInstruction(deviceKey,
                                                            opcode,
                                                            -1);
        if (expectToFind) {
          assertNotNull(instruction,
                        context + "not null");
          Class<?> realClass = instruction.getClass();
          assertTrue(clazz.isInstance(instruction),
                     context + " clazz is " + realClass.getName());
          Instruction_Rd_K8 rdK8 = clazz.cast(instruction);
          assertEquals(mnemonic,
                       rdK8.getMnemonic(),
                       context + " mnemonic");
          assertEquals(rd,
                       rdK8.getRdAddress(),
                       context + " rd");
          assertEquals(k8,
                       rdK8.getK8(),
                       context + " k8");
        } else {
          assertFalse(clazz.isInstance(instruction),
                      context + " clazz");
        }
      }
    }
  }

  protected void testBranch(int baseOpcode,
                            Class<? extends Brbs_Brbc> clazz,
                            String mnemonic,
                            boolean bitSet)
  {
    for (int b = 0; b < 7; ++b) {
      for (int dist = -64; dist < 64; ++dist) {
        int opcode = Brbs_Brbc.composeOpcode(baseOpcode,
                                             b,
                                             dist);
        String context = mnemonic + " b=" + b + ", dist=" + dist + " | ";
        Instruction instruction = decoder.decodeInstruction(deviceKey,
                                                            opcode,
                                                            -1);
        assertNotNull(instruction,
                      context + "nulltest");
        assertEquals(clazz,
                     instruction.getClass(),
                     context + "classtest");
        Brbs_Brbc bi = clazz.cast(instruction);
        assertEquals(b,
                     bi.getBitIndex(),
                     context + "bit");
        assertEquals(dist,
                     bi.getOffset(),
                     context + "offset");
        assertEquals(bitSet,
                     bi.isBitSet(),
                     context + "set");
      }
    }
  }

  protected void testInstruction_k12(int baseOpcode,
                                     Class<? extends Instruction_k12> clazz,
                                     String mnemonic,
                                     boolean expectToFind)
  {
    for (int k = -2048; k < 2048; ++k) {
      int opcode = Instruction_k12.composeOpcode(baseOpcode,
                                                 k);
      String context = mnemonic + " k=" + k + " | ";
      Instruction i = decoder.decodeInstruction(deviceKey,
                                                opcode,
                                                -1);
      if (expectToFind) {
        assertNotNull(i,
                      context + "nulltest");
        assertTrue(clazz.isInstance(i),
                   context + "classtest");
        assertEquals(k,
                     ((Instruction_k12) i).getK12(),
                     context + "k12");
        assertEquals(mnemonic,
                     i.getMnemonic(),
                     context + "mnemonic");
      } else {
        assertFalse(clazz.isInstance(i),
                    context + "class");
      }
    }
  }

  protected void testInOut(int baseOpcode,
                           String mnemonic)
  {
    for (int rd = 0; rd < 32; ++rd) {
      for (int p = 0; p < 64; ++p) {
        int opcode = InOut.composeOpcode(baseOpcode,
                                         rd,
                                         p);
        String context = mnemonic + " r" + rd + ", 0x" + Integer.toHexString(p) + " | ";
        Instruction i = decoder.decodeInstruction(deviceKey,
                                                  opcode,
                                                  0);
        assertNotNull(i,
                      context + "nulltest");
        assertTrue(i instanceof InOut,
                   context + "classtest");
        InOut io = (InOut) i;
        assertEquals(io.getRegisterAddress(),
                     rd,
                     context + "rd");
        assertEquals(io.getPortAddress(),
                     p,
                     context + "p");
        assertEquals(io.getMnemonic(),
                     mnemonic,
                     context + "mnemonic");
      }
    }
  }

  protected void testRdlK6(int baseOpcode,
                           Class<? extends Instruction_Rdl_K6> clazz,
                           String mnemonic,
                           boolean expectToFind)
  {
    for (int rdl = 24; rdl < 32; rdl += 2) {
      for (int k6 = 0; k6 < 64; ++k6) {
        int opcode = Instruction_Rdl_K6.composeOpcode(baseOpcode,
                                                      rdl,
                                                      k6);
        String context = mnemonic + " r" + rdl + ":r" + (rdl + 1) + ", " + k6 + " | ";
        Instruction i = decoder.decodeInstruction(deviceKey,
                                                  opcode,
                                                  0);
        if (expectToFind) {
          assertNotNull(i,
                        context + "nulltest");
          assertTrue(clazz.isInstance(i),
                     context + "classtest");
          Instruction_Rdl_K6 ik6 = clazz.cast(i);
          assertEquals(ik6.getRdlAddress(),
                       rdl,
                       context + "rdl");
          assertEquals(ik6.getK6(),
                       k6,
                       context + "K6");
          assertEquals(ik6.getMnemonic(),
                       mnemonic,
                       context + "mnemonic");
        } else {
          assertFalse(clazz.isInstance(i),
                      context + "classtest");
        }
      }
    }
  }

  protected void testBitClearSet()
  {
    for (int b = 0; b < 7; ++b) {
      int opcode = Bclr_Bset.composeOpcode(Bclr_Bset.OPCODE_BCLR,
                                           false,
                                           b);
      String context = "BCLR" + " " + b + " | ";
      Instruction i = decoder.decodeInstruction(deviceKey,
                                                opcode,
                                                0);
      assertNotNull(i,
                    context + "nulltest");
      assertTrue(i instanceof Bclr_Bset,
                 context);
      Bclr_Bset bcs = (Bclr_Bset) i;
      assertEquals(bcs.getBit(),
                   b,
                   context + "bit");
      assertFalse(bcs.isBitSet(),
                  context + "bitSet");
    }
    for (int b = 0; b < 7; ++b) {
      int opcode = Bclr_Bset.composeOpcode(Bclr_Bset.OPCODE_BSET,
                                           true,
                                           b);
      String context = "BCLR" + " " + b + " | ";
      Instruction i = decoder.decodeInstruction(deviceKey,
                                                opcode,
                                                0);
      assertNotNull(i,
                    context + "nulltest");
      assertTrue(i instanceof Bclr_Bset,
                 context);
      Bclr_Bset bcs = (Bclr_Bset) i;
      assertEquals(bcs.getBit(),
                   b,
                   context + "bit");
      assertTrue(bcs.isBitSet(),
                 context + "bitSet");
    }
  }

  protected void testRdb(int baseOpcode,
                         Class<? extends Instruction_Rd_b> clazz,
                         String mnemonic,
                         boolean expectToFind)
  {
    for (int rd = 0; rd < 32; ++rd) {
      for (int b = 0; b < 8; ++b) {
        int opcode = Instruction_Rd_b.composeOpcode(baseOpcode,
                                                    rd,
                                                    b);
        String context = mnemonic + " r" + rd + ", " + b + " | ";
        Instruction i = decoder.decodeInstruction(deviceKey,
                                                  opcode,
                                                  0);
        if (expectToFind) {
          assertNotNull(i,
                        context + "nulltest");
          assertTrue(clazz.isInstance(i),
                     context + "class");
          Instruction_Rd_b rdb = clazz.cast(i);
          assertEquals(rdb.getRdAddress(),
                       rd,
                       context + "rd");
          assertEquals(rdb.getBit(),
                       b,
                       context + "bit");
          assertEquals(rdb.getMnemonic(),
                       mnemonic,
                       context + "mnemonic");
        } else {
          assertFalse(clazz.isInstance(i),
                      context + "class");
        }
      }
    }
  }

  protected void testSimple(int opcode,
                            Class<? extends Instruction> clazz,
                            String mnemonic,
                            boolean expectToFind)
  {
    Instruction i = decoder.decodeInstruction(deviceKey,
                                              opcode,
                                              0);
    if (expectToFind) {
      assertNotNull(i,
                    mnemonic + "|nulltest");
      assertTrue(clazz.isInstance(i),
                 mnemonic + "|class");
      assertEquals(i.getMnemonic(),
                   mnemonic);
    } else {
      assertFalse(clazz.isInstance(i),
                  mnemonic + "|class");
    }
  }

  protected void testK22(int baseOpcode,
                         Class<? extends Instruction_K22> clazz,
                         String mnemonic,
                         boolean expectToFind)
  {
    for (int k = 0; k < 0x40; ++k) {
      int k22 = k << 16;
      int opcode = Instruction_K22.composeOpcode(baseOpcode,
                                                 k22);
      Instruction i = decoder.decodeInstruction(deviceKey,
                                                (opcode >> 16) & 0xffff,
                                                opcode & 0xffff);
      String context = mnemonic + " 0x" + Integer.toHexString(k22) + " | ";
      if (expectToFind) {
        assertNotNull(i,
                      context + "nulltest");
        assertTrue(clazz.isInstance(i),
                   context + "classtest");
        assertEquals(clazz.cast(i).getK22(),
                     k22,
                     context + "k22");
        assertEquals(i.getMnemonic(),
                     mnemonic,
                     context + "mnemonic");
      } else {
        assertFalse(clazz.isInstance(i),
                    context + "classtest");
      }
    }
    for (int k = 0; k < 0x40; ++k) {
      int k22 = (k << 16) | 0xffff;
      int opcode = Instruction_K22.composeOpcode(baseOpcode,
                                                 k22);
      Instruction i = decoder.decodeInstruction(deviceKey,
                                                (opcode >> 16) & 0xffff,
                                                opcode & 0xffff);
      String context = mnemonic + " 0x" + Integer.toHexString(k22) + " | ";
      if (expectToFind) {
        assertNotNull(i,
                      context + "nulltest");
        assertTrue(clazz.isInstance(i),
                   context + "classtest");
        assertEquals(clazz.cast(i).getK22(),
                     k22,
                     context + "k22");
        assertEquals(i.getMnemonic(),
                     mnemonic,
                     context + "mnemonic");
      } else {
        assertFalse(clazz.isInstance(i),
                    context + "classtest");

      }
    }
  }

  protected void test_Elpm(int baseOpcode,
                           Class<? extends Elpm> clazz,
                           String mnemonic,
                           boolean expectToFind)
  {
    for (int rd = 0; rd < 32; ++rd) {
      String context = mnemonic + " r" + rd + " | ";
      int opcode = Instruction_Rd.composeOpcode(baseOpcode,
                                                rd);
      Instruction inst = decoder.decodeInstruction(deviceKey,
                                                   opcode,
                                                   0);
      if (expectToFind) {
        assertTrue(clazz.isInstance(inst),
                   context + "class");
        Elpm ird = clazz.cast(inst);
        assertEquals(rd,
                     ird.getRdAddress(),
                     context + "rd");
        assertEquals(mnemonic,
                     ird.getMnemonic(),
                     context + "mnemonic");
      } else {
        assertFalse(clazz.isInstance(inst),
                    context + "class");
      }
    }
  }

  protected void testrdK16(int baseOpcode,
                           Class<? extends Instruction_Rd_K16> clazz,
                           String mnemonic,
                           boolean expectToFind)
  {
    for (int rd = 0; rd < 32; ++rd) {
      int opcode = Instruction_Rd_K16.composeOpcode(baseOpcode,
                                                    rd,
                                                    0);
      Instruction i = decoder.decodeInstruction(deviceKey,
                                                (opcode >> 16) & 0xffff,
                                                opcode & 0xffff);
      String context = mnemonic + " r" + rd + ", 0x" + Integer.toHexString(rd) + " | ";
      if (expectToFind) {
        assertNotNull(i,
                      context + "nulltest");
        assertTrue(clazz.isInstance(i),
                   context + "classtest");
        Instruction_Rd_K16 ik16 = clazz.cast(i);
        assertEquals(ik16.getRdAddress(),
                     rd,
                     context + "rd");
        assertEquals(ik16.getK16(),
                     0,
                     context + "k16");
      } else {
        assertFalse(clazz.isInstance(i),
                    context + "classtest");
      }
    }
    for (int rd = 0; rd < 32; ++rd) {
      int opcode = Instruction_Rd_K16.composeOpcode(baseOpcode,
                                                    rd,
                                                    0xffff);
      Instruction i = decoder.decodeInstruction(deviceKey,
                                                (opcode >> 16) & 0xffff,
                                                opcode & 0xffff);
      String context = mnemonic + " r" + rd + ", 0x" + Integer.toHexString(rd);
      if (expectToFind) {
        assertNotNull(i,
                      context + "nulltest");
        assertTrue(clazz.isInstance(i),
                   context + "classtest");
        Instruction_Rd_K16 ik16 = clazz.cast(i);
        assertEquals(ik16.getRdAddress(),
                     rd,
                     context + "rd");
        assertEquals(ik16.getK16(),
                     0xffff,
                     context + "k16");
      } else {
        assertFalse(clazz.isInstance(i),
                    context + "classtest");

      }
    }
  }

  protected void testLdPtr_UNMODIFIED(int baseOpcode,
                                      Class<? extends Instruction_LdSt> clazz,
                                      Pointer pointer,
                                      String mnemonic,
                                      boolean expectFind)
  {
    Instruction instruction;
    for (int rd = 0; rd < 32; ++rd) {
      String context = mnemonic + " r" + rd + ", " + pointer.name() + " | ";
      int opcode = Instruction_LdSt.composeOpcode(baseOpcode,
                                                  pointer,
                                                  rd,
                                                  Instruction_LdSt.Mode.UNMODIFIED,
                                                  0);
      instruction = decoder.decodeInstruction(deviceKey,
                                              opcode,
                                              0);
      if (expectFind) {
        assertNotNull(instruction,
                      context + "nulltest");
        assertTrue(clazz.isInstance(instruction),
                   context + "class" + " found:" + instruction.getClass().getName());
        Instruction_LdSt ld = clazz.cast(instruction);
        assertEquals(ld.getMnemonic(),
                     mnemonic,
                     context + "mnemonic");
        assertEquals(ld.getRdAddress(),
                     rd,
                     context + "rd");
        assertEquals(ld.getPointer(),
                     pointer,
                     context + "ptr");
        assertEquals(ld.getDisplacement(),
                     0,
                     context + "disp");
        assertEquals(ld.getMode(),
                     Instruction_LdSt.Mode.UNMODIFIED,
                     context + "mode");
      } else {
        assertFalse(clazz.isInstance(instruction),
                    context + "class");

      }
    }
  }

  protected void testLdPtr_POST_INCREMENT(int baseOpcode,
                                          Class<? extends Instruction_LdSt> clazz,
                                          Pointer pointer,
                                          String mnemonic,
                                          boolean expectFind)
  {
    Instruction instruction;
    for (int rd = 0; rd < 32; ++rd) {
      String context = "ld r" + rd + ", " + pointer.name() + "+ | ";
      int opcode = Instruction_LdSt.composeOpcode(baseOpcode,
                                                  pointer,
                                                  rd,
                                                  Instruction_LdSt.Mode.POST_INCREMENT,
                                                  0);
      instruction = decoder.decodeInstruction(deviceKey,
                                              opcode,
                                              0);
      if (expectFind) {
        assertNotNull(instruction,
                      context + "nulltest");
        assertTrue(clazz.isInstance(instruction),
                   context + "class");
        Instruction_LdSt ld = clazz.cast(instruction);
        assertEquals(ld.getMnemonic(),
                     mnemonic,
                     context + "mnemonic");
        assertEquals(ld.getRdAddress(),
                     rd,
                     context + "rd");
        assertEquals(ld.getPointer(),
                     pointer,
                     context + "ptr");
        assertEquals(ld.getDisplacement(),
                     0,
                     context + "disp");
        assertEquals(ld.getMode(),
                     Instruction_LdSt.Mode.POST_INCREMENT);
      } else {
        assertFalse(clazz.isInstance(instruction),
                    context + "class");
      }
    }
  }

  protected void testLdPtr_PRE_DECREMENT(int baseOpcode,
                                         Class<? extends Instruction_LdSt> clazz,
                                         Pointer pointer,
                                         String mnemonic,
                                         boolean expectFind)
  {
    Instruction instruction;
    for (int rd = 0; rd < 32; ++rd) {
      String context = "ld r" + rd + ", -" + pointer.name() + " | ";
      int opcode = Instruction_LdSt.composeOpcode(baseOpcode,
                                                  pointer,
                                                  rd,
                                                  Instruction_LdSt.Mode.PRE_DECREMENT,
                                                  0);
      instruction = decoder.decodeInstruction(deviceKey,
                                              opcode,
                                              0);
      if (expectFind) {
        assertTrue(clazz.isInstance(instruction),
                   context + "class");
        Instruction_LdSt ld = clazz.cast(instruction);
        assertEquals(ld.getMnemonic(),
                     mnemonic,
                     context + "mnemonic");
        assertEquals(ld.getRdAddress(),
                     rd,
                     context + "rd");
        assertEquals(ld.getPointer(),
                     pointer,
                     context + "ptr");
        assertEquals(ld.getDisplacement(),
                     0,
                     context + "disp");
        assertEquals(ld.getMode(),
                     Instruction_LdSt.Mode.PRE_DECREMENT);
      } else {
        assertFalse(clazz.isInstance(instruction),
                    context + "class");
      }
    }
  }

  protected void testLdPtr_DISP(int baseOpcode,
                                Class<? extends Instruction_LdSt> clazz,
                                Pointer pointer,
                                String mnemonic,
                                boolean expectFind)
  {
    Instruction instruction;
    for (int rd = 0; rd < 32; ++rd) {
      for (int q = 1; q < 64; ++q) {
        String context = "ldd r" + rd + ", " + pointer.name() + "+" + q + " | ";
        int opcode = Instruction_LdSt.composeOpcode(baseOpcode,
                                                    pointer,
                                                    rd,
                                                    Instruction_LdSt.Mode.DISPLACEMENT,
                                                    q);
        instruction = decoder.decodeInstruction(deviceKey,
                                                opcode,
                                                0);
        if (expectFind) {
          assertTrue(clazz.isInstance(instruction),
                     context + "class");
          Instruction_LdSt ld = clazz.cast(instruction);
          assertEquals(ld.getMnemonic(),
                       mnemonic + "d",
                       context + "mnemonic");
          assertEquals(ld.getRdAddress(),
                       rd,
                       context + "rd");
          assertEquals(ld.getPointer(),
                       pointer,
                       context + "ptr");
          assertEquals(ld.getDisplacement(),
                       q,
                       context + "disp");
          assertEquals(ld.getMode(),
                       Instruction_LdSt.Mode.DISPLACEMENT);
        } else {
          assertFalse(clazz.isInstance(instruction),
                      context + "class");
        }
      }
    }
  }

  protected void test_P_b(int baseOpcode,
                          Class<? extends Instruction_P_b> clazz,
                          String mnemonic,
                          boolean expectFind)
  {
    for (int p = 0; p < 32; ++p) {
      for (int b = 0; b < 8; ++b) {
        int opcode = Instruction_P_b.composeOpcode(baseOpcode,
                                                   p,
                                                   b);
        String context = mnemonic + " 0x" + Integer.toHexString(p) + ", " + b + " | ";
        Instruction i = decoder.decodeInstruction(deviceKey,
                                                  opcode,
                                                  0);
        if (expectFind) {
          assertNotNull(i,
                        context + "nulltest");
          assertTrue(clazz.isInstance(i),
                     context + "classtest");
          Instruction_P_b ipb = clazz.cast(i);
          assertEquals(ipb.getBitOffset(),
                       b,
                       context + "bit");
          assertEquals(ipb.getPortAddress(),
                       p,
                       context + "pot");
          assertEquals(ipb.getMnemonic(),
                       mnemonic,
                       context + "mnemonic");
        } else {
          assertFalse(clazz.isInstance(i),
                      context + "classtest");
        }
      }
    }
  }

  protected void test_K4(int baseOpcode,
                         Class<? extends Instruction_K4> clazz,
                         String mnemonic,
                         boolean expectToFind)
  {
    for (int i = 0; i < 16; ++i) {
      int opcode = Instruction_K4.composeOpcode(baseOpcode,
                                                i);
      String context = mnemonic + " " + i + " | ";
      Instruction inst = decoder.decodeInstruction(deviceKey,
                                                   opcode,
                                                   -1);
      if (expectToFind) {
        assertNotNull(inst,
                      context + "nulltest");
        assertTrue(clazz.isInstance(inst),
                   context + "classtest");
        assertEquals(((Instruction_K4) inst).getK4(),
                     i,
                     context + "k4");
        assertEquals(inst.getMnemonic(),
                     mnemonic,
                     context + "mnemonic");
      } else {
        assertFalse(clazz.isInstance(inst),
                    context + "classtest");
      }
    }
  }

  protected void testRdh23_Rrh23(int baseOpcode,
                                 Class<? extends Instruction_Rdh23_Rrh23> clazz,
                                 String mnemonic,
                                 boolean expectToFind)
  {
    for (int rd = 16; rd < 24; ++rd) {
      for (int rr = 16; rr < 24; ++rr) {
        final String context = mnemonic + " r" + rd + ",r" + rr + " | ";
        final int opcode = Instruction_Rdh23_Rrh23.composeOpcode(baseOpcode,
                                                                 rd,
                                                                 rr);
        final Instruction i = decoder.decodeInstruction(deviceKey,
                                                        opcode,
                                                        0);
        if (expectToFind) {
          assertNotNull(i,
                        context + "nulltest");
          assertTrue(clazz.isInstance(i),
                     context + "class");
          Instruction_Rdh23_Rrh23 inst = clazz.cast(i);
          assertEquals(inst.getRdhAddress(),
                       rd,
                       context + "rd");
          assertEquals(inst.getRrhAddress(),
                       rr,
                       context + "rr");
          assertEquals(inst.getMnemonic(),
                       mnemonic,
                       context + "mnemonic");
        } else {
          assertFalse(clazz.isInstance(i),
                      context + "class");
        }
      }
    }
  }

  protected void testRdh_K7(int baseOpcode,
                            Class<? extends Instruction_Rdh_K7> clazz,
                            String mnemonic,
                            boolean expectToFind)
  {
    for (int rdh = 16; rdh < 32; ++rdh) {
      for (int k7 = 0; k7 < 128; ++k7) {
        int opcode = Instruction_Rdh_K7.composeOpcode(baseOpcode,
                                                      rdh,
                                                      k7);
        Instruction i = decoder.decodeInstruction(deviceKey,
                                                  opcode,
                                                  -1);
        String context = mnemonic + " r" + rdh + ", " + k7 + " | ";
        if (expectToFind) {
          assertNotNull(i,
                        context + "nulltest");
          assertTrue(clazz.isInstance(i),
                     context + "class");
          Instruction_Rdh_K7 ik7 = clazz.cast(i);
          assertEquals(ik7.getRdhAddress(),
                       rdh,
                       context + "rdh");
          assertEquals(ik7.getK7(),
                       k7,
                       context + "k7");
          assertEquals(ik7.getMnemonic(),
                       mnemonic,
                       context + "mnemonic");
        } else {
          assertFalse(clazz.isInstance(i),
                      context + "class");
        }
      }
    }
  }

  protected void testRdhRrh(int baseOpcode,
                            Class<? extends Instruction_Rdh_Rrh> clazz,
                            String mnemonic,
                            boolean expectToFind)
  {
    for (int rd = 16; rd < 33; ++rd) {
      for (int rr = 16; rr < 33; ++rr) {
        int opcode = Instruction_Rdh_Rrh.composeOpcode(baseOpcode,
                                                       rd,
                                                       rr);
        Instruction i = decoder.decodeInstruction(deviceKey,
                                                  opcode,
                                                  -1);
        String context = mnemonic + " r" + rd + ", r" + rr + " | ";
        if (expectToFind) {
          assertNotNull(i,
                        context + "nulltest");
          assertTrue(clazz.isInstance(i),
                     context + "class");
          Instruction_Rdh_Rrh inst = clazz.cast(i);
          assertEquals(inst.getRdhAddress(),
                       rd,
                       context + "rd");
          assertEquals(inst.getRrhAddress(),
                       rr,
                       context + "rr");
          assertEquals(inst.getMnemonic(),
                       mnemonic,
                       context + "mnemonic");
        } else {
          assertFalse(clazz.isInstance(i),
                      context + "class");
        }
      }
    }
  }

}
