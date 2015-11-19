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

import com.avrwb.atmelschema.XA_Value;
import com.avrwb.annotations.Invariants;
import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NotThreadSave;
import com.avrwb.avr8.RegisterBitGrpValue;
import com.avrwb.avr8.RegisterBitGrpValueBuilder;
import java.util.Objects;

/**
 * Standardimplmenentierung von RegisterBitGrpValueBuilder
 *
 * @see com.avrwb.avr8.RegisterBitGrpValueBuilder
 * @see at.reder.avrwb.avr8.api.InstanceFactories
 * @author Wolfgang Reder
 */
@NotThreadSave
public final class RegisterBitGrpValueBuilderImpl implements RegisterBitGrpValueBuilder
{

  private String name;
  private String caption;
  private int value;

  @Override
  @NotNull
  public RegisterBitGrpValueBuilder fromDescriptor(@NotNull XA_Value valueGroup) throws NullPointerException
  {
    Objects.requireNonNull(valueGroup, "valueGroup==null");
    name = valueGroup.getName();
    caption = valueGroup.getCaption();
    value = valueGroup.getValue();
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrpValueBuilder name(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                                        IllegalArgumentException
  {
    Objects.requireNonNull(name, "name==null");
    if (name.trim().isEmpty()) {
      throw new IllegalArgumentException("name is empty");
    }
    this.name = name;
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrpValueBuilder caption(@NotNull String caption) throws NullPointerException
  {
    Objects.requireNonNull(caption, "caption==null");
    this.caption = caption;
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrpValueBuilder value(int value)
  {
    this.value = value;
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrpValue build() throws NullPointerException, IllegalStateException
  {
    Objects.requireNonNull(name, "name==null");
    if (name.trim().isEmpty()) {
      throw new IllegalStateException("name is empty");
    }
    Objects.requireNonNull(caption, "caption==null");
    return new RegisterBitGrpValueImpl(name, caption, value);
  }

}
