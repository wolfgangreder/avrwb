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
 * Implementiert eine 1:15 fixed Point Gleitkommazahl
 *
 * @author wolfi
 */
public final class Fract16 extends Number implements Comparable<Number>
{

  private static final float DIVISOR = (1 << 15);
  private final int byteValue;
  private transient final float floatValue;

  public static Fract16 valueOfFloat(float fv)
  {
    if (fv >= 2 || fv < 0) {
      throw new IllegalArgumentException("Float out of range");
    }
    return new Fract16((int) (fv * DIVISOR));
  }

  public static Fract16 valueOfInt(int b)
  {
    return new Fract16(b);
  }

  private Fract16(int byteValue)
  {
    this.byteValue = byteValue & 0xffff;
    float tmp = this.byteValue;
    floatValue = tmp / DIVISOR;
  }

  public int getInt()
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

  public Fract32 mult(Fract16 o)
  {
    int tmp = (byteValue * o.byteValue);
    return Fract32.valueOfInt(tmp);
  }

  public Fract32 toFract32()
  {
    return Fract32.valueOfInt(byteValue << 16);
  }

  public Fract8 toFract8()
  {
    return Fract8.valueOfByte(byteValue >> 8);
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
    final Fract16 other = (Fract16) obj;
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
