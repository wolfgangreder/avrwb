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
import at.reder.avrwb.annotations.NotThreadSave;
import at.reder.avrwb.avr8.AVRDeviceKey;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.Memory;
import at.reder.avrwb.avr8.api.Instruction;
import at.reder.avrwb.avr8.api.InstructionDecoder;
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
import at.reder.avrwb.avr8.api.instructions.IJmp;
import at.reder.avrwb.avr8.api.instructions.InOut;
import at.reder.avrwb.avr8.api.instructions.Inc;
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
import at.reder.avrwb.avr8.helper.InstructionNotAvailableException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
@NotThreadSave
public class DefaultInstructionDecoder implements InstructionDecoder
{

  private final Map<Integer, Instruction> instructionCache = new HashMap<>();

  public DefaultInstructionDecoder()
  {
  }

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
      throw new InstructionNotAvailableException("Unknown opcode " + HexIntAdapter.toHexString(word,
                                                                                               4));
    }
    return result;
  }

  protected Instruction decodeInstruction(AVRDeviceKey deviceKey,
                                          int opcode,
                                          int nextOpcode)
  {
    switch (opcode & 0xc000) {
      case 0x0000:
        return decode_0_3fff(deviceKey,
                             opcode,
                             nextOpcode);
      case 0x4000:
        return decode_4_3fff(deviceKey,
                             opcode,
                             nextOpcode);
      case 0x8000:
        return decode_8_3fff(deviceKey,
                             opcode,
                             nextOpcode);
      case 0xc000:
        return decode_c_3fff(deviceKey,
                             opcode,
                             nextOpcode);
    }
    return null;
  }

  /*
     decodiert 00xx xxxx xxxx xxxx
   */
  private Instruction decode_0_3fff(AVRDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcde)
  {

    switch (opcode & 0x3000) {
      case 0x0000:
        // 0000 xxxx xxxx xxxx
        switch (opcode & 0x0c00) {
          case 0x0000:
            return new Nop(opcode);
          case 0x0c00:
            // 0000 11xx xxxx xxxx
            return new Add(opcode);
          case 0x0800:
            // 0000 10xx xxxx xxxx
            return new Sbc(opcode);
          case 0x0400:
            // 0000 01xx xxxx xxxx
            return new Cpc(opcode);
        }
        break;
      case 0x1000:
        // 0001 xxxx xxxx xxxx
        switch (opcode & 0x0c00) {
          case 0x0000:
            return new Cpse(opcode);
          case 0x0400:
            return new Cp(opcode);
          case 0x0800:
            return new Sub(opcode);
          case 0x0c00:
            return new Adc(opcode);
        }
        break;
      case 0x2000:
        // 0010 xxxx xxxx xxxx
        switch (opcode & 0x0c00) {
          case 0x0000:
            return new And(opcode);
          case 0x0400:
            return new Eor(opcode);
          case 0x0800:
            return new Or(opcode);
          case 0x0c00:
            return new Mov(opcode);
        }
        break;
      case 0x3000:
        return new Cpi(opcode);

    }
    return null;
  }

  /*
     decodiert 01xx xxxx xxxx xxxx
   */
  private Instruction decode_4_3fff(AVRDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcde)
  {
    switch (opcode & 0x3000) {
      case 0x0000:
      case 0x1000:
        // 01x0 xxxx xxxx xxxx
        return new Sbci_Subi(opcode);
      case 0x2000:
        // 0110 xxxx xxxx xxxx
        return new Sbr(opcode);
      case 0x3000:
        // 0111 xxxx xxxx xxxx
        return new Andi(opcode);
    }
    return null;
  }

  /*
     decodiert 10xx xxxx xxxx xxxx
   */
  private Instruction decode_8_3fff(AVRDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcde)
  {
    switch (opcode & 0x3000) {
      case 0x0000:
        // 1000 xxxx xxxx xxxx
        if ((opcode & 0x0200) == 0) {
          return Ld.getInstance(deviceKey,
                                opcode);
        } else {
//          return St.getInstance(deviceKey,opcode);
          return null;
        }
      case 0x1000:
        //1001 xxxx xxxx xxxx
        return decode_9_0fff(deviceKey,
                             opcode,
                             nextOpcde);
      case 0x2000:
        //1010 xxxx xxxx xxxx
        if ((opcode & 0x0200) == 0) {
          return Ld.getInstance(deviceKey,
                                opcode);
        } else {
//          return St.getInstance(deviceKey,opcode);
          return null;
        }
      case 0x3000:
        // 1011 xxxx xxxx xxxx
        return new InOut(opcode);
    }
    return null;
  }

  /*
    decodiert 1001 xxxx xxxx xxxx
   */
  private Instruction decode_9_0fff(AVRDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcode)
  {
    if ((opcode & 0xfc00) == Mul.OPCODE) {
      return new Mul(opcode);
    } else {
      switch (opcode & 0xff9f) {
        case 0x9508:
        case 0x9518:
          return new Ret_i(opcode);
      }
      switch (opcode & 0xfe0f) {
        case 0x9000:
//          return new Lds(opcode,nextOpcode);
          return null;
        case 0x9001:
        case 0x9002:
        case 0x9008:
        case 0x9009:
        case 0x900a:
        case 0x900c:
        case 0x900d:
        case 0x900e:
          return Ld.getInstance(deviceKey,
                                opcode);
        case 0x900f:
          return new Pop(opcode);
        case 0x9101:
        case 0x9102:
        case 0x9108:
        case 0x9109:
        case 0x910a:
        case 0x910c:
        case 0x910d:
        case 0x910e:
//          return St.getInstance(deviceKey,
//                                opcode);
          return null;
        case 0x9200:
//          return new Sts(opcode,nextOpcode);
          return null;
        case 0x920f:
          return new Push(opcode);
        case 0x9400:
          return new Com(opcode);
        case 0x9401:
          return new Neg(opcode);
        case 0x9402:
          return new Swap(opcode);
        case 0x9403:
          return new Inc(opcode);
        case 0x9405:
          return new Asr(opcode);
        case 0x9406:
//          return new Lsr(opcode);
          return null;
        case 0x9407:
          return new Ror(opcode);
        case 0x940a:
          return new Dec(opcode);
      }
      switch (opcode & 0xff00) {
        case 0x9600:
          return new Adiw(opcode);
        case 0x9700:
          return new Sbiw(opcode);
        case 0x9800:
        case 0x9a00:
          return new SetClearIOBit(opcode);
        case 0x9900:
//          return new Sbic(opcode);
          return null;
        case 0x9b00:
//          return new Sbis(opcode);
          return null;
      }
      switch (opcode & 0xff0f) {
        case 0x9408:
        case 0x9808:
          return new BitClearSet(opcode);
        case 0x940c:
        case 0x940d:
        case 0x950c:
        case 0x950d:
          return new Jmp(opcode,
                         nextOpcode);
        case 0x940e:
        case 0x940f:
        case 0x950e:
        case 0x950f:
          return new Call(opcode,
                          nextOpcode);
      }
      switch (opcode) {
        case 0x9409:
          return new IJmp();
        case 0x9509:
          return new ICall();
        case 0x9588:
//          return new Sleep();
          return null;
        case 0x9598:
          return new Break();
        case 0x95a8:
//          return new Wdr();
          return null;
        case 0x95c8:
//          return new Lpm(opcode);
          return null;
      }
    }
    return null;
  }

  /*
     decodiert 11xx xxxx xxxx xxxx
   */
  private Instruction decode_c_3fff(AVRDeviceKey deviceKey,
                                    int opcode,
                                    int nextOpcde)
  {
    switch (opcode & 0x3000) {
      case 0x0000:
        // 1100 xxxx xxxx xxxx
//        return new Rjmp(opcode);
        return null;
      case 0x1000:
        // 1101 xxxx xxxx xxxx
        return new Rcall(opcode);
      case 0x2000:
        // 1110 xxxx xxxx xxxx
        return new Ldi(opcode);
      case 0x3000:
        // 1111 xxxx xxxx xxxx
        switch (opcode & 0x0c00) {
          case 0x0000:
          // 1111 00xx xxxx xxxx
          case 0x0400:
            // 1111 01xx xxxx xxxx
            return new BranchInstruction(opcode);
          case 0x0800:
            // 1111 10xx xxxx xxxx
            if ((opcode & 0x0200) == 0) {
              return new Bld(opcode);
            } else {
              return new Bst(opcode);
            }
          case 0x0c00:
            // 1111 11xx xxxx xxxx
            if ((opcode & 0x0200) == 0) {
//              return new Sbrc(opcode);
            } else {
//              return new Sbrs(opcode);
            }
        }
    }
    return null;
  }

}
