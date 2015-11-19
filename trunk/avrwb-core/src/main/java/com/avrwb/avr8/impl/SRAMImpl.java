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

import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.SRAM;
import java.nio.ByteOrder;

/**
 *
 * @author wolfi
 */
class SRAMImpl extends MemoryImpl implements SRAM
{

  public SRAMImpl(String id,
                  String name,
                  ByteOrder byteOrder,
                  int size,
                  int start)
  {
    super(id,
          name,
          byteOrder,
          size,
          start);
  }

  @Override
  public boolean isAddressExternal(int address)
  {
    return false;
  }

  @Override
  public int getPointer(Pointer ptr)
  {
    return getWordAt(ptr.getAddressLo());
  }

  @Override
  public int setPointer(Pointer ptr,
                        int newValue)
  {
    int result = getWordAt(ptr.getAddressLo());
    setWordAt(ptr.getAddressLo(),
              newValue);
    return result;
  }

}
