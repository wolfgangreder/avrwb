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
import com.avrwb.avr8.Memory;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.InstructionDecoder;
import com.avrwb.avr8.api.instructions.Adc;
import com.avrwb.avr8.api.instructions.Add;
import com.avrwb.avr8.api.instructions.Adiw;
import com.avrwb.avr8.api.instructions.And;
import com.avrwb.avr8.api.instructions.Andi;
import com.avrwb.avr8.api.instructions.Asr;
import com.avrwb.avr8.api.instructions.BitClearSet;
import com.avrwb.avr8.api.instructions.Bld;
import com.avrwb.avr8.api.instructions.BranchInstruction;
import com.avrwb.avr8.api.instructions.Break;
import com.avrwb.avr8.api.instructions.Bst;
import com.avrwb.avr8.api.instructions.Call;
import com.avrwb.avr8.api.instructions.Com;
import com.avrwb.avr8.api.instructions.Cp;
import com.avrwb.avr8.api.instructions.Cpi;
import com.avrwb.avr8.api.instructions.Cpse;
import com.avrwb.avr8.api.instructions.Dec;
import com.avrwb.avr8.api.instructions.Eor;
import com.avrwb.avr8.api.instructions.ICall;
import com.avrwb.avr8.api.instructions.IJmp;
import com.avrwb.avr8.api.instructions.InOut;
import com.avrwb.avr8.api.instructions.Inc;
import com.avrwb.avr8.api.instructions.Jmp;
import com.avrwb.avr8.api.instructions.Ld;
import com.avrwb.avr8.api.instructions.Mov;
import com.avrwb.avr8.api.instructions.Mul;
import com.avrwb.avr8.api.instructions.Neg;
import com.avrwb.avr8.api.instructions.Nop;
import com.avrwb.avr8.api.instructions.Or;
import com.avrwb.avr8.api.instructions.Pop;
import com.avrwb.avr8.api.instructions.Push;
import com.avrwb.avr8.api.instructions.Rcall;
import com.avrwb.avr8.api.instructions.Ret_i;
import com.avrwb.avr8.api.instructions.Ror;
import com.avrwb.avr8.api.instructions.Sbiw;
import com.avrwb.avr8.api.instructions.SetClearIOBit;
import com.avrwb.avr8.api.instructions.Sub;
import com.avrwb.avr8.api.instructions.Swap;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.InstructionNotAvailableException;
import com.avrwb.schema.AvrCore;
import com.avrwb.schema.util.Converter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public class DefaultInstructionDecoder1 implements InstructionDecoder
{

  private final Map<Integer, Instruction> instructionCache = new HashMap<>();

  @Override
  public Instruction getInstruction(Device device,
                                    int address) throws NullPointerException, IllegalArgumentException,
                                                        InstructionNotAvailableException
  {
    Objects.requireNonNull(device);
    if ((address % 2) != 0) {
      throw new IllegalArgumentException("address mod 2 !=0");
    }
    Memory flash = device.getFlash();
    int word = flash.getWordAt(address);
    int nextWord = flash.getWordAt(address + 2);
    Instruction result = instructionCache.computeIfAbsent(word,
                                                          (Integer opcode) -> decodeInstruction(device.getDeviceKey(),
                                                                                                opcode,
                                                                                                nextWord));
    if (result == null) {
      throw new InstructionNotAvailableException("Unknown opcode " + Converter.printHexString(word,
                                                                                              4));
    }
    return result;
  }

  @Override
  public Instruction decodeInstruction(AvrDeviceKey deviceKey,
                                       int opcode,
                                       int nextOpcode)
  {
    switch (opcode & 0xc000) {
      case 0x0000:
        return decode_0xxx(deviceKey,
                           opcode,
                           nextOpcode);
      case 0x4000:
        return decode_4xxx(deviceKey,
                           opcode,
                           nextOpcode);
      case 0x8000:
        return decode_8xxx(deviceKey,
                           opcode,
                           nextOpcode);
      case 0xc000:
        return decode_cxxx(deviceKey,
                           opcode,
                           nextOpcode);
    }
    return null;
  }

  protected Instruction decode_0xxx(AvrDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcode)
  {
    switch (opcode & 0x3000) {
      case 0x0000:
        switch (opcode & 0x0c00) {
          case 0x0000:
            if (opcode == 0) {
              return new Nop(deviceKey,
                             opcode,
                             nextOpcode);
//            } else if ((opcode & Operation_Rd_Rr.MASK) == Movw.OPCODE) {
//              return Movw.getOperation(opcode);
//            } else {
//              // hier fehlen die (F)MUL(SU) befehle
            }
            break;
          case 0x0400:
//            if ((opcode & Instruction_Rd_Rr.OPCODE_MASK) == Cpc.OPCODE) {
//              return new Cpc(opcode);
//            }
            break;
          case 0x0800:
//            if ((opcode & Instruction_Rd_Rr.OPCODE_MASK) == Sbc.OPCODE) {
//              return new Sbc(opcode);
//            }
            break;
          case 0x0c00:
            return new Add(deviceKey,
                           opcode,
                           nextOpcode);
        }
        break;
      case 0x1000:
        return decode_1xxx(deviceKey,
                           opcode,
                           nextOpcode);
      case 0x2000:
        return decode_2xxx(deviceKey,
                           opcode,
                           nextOpcode);
      case 0x3000:
        return new Cpi(deviceKey,
                       opcode,
                       nextOpcode);
    }
    return null;
  }

  protected Instruction decode_1xxx(AvrDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcode)
  {
    switch (opcode & 0x0c00) {
      case 0x0000:
        return new Cpse(deviceKey,
                        opcode,
                        nextOpcode);
      case 0x0400:
        return new Cp(deviceKey,
                      opcode,
                      nextOpcode);
      case 0x0800:
        return new Sub(deviceKey,
                       opcode,
                       nextOpcode);
      case 0x0c00:
        return new Adc(deviceKey,
                       opcode,
                       opcode);
    }
    return null;
  }

  protected Instruction decode_2xxx(AvrDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcode)
  {
    switch (opcode & 0x0c00) {
      case 0x0000:
        return new And(deviceKey,
                       opcode,
                       nextOpcode);
      case 0x0400:
        return new Eor(deviceKey,
                       opcode,
                       nextOpcode);
      case 0x0800:
        return new Or(deviceKey,
                      opcode,
                      nextOpcode);
      case 0x0c00:
        return new Mov(deviceKey,
                       opcode,
                       nextOpcode);
    }
    return null;
  }

  protected Instruction decode_4xxx(AvrDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcode)
  {
    switch (opcode & 0xf000) {
//      case 0x4000:
//        return Sbci.getOperation(opcode);
//      case 0x5000:
//        return Subi.getOperation(opcode);
//      case 0x6000:
//        return Ori.getOperation(opcode);
      case 0x7000:
        return new Andi(deviceKey,
                        opcode,
                        nextOpcode);
    }
    return null;
  }

  protected Instruction decode_8xxx(AvrDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcode)
  {
    switch (opcode & 0xf000) {
      case 0x8000:
        if ((opcode & 0x0200) == 0) {
          return Ld.getInstance(deviceKey,
                                opcode,
                                nextOpcode);
        } else {
//          if ((opcode & 0x2c07) == 0) {
//            return St.getOperation(opcode);
//          } else {
//            return Std.getOperation(opcode);
//          }
        }
      case 0x9000:
        switch (opcode & 0xff00) {
          case 0x9000:
          case 0x9100:
            return decode_90xx(deviceKey,
                               opcode,
                               nextOpcode);
          case 0x9200:
          case 0x9300:
            return decode_92xx(deviceKey,
                               opcode,
                               nextOpcode);
          case 0x9400:
          case 0x9500:
            return decode_94xx(deviceKey,
                               opcode,
                               nextOpcode);
          case 0x9600:
            if (deviceKey.getCore() == AvrCore.V2E) {
              return new Adiw(deviceKey,
                              opcode,
                              nextOpcode);
            }
            break;
          case 0x9700:
            if (deviceKey.getCore() == AvrCore.V2E) {
              return new Sbiw(deviceKey,
                              opcode,
                              nextOpcode);
            }
            break;
          case 0x9800:
            return new SetClearIOBit(deviceKey,
                                     opcode,
                                     nextOpcode);
//          case 0x9900:
//            return Sbic.getOperation(opcode);
          case 0x9a00:
            return new SetClearIOBit(deviceKey,
                                     opcode,
                                     nextOpcode);
//          case 0x9b00:
//            return Sbis.getOperation(opcode);
          case 0x9c00:
            if (deviceKey.getCore() == AvrCore.V2E) {
              return new Mul(deviceKey,
                             opcode,
                             nextOpcode);
            }
            break;
        }
        break;
      case 0xb000:
        switch (opcode & 0xf800) {
          case 0xb000:
          case 0xb800:
            return new InOut(deviceKey,
                             opcode,
                             nextOpcode);
        }
        break;
    }
    return null;
  }

  protected Instruction decode_90xx(AvrDeviceKey deviceKey,
                                    int opcode,
                                    int nextopcode)
  {
    switch (opcode & 0xfe0f) {
//      case 0x9000:
//        if (nextopcode == -1) {
//          throw new IOException("stream end");
//        }
//        return Lds.getOperation((opcode << 16) | nextopcode);
//      case 0x9001:
//      case 0x9002:
//        return Ld.getOperation(opcode);
//      case 0x9004:
//      case 0x9005:
//        return Lpm_Rd.getOperation(opcode);
//      case 0x9006:
//      case 0x9007:
//        return Elpm_Rd.getOperation(opcode);
//      case 0x9009:
//      case 0x900a:
//      case 0x900b:
//      case 0x900c:
//      case 0x900d:
//      case 0x900e:
//        return Ld.getOperation(opcode);
      case 0x900f:
        return new Pop(deviceKey,
                       opcode,
                       nextopcode);
    }
    return null;
  }

  protected Instruction decode_92xx(AvrDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcode)
  {
    switch (opcode & 0xfe0f) {
//      case 0x9200:
//        if (nextOpcode == -1) {
//          throw new IOException("stream end");
//        }
//        return Sts.getOperation((opcode << 16) | nextOpcode);
//      case 0x9201:
//      case 0x9202:
//        return St.getOperation(opcode);
//      case 0x9209:
//      case 0x920a:
//        return St.getOperation(opcode);
//      case 0x920c:
//      case 0x920d:
//      case 0x920e:
//        return St.getOperation(opcode);
      case 0x920f:
        return new Push(deviceKey,
                        opcode,
                        nextOpcode);
    }
    return null;
  }

  protected Instruction decode_94xx(AvrDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcode)
  {
    switch (opcode) {
      case 0x9409:
        return new IJmp(deviceKey,
                        opcode,
                        nextOpcode);
//      case 0x9419:
//        return Eijmp.getOperation();
      case 0x9408:
      case 0x9418:
      case 0x9428:
      case 0x9438:
      case 0x9448:
      case 0x9458:
      case 0x9468:
      case 0x9478:
      case 0x9488:
      case 0x9498:
      case 0x94a8:
      case 0x94b8:
      case 0x94c8:
      case 0x94d8:
      case 0x94e8:
      case 0x94f8:
        return new BitClearSet(deviceKey,
                               opcode,
                               nextOpcode);
      case 0x9508:
        return new Ret_i(deviceKey,
                         opcode,
                         nextOpcode);
      case 0x9509:
        return new ICall(deviceKey,
                         opcode,
                         nextOpcode);
      case 0x9518:
        return new Ret_i(deviceKey,
                         opcode,
                         nextOpcode);
//      case 0x9519:
//        return Eicall.getOperation();
//      case 0x9588:
//        return Sleep.getOperation();
      case 0x9598:
        return new Break(deviceKey,
                         opcode,
                         nextOpcode);
//      case 0x95a8:
//        return Wdr.getOperation();
//      case 0x95c8:
//        return Lpm.getOperation();
//      case 0x95d8:
//        return Elpm.getOperation();
//      case 0x95e8:
//        return Spm.getOperation();
    }
    switch (opcode & 0xfe0f) {
      case 0x9400:
        return new Com(deviceKey,
                       opcode,
                       nextOpcode);
      case 0x9401:
        return new Neg(deviceKey,
                       opcode,
                       nextOpcode);
      case 0x9402:
        return new Swap(deviceKey,
                        opcode,
                        nextOpcode);
      case 0x9403:
        return new Inc(deviceKey,
                       opcode,
                       nextOpcode);
      case 0x9405:
        return new Asr(deviceKey,
                       opcode,
                       nextOpcode);
      case 0x9407:
        return new Ror(deviceKey,
                       opcode,
                       nextOpcode);
      case 0x940a:
        return new Dec(deviceKey,
                       opcode,
                       nextOpcode);
    }
    switch (opcode & 0xfe0e) {
      case 0x940c:
        return new Jmp(deviceKey,
                       opcode,
                       nextOpcode);
      case 0x940e:
        return new Call(deviceKey,
                        opcode,
                        nextOpcode);
    }
    return null;
  }

  protected Instruction decode_cxxx(AvrDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcode)
  {
    switch (opcode & 0xf000) {
//      case 0xc000:
//        return Rjmp.getOperation(opcode);
      case 0xd000:
        return new Rcall(deviceKey,
                         opcode,
                         nextOpcode);
      case 0xe000:
//        if ((opcode & Ldi.OPCODE) == Ldi.OPCODE) {
//          return new Ldi(opcode);
//        }
      //return Ldi.getOperation(opcode);      //return Ldi.getOperation(opcode);
    }
    switch (opcode & 0xfc00) {
      case 0xf000:
      case 0xf400:
        return new BranchInstruction(deviceKey,
                                     opcode,
                                     nextOpcode);
    }
    switch (opcode & 0xfe00) {
      case 0xf800:
        return new Bld(deviceKey,
                       opcode,
                       nextOpcode);
      case 0xfa00:
        return new Bst(deviceKey,
                       opcode,
                       nextOpcode);
//      case 0xfc00:
//        return Sbrc.getOperation(opcode);
//      case 0xfe00:
//        return Sbrs.getOperation(opcode);
    }
    return null;
  }

}
