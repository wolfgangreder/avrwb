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
package at.reder.avrwb.io;

import at.reder.avrwb.annotations.NotNull;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class DefaultMemoryChunk implements MemoryChunk
{

  private final int startAddress;
  private final int size;
  private final ByteBuffer data;

  public DefaultMemoryChunk(int startAddress, @NotNull ByteBuffer data, int size)
  {
    Objects.requireNonNull(data, "data==null");
    this.startAddress = startAddress;
    this.data = data;
    this.size = size;
  }

  @Override
  public int getSize()
  {
    return size;
  }

  @Override
  public int getStartAddress()
  {
    return startAddress;
  }

  @Override
  public ByteBuffer getData()
  {
    return data;
  }

  @Override
  public String toString()
  {
    return "DefaultMemoryChunk{" + "startAddress=" + startAddress + ", size=" + size + '}';
  }

}
