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

import com.avrwb.atmelschema.util.HexIntAdapter;
import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NotThreadSave;
import com.avrwb.avr8.AVRCoreVersion;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstanceFactories;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.InstructionResult;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.SimulationException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author wolfi
 */
@NotThreadSave
public abstract class AbstractInstruction implements Instruction
{

  public static Set<AVRCoreVersion> ALL_CORES = Collections.unmodifiableSet(EnumSet.allOf(AVRCoreVersion.class));
  private final int opcode;
  private final int opcodeMask;
  private final String mnemonic;
  private String currentDeviceStateMessage;
  protected long finishCycle = -1;

  protected AbstractInstruction(int opcode,
                                int opcodeMask,
                                String mnemonic)
  {
    this.opcode = opcode;
    this.opcodeMask = opcodeMask;
    this.mnemonic = mnemonic;
  }

  @Override
  public final int getOpcode()
  {
    return opcode;
  }

  @Override
  public final int getOpcodeMask()
  {
    return opcodeMask;
  }

  @Override
  public final String getMnemonic()
  {
    return mnemonic;
  }

  /**
   * Führt eine Addition aus, und setzt die Bits im SREG entrsprechend des Ergebnisses.
   *
   * @param sreg SREG
   * @param rd erster operand
   * @param rr zweiter operand
   * @param withCarry withCarry
   * @return summe
   */
  protected int performAdd(SREG sreg,
                           int rd,
                           int rr,
                           boolean withCarry)
  {
    int v = (rd + rr + (withCarry && sreg.getC() ? 1 : 0)) & 0xff;
    boolean c = (rd & rr & 0x80) != 0 || ((rr & ~v) & 0x80) != 0 || ((rd & ~v) & 0x80) != 0;
    sreg.setC(c);
    sreg.setZ(v == 0);
    sreg.setN((v & 0x80) != 0);
    sreg.setV((((rr & rd & ~v) & 0x80) != 0) || (((~rr & ~rd & v) & 0x80) != 0));
    sreg.fixSignBit();
    sreg.setH((((rr & rd & ~v) & 0x08) != 0) || (((~rr & ~rd & v) & 0x08) != 0));
    return v;
  }

  /**
   * Führt eine Subtraktion aus, und setzt die Bits im SREG entrsprechend des Ergebnisses.
   *
   * @param sreg SREG
   * @param rd erster operand
   * @param rr zweiter operand
   * @param withCarry withCarry
   * @return differenz
   */
  protected int performSub(SREG sreg,
                           int rd,
                           int rr,
                           boolean withCarry)
  {
    int v = (rd - rr - (withCarry && sreg.getC() ? 1 : 0)) & 0xff;
    sreg.setC(((~rd & rr & 0x80) != 0) || ((rr & v & 0x80) != 0) || ((~rd & v & 0x80) != 0));
    sreg.setZ((v == 0) && sreg.getZ());
    sreg.setN((v & 0x80) != 0);
    sreg.setV(((rd & ~rr & ~v & 0x80) != 0) || ((~rd & rr & v & 0x80) != 0));
    sreg.fixSignBit();
    sreg.setH(((~rd & rr & 0x08) != 0) || ((rr & v & 0x08) != 0) || ((~rd & v & 0x08) != 0));
    return v;
  }

  @Override
  public int getCycleCount()
  {
    return 1;
  }

  @Override
  public int getSize()
  {
    return 2;
  }

  @Override
  public Set<AVRCoreVersion> getCoresImplemented()
  {
    return ALL_CORES;
  }

  @Override
  public String toString()
  {
    return mnemonic;
  }

  protected abstract void doExecute(@NotNull ClockState clockState,
                                    @NotNull Device device,
                                    @NotNull InstructionResultBuilder resultBuilder) throws SimulationException;

  protected void doPrepare(@NotNull ClockState clockState,
                           @NotNull Device device,
                           @NotNull InstructionResultBuilder resultBuilder) throws SimulationException
  {
  }

  @Override
  @NotNull
  public final InstructionResult execute(@NotNull ClockState clockState,
                                         @NotNull Device device) throws SimulationException
  {
    Objects.requireNonNull(clockState,
                           "clockState==null");
    currentDeviceStateMessage = null;
    InstructionResultBuilder resultBuilder = InstanceFactories.getInstructionResultBuilder(device);
    switch (clockState.getPhase()) {
      case HI:
        doPrepare(clockState,
                  device,
                  resultBuilder);
        break;
      case LO:
        doExecute(clockState,
                  device,
                  resultBuilder);
    }
    if (resultBuilder.isFinished()) {
      finishCycle = -1;
    }
    return resultBuilder.build();
  }

  /**
   * Erzeugt einen formatierten String der die aktuelle Taktphase beschreibt.
   *
   * @param device device
   * @param clockState clockState
   * @return Loggingstring
   * @throws NullPointerException wenn {@code clockState==null} oder {@code device==null}
   */
  @NotNull
  protected String getCurrentDeviceMessage(@NotNull ClockState clockState,
                                           @NotNull Device device) throws NullPointerException
  {
    if (currentDeviceStateMessage == null) {
      Objects.requireNonNull(clockState,
                             "clockState==null");
      Objects.requireNonNull(device,
                             "device==null");
      currentDeviceStateMessage = MessageFormat.format("Exec \"{0}\" @ {1} | IP={2}, #CY={5,number,#.###}, PH={3}|",
                                                       toString(),
                                                       device.getName(),
                                                       HexIntAdapter.toHexString(device.getCPU().getIP(),
                                                                                 device.getFlash().getHexAddressStringWidth()),
                                                       clockState.getPhase().name(),
                                                       clockState.getCurrentNanos(),
                                                       clockState.getCycleCount());
    }
    return currentDeviceStateMessage;
  }

}
