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
package com.avrwb.avr8.api.instructions;

import com.avrwb.avr8.Device;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AVRWBDefaults;
import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
public abstract class AbstractCp extends Instruction_Rd_Rr
{

  private final boolean withCarry;

  protected AbstractCp(int opcode,
                       String mnemonic,
                       boolean withCarry)
  {
    super(opcode,
          mnemonic);
    this.withCarry = withCarry;
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    SREG sreg = device.getCPU().getSREG();
    int oldSreg = sreg.getValue();
    performSub(sreg,
               rdVal,
               rrVal,
               withCarry);
    if (oldSreg != sreg.getValue()) {
      resultBuilder.addModifiedRegister(sreg);
    }
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    if (AVRWBDefaults.isDebugLoggingActive()) {
      device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                             () -> MessageFormat.format("{0} compared r{1,number,0} with r{2,number,0} -> {3}",
                                                        getCurrentDeviceMessage(clockState,
                                                                                device),
                                                        rdAddress,
                                                        rrAddress,
                                                        sreg.toString()));
    }

  }

}
