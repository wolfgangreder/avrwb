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
package at.reder.avrwb.core.impl;

import at.reder.atmelschema.RegisterVector;
import at.reder.atmelschema.XA_AvrToolsDeviceFile;
import at.reder.atmelschema.XA_Bitfield;
import at.reder.atmelschema.XA_Register;
import at.reder.atmelschema.XA_ValueGroup;
import at.reder.avrwb.annotations.Invariants;
import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.core.RegisterBit;
import at.reder.avrwb.core.RegisterBitBuilder;
import at.reder.avrwb.core.RegisterBitValue;
import at.reder.avrwb.core.RegisterBitValueBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Standardimplementierung von {@link at.reder.avrwb.core.RegisterBitBuilder}
 *
 * @see at.reder.avrwb.core.RegisterBitBuilder
 * @see at.reder.avrwb.core.InstanceFactories
 * @author Wolfgang Reder
 */
public final class RegisterBitBuilderImpl implements RegisterBitBuilder
{

  private String name;
  private String caption;
  private int mask;
  private final List<RegisterBitValue> bitValues = new ArrayList<>();

  @Override
  @NotNull
  public RegisterBitBuilder fromDescriptor(@NotNull XA_AvrToolsDeviceFile descriptor,
                                           @NotNull RegisterVector registerVector,
                                           @NotNull String name) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(descriptor, "descriptor==null");
    Objects.requireNonNull(registerVector, "registerVector==null");
    Objects.requireNonNull(name, "name==null");
    if (name.trim().isEmpty()) {
      throw new IllegalArgumentException("name is empty");
    }
    XA_Register register = descriptor.findRegister(registerVector);
    for (XA_Bitfield bf : register.getBitfields()) {
      if (name.equals(bf.getName())) {
        return fromDescriptor(descriptor, registerVector, bf);
      }
    }
    throw new IllegalArgumentException("cannot find bitfield " + name);
  }

  @Override
  @NotNull
  public RegisterBitBuilder fromDescriptor(@NotNull XA_AvrToolsDeviceFile descriptor,
                                           @NotNull RegisterVector registerVector,
                                           @NotNull XA_Bitfield bf) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(descriptor, "descriptor==null");
    Objects.requireNonNull(registerVector, "registerVector==null");
    Objects.requireNonNull(name, "bitfield==null");
    this.name = bf.getName();
    this.caption = bf.getCaption();
    this.mask = bf.getMask();
    RegisterBitValueBuilder builder = new RegisterBitValueBuilderImpl();
    for (XA_ValueGroup vg : descriptor.findValueGroup(registerVector)) {
      bitValues.add(builder.fromDescriptor(vg).build());
    }
    return this;
  }

  @Override
  @NotNull
  public RegisterBitBuilder name(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
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
  public RegisterBitBuilder caption(@NotNull String caption) throws NullPointerException
  {
    Objects.requireNonNull(caption, "caption is null");
    this.caption = caption;
    return this;
  }

  @Override
  @NotNull
  public RegisterBitBuilder mask(int mask)
  {
    this.mask = mask;
    return this;
  }

  @Override
  @NotNull
  public RegisterBitBuilder addBitValue(@NotNull RegisterBitValue bitValue) throws NullPointerException
  {
    Objects.requireNonNull(bitValue, "bitValue==null");
    bitValues.add(bitValue);
    return this;
  }

  @Override
  @NotNull
  public RegisterBitBuilder clearBitValues()
  {
    bitValues.clear();
    return this;
  }

  @Override
  @NotNull
  public RegisterBit build() throws NullPointerException, IllegalStateException
  {
    Objects.requireNonNull(name, "name is null");
    if (name.trim().isEmpty()) {
      throw new IllegalStateException("name is empty");
    }
    Objects.requireNonNull(caption, "caption is null");
    return new RegisterBitImpl(name, caption, mask, bitValues);
  }

}
