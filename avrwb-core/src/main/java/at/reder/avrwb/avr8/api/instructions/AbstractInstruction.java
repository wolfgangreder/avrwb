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
package at.reder.avrwb.avr8.api.instructions;

import at.reder.avrwb.avr8.AVRCoreVersion;
import at.reder.avrwb.avr8.SREG;
import at.reder.avrwb.avr8.api.Instruction;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author wolfi
 */
public abstract class AbstractInstruction implements Instruction
{

  public static Set<AVRCoreVersion> ALL_CORES = Collections.unmodifiableSet(EnumSet.allOf(AVRCoreVersion.class));

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
    int v = rd + rr + (withCarry && sreg.getC() ? 1 : 0);
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
    int v = rd - rr - (withCarry && sreg.getC() ? 1 : 0);
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

}
