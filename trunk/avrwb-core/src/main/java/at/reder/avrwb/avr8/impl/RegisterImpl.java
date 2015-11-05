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

import at.reder.atmelschema.util.HexIntAdapter;
import at.reder.avrwb.avr8.Register;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import at.reder.avrwb.avr8.RegisterBitGrp;

/**
 * StandardImplementierung eines Registers
 *
 * @see at.reder.avrwb.avr8.Register
 * @author Wolfgang Reder
 */
final class RegisterImpl implements Register
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
  public String toString()
  {
    return "Register{" + "name=" + name + ", address(" + HexIntAdapter.toHexString(memoryAddress, 2) + ","
                   + HexIntAdapter.toHexString(ioAddress, 2) + ")}";
  }

}
