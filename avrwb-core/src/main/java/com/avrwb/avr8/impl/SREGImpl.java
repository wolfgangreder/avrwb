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
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.helper.ItemNotFoundException;
import java.util.Collection;
import java.util.function.Function;

public final class SREGImpl extends RegisterImpl implements SREG
{

  public static final char[] BITNAMES = {'C', 'Z', 'N', 'V', 'S', 'H', 'T', 'I'};

  private String toString;

  public SREGImpl(Register register)
  {
    this(register.getName(),
         register.getCaption(),
         register.getMemoryAddress(),
         register.getIOAddress(),
         register.getMask(),
         register.getSize(),
         register.getBits());
  }

  public SREGImpl(String name,
                  String caption,
                  int memoryAddress,
                  int ioAddress,
                  int mask,
                  int size,
                  Collection<? extends RegisterBitGrp> bits)
  {
    super(name,
          caption,
          memoryAddress,
          ioAddress,
          mask,
          size,
          bits);
    if (!"SREG".equals(name)) {
      throw new IllegalArgumentException("register is not SREG");
    }
  }

  @Override
  public boolean getC()
  {
    return getBit(C);
  }

  @Override
  public boolean setC(boolean newState)
  {
    return setBit(C,
                  newState);
  }

  @Override
  public boolean getZ()
  {
    return getBit(Z);
  }

  @Override
  public boolean setZ(boolean newState)
  {
    return setBit(Z,
                  newState);
  }

  @Override
  public boolean getN()
  {
    return getBit(N);
  }

  @Override
  public boolean setN(boolean newState)
  {
    return setBit(N,
                  newState);
  }

  @Override
  public boolean getV()
  {
    return getBit(V);
  }

  @Override
  public boolean setV(boolean newState)
  {
    return setBit(V,
                  newState);
  }

  @Override
  public boolean getS()
  {
    return getBit(S);
  }

  @Override
  public boolean setS(boolean newState)
  {
    return setBit(S,
                  newState);
  }

  @Override
  public boolean getH()
  {
    return getBit(H);
  }

  @Override
  public boolean setH(boolean newState)
  {
    return setBit(H,
                  newState);
  }

  @Override
  public boolean getT()
  {
    return getBit(T);
  }

  @Override
  public boolean setT(boolean newState)
  {
    return setBit(T,
                  newState);
  }

  @Override
  public boolean getI()
  {
    return getBit(I);
  }

  @Override
  public boolean setI(boolean newState)
  {
    return setBit(I,
                  newState);
  }

  @Override
  public boolean setBit(int bit,
                        boolean state) throws IllegalArgumentException
  {
    synchronized (this) {
      toString = null;
    }
    return super.setBit(bit,
                        state);
  }

  @Override
  public int setBitGrpValue(RegisterBitGrp bitGrp,
                            int value) throws ItemNotFoundException, NullPointerException
  {
    synchronized (this) {
      toString = null;
    }
    return super.setBitGrpValue(bitGrp,
                                value);
  }

  @Override
  public int setBitGrpValue(RegisterBitGrp bitGrp,
                            RegisterBitGrpValue value) throws ItemNotFoundException, NullPointerException
  {
    synchronized (this) {
      toString = null;
    }
    return super.setBitGrpValue(bitGrp,
                                value);
  }

  @Override
  public void setValue(int newValue)
  {
    synchronized (this) {
      toString = null;
    }
    super.setValue(newValue);
  }

  @Override
  public void fixSignBit()
  {
    int v = getValue();
    setS(((v & MASK_N) != 0) ^ ((v & MASK_V) != 0));
  }

  private static void appendBit(int index,
                                StringBuilder builder,
                                Function<Integer, Boolean> bitSet)
  {
    builder.append(BITNAMES[index]);
    builder.append(':');
    if (bitSet.apply(index)) {
      builder.append('1');
    } else {
      builder.append('0');
    }
  }

  @Override
  public synchronized String toString()
  {
    if (toString == null) {
      toString = getSREGString(getValue());
    }
    return toString;
  }

  public static String getSREGString(int sregValue)
  {
    StringBuilder tmp = new StringBuilder("SREG{");
    for (int i = 0; i < 8; ++i) {
      appendBit(i,
                tmp,
                (Integer b) -> (sregValue & (1 << b)) != 0);
      tmp.append(", ");
    }
    tmp.setLength(tmp.length() - 2);
    tmp.append('}');
    return tmp.toString();
  }

}
