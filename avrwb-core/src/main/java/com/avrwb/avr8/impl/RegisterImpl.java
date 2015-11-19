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

import com.avrwb.atmelschema.util.HexIntAdapter;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.RegisterBitGrp;
import com.avrwb.avr8.RegisterBitGrpValue;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.avr8.helper.NotFoundStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * StandardImplementierung eines Registers
 *
 * @see com.avrwb.avr8.Register
 * @author Wolfgang Reder
 */
class RegisterImpl implements Register
{

  private final String name;
  private final String caption;
  private final int memoryAddress;
  private final int ioAddress;
  private final int mask;
  private final int size;
  private final List<RegisterBitGrp> registerBits;
  private int value;

  RegisterImpl(String name,
               String caption,
               int memoryAddress,
               int ioAddress,
               int mask,
               int size,
               Collection<? extends RegisterBitGrp> bits)
  {
    this.name = name;
    this.caption = caption;
    this.memoryAddress = memoryAddress;
    this.ioAddress = ioAddress;
    this.mask = mask;
    this.size = size;
    if (bits == null || bits.isEmpty()) {
      registerBits = Collections.emptyList();
    } else {
      registerBits = Collections.unmodifiableList(new ArrayList<>(bits));
    }
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getCaption()
  {
    return caption;
  }

  @Override
  public int getMemoryAddress()
  {
    return memoryAddress;
  }

  @Override
  public int getIOAddress()
  {
    return ioAddress;
  }

  @Override
  public int getMask()
  {
    return mask;
  }

  @Override
  public int getSize()
  {
    return size;
  }

  @Override
  public List<RegisterBitGrp> getBits()
  {
    return registerBits;
  }

  @Override
  public int getValue()
  {
    return value;
  }

  @Override
  public void setValue(int newValue)
  {
    this.value = newValue & mask;
  }

  @Override
  public void increment()
  {
    this.value = (value + 1) & mask;
  }

  @Override
  public void decrement()
  {
    this.value = (value - 1) & mask;
  }

  @Override
  public int setBitGrpValue(RegisterBitGrp bitGrp,
                            RegisterBitGrpValue value) throws ItemNotFoundException, NullPointerException
  {
    Objects.requireNonNull(bitGrp,
                           "bitGrp==null");
    Objects.requireNonNull(value,
                           "value==null");
    if (!registerBits.contains(bitGrp)) {
      ItemNotFoundException.processItemNotFound(name,
                                                bitGrp.getName(),
                                                NotFoundStrategy.ERROR);
      assert false;
    }
    if (!bitGrp.getValues().contains(value)) {
      ItemNotFoundException.processItemNotFound(name,
                                                value.getName(),
                                                NotFoundStrategy.ERROR);
      assert false;
    }
    return setBitGrpValue(bitGrp,
                          value.getValue());
  }

  @Override
  public int setBitGrpValue(RegisterBitGrp bitGrp,
                            int value) throws ItemNotFoundException, NullPointerException
  {
    Objects.requireNonNull(bitGrp,
                           "bitGrp==null");
    if (!registerBits.contains(bitGrp)) {
      ItemNotFoundException.processItemNotFound(name,
                                                bitGrp.getName(),
                                                NotFoundStrategy.ERROR);
      assert false;
    }
    int newBits = (value << bitGrp.getRightShift()) & bitGrp.getMask();
    int oldValue = getBitGrpValue(bitGrp);
    this.value &= ~bitGrp.getMask();
    this.value |= newBits;
    return oldValue;
  }

  @Override
  public boolean getBit(int bit) throws IllegalArgumentException
  {
    if (bit < 0 || bit > 7) {
      throw new IllegalArgumentException("bitindex out of range");
    }
    return (value & (1 << bit)) != 0;
  }

  @Override
  public boolean setBit(int bit,
                        boolean state) throws IllegalArgumentException
  {
    if (bit < 0 || bit > 7) {
      throw new IllegalArgumentException("bitindex out of range");
    }
    final int m = 1 << bit;
    boolean old = (value & m) != 0;
    if (state) {
      value |= m;
    } else {
      value &= ~m;
    }
    return old;
  }

  @Override
  public int getBitGrpValue(RegisterBitGrp bitGrp) throws ItemNotFoundException, NullPointerException
  {
    Objects.requireNonNull(bitGrp,
                           "bitGrp==null");
    if (!registerBits.contains(bitGrp)) {
      ItemNotFoundException.processItemNotFound(name,
                                                bitGrp.getName(),
                                                NotFoundStrategy.ERROR);
      assert false;
    }
    int tmp = value & bitGrp.getMask();
    return tmp >> bitGrp.getRightShift();
  }

  @Override
  public String toString()
  {
    return "Register{" + "name=" + name + ", address(" + HexIntAdapter.toHexString(memoryAddress,
                                                                                   2) + ","
                   + HexIntAdapter.toHexString(ioAddress,
                                               2) + ")}";
  }

}
