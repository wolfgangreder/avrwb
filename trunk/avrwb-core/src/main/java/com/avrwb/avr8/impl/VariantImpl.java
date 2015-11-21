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

import com.avrwb.avr8.Variant;

/**
 *
 * @author wolfi
 */
final class VariantImpl implements Variant
{

  private final String name;
  private final float vccMin;
  private final float vccMax;
  private final long speedMax;

  VariantImpl(String name,
              float vccMin,
              float vccMax,
              long speedMax)
  {
    this.name = name;
    this.vccMin = vccMin;
    this.vccMax = vccMax;
    this.speedMax = speedMax;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public float getVccMin()
  {
    return vccMin;
  }

  @Override
  public float getVccMax()
  {
    return vccMax;
  }

  @Override
  public long getSpeedMax()
  {
    return speedMax;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 97 * hash + Float.floatToIntBits(this.vccMin);
    hash = 97 * hash + Float.floatToIntBits(this.vccMax);
    hash = 97 * hash + (int) (this.speedMax ^ (this.speedMax >>> 32));
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
    final VariantImpl other = (VariantImpl) obj;
    if (Float.floatToIntBits(this.vccMin) != Float.floatToIntBits(other.vccMin)) {
      return false;
    }
    if (Float.floatToIntBits(this.vccMax) != Float.floatToIntBits(other.vccMax)) {
      return false;
    }
    return this.speedMax == other.speedMax;
  }

  @Override
  public String toString()
  {
    return "VariantImpl{" + "vccMin=" + vccMin + ", vccMax=" + vccMax + ", speedMax=" + speedMax + '}';
  }

}
