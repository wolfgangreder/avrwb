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

import com.avrwb.avr8.api.instructions.Des;
import com.avrwb.avr8.api.instructions.EICall;
import com.avrwb.avr8.api.instructions.EIJmp;
import com.avrwb.avr8.api.instructions.Elpm;
import com.avrwb.avr8.api.instructions.Fmul;
import com.avrwb.avr8.api.instructions.Fmuls;
import com.avrwb.avr8.api.instructions.Fmulsu;
import com.avrwb.avr8.api.instructions.Lac;
import com.avrwb.avr8.api.instructions.Las;
import com.avrwb.avr8.api.instructions.Lat;
import com.avrwb.avr8.api.instructions.Lds16;
import com.avrwb.avr8.api.instructions.Lpm;
import com.avrwb.avr8.api.instructions.Movw;
import com.avrwb.avr8.api.instructions.Mul;
import com.avrwb.avr8.api.instructions.Muls;
import com.avrwb.avr8.api.instructions.Mulsu;
import com.avrwb.avr8.api.instructions.Spm;
import com.avrwb.avr8.api.instructions.Sts16;
import com.avrwb.avr8.api.instructions.Xch;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author wolfi
 */
public class InstructionDecoder_V1 extends BaseInstructionDecoder
{

  private static final Set<Class<?>> NOT_SUPPORTED_CLASSES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
          Lds16.class,
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
          Sts16.class,
          Movw.class,
          Des.class,
          Xch.class
  )));

  @Override
  protected boolean acceptDescriptor(Descriptor d)
  {
    if (NOT_SUPPORTED_CLASSES.contains(d.getInstruction())) {
      return false;
    } else if (Lpm.class == d.getInstruction()) {
      return d.getOpcodeMask() == Lpm.OPCODE_MASK_LPM && d.getBaseOpcode() == Lpm.OPCODE_LPM;
    }
    return true;
  }

}
