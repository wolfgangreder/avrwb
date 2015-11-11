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
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.Memory;
import at.reder.avrwb.avr8.api.Instruction;
import at.reder.avrwb.avr8.api.InstructionDecoder;
import at.reder.avrwb.avr8.api.instructions.Adc;
import at.reder.avrwb.avr8.api.instructions.Add;
import at.reder.avrwb.avr8.api.instructions.And;
import at.reder.avrwb.avr8.api.instructions.Eor;
import at.reder.avrwb.avr8.api.instructions.Instruction_Rd_Rr;
import at.reder.avrwb.avr8.api.instructions.Mov;
import at.reder.avrwb.avr8.api.instructions.Nop;
import at.reder.avrwb.avr8.api.instructions.Or;
import at.reder.avrwb.avr8.api.instructions.Sbc;
import at.reder.avrwb.avr8.api.instructions.Sub;
import at.reder.avrwb.avr8.helper.InstructionNotAvailableException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public class DefaultInstructionDecoder implements InstructionDecoder
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
                                                          (Integer opcode) -> decodeInstruction(opcode,
                                                                                                nextWord));
    if (result == null) {
      throw new InstructionNotAvailableException("Unknown opcode " + HexIntAdapter.toHexString(word,
                                                                                               4));
    }
    return result;
  }

  protected Instruction decodeInstruction(int opcode,
                                          int nextOpcode)
  {
    switch (opcode & 0xc000) {
      case 0x0000:
        return decode_0xxx(opcode);
      case 0x4000:
        return decode_4xxx(opcode);
      case 0x8000:
        return decode_8xxx(opcode,
                           nextOpcode);
      case 0xc000:
        return decode_cxxx(opcode);
    }
    return null;
  }

  protected Instruction decode_0xxx(int opcode)
  {
    switch (opcode & 0x3000) {
      case 0x0000:
        switch (opcode & 0x0c00) {
          case 0x0000:
            if (opcode == 0) {
              return new Nop();
//            } else if ((opcode & Operation_Rd_Rr.MASK) == Movw.OPCODE) {
//              return Movw.getOperation(opcode);
//            } else {
//              // hier fehlen die (F)MUL(SU) befehle
            }
            break;
//          case 0x0400:
//            if ((opcode & Operation_Rd_Rr.MASK) == Cpc.OPCODE) {
//              return Cpc.getOperation(opcode);
//            }
//            break;
          case 0x0800:
            if ((opcode & Instruction_Rd_Rr.MASK) == Sbc.OPCODE) {
              return new Sbc(opcode);
            }
            break;
          case 0x0c00:
            return new Add(opcode);
        }
        break;
      case 0x1000:
        return decode_1xxx(opcode);
      case 0x2000:
        return decode_2xxx(opcode);
//      case 0x3000:
//        return Cpi.getOperation(opcode);
    }
    return null;
  }

  protected Instruction decode_1xxx(int opcode)
  {
    switch (opcode & 0x0c00) {
//      case 0x0000:
//        return Cpse.getOperation(opcode);
//      case 0x0400:
//        return Cp.getOperation(opcode);
      case 0x0800:
        return new Sub(opcode);
      case 0x0c00:
        return new Adc(opcode);
    }
    return null;
  }

  protected Instruction decode_2xxx(int opcode)
  {
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
    return null;
  }

  protected Instruction decode_4xxx(int opcode)
  {
    switch (opcode & 0xf000) {
//      case 0x4000:
//        return Sbci.getOperation(opcode);
//      case 0x5000:
//        return Subi.getOperation(opcode);
//      case 0x6000:
//        return Ori.getOperation(opcode);
//      case 0x7000:
//        return Andi.getOperation(opcode);
    }
    return null;
  }

  protected Instruction decode_8xxx(int opcode,
                                    int nextopcode)
  {
    switch (opcode & 0xf000) {
//      case 0x8000:
//        if ((opcode & 0x0200) == 0) {
//          if ((opcode & 0x2c07) == 0) {
//            return Ld.getOperation(opcode);
//          } else {
//            return Ldd.getOperation(opcode);
//          }
//        } else {
//          if ((opcode & 0x2c07) == 0) {
//            return St.getOperation(opcode);
//          } else {
//            return Std.getOperation(opcode);
//          }
//        }
      case 0x9000:
        switch (opcode & 0xff00) {
//          case 0x9000:
//          case 0x9100:
//            return decode_90xx(opcode, nextopcode);
//          case 0x9200:
//          case 0x9300:
//            return decode_92xx(opcode, nextopcode);
//          case 0x9400:
//          case 0x9500:
//            return decode_94xx(opcode, nextopcode);
//          case 0x9600:
//            return Adiw.getOperation(opcode);
//          case 0x9700:
//            return Sbiw.getOperation(opcode);
//          case 0x9800:
//            return Cbi.getOperation(opcode);
//          case 0x9900:
//            return Sbic.getOperation(opcode);
//          case 0x9a00:
//            return Sbi.getOperation(opcode);
//          case 0x9b00:
//            return Sbis.getOperation(opcode);
//          case 0x9c00:
//            return Mul.getOperation(opcode);
        }
        break;
      case 0xb000:
        switch (opcode & 0xf800) {
//          case 0xb000:
//            return In.getOperation(opcode);
//          case 0xb800:
//            return Out.getOperation(opcode);
        }
        break;
    }
    return null;
  }

  protected Instruction decode_90xx(int opcode,
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
//      case 0x900f:
//        return Pop.getOperation(opcode);
    }
    return null;
  }

  protected Instruction decode_92xx(int opcode,
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
//      case 0x920f:
//        return Push.getOperation(opcode);
    }
    return null;
  }

  protected Instruction decode_94xx(int opcode,
                                    int nextOpcode)
  {
    switch (opcode) {
//      case 0x9409:
//        return Ijmp.getOperation();
//      case 0x9419:
//        return Eijmp.getOperation();
//      case 0x9408:
//        return Sec.getOperation();
//      case 0x9418:
//        return Sez.getOperation();
//      case 0x9428:
//        return Sen.getOperation();
//      case 0x9438:
//        return Sev.getOperation();
//      case 0x9448:
//        return Ses.getOperation();
//      case 0x9458:
//        return Seh.getOperation();
//      case 0x9468:
//        return Set.getOperation();
//      case 0x9478:
//        return Sei.getOperation();
//      case 0x9488:
//        return Clc.getOperation();
//      case 0x9498:
//        return Clz.getOperation();
//      case 0x94a8:
//        return Cln.getOperation();
//      case 0x94b8:
//        return Clv.getOperation();
//      case 0x94c8:
//        return Cls.getOperation();
//      case 0x94d8:
//        return Clh.getOperation();
//      case 0x94e8:
//        return Clt.getOperation();
//      case 0x94f8:
//        return Cli.getOperation();
//      case 0x9508:
//        return Ret.getOperation(opcode);
//      case 0x9509:
//        return Icall.getOperation();
//      case 0x9518:
//        return Ret.getOperation(opcode);
//      case 0x9519:
//        return Eicall.getOperation();
//      case 0x9588:
//        return Sleep.getOperation();
//      case 0x9598:
//        return Break.getOperation();
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
//      case 0x9400:
//        return Com.getOperation(opcode);
//      case 0x9401:
//        return Neg.getOperation(opcode);
//      case 0x9402:
//        return Swap.getOperation(opcode);
//      case 0x9403:
//        return Inc.getOperation(opcode);
//      case 0x9405:
//        return Asr.getOperation(opcode);
//      case 0x9406:
//        return Lsr.getOperation(opcode);
//      case 0x9407:
//        return Ror.getOperation(opcode);
//      case 0x940a:
//        return Dec.getOperation(opcode);
    }
    switch (opcode & 0xfe0e) {
//      case 0x940c:
//        if (nextOpcode == -1) {
//          throw new IOException("stream end");
//        }
//        return Jmp.getOperation((opcode << 16) | nextOpcode);
//      case 0x940e:
//        if (nextOpcode == -1) {
//          throw new IOException("stream end");
//        }
//        return Call.getOperation((opcode << 16) | nextOpcode);
    }
    return null;
  }

  protected Instruction decode_cxxx(int opcode)
  {
    switch (opcode & 0xf000) {
//      case 0xc000:
//        return Rjmp.getOperation(opcode);
//      case 0xd000:
//        return Rcall.getOperation(opcode);
//      case 0xe000:
//        return Ldi.getOperation(opcode);
    }
    switch (opcode & 0xfc00) {
//      case 0xf000:
//        return Brbs.getOperation(opcode);
//      case 0xf400:
//        return Brbc.getOperation(opcode);
    }
    switch (opcode & 0xfe00) {
//      case 0xf800:
//        return Bld.getOperation(opcode);
//      case 0xfa00:
//        return Bst.getOperation(opcode);
//      case 0xfc00:
//        return Sbrc.getOperation(opcode);
//      case 0xfe00:
//        return Sbrs.getOperation(opcode);
    }
    return null;
  }

}
