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

import com.avrwb.avr8.Memory;
import com.avrwb.avr8.MemoryBuilder;
import com.avrwb.avr8.api.AVRWBDefaults;
import com.avrwb.schema.XmlAddressSpace;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
public final class MemoryBuilderImpl implements MemoryBuilder
{

  private String id;
  private String name;
  private ByteOrder byteOrder;
  private int size;
  private int start;

  @Override
  public MemoryBuilder id(String id) throws NullPointerException
  {
    Objects.requireNonNull(id,
                           "id==null");
    this.id = id;
    return this;
  }

  @Override
  public MemoryBuilder name(String name) throws NullPointerException
  {
    Objects.requireNonNull(name,
                           "name==null");
    this.name = name;
    return this;
  }

  @Override
  public MemoryBuilder endianess(ByteOrder byteOrder) throws NullPointerException
  {
    Objects.requireNonNull(byteOrder,
                           "byteOrder==null");
    this.byteOrder = byteOrder;
    return this;
  }

  @Override
  public MemoryBuilder size(int size) throws IllegalArgumentException
  {
    if (size <= 0) {
      throw new IllegalArgumentException("size<=0");
    }
    this.size = size;
    return this;
  }

  @Override
  public MemoryBuilder start(int start) throws IllegalArgumentException
  {
    if (start < 0) {
      throw new IllegalArgumentException("start<0");
    }
    this.start = start;
    return this;
  }

  @Override
  public MemoryBuilder fromAddressSpace(XmlAddressSpace space) throws NullPointerException
  {
    Objects.requireNonNull(space,
                           "space==null");
    this.byteOrder = space.getByteOrder();
    this.name = space.getName();
    this.id = space.getId();
    this.size = space.getMaxSize();
    this.start = 0;
    return this;
  }

  @Override
  public Memory build() throws NullPointerException, IllegalStateException
  {
    Objects.requireNonNull(id,
                           "id==null");
    if (name == null) {
      name = id;
    }
    Objects.requireNonNull(byteOrder,
                           "byteOrder==null");
    if (size <= 0) {
      throw new IllegalStateException("size<=0");
    }
    if (start < 0) {
      throw new IllegalStateException("start<0");
    }
    if (AVRWBDefaults.MEMNAME_SRAM.equals(id)) {
      return new SRAMImpl(id,
                          name,
                          byteOrder,
                          size,
                          start);
    } else {
      return new MemoryImpl(id,
                            name,
                            byteOrder,
                            size,
                            start);
    }
  }

}
