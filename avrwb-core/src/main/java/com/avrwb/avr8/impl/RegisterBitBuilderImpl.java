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

import com.avrwb.annotations.Invariants;
import com.avrwb.annotations.NotNull;
import com.avrwb.avr8.RegisterBitGrp;
import com.avrwb.avr8.RegisterBitGrpBuilder;
import com.avrwb.avr8.RegisterBitGrpValue;
import com.avrwb.avr8.RegisterBitGrpValueBuilder;
import com.avrwb.schema.XmlBitgroup;
import com.avrwb.schema.XmlBitvalue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Standardimplementierung von {@link com.avrwb.avr8.RegisterBitGrpBuilder}
 *
 * @see com.avrwb.avr8.RegisterBitGrpBuilder
 * @see at.reder.avrwb.avr8.api.InstanceFactories
 * @author Wolfgang Reder
 */
public final class RegisterBitBuilderImpl implements RegisterBitGrpBuilder
{

  private String name;
  private String caption;
  private int mask;
  private final List<RegisterBitGrpValue> bitValues = new ArrayList<>();

  @Override
  public RegisterBitGrpBuilder fromDescriptor(XmlBitgroup bitField) throws NullPointerException
  {
    Objects.requireNonNull(bitField,
                           "bitField==null");
    String newName = bitField.getName();
    String newCaption = bitField.getCaption();
    List<RegisterBitGrpValue> newBitValues = null;
    if (bitField.getBitvalues() != null && !bitField.getBitvalues().getBitvalue().isEmpty()) {
      RegisterBitGrpValueBuilder builder = new RegisterBitGrpValueBuilderImpl();
      newBitValues = new LinkedList<>();
      for (XmlBitvalue v : bitField.getBitvalues().getBitvalue()) {
        newBitValues.add(builder.fromDescriptor(v).build());
      }
    }
    Objects.requireNonNull(newName,
                           "name==null");
    if (newName.trim().isEmpty()) {
      throw new IllegalArgumentException("name is empty");
    }

    Objects.requireNonNull(newCaption,
                           "caption==null");
    name = newName;
    caption = newCaption;
    mask = bitField.getBitmask();
    bitValues.clear();
    if (newBitValues != null) {
      bitValues.addAll(newBitValues);
    }
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrpBuilder name(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                                   IllegalArgumentException
  {
    Objects.requireNonNull(name,
                           "name is null");
    if (name.trim().isEmpty()) {
      throw new IllegalArgumentException("name is empty");
    }
    this.name = name;
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrpBuilder caption(@NotNull String caption) throws NullPointerException
  {
    Objects.requireNonNull(caption,
                           "caption is null");
    this.caption = caption;
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrpBuilder mask(int mask)
  {
    this.mask = mask;
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrpBuilder addBitValue(@NotNull RegisterBitGrpValue bitValue) throws NullPointerException
  {
    Objects.requireNonNull(bitValue,
                           "bitValue==null");
    bitValues.add(bitValue);
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrpBuilder clearBitValues()
  {
    bitValues.clear();
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrp build() throws NullPointerException, IllegalStateException
  {
    Objects.requireNonNull(name,
                           "name is null");
    if (name.trim().isEmpty()) {
      throw new IllegalStateException("name is empty");
    }
    Objects.requireNonNull(caption,
                           "caption is null");
    return new RegisterBitGrpImpl(name,
                                  caption,
                                  mask,
                                  bitValues);
  }

}
