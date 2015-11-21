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
import com.avrwb.avr8.VariantBuilder;
import com.avrwb.schema.XmlVariant;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class VariantBuilderImpl implements VariantBuilder
{

  private String name;
  private float vccMin;
  private float vccMax;
  private long speedMax;

  @Override
  public VariantBuilder xmlVariant(XmlVariant variant) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(variant,
                           "variant==null");
    if (variant.getVccMin() <= 0) {
      throw new IllegalArgumentException("vccMin<=0");
    }
    if (variant.getVccMax() <= 0) {
      throw new IllegalArgumentException("vccMax<=0");
    }
    if (variant.getSpeedMax() <= 0) {
      throw new IllegalArgumentException("speedMax<=0");
    }
    if (variant.getVccMin() > variant.getVccMax()) {
      throw new IllegalArgumentException("vccMin>vccMax");
    }
    this.name = variant.getName();
    this.speedMax = variant.getSpeedMax();
    this.vccMin = variant.getVccMin();
    this.vccMax = variant.getVccMax();
    return this;
  }

  @Override
  public VariantBuilder name(String name)
  {
    this.name = name;
    return this;
  }

  @Override
  public VariantBuilder vccMin(float vccMin) throws IllegalArgumentException
  {
    if (vccMin <= 0) {
      throw new IllegalArgumentException("vccMin<=0");
    }
    this.vccMin = vccMin;
    return this;
  }

  @Override
  public VariantBuilder vccMax(float vccMax) throws IllegalArgumentException
  {
    if (vccMax <= 0) {
      throw new IllegalArgumentException("vccMax<=0");
    }
    this.vccMax = vccMax;
    return this;
  }

  @Override
  public VariantBuilder speedMax(long speedMax) throws IllegalArgumentException
  {
    if (speedMax <= 0) {
      throw new IllegalArgumentException("speedMax<=0");
    }
    this.speedMax = speedMax;
    return this;
  }

  @Override
  public Variant build() throws IllegalStateException
  {
    if (vccMin <= 0) {
      throw new IllegalStateException("vccMin<=0");
    }
    if (vccMax <= 0) {
      throw new IllegalStateException("vccMax<=0");
    }
    if (speedMax <= 0) {
      throw new IllegalStateException("speedMax<=0");
    }
    if (vccMin > vccMax) {
      throw new IllegalStateException("vccMin>vccMaX");
    }
    return new VariantImpl(name,
                           vccMin,
                           vccMax,
                           speedMax);
  }

}
