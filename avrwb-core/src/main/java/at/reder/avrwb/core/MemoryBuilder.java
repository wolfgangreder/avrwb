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
package at.reder.avrwb.core;

import at.reder.atmelschema.XA_AddressSpace;
import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.annotations.NotThreadSave;
import java.nio.ByteOrder;

@NotThreadSave
public interface MemoryBuilder
{

  public MemoryBuilder initValue(int init);

  public MemoryBuilder id(@NotNull String id) throws NullPointerException;

  public MemoryBuilder name(@NotNull String name) throws NullPointerException;

  public MemoryBuilder endianess(@NotNull ByteOrder byteOrder) throws NullPointerException;

  public MemoryBuilder size(int size) throws IllegalArgumentException;

  public MemoryBuilder start(int start) throws IllegalArgumentException;

  public MemoryBuilder fromAddressSpace(@NotNull XA_AddressSpace space) throws NullPointerException;

  public Memory build() throws NullPointerException, IllegalStateException;

}
