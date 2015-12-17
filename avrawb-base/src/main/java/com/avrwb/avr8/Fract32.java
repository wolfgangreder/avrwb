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
public final class Fract32 extends Number implements Comparable<Number>
{

  private static final float DIVISOR = (1 << 31);
  private final long byteValue;
  private transient final float floatValue;

  public static Fract32 valueOfFloat(float fv)
  {
    if (fv >= 2 || fv < 0) {
      throw new IllegalArgumentException("Float out of range");
    }
    return new Fract32((int) (fv * DIVISOR));
  }

  public static Fract32 valueOfInt(long b)
  {
    return new Fract32(b);
  }

  private Fract32(long byteValue)
  {
    this.byteValue = byteValue & 0xffffffff;
    float tmp = this.byteValue;
    floatValue = Math.abs(tmp / DIVISOR);
  }

  public long getInt()
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

  public Fract8 toFract8()
  {
    return Fract8.valueOfByte((int) (byteValue >> 24));
  }

  public Fract16 toFract16()
  {
    return Fract16.valueOfInt((int) (byteValue >> 16));
  }

  @Override
  public int hashCode()
  {
    return Long.hashCode(byteValue);
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
    final Fract32 other = (Fract32) obj;
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
