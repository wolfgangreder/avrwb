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

import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.instructions.Adc;
import com.avrwb.avr8.api.instructions.Add;
import com.avrwb.avr8.api.instructions.Adiw;
import com.avrwb.avr8.api.instructions.And;
import com.avrwb.avr8.api.instructions.Andi;
import com.avrwb.avr8.api.instructions.Asr;
import com.avrwb.avr8.api.instructions.Bld;
import com.avrwb.avr8.api.instructions.BranchInstruction;
import com.avrwb.avr8.api.instructions.Break;
import com.avrwb.avr8.api.instructions.Bst;
import com.avrwb.avr8.api.instructions.Call;
import com.avrwb.avr8.api.instructions.Com;
import com.avrwb.avr8.api.instructions.Cp;
import com.avrwb.avr8.api.instructions.Cpc;
import com.avrwb.avr8.api.instructions.Cpi;
import com.avrwb.avr8.api.instructions.Cpse;
import com.avrwb.avr8.api.instructions.Dec;
import com.avrwb.avr8.api.instructions.EICall;
import com.avrwb.avr8.api.instructions.EIJmp;
import com.avrwb.avr8.api.instructions.Elpm;
import com.avrwb.avr8.api.instructions.Eor;
import com.avrwb.avr8.api.instructions.ICall;
import com.avrwb.avr8.api.instructions.IJmp;
import com.avrwb.avr8.api.instructions.InOut;
import com.avrwb.avr8.api.instructions.Inc;
import com.avrwb.avr8.api.instructions.Instruction_K22;
import com.avrwb.avr8.api.instructions.Instruction_LdSt;
import com.avrwb.avr8.api.instructions.Instruction_Rd;
import com.avrwb.avr8.api.instructions.Instruction_Rd_K16;
import com.avrwb.avr8.api.instructions.Instruction_Rd_K8;
import com.avrwb.avr8.api.instructions.Instruction_Rd_Rr;
import com.avrwb.avr8.api.instructions.Instruction_Rd_b;
import com.avrwb.avr8.api.instructions.Instruction_Rdl_K6;
import com.avrwb.avr8.api.instructions.Instruction_k12;
import com.avrwb.avr8.api.instructions.Jmp;
import com.avrwb.avr8.api.instructions.Lac;
import com.avrwb.avr8.api.instructions.Las;
import com.avrwb.avr8.api.instructions.Lat;
import com.avrwb.avr8.api.instructions.Ld;
import com.avrwb.avr8.api.instructions.Ldi;
import com.avrwb.avr8.api.instructions.Lds;
import com.avrwb.avr8.api.instructions.Lpm;
import com.avrwb.avr8.api.instructions.Lsr;
import com.avrwb.avr8.api.instructions.Mov;
import com.avrwb.avr8.api.instructions.Mul;
import com.avrwb.avr8.api.instructions.Neg;
import com.avrwb.avr8.api.instructions.Nop;
import com.avrwb.avr8.api.instructions.Or;
import com.avrwb.avr8.api.instructions.Ori;
import com.avrwb.avr8.api.instructions.Pop;
import com.avrwb.avr8.api.instructions.Push;
import com.avrwb.avr8.api.instructions.Rcall;
import com.avrwb.avr8.api.instructions.Ret_i;
import com.avrwb.avr8.api.instructions.Rjmp;
import com.avrwb.avr8.api.instructions.Ror;
import com.avrwb.avr8.api.instructions.Sbc;
import com.avrwb.avr8.api.instructions.Sbci_Subi;
import com.avrwb.avr8.api.instructions.Sbiw;
import com.avrwb.avr8.api.instructions.Sleep;
import com.avrwb.avr8.api.instructions.Spm;
import com.avrwb.avr8.api.instructions.St;
import com.avrwb.avr8.api.instructions.Sts;
import com.avrwb.avr8.api.instructions.Sub;
import com.avrwb.avr8.api.instructions.Swap;
import com.avrwb.avr8.api.instructions.Wdr;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.schema.AvrCore;
import com.avrwb.schema.AvrFamily;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class InstructionDecoder_V0NGTest extends BaseInstructionDecoderNGTest
{

  @BeforeClass
  public static void createDecoder()
  {
    decoder = new ProtocollDecoder(new InstructionDecoder_V0());
    deviceKey = new AvrDeviceKey(AvrFamily.BASIC,
                                 AvrCore.V0,
                                 "AT90S1200");
  }

  @AfterClass
  public static void afterClass()
  {
    BaseInstructionDecoderNGTest.afterClass();
  }

  @DataProvider(name = "rdRrProvider")
  public Object[][] rdRrProvider()
  {
    return new Object[][]{
      {Adc.OPCODE, Adc.class, "adc", true},
      {Add.OPCODE, Add.class, "add", true},
      {And.OPCODE, And.class, "and", true},
      {Cp.OPCODE, Cp.class, "cp", true},
      {Cpc.OPCODE, Cpc.class, "cpc", true},
      {Cpse.OPCODE, Cpse.class, "cpse", true},
      {Eor.OPCODE, Eor.class, "eor", true},
      {Mov.OPCODE, Mov.class, "mov", true},
      {Mul.OPCODE, Mul.class, "mul", false},
      {Or.OPCODE, Or.class, "or", true},
      {Sbc.OPCODE, Sbc.class, "sbc", true},
      {Sub.OPCODE, Sub.class, "sub", true}
    };
  }

  @Override
  @Test(dataProvider = "rdRrProvider")
  public void test_Rd_Rr(int baseOpcode,
                         Class<? extends Instruction_Rd_Rr> clazz,
                         String menmonic,
                         boolean expectToFind)
  {
    super.test_Rd_Rr(baseOpcode,
                     clazz,
                     menmonic,
                     expectToFind);
  }

  @DataProvider(name = "rdProvider")
  public Object[][] rdProvider()
  {
    return new Object[][]{
      {Asr.OPCODE, Asr.class, "asr", true},
      {Com.OPCODE, Com.class, "com", true},
      {Dec.OPCODE, Dec.class, "dec", true},
      {Inc.OPCODE, Inc.class, "inc", true},
      {Lac.OPCODE, Lac.class, "lac", false},
      {Las.OPCODE, Las.class, "las", false},
      {Lat.OPCODE, Lat.class, "lat", false},
      {Lsr.OPCODE, Lsr.class, "lsr", true},
      {Neg.OPCODE, Neg.class, "neg", true},
      {Pop.OPCODE, Pop.class, "pop", false},
      {Push.OPCODE, Push.class, "push", false},
      {Ror.OPCODE, Ror.class, "ror", true},
      {Swap.OPCODE, Swap.class, "swap", true}
    };
  }

  @Test(dataProvider = "rdProvider")
  @Override
  public void test_Rd(int baseOpcode,
                      Class<? extends Instruction_Rd> clazz,
                      String mnemonic,
                      boolean expectToFind)
  {
    super.test_Rd(baseOpcode,
                  clazz,
                  mnemonic,
                  expectToFind);
  }

  @DataProvider(name = "rdK8Provider")
  public Object[][] rdK8Provider()
  {
    return new Object[][]{
      {Andi.OPCODE, Andi.class, "andi", true},
      {Cpi.OPCODE, Cpi.class, "cpi", true},
      {Ldi.OPCODE, Ldi.class, "ldi", true},
      {Ori.OPCODE, Ori.class, "ori", true},
      {Sbci_Subi.OPCODE_SBCI, Sbci_Subi.class, "sbci", true},
      {Sbci_Subi.OPCODE_SUBI, Sbci_Subi.class, "subi", true}};
  }

  @Test(dataProvider = "rdK8Provider")
  @Override
  public void test_Rd_K8(int baseOpcode,
                         Class<? extends Instruction_Rd_K8> clazz,
                         String mnemonic,
                         boolean expectToFind)
  {
    super.test_Rd_K8(baseOpcode,
                     clazz,
                     mnemonic,
                     expectToFind);
  }

  @DataProvider(name = "branchProvider")
  public Object[][] brachProvider()
  {
    return new Object[][]{
      {BranchInstruction.OPCODE_SET, BranchInstruction.class, "BR?S", true},
      {BranchInstruction.OPCODE_CLR, BranchInstruction.class, "BR?C", false}
    };
  }

  @Test(dataProvider = "branchProvider")
  @Override
  public void testBranch(int baseOpcode,
                         Class<? extends BranchInstruction> clazz,
                         String mnemonic,
                         boolean bitSet)
  {
    super.testBranch(baseOpcode,
                     clazz,
                     mnemonic,
                     bitSet);
  }

  @DataProvider(name = "k12Provider")
  public Object[][] k12Provider()
  {
    return new Object[][]{
      {Rcall.OPCODE, Rcall.class, "rcall", true},
      {Rjmp.OPCODE, Rjmp.class, "rjmp", true}
    };
  }

  @Test(dataProvider = "k12Provider")
  @Override
  public void testInstruction_k12(int baseOpcode,
                                  Class<? extends Instruction_k12> clazz,
                                  String mnemonic,
                                  boolean expectToFind)
  {
    super.testInstruction_k12(baseOpcode,
                              clazz,
                              mnemonic,
                              expectToFind);
  }

  @DataProvider(name = "inOutProvider")
  public Object[][] inOutProvider()
  {
    return new Object[][]{
      {InOut.OPCODE_IN, "in"},
      {InOut.OPCODE_OUT, "out"}
    };
  }

  @Test(dataProvider = "inOutProvider")
  @Override
  public void testInOut(int baseOpcode,
                        String mnemonic)
  {
    super.testInOut(baseOpcode,
                    mnemonic);
  }

  @DataProvider(name = "rdlK6Provider")
  public Object[][] rdlK6Provider()
  {
    return new Object[][]{
      {Adiw.OPCODE, Adiw.class, "adiw", false},
      {Sbiw.OPCODE, Sbiw.class, "sbiw", false}
    };
  }

  @DataProvider(name = "rdK16Provider")
  public Object[][] rdK16Provider()
  {
    return new Object[][]{
      {Lds.OPCODE, Lds.class, "lds", false},
      {Sts.OPCODE, Sts.class, "sts", false}
    };
  }

  @Test(dataProvider = "rdK16Provider")
  @Override
  public void testrdK16(int baseOpcode,
                        Class<? extends Instruction_Rd_K16> clazz,
                        String mnemonic,
                        boolean expectToFind)
  {
    super.testrdK16(baseOpcode,
                    clazz,
                    mnemonic,
                    expectToFind);
  }

  @DataProvider(name = "testRdb")
  public Object[][] rdbProvider()
  {
    return new Object[][]{
      {Bld.OPCODE, Bld.class, "bld", true},
      {Bst.OPCODE, Bst.class, "bst", true}
    };
  }

  @Test(dataProvider = "testRdb")
  @Override
  public void testRdb(int baseOpcode,
                      Class<? extends Instruction_Rd_b> clazz,
                      String mnemonic,
                      boolean expectToFind)
  {
    super.testRdb(baseOpcode,
                  clazz,
                  mnemonic,
                  expectToFind);
  }

  @DataProvider(name = "simpleProvider")
  public Object[][] simpleProvider()
  {
    return new Object[][]{
      {Break.OPCODE, Break.class, "break", true},
      {EICall.OPCODE, EICall.class, "eicall", false},
      {EIJmp.OPCODE, EIJmp.class, "eijmp", false},
      {Elpm.OPCODE_ELPM, Elpm.class, "elpm", false},
      {ICall.OPCODE, ICall.class, "icall", false},
      {IJmp.OPCODE, IJmp.class, "ijmp", false},
      {Lpm.OPCODE_LPM, Lpm.class, "lpm", false},
      {Nop.OPCODE, Nop.class, "nop", true},
      {Ret_i.OPCODE_RET, Ret_i.class, "ret", true},
      {Ret_i.OPCODE_RETI, Ret_i.class, "reti", true},
      {Sleep.OPCODE, Sleep.class, "sleep", true},
      {Spm.OPCODE, Spm.class, "spm", false},
      {Spm.OPCODE_ZI, Spm.class, "spm", false},
      {Wdr.OPCODE, Wdr.class, "wdr", true}
    };
  }

  @Test(dataProvider = "simpleProvider")
  @Override
  public void testSimple(int opcode,
                         Class<? extends Instruction> clazz,
                         String mnemonic,
                         boolean expectToFind)
  {
    super.testSimple(opcode,
                     clazz,
                     mnemonic,
                     expectToFind);
  }

  @DataProvider(name = "k22Provider")
  public Object[][] k22Provider()
  {
    return new Object[][]{
      {Call.OPCODE, Call.class, "call", false},
      {Jmp.OPCODE, Jmp.class, "jmp", false}
    };
  }

  @Test(dataProvider = "k22Provider")
  @Override
  public void testK22(int baseOpcode,
                      Class<? extends Instruction_K22> clazz,
                      String mnemonic,
                      boolean expectToFind)
  {
    super.testK22(baseOpcode,
                  clazz,
                  mnemonic,
                  expectToFind);
  }

  @DataProvider(name = "elpmrdProvider")
  public Object[][] elpmrdProvider()
  {
    return new Object[][]{
      {Elpm.OPCODE_ELPM_RD, Elpm.class, "elpm", false},
      {Elpm.OPCODE_ELPM_RDP, Elpm.class, "elpm", false},};
  }

  @Test(dataProvider = "elpmrdProvider")
  @Override
  public void test_Elpm(int baseOpcode,
                        Class<? extends Elpm> clazz,
                        String mnemonic,
                        boolean expectToFind)
  {
    super.test_Elpm(baseOpcode,
                    clazz,
                    mnemonic,
                    expectToFind);
  }

  @Test(dataProvider = "rdlK6Provider")
  @Override
  public void testRdlK6(int baseOpcode,
                        Class<? extends Instruction_Rdl_K6> clazz,
                        String mnemonic,
                        boolean expectToFind)
  {
    super.testRdlK6(baseOpcode,
                    clazz,
                    mnemonic,
                    expectToFind);
  }

  @Test
  @Override
  public void testBitClearSet()
  {
    super.testBitClearSet();
  }

  @DataProvider(name = "ldProvider")
  public Object[][] ldPtrProvider()
  {
    return new Object[][]{
      {Ld.OPCODE_LD_X, Ld.class, Pointer.X, "ld", false, false, false, false},
      {Ld.OPCODE_LD_X_M, Ld.class, Pointer.X, "ld", false, false, false, false},
      {Ld.OPCODE_LD_X_P, Ld.class, Pointer.X, "ld", false, false, false, false},
      {Ld.OPCODE_LD_Y, Ld.class, Pointer.Y, "ld", false, false, false, false},
      {Ld.OPCODE_LD_Y_M, Ld.class, Pointer.Y, "ld", false, false, false, false},
      {Ld.OPCODE_LD_Y_P, Ld.class, Pointer.Y, "ld", false, false, false, false},
      {Ld.OPCODE_LD_Z, Ld.class, Pointer.Z, "ld", true, false, false, false},
      {Ld.OPCODE_LD_Z_M, Ld.class, Pointer.Z, "ld", false, false, false, false},
      {Ld.OPCODE_LD_Z_P, Ld.class, Pointer.Z, "ld", false, false, false, false},
      {St.OPCODE_ST_X, St.class, Pointer.X, "st", false, false, false, false},
      {St.OPCODE_ST_X_M, St.class, Pointer.X, "st", false, false, false, false},
      {St.OPCODE_ST_X_P, St.class, Pointer.X, "st", false, false, false, false},
      {St.OPCODE_ST_Y, St.class, Pointer.Y, "st", false, false, false, false},
      {St.OPCODE_ST_Y_M, St.class, Pointer.Y, "st", false, false, false, false},
      {St.OPCODE_ST_Y_P, St.class, Pointer.Y, "st", false, false, false, false},
      {St.OPCODE_ST_Z, St.class, Pointer.Z, "st", true, false, false, false},
      {St.OPCODE_ST_Z_M, St.class, Pointer.Z, "st", false, false, false, false},
      {St.OPCODE_ST_Z_P, St.class, Pointer.Z, "st", false, false, false, false}
    };
  }

  @Test(dataProvider = "ldProvider")
  @Override
  protected void testLdPtr(int baseOpcode,
                           Class<? extends Instruction_LdSt> clazz,
                           Pointer pointer,
                           String mnemonic,
                           boolean expectFind,
                           boolean expectFind_PI,
                           boolean expectFind_PD,
                           boolean expectFind_Q)
  {
    super.testLdPtr(baseOpcode,
                    clazz,
                    pointer,
                    mnemonic,
                    expectFind,
                    expectFind_PI,
                    expectFind_PD,
                    expectFind_Q);
  }

}
