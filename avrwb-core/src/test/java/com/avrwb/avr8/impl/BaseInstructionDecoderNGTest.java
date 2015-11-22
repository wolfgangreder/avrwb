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
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.InstructionDecoder;
import com.avrwb.avr8.api.instructions.Adc;
import com.avrwb.avr8.api.instructions.Add;
import com.avrwb.avr8.api.instructions.And;
import com.avrwb.avr8.api.instructions.Andi;
import com.avrwb.avr8.api.instructions.Asr;
import com.avrwb.avr8.api.instructions.BranchInstruction;
import com.avrwb.avr8.api.instructions.Com;
import com.avrwb.avr8.api.instructions.Cp;
import com.avrwb.avr8.api.instructions.Cpc;
import com.avrwb.avr8.api.instructions.Cpi;
import com.avrwb.avr8.api.instructions.Cpse;
import com.avrwb.avr8.api.instructions.Dec;
import com.avrwb.avr8.api.instructions.Eor;
import com.avrwb.avr8.api.instructions.InOut;
import com.avrwb.avr8.api.instructions.Inc;
import com.avrwb.avr8.api.instructions.Instruction_Rd;
import com.avrwb.avr8.api.instructions.Instruction_Rd_K8;
import com.avrwb.avr8.api.instructions.Instruction_Rd_Rr;
import com.avrwb.avr8.api.instructions.Instruction_k12;
import com.avrwb.avr8.api.instructions.Lac;
import com.avrwb.avr8.api.instructions.Las;
import com.avrwb.avr8.api.instructions.Lat;
import com.avrwb.avr8.api.instructions.Ldi;
import com.avrwb.avr8.api.instructions.Lsr;
import com.avrwb.avr8.api.instructions.Mov;
import com.avrwb.avr8.api.instructions.Mul;
import com.avrwb.avr8.api.instructions.Neg;
import com.avrwb.avr8.api.instructions.Or;
import com.avrwb.avr8.api.instructions.Ori;
import com.avrwb.avr8.api.instructions.Pop;
import com.avrwb.avr8.api.instructions.Push;
import com.avrwb.avr8.api.instructions.Rcall;
import com.avrwb.avr8.api.instructions.Rjmp;
import com.avrwb.avr8.api.instructions.Ror;
import com.avrwb.avr8.api.instructions.Sbc;
import com.avrwb.avr8.api.instructions.Sbci_Subi;
import com.avrwb.avr8.api.instructions.Sub;
import com.avrwb.avr8.api.instructions.Swap;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.InstructionNotAvailableException;
import com.avrwb.schema.AvrCore;
import com.avrwb.schema.AvrFamily;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
  private static InstructionDecoder decoder;

  private static final class ProtocollDecoder implements InstructionDecoder
  {

    private final BaseInstructionDecoder decoder = new BaseInstructionDecoder();

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

  @BeforeClass
  public static void createDecoder()
  {
    decoder = new ProtocollDecoder();
  }

  @AfterClass
  public static void afterClass()
  {
    double coverage = TESTED_OPCODES.size();
    coverage /= 65535d;
    System.err.println(MessageFormat.format("Tested {0,number,0,000} ({1,number,0.0}%) opcodes.",
                                            TESTED_OPCODES.size(),
                                            coverage * 100));
  }

  @DataProvider(name = "rdRrProvider")
  public Object[][] rdRrProvider()
  {
    AvrDeviceKey deviceKey = new AvrDeviceKey(AvrFamily.TINY,
                                              AvrCore.V2,
                                              "ATtiny4313");
    return new Object[][]{
      {deviceKey, Adc.OPCODE, Adc.class, "adc", true},
      {deviceKey, Add.OPCODE, Add.class, "add", true},
      {deviceKey, And.OPCODE, And.class, "and", true},
      {deviceKey, Cp.OPCODE, Cp.class, "cp", true},
      {deviceKey, Cpc.OPCODE, Cpc.class, "cpc", true},
      {deviceKey, Cpse.OPCODE, Cpse.class, "cpse", true},
      {deviceKey, Eor.OPCODE, Eor.class, "eor", true},
      {deviceKey, Mov.OPCODE, Mov.class, "mov", true},
      {deviceKey, Mul.OPCODE, Mul.class, "mul", true},
      {deviceKey, Or.OPCODE, Or.class, "or", true},
      {deviceKey, Sbc.OPCODE, Sbc.class, "sbc", true},
      {deviceKey, Sub.OPCODE, Sub.class, "sub", true}
    };

  }

  @Test(dataProvider = "rdRrProvider")
  public void test_Rd_Rr(AvrDeviceKey deviceKey,
                         int baseOpcode,
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

  @DataProvider(name = "rdProvider")
  public Object[][] rdProvider()
  {
    AvrDeviceKey deviceKey = new AvrDeviceKey(AvrFamily.TINY,
                                              AvrCore.V2,
                                              "ATtiny4313");
    return new Object[][]{
      {deviceKey, Asr.OPCODE, Asr.class, "asr", true},
      {deviceKey, Com.OPCODE, Com.class, "com", true},
      {deviceKey, Dec.OPCODE, Dec.class, "dec", true},
      {deviceKey, Inc.OPCODE, Inc.class, "inc", true},
      {deviceKey, Lac.OPCODE, Lac.class, "lac", true},
      {deviceKey, Las.OPCODE, Las.class, "las", true},
      {deviceKey, Lat.OPCODE, Lat.class, "lat", true},
      {deviceKey, Lsr.OPCODE, Lsr.class, "lsr", true},
      {deviceKey, Neg.OPCODE, Neg.class, "neg", true},
      {deviceKey, Pop.OPCODE, Pop.class, "pop", true},
      {deviceKey, Push.OPCODE, Push.class, "push", true},
      {deviceKey, Ror.OPCODE, Ror.class, "ror", true},
      {deviceKey, Swap.OPCODE, Swap.class, "swap", true}
    };
  }

  @Test(dataProvider = "rdProvider")
  public void test_Rd(AvrDeviceKey deviceKey,
                      int baseOpcode,
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

  @DataProvider(name = "rdK8Provider")
  public Object[][] rdK8Provider()
  {
    AvrDeviceKey deviceKey = new AvrDeviceKey(AvrFamily.TINY,
                                              AvrCore.V2,
                                              "ATtiny4313");
    return new Object[][]{
      {deviceKey, Andi.OPCODE, Andi.class, "andi", true},
      {deviceKey, Cpi.OPCODE, Cpi.class, "cpi", true},
      {deviceKey, Ldi.OPCODE, Ldi.class, "ldi", true},
      {deviceKey, Ori.OPCODE, Ori.class, "ori", true},
      {deviceKey, Sbci_Subi.OPCODE_SBCI, Sbci_Subi.class, "sbci", true},
      {deviceKey, Sbci_Subi.OPCODE_SUBI, Sbci_Subi.class, "subi", true}};
  }

  @Test(dataProvider = "rdK8Provider")
  public void test_Rd_K8(AvrDeviceKey deviceKey,
                         int baseOpcode,
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

  @DataProvider(name = "branchProvider")
  public Object[][] brachProvider()
  {
    AvrDeviceKey deviceKey = new AvrDeviceKey(AvrFamily.TINY,
                                              AvrCore.V2,
                                              "ATtiny4313");
    return new Object[][]{
      {deviceKey, BranchInstruction.OPCODE_SET, BranchInstruction.class, "BR?S", true},
      {deviceKey, BranchInstruction.OPCODE_CLR, BranchInstruction.class, "BR?C", false}
    };
  }

  @Test(dataProvider = "branchProvider")
  public void testBranch(AvrDeviceKey deviceKey,
                         int baseOpcode,
                         Class<? extends BranchInstruction> clazz,
                         String mnemonic,
                         boolean bitSet)
  {
    for (int b = 0; b < 7; ++b) {
      for (int dist = -64; dist < 64; ++dist) {
        int opcode = BranchInstruction.composeOpcode(baseOpcode,
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
        BranchInstruction bi = clazz.cast(instruction);
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

  @DataProvider(name = "k12Provider")
  public Object[][] k12Provider()
  {
    AvrDeviceKey deviceKey = new AvrDeviceKey(AvrFamily.TINY,
                                              AvrCore.V2,
                                              "ATtiny4313");
    return new Object[][]{
      {deviceKey, Rcall.OPCODE, Rcall.class, "rcall", true},
      {deviceKey, Rjmp.OPCODE, Rjmp.class, "rjmp", true}
    };
  }

  @Test(dataProvider = "k12Provider")
  public void testInstruction_k12(AvrDeviceKey deviceKey,
                                  int baseOpcode,
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

  @DataProvider(name = "inOutProvider")
  public Object[][] inOutProvider()
  {
    AvrDeviceKey deviceKey = new AvrDeviceKey(AvrFamily.TINY,
                                              AvrCore.V2,
                                              "ATtiny4313");
    return new Object[][]{
      {deviceKey, InOut.OPCODE_IN, "in"},
      {deviceKey, InOut.OPCODE_OUT, "out"}
    };
  }

  @Test(dataProvider = "inOutProvider")
  public void testInOut(AvrDeviceKey deviceKey,
                        int baseOpcode,
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

}
