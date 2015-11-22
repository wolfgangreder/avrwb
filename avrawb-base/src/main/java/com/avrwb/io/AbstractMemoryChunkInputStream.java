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
package com.avrwb.io;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author Wolfgang Reder
 */
public abstract class AbstractMemoryChunkInputStream implements MemoryChunkInputStream
{

  public static final int DEFAULT_MAX_CHUNK_SIZE = 1024;
  private int maxChunkSize;

  public AbstractMemoryChunkInputStream()
  {
    maxChunkSize = DEFAULT_MAX_CHUNK_SIZE;
  }

  public AbstractMemoryChunkInputStream(int maxChunkSize) throws IllegalArgumentException
  {
    setMaxChunkSize(maxChunkSize);
  }

  @Override
  public final void setMaxChunkSize(int maxChunkSize) throws IllegalArgumentException
  {
    if (maxChunkSize <= 0) {
      throw new IllegalArgumentException("maxChunkSize<=0");
    }
    this.maxChunkSize = maxChunkSize;
  }

  @Override
  public final int getMaxChunkSize()
  {
    return maxChunkSize;
  }

  @Override
  public MemoryChunk read() throws IOException
  {
    return read(ByteBuffer.allocateDirect(maxChunkSize));
  }

  @Override
  public MemoryChunk read(int maxChunkSize) throws IOException, IllegalArgumentException
  {
    if (maxChunkSize <= 0) {
      throw new IllegalArgumentException("maxChunkSize<=0");
    }
    return read(ByteBuffer.allocateDirect(maxChunkSize));
  }

}
