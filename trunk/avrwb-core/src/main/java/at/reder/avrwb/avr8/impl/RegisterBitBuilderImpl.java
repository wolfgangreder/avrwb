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

import at.reder.atmelschema.RegisterBitGrpVector;
import at.reder.atmelschema.XA_AvrToolsDeviceFile;
import at.reder.atmelschema.XA_Bitfield;
import at.reder.atmelschema.XA_Value;
import at.reder.atmelschema.XA_ValueGroup;
import at.reder.avrwb.annotations.Invariants;
import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.avr8.RegisterBitGrp;
import at.reder.avrwb.avr8.RegisterBitGrpBuilder;
import at.reder.avrwb.avr8.RegisterBitGrpValue;
import at.reder.avrwb.avr8.RegisterBitGrpValueBuilder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Standardimplementierung von {@link at.reder.avrwb.avr8.RegisterBitGrpBuilder}
 *
 * @see at.reder.avrwb.avr8.RegisterBitGrpBuilder
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
  @NotNull
  public RegisterBitGrpBuilder fromDescriptor(@NotNull XA_AvrToolsDeviceFile descriptor,
                                              @NotNull RegisterBitGrpVector registerVector) throws NullPointerException,
                                                                                                   IllegalArgumentException
  {
    Objects.requireNonNull(descriptor, "descriptor==null");
    Objects.requireNonNull(registerVector, "registerVector==null");
    XA_Bitfield bf = descriptor.findBitField(registerVector);
    String newName = bf.getName();
    String newCaption = bf.getCaption();
    if (bf == null) {
      throw new IllegalArgumentException("bitfield not found");
    }
    XA_ValueGroup vg = descriptor.findValueGroup(registerVector);
    if (vg == null) {
      throw new IllegalArgumentException("valuegroup not found");
    }
    RegisterBitGrpValueBuilder builder = new RegisterBitGrpValueBuilderImpl();
    List<RegisterBitGrpValue> newBitValues = new LinkedList<>();
    for (XA_Value v : vg.getValues()) {
      newBitValues.add(builder.fromDescriptor(v).build());
    }
    Objects.requireNonNull(newName, "name==null");
    if (newName.trim().isEmpty()) {
      throw new IllegalArgumentException("name is empty");
    }
    Objects.requireNonNull(newCaption, "caption==null");
    name = newName;
    caption = newCaption;
    mask = bf.getMask();
    bitValues.clear();
    bitValues.addAll(newBitValues);
    return this;
  }

  @Override
  @NotNull
  public RegisterBitGrpBuilder name(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                                   IllegalArgumentException
  {
    Objects.requireNonNull(name, "name is null");
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
    Objects.requireNonNull(caption, "caption is null");
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
    Objects.requireNonNull(bitValue, "bitValue==null");
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
    Objects.requireNonNull(name, "name is null");
    if (name.trim().isEmpty()) {
      throw new IllegalStateException("name is empty");
    }
    Objects.requireNonNull(caption, "caption is null");
    return new RegisterBitGrpImpl(name, caption, mask, bitValues);
  }

}
