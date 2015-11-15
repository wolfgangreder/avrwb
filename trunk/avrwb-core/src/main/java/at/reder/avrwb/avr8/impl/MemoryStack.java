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

import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.avr8.Memory;
import at.reder.avrwb.avr8.Register;
import at.reder.avrwb.avr8.Stack;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class MemoryStack implements Stack
{

  private final Register sp;
  private final Memory sram;

  public MemoryStack(@NotNull Register sp,
                     @NotNull Memory sram)
  {
    Objects.requireNonNull(sp,
                           "sp==null");
    Objects.requireNonNull(sram,
                           "sram==null");
    this.sp = sp;
    this.sram = sram;
  }

  @Override
  public int push(int byteToPush)
  {
    int oldByte = sram.getByteAt(sp.getValue());
    sram.setByteAt(sp.getValue(),
                   byteToPush);
    sp.decrement();
    return oldByte;
  }

  @Override
  public int pop()
  {
    sp.increment();
    return sram.getByteAt(sp.getValue());
  }

  @Override
  public int getSP()
  {
    return sp.getValue();
  }

}
