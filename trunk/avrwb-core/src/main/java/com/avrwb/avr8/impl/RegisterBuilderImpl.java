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

import com.avrwb.atmelschema.RegisterVector;
import com.avrwb.atmelschema.XA_AvrToolsDeviceFile;
import com.avrwb.atmelschema.XA_Bitfield;
import com.avrwb.atmelschema.XA_Register;
import com.avrwb.annotations.NotThreadSave;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.RegisterBitGrp;
import com.avrwb.avr8.RegisterBitGrpBuilder;
import com.avrwb.avr8.RegisterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Standardimplementierung für {@link com.avrwb.avr8.RegisterBuilder}
 *
 * @see com.avrwb.avr8.RegisterBuilder
 * @see at.reder.avrwb.avr8.api.InstanceFactories
 * @author Wolfgang Reder
 */
@NotThreadSave
public final class RegisterBuilderImpl implements RegisterBuilder
{

  private String name;
  private String caption;
  private int memoryAddress = -1;
  private int ioAddress = -1;
  private int mask = -1;
  private int size = -1;
  private final List<RegisterBitGrp> registerBits = new ArrayList<>();

  @Override
  public RegisterBuilder fromDescritpor(XA_AvrToolsDeviceFile descriptorFile,
                                        RegisterVector vector) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(descriptorFile,
                           "descriptorFile==null");
    Objects.requireNonNull(vector,
                           "vector==null");
    XA_Register register = descriptorFile.findRegister(vector);
    if (register == null) {
      throw new IllegalArgumentException("cannot find register " + vector);
    }
    name = register.getName();
    caption = register.getCaption();
    memoryAddress = register.getOffset();
    ioAddress = memoryAddress - 32;
    mask = register.getMask();
    size = register.getSize();
    RegisterBitGrpBuilder builder = new RegisterBitBuilderImpl();
    for (XA_Bitfield bf : register.getBitfields()) {
      registerBits.add(builder.fromDescriptor(descriptorFile,
                                              bf,
                                              vector).build());
    }
    return this;
  }

  @Override
  public RegisterBuilder name(String name) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(name,
                           "name==null");
    if (name.trim().length() == 0) {
      throw new IllegalArgumentException("name is empty");
    }
    this.name = name;
    return this;
  }

  @Override
  public RegisterBuilder caption(String caption) throws NullPointerException
  {
    Objects.requireNonNull(caption,
                           "caption==null");
    this.caption = caption;
    return this;
  }

  @Override
  public RegisterBuilder memoryAddress(int memoryAddress) throws IllegalArgumentException
  {
    if (memoryAddress < 0) {
      throw new IllegalArgumentException("memoryAddress<0");
    }
    this.memoryAddress = memoryAddress;
    return this;
  }

  @Override
  public RegisterBuilder ioAddress(int ioAddress) throws IllegalArgumentException
  {
    if (ioAddress < 0) {
      throw new IllegalArgumentException("ioAddress<0");
    }
    this.ioAddress = ioAddress;
    return this;
  }

  @Override
  public RegisterBuilder mask(int mask)
  {
    this.mask = mask;
    return this;
  }

  @Override
  public RegisterBuilder size(int size) throws IllegalArgumentException
  {
    if (size < 1) {
      throw new IllegalArgumentException("size<1");
    }
    this.size = size;
    return this;
  }

  @Override
  public RegisterBuilder addRegisterBit(RegisterBitGrp bit) throws NullPointerException
  {
    Objects.requireNonNull(bit,
                           "bit==null");
    registerBits.add(bit);
    return this;
  }

  @Override
  public RegisterBuilder clearRegisterBits()
  {
    registerBits.clear();
    return this;
  }

  @Override
  public Register build() throws NullPointerException, IllegalStateException
  {
    Objects.requireNonNull(name,
                           "name==null");
    if (name.trim().length() == 0) {
      throw new IllegalStateException("name is empty");
    }
    Objects.requireNonNull(caption,
                           "caption==null");
    if (memoryAddress < 0) {
      throw new IllegalStateException("memoryAddress<0");
    }
    if (ioAddress < 0) {
      throw new IllegalStateException("ioAddress<0");
    }
    if (size < 1) {
      throw new IllegalStateException("size<1");
    }
    return new RegisterImpl(name,
                            caption,
                            memoryAddress,
                            ioAddress,
                            mask,
                            size,
                            registerBits);
  }

}
