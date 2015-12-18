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
package com.avrwb.avr8.impl.instructions.helper;

import com.avrwb.io.MemoryChunk;
import com.avrwb.io.MemoryChunkOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author wolfi
 */
public final class ListMemoryChunkOutputStream implements MemoryChunkOutputStream
{

  private final List<MemoryChunk> data = new ArrayList<>();

  @Override
  public void write(MemoryChunk chunk)
  {
    data.add(chunk);
  }

  public List<MemoryChunk> getData()
  {
    return data;
  }

  public Stream<MemoryChunk> stream()
  {
    return data.stream();
  }

  @Override
  public void close()
  {
  }

}
