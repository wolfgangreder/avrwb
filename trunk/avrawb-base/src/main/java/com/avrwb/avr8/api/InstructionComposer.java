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
package com.avrwb.avr8.api;

/**
 *
 * @author wolfi
 */
public final class InstructionComposer
{

  public static int composeOpcode_Rd_Rr(int opcode,
                                        int rd,
                                        int rr)
  {
    if ((opcode & ~0xfc00) != 0) {
      throw new IllegalArgumentException("invalid rd,rr opcode" + Integer.toHexString(opcode));
    }
    if (rd > 31 || rd < 0) {
      throw new IllegalArgumentException("invalid register r" + rd);
    }
    if (rr > 31 || rr < 0) {
      throw new IllegalArgumentException("invalid register r" + rr);
    }
    return opcode | (rd << 4) | ((rr & 0x10) << 5) | (rr & 0xf);
  }

  public static int composeOpcode_Rdl_K6(int baseOpcode,
                                         int rdl,
                                         int k6)
  {
    if ((baseOpcode & ~0xff00) != 0) {
      throw new IllegalArgumentException("invalid base opcode");
    }
    if (rdl < 24 || rdl > 30 || ((rdl % 2) != 0)) {
      throw new IllegalArgumentException("invalid rdl");
    }
    if (k6 < 0 || k6 > 63) {
      throw new IllegalArgumentException("invalid k6");
    }
    return baseOpcode | ((rdl - 24) << 3) | ((k6 & 0x30) << 2) | (k6 & 0xf);
  }

  public static int composeOpcode_Rd_K8(int opcode,
                                        int rd,
                                        int k8)
  {
    if ((opcode & ~0xf000) != 0) {
      throw new IllegalArgumentException("invalid rd,rr opcode" + Integer.toHexString(opcode));
    }
    if (rd > 31 || rd < 16) {
      throw new IllegalArgumentException("invalid register r" + rd);
    }
    if (k8 < 0 || k8 > 0xff) {
      throw new IllegalArgumentException("invalid k8 " + k8);
    }
    return opcode | ((k8 & 0xf0) << 4) | (k8 & 0xf) | ((rd - 16) << 4);
  }

  public static int composeOpcode_Rd(int baseOpcode,
                                     int rd)
  {
    if ((baseOpcode & ~0xfe0f) != 0) {
      throw new IllegalArgumentException("invalid base opcode");
    }
    if (rd < 0 || rd > 31) {
      throw new IllegalArgumentException("invalid rd");
    }
    return baseOpcode | (rd << 4);
  }

  public static int composeOpcode_Bclr_Bset(int baseOpcode,
                                            boolean setBit,
                                            int bit)
  {
    if ((baseOpcode & ~0xff8f) != 0) {
      throw new IllegalArgumentException("invalid base opcode");
    }
    if (bit < 0 || bit > 7) {
      throw new IllegalArgumentException("invalid bit");
    }
    return baseOpcode | (bit << 4) | (setBit ? 0x0 : 0x80);
  }

  public static int composeOpcode_Rd_b(int baseOpcode,
                                       int rd,
                                       int b)
  {
    if ((baseOpcode & ~0xfe08) != 0) {
      throw new IllegalArgumentException("invalid rd,b opcode" + Integer.toHexString(baseOpcode));
    }
    if (rd > 31 || rd < 0) {
      throw new IllegalArgumentException("invalid register r" + rd);
    }
    if (b > 7 || b < 0) {
      throw new IllegalArgumentException("invalid bit index " + b);
    }
    return baseOpcode | (rd << 4) | (b);
  }

  public static int composeOpcode_b_k7(int baseOpcode,
                                       int bit,
                                       int k7)
  {
    if ((baseOpcode & ~0xfc00) != 0) {
      throw new IllegalArgumentException("invalid opcode");
    }
    if (bit < 0 || bit > 7) {
      throw new IllegalArgumentException("invalid bit");
    }
    if (k7 < -64 || k7 > 63) {
      throw new IllegalArgumentException("invalid offset");
    }
    return baseOpcode | bit | ((k7 & 0x7f) << 3);
  }

  public static int composeOpcode_K22(int baseOpcode,
                                      int k22)
  {
    if ((baseOpcode & ~0xfe0e) != 0) {
      throw new IllegalArgumentException("invalid baseopcode");
    }
    if (k22 < 0 || k22 > 0x3fffff) {
      throw new IllegalArgumentException("invalid k22");
    }
    return (baseOpcode << 16) | (k22 & 0x1ffff) | ((k22 & 0x3e0000) << 3);
  }

  public static final int composeOpcode_P_b(int baseOpcode,
                                            int io,
                                            int b)
  {
    if ((baseOpcode & ~0xff00) != 0) {
      throw new IllegalArgumentException("invalid opcode");
    }
    if (io < 0 || io > 31) {
      throw new IllegalArgumentException("invalid io");
    }
    if (b < 0 || b > 7) {
      throw new IllegalArgumentException("invalid bit");
    }
    return baseOpcode | b | (io << 3);
  }

  public static final int composeOpcode_K4(int baseOpcode,
                                           int k4)
  {
    if ((baseOpcode & ~0xff0f) != 0) {
      throw new IllegalArgumentException("invalid base opcode");
    }
    if (k4 < 0 || k4 > 15) {
      throw new IllegalArgumentException("invalid k4");
    }
    return baseOpcode | (k4 << 4);
  }

  public static int composeOpcode_Rdh23_Rrh23(int baseOpcode,
                                              int rdh,
                                              int rrh)
  {
    if ((baseOpcode & ~0xff88) != 0) {
      throw new IllegalArgumentException("invalid base opcode");
    }
    if (rdh < 16 || rdh > 23) {
      throw new IllegalArgumentException("invalid rdh");
    }
    if (rrh < 16 || rrh > 23) {
      throw new IllegalArgumentException("invalid rrh");
    }
    return baseOpcode | ((rdh & 0x7) << 4) | (rrh & 0x7);
  }

  public static int composeOpcode_Rd_P(int baseOpcode,
                                       int rd,
                                       int io)
  {
    if ((baseOpcode & ~0xf800) != 0) {
      throw new IllegalArgumentException("invalid opcode");
    }
    if (rd < 0 || rd > 31) {
      throw new IllegalArgumentException("invalid rd");
    }
    if (io < 0 || io > 63) {
      throw new IllegalArgumentException("invalid io");
    }
    return baseOpcode | (rd << 4) | (io & 0xf) | ((io & 0x30) << 5);
  }

  public static int composeOpcode_Rd_K16(int baseOpcode,
                                         int rdAddress,
                                         int k16)
  {
    if ((baseOpcode & ~0xfe0f) != 0) {
      throw new IllegalArgumentException("illegal base opcode");
    }
    if (rdAddress < 0 || rdAddress > 31) {
      throw new IllegalArgumentException("illegal rd");
    }
    if (k16 < 0 || k16 > 0xffff) {
      throw new IllegalArgumentException("illegal k16");
    }
    return (baseOpcode << 16) | ((rdAddress) << 20) | k16;
  }

  public static int composeOpcode_Rdh_Rrh(int baseOpcode,
                                          int rdh,
                                          int rrh)
  {
    if ((baseOpcode & ~0xff00) != 0) {
      throw new IllegalArgumentException("invalid base opcode");
    }
    if (rdh < 16 || rdh > 32) {
      throw new IllegalArgumentException("invalid rdh");
    }
    if (rrh < 16 || rrh > 32) {
      throw new IllegalArgumentException("invalid rrh");
    }
    return baseOpcode | ((rdh & 0xf) << 4) | (rrh & 0xf);
  }

  public static int composeOpcode_k12(int baseOpcode,
                                      int k12)
  {
    if ((baseOpcode & ~0xf000) != 0) {
      throw new IllegalArgumentException("illegal baseopcode");
    }
    if (k12 < -2048 || k12 >= 2048) {
      throw new IllegalArgumentException("illegal k12");
    }
    return baseOpcode | (k12 & 0x0fff);
  }

  private InstructionComposer()
  {
  }

}
