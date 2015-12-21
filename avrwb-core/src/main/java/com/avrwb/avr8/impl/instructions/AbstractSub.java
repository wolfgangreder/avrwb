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
package com.avrwb.avr8.impl.instructions;

import com.avrwb.avr8.Device;
import com.avrwb.avr8.Memory;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.InstructionResultBuilder;

/**
 *
 * @author wolfi
 */
public abstract class AbstractSub extends Instruction_Rd_Rr
{

  private final boolean withCarry;

  protected AbstractSub(int opcode,
                        String mnemoic,
                        boolean withCarry)
  {
    super(opcode,
          mnemoic);
    this.withCarry = withCarry;
  }

  @Override
  protected void doExecute(ClockDomain clockDomain,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    SREG sreg = device.getCPU().getSREG();
    int oldSreg = sreg.getValue();
    int result = performSub(sreg,
                            rdVal,
                            rrVal,
                            withCarry);
    if (result != rdVal) {
      Memory sram = device.getSRAM();
      resultBuilder.addModifiedDataAddresses(rdAddress);
      sram.setByteAt(rdAddress,
                     result);
    }
    if (oldSreg != sreg.getValue()) {
      resultBuilder.addModifiedRegister(sreg);
    }
    device.getSRAM().setByteAt(rdAddress,
                               result);
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    logExecutionResult(clockDomain,
                       device,
                       result,
                       rdAddress);
  }

}
