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

import com.avrwb.avr8.api.instructions.Adiw;
import com.avrwb.avr8.api.instructions.Call;
import com.avrwb.avr8.api.instructions.EICall;
import com.avrwb.avr8.api.instructions.EIJmp;
import com.avrwb.avr8.api.instructions.Elpm;
import com.avrwb.avr8.api.instructions.Fmul;
import com.avrwb.avr8.api.instructions.Fmuls;
import com.avrwb.avr8.api.instructions.Fmulsu;
import com.avrwb.avr8.api.instructions.ICall;
import com.avrwb.avr8.api.instructions.IJmp;
import com.avrwb.avr8.api.instructions.Jmp;
import com.avrwb.avr8.api.instructions.Lac;
import com.avrwb.avr8.api.instructions.Las;
import com.avrwb.avr8.api.instructions.Lat;
import com.avrwb.avr8.api.instructions.Ld;
import com.avrwb.avr8.api.instructions.Lds;
import com.avrwb.avr8.api.instructions.Lds16;
import com.avrwb.avr8.api.instructions.Lpm;
import com.avrwb.avr8.api.instructions.Mul;
import com.avrwb.avr8.api.instructions.Muls;
import com.avrwb.avr8.api.instructions.Mulsu;
import com.avrwb.avr8.api.instructions.Pop;
import com.avrwb.avr8.api.instructions.Push;
import com.avrwb.avr8.api.instructions.Sbiw;
import com.avrwb.avr8.api.instructions.Spm;
import com.avrwb.avr8.api.instructions.St;
import com.avrwb.avr8.api.instructions.Sts;
import com.avrwb.avr8.api.instructions.Sts16;
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
          Sts16.class
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
        if (opcode != Ld.OPCODE_LD_Z) {
          iter.remove();
        }
      } else if (St.class == d.getInstruction()) {
        if (opcode != St.OPCODE_ST_Z) {
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
