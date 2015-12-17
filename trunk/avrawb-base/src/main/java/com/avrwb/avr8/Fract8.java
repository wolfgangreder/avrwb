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
package com.avrwb.avr8;

/**
 * Implementiert eine 1:7 fixed Point Gleitkommazahl
 *
 * @author wolfi
 */
public final class Fract8 extends Number implements Comparable<Number>
{

  private static final float DIVISOR = (1 << 7);
  private final int byteValue;
  private transient final float floatValue;

  public static Fract8 valueOfFloat(float fv)
  {
    if (fv >= 2 || fv < 0) {
      throw new IllegalArgumentException("Float out of range");
    }
    return new Fract8((int) (fv * DIVISOR));
  }

  public static Fract8 valueOfByte(int b)
  {
    return new Fract8(b);
  }

  private Fract8(int byteValue)
  {
    this.byteValue = byteValue & 0xff;
    float tmp = this.byteValue;
    floatValue = tmp / DIVISOR;
  }

  public int getByte()
  {
    return byteValue;
  }

  @Override
  public int intValue()
  {
    return (int) floatValue();
  }

  @Override
  public long longValue()
  {
    return intValue();
  }

  @Override
  public float floatValue()
  {
    return floatValue;
  }

  @Override
  public double doubleValue()
  {
    return floatValue();
  }

  public Fract16 mult(Fract8 o)
  {
    int tmp = (byteValue * o.byteValue);
    return Fract16.valueOfInt(tmp << 1);
  }

  public Fract32 toFract32()
  {
    return Fract32.valueOfInt(byteValue << 24);
  }

  public Fract16 toFract16()
  {
    return Fract16.valueOfInt(byteValue << 8);
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 47 * hash + this.byteValue;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Fract8 other = (Fract8) obj;
    return this.byteValue == other.byteValue;
  }

  @Override
  public int compareTo(Number o)
  {
    return Float.compare(byteValue,
                         o.byteValue());
  }

  @Override
  public String toString()
  {
    return "Fract{" + "floatValue=" + floatValue + '}';
  }

}
