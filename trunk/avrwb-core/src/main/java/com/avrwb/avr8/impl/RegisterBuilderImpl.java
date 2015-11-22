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

import com.avrwb.annotations.NotThreadSave;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.RegisterBitGrp;
import com.avrwb.avr8.RegisterBitGrpBuilder;
import com.avrwb.avr8.RegisterBuilder;
import com.avrwb.schema.XmlBitgroup;
import com.avrwb.schema.XmlRegister;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Standardimplementierung für {@link com.avrwb.avr8.RegisterBuilder}
 *
 * @see com.avrwb.avr8.RegisterBuilder
 * @see com.avrwb.avr8.spi.InstanceFactories
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
  public RegisterBuilder fromDescritpor(XmlRegister register) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(register,
                           "register==null");
    name = register.getName();
    caption = register.getCaption();
    memoryAddress = register.getRamAddress();
    ioAddress = register.getIoAddress();
    if (register.getSize() != null) {
      size = register.getSize();
    } else {
      size = 1;
    }
    if (register.getBitmask() != null) {
      mask = register.getBitmask();
    } else {
      mask = (1 << (size * 8)) - 1;
    }
    RegisterBitGrpBuilder builder = new RegisterBitBuilderImpl();
    for (XmlBitgroup bf : register.getBitgroup()) {
      registerBits.add(builder.fromDescriptor(bf).build());
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
