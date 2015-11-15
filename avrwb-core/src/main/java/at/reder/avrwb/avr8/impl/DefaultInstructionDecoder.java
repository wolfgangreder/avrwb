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
import at.reder.avrwb.avr8.AVRDeviceKey;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.Memory;
import at.reder.avrwb.avr8.api.Instruction;
import at.reder.avrwb.avr8.api.InstructionDecoder;
import at.reder.avrwb.avr8.api.instructions.Adc;
import at.reder.avrwb.avr8.api.instructions.Add;
import at.reder.avrwb.avr8.api.instructions.And;
import at.reder.avrwb.avr8.api.instructions.Andi;
import at.reder.avrwb.avr8.api.instructions.Bld;
import at.reder.avrwb.avr8.api.instructions.Bst;
import at.reder.avrwb.avr8.api.instructions.Cp;
import at.reder.avrwb.avr8.api.instructions.Cpc;
import at.reder.avrwb.avr8.api.instructions.Cpi;
import at.reder.avrwb.avr8.api.instructions.Cpse;
import at.reder.avrwb.avr8.api.instructions.Eor;
import at.reder.avrwb.avr8.api.instructions.Ld;
import at.reder.avrwb.avr8.api.instructions.Mov;
import at.reder.avrwb.avr8.api.instructions.Nop;
import at.reder.avrwb.avr8.api.instructions.Or;
import at.reder.avrwb.avr8.api.instructions.Sbc;
import at.reder.avrwb.avr8.api.instructions.Sbci_Subi;
import at.reder.avrwb.avr8.api.instructions.Sbr;
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
        if ((opcode & 0x200) == 0) {
          return Ld.getInstance(deviceKey,
                                opcode);
        } else {
//          return St.getInstance(deviceKey,opcode);
          return null;
        }
      case 0x1000:
        //1001 xxxx xxxx xxxx
        // noch nict belegt
        return null;
      case 0x2000:
        //1010 xxxx xxxx xxxx
        if ((opcode & 0x200) == 0) {
          return Ld.getInstance(deviceKey,
                                opcode);
        } else {
//          return St.getInstance(deviceKey,opcode);
          return null;
        }
      case 0x3000:
        // 1011 xxxx xxxx xxxx
        if ((opcode & 0x800) == 0) {
//          return new In(opcode);
        } else {
//          return new Out(opcode);
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
      case 0x1000:
      // 1101 xxxx xxxx xxxx
      case 0x2000:
      // 1110 xxxx xxxx xxxx
      case 0x3000:
        // 1111 xxxx xxxx xxxx
        switch (opcode & 0x0c00) {
          case 0x0000:
            // 1111 00xx xxxx xxxx
//            return new Brbs(opcode);
            return null;
          case 0x0400:
            // 1111 01xx xxxx xxxx
//            return new Brbc(opcode);
            return null;
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
