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

import com.avrwb.avr8.SRAM;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class WordMemoryCellAdapter implements MemoryCellAdapter
{

  private final SRAM memory;
  private final int address;
  private final int mask;

  public WordMemoryCellAdapter(SRAM memory,
                               int address,
                               int mask)
  {
    Objects.requireNonNull(memory,
                           "memory==null");
    if ((address + 1) >= (memory.getSize() + memory.getStart())) {
      throw new IllegalArgumentException("invalid address");
    }
    this.memory = memory;
    this.address = address;
    this.mask = mask;
  }

  @Override
  public int getValue()
  {
    return memory.getWordAt(address) & mask;
  }

  @Override
  public void setValue(int val)
  {
    memory.setWordAt(address,
                     val & mask);
  }

  @Override
  public void increment()
  {
    memory.setWordAt(address,
                     (memory.getWordAt(address) + 1) & mask);
  }

  @Override
  public void decrement()
  {
    memory.setWordAt(address,
                     (memory.getWordAt(address) - 1) & mask);
  }

  @Override
  public int getMask()
  {
    return mask;
  }

  @Override
  public String toString()
  {
    return "WordMemoryCellAdapter{" + "address=" + address + ", value=0x" + Integer.toHexString(getValue()) + '}';
  }

}
