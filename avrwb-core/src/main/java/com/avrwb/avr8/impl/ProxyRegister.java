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

import com.avrwb.avr8.Register;
import com.avrwb.avr8.RegisterBitGrp;
import com.avrwb.avr8.RegisterBitGrpValue;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.ItemNotFoundException;
import com.avrwb.avr8.api.NotFoundStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public class ProxyRegister implements Register
{

  private final List<Register> subRegister;
  private final Register masterRegister;
  private final int mask;
  private final List<RegisterBitGrp> registerBits;
  private final SRAM sram;
  private final MemoryCellAdapter cellAdapter;

  public ProxyRegister(Collection<? extends Register> subRegister)
  {
    this.subRegister = Collections.unmodifiableList(new ArrayList<>(subRegister));
    masterRegister = this.subRegister.get(0);
    mask = collectMask(this.subRegister);
    sram = masterRegister.getMemory();
    List<RegisterBitGrp> tmp = collectBitGroups(this.subRegister);
    if (tmp.isEmpty()) {
      this.registerBits = Collections.emptyList();
    } else {
      this.registerBits = Collections.unmodifiableList(new ArrayList<>(tmp));
    }
    switch (masterRegister.getSize()) {
      case 1:
        cellAdapter = new ByteMemoryCellAdapter(sram,
                                                masterRegister.getMemoryAddress(),
                                                mask);
        break;
      case 2:
        cellAdapter = new WordMemoryCellAdapter(sram,
                                                masterRegister.getMemoryAddress(),
                                                mask);
        break;
      case 3:
        cellAdapter = new ThreeByteMemoryCellAdapter(sram,
                                                     masterRegister.getMemoryAddress(),
                                                     mask);
        break;
      case 4:
        cellAdapter = new DWordMemoryCellAdapter(sram,
                                                 masterRegister.getMemoryAddress(),
                                                 mask);
      default:
        throw new IllegalArgumentException("invalid registersize " + masterRegister.getSize());
    }
  }

  private int collectMask(Collection<? extends Register> sr)
  {
    int result = 0;
    for (Register r : sr) {
      result |= r.getMask();
    }
    return result;
  }

  private List<RegisterBitGrp> collectBitGroups(Collection<? extends Register> sr)
  {
    List<RegisterBitGrp> result = new LinkedList<>();
    for (Register r : sr) {
      result.addAll(r.getBits());
    }
    return result;
  }

  public Register getMasterRegister()
  {
    return masterRegister;
  }

  @Override
  public String getName()
  {
    return masterRegister.getName();
  }

  @Override
  public String getCaption()
  {
    return masterRegister.getCaption();
  }

  @Override
  public int getValue()
  {
    return cellAdapter.getValue();
  }

  @Override
  public void setValue(int newValue)
  {
    cellAdapter.setValue(newValue);
  }

  @Override
  public int getMemoryAddress()
  {
    return masterRegister.getMemoryAddress();
  }

  @Override
  public int getIOAddress()
  {
    return masterRegister.getIOAddress();
  }

  @Override
  public int getMask()
  {
    return mask;
  }

  @Override
  public int getSize()
  {
    return masterRegister.getSize();
  }

  @Override
  public List<RegisterBitGrp> getBits()
  {
    return registerBits;
  }

  @Override
  public int getBitGrpValue(RegisterBitGrp bitGrp) throws ItemNotFoundException, NullPointerException
  {
    Objects.requireNonNull(bitGrp,
                           "bitGrp==null");
    if (!registerBits.contains(bitGrp)) {
      ItemNotFoundException.processItemNotFound(masterRegister.getName(),
                                                bitGrp.getName(),
                                                NotFoundStrategy.ERROR);
      assert false;
    }
    int value = cellAdapter.getValue();
    int tmp = value & bitGrp.getMask();
    return tmp >> bitGrp.getRightShift();
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
      ItemNotFoundException.processItemNotFound(masterRegister.getName(),
                                                bitGrp.getName(),
                                                NotFoundStrategy.ERROR);
      assert false;
    }
    if (!bitGrp.getValues().contains(value)) {
      ItemNotFoundException.processItemNotFound(masterRegister.getName(),
                                                value.getName(),
                                                NotFoundStrategy.ERROR);
      assert false;
    }
    return setBitGrpValue(bitGrp,
                          value.getValue());
  }

  @Override
  public int setBitGrpValue(RegisterBitGrp bitGrp,
                            int val) throws ItemNotFoundException, NullPointerException
  {
    Objects.requireNonNull(bitGrp,
                           "bitGrp==null");
    if (!registerBits.contains(bitGrp)) {
      ItemNotFoundException.processItemNotFound(masterRegister.getName(),
                                                bitGrp.getName(),
                                                NotFoundStrategy.ERROR);
      assert false;
    }
    int newBits = (val << bitGrp.getRightShift()) & bitGrp.getMask();
    int oldValue = getBitGrpValue(bitGrp);
    int value = cellAdapter.getValue();
    value &= ~bitGrp.getMask();
    value |= newBits;
    cellAdapter.setValue(value);
    return oldValue;
  }

  @Override
  public boolean getBit(int bit) throws IllegalArgumentException
  {
    if (bit < 0 || bit > ((getSize() * 8) - 1)) {
      throw new IllegalArgumentException("bitindex out of range");
    }
    return (getValue() & (1 << bit)) != 0;
  }

  @Override
  public boolean setBit(int bit,
                        boolean state) throws IllegalArgumentException
  {
    if (bit < 0 || bit > ((getSize() * 8) - 1)) {
      throw new IllegalArgumentException("bitindex out of range");
    }
    final int m = 1 << bit;
    int value = cellAdapter.getValue();
    boolean old = (value & m) != 0;
    if (state) {
      value |= m;
    } else {
      value &= ~m;
    }
    cellAdapter.setValue(value);
    return old;
  }

  @Override
  public void increment()
  {
    cellAdapter.increment();
  }

  @Override
  public void decrement()
  {
    cellAdapter.decrement();
  }

  @Override
  public SRAM getMemory()
  {
    return sram;
  }

}
