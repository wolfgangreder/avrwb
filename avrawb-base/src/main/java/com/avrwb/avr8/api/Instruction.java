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

import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NotThreadSave;
import com.avrwb.atmelschema.AVRCoreVersion;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.helper.SimulationException;
import java.util.Set;

/**
 * Basisinterface zur Abbildung von Befehlen.
 *
 * @author wolfi
 */
@NotThreadSave
public interface Instruction
{

  /**
   * Mnemonic des Befehls (z.B mov )
   *
   * @return menomic
   */
  @NotNull
  public String getMnemonic();

  /**
   * Die Dauer des Befehls in Systemtakten.
   *
   * @return cyclecount
   */
  public int getCycleCount();

  /**
   * Der Opcode.
   *
   * @return opcode
   */
  public int getOpcode();

  /**
   * Größe des Befehls in Byte.
   *
   * @return size of instruction
   */
  public int getSize();

  /**
   * Führt den Befehl aus.
   *
   * @param clockState aktueller Taktzustand.
   * @param device controller
   * @return Das Ergebnis des Befehls
   * @throws com.avrwb.avr8.helper.SimulationException wenn bei der ausführung ein fehler auftritt.
   */
  @NotNull
  public InstructionResult execute(@NotNull ClockState clockState,
                                   @NotNull Device device) throws SimulationException;

  /**
   * In welchen Prozessorkernen ist der Befehl implementiert.
   *
   * @return set of coreversions
   */
  @NotNull
  public Set<AVRCoreVersion> getCoresImplemented();

}
