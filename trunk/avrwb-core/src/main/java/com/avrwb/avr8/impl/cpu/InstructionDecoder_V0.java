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
package com.avrwb.avr8.impl.cpu;

import com.avrwb.avr8.impl.instructions.Adiw;
import com.avrwb.avr8.impl.instructions.Call;
import com.avrwb.avr8.impl.instructions.Des;
import com.avrwb.avr8.impl.instructions.EICall;
import com.avrwb.avr8.impl.instructions.EIJmp;
import com.avrwb.avr8.impl.instructions.Elpm;
import com.avrwb.avr8.impl.instructions.Fmul;
import com.avrwb.avr8.impl.instructions.Fmuls;
import com.avrwb.avr8.impl.instructions.Fmulsu;
import com.avrwb.avr8.impl.instructions.ICall;
import com.avrwb.avr8.impl.instructions.IJmp;
import com.avrwb.avr8.impl.instructions.Jmp;
import com.avrwb.avr8.impl.instructions.Lac;
import com.avrwb.avr8.impl.instructions.Las;
import com.avrwb.avr8.impl.instructions.Lat;
import com.avrwb.avr8.impl.instructions.Ld;
import com.avrwb.avr8.impl.instructions.Lds;
import com.avrwb.avr8.impl.instructions.Lds16;
import com.avrwb.avr8.impl.instructions.Lpm;
import com.avrwb.avr8.impl.instructions.Movw;
import com.avrwb.avr8.impl.instructions.Mul;
import com.avrwb.avr8.impl.instructions.Muls;
import com.avrwb.avr8.impl.instructions.Mulsu;
import com.avrwb.avr8.impl.instructions.Pop;
import com.avrwb.avr8.impl.instructions.Push;
import com.avrwb.avr8.impl.instructions.Sbiw;
import com.avrwb.avr8.impl.instructions.Spm;
import com.avrwb.avr8.impl.instructions.St;
import com.avrwb.avr8.impl.instructions.Sts;
import com.avrwb.avr8.impl.instructions.Sts16;
import com.avrwb.avr8.impl.instructions.Xch;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author wolfi
 */
public class InstructionDecoder_V0 extends BaseInstructionDecoder
{

  private static final Set<Class<?>> NOT_SUPPORTED_CLASSES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
          Lds.class,
          Lds16.class,
          Lpm.class,
          Pop.class,
          Push.class,
          Adiw.class,
          Sbiw.class,
          IJmp.class,
          ICall.class,
          Call.class,
          Jmp.class,
          EICall.class,
          EIJmp.class,
          Elpm.class,
          Fmul.class,
          Fmuls.class,
          Fmulsu.class,
          Lac.class,
          Lat.class,
          Las.class,
          Mul.class,
          Muls.class,
          Mulsu.class,
          Spm.class,
          Sts.class,
          Sts16.class,
          Movw.class,
          Des.class,
          Xch.class
  )));

  @Override
  protected boolean filterCandicates(int opcode,
                                     int nextOpcode,
                                     List<? extends Descriptor> candicates)
  {
    Iterator<? extends Descriptor> iter = candicates.iterator();
    while (iter.hasNext()) {
      Descriptor d = iter.next();
      if (Ld.class == d.getInstruction()) {
        if ((opcode & Ld.OPCODE_MASK) != Ld.OPCODE_LD_Z) {
          iter.remove();
        }
      } else if (St.class == d.getInstruction()) {
        if ((opcode & St.OPCODE_MASK) != St.OPCODE_ST_Z) {
          iter.remove();
        }
      }
    }
    return !candicates.isEmpty();
  }

  @Override
  protected boolean acceptDescriptor(Descriptor d)
  {
    if (NOT_SUPPORTED_CLASSES.contains(d.getInstruction())) {
      return false;
    } else if (Ld.class == d.getInstruction()) {
      return d.getOpcodeMask() == Ld.OPCODE_MASK && d.getBaseOpcode() == Ld.OPCODE_LD_Z;
    } else if (St.class == d.getInstruction()) {
      return d.getOpcodeMask() == St.OPCODE_MASK && d.getBaseOpcode() == St.OPCODE_ST_Z;
    }
    return true;
  }

}
