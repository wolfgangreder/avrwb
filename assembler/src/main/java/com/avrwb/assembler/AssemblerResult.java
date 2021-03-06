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
package com.avrwb.assembler;

import com.avrwb.io.MemoryChunk;
import com.avrwb.io.MemoryChunkOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.stream.Stream;
import org.openide.util.Lookup;

/**
 *
 * @author wolfi
 */
public interface AssemblerResult extends Lookup.Provider
{

  public void getCSEG(MemoryChunkOutputStream os) throws IOException;

  public Stream<MemoryChunk> getCSEG();

  public boolean isCSEGAvailable();

  public void getESEG(MemoryChunkOutputStream os) throws IOException;

  public Stream<MemoryChunk> getESEG();

  public boolean isESEGAvailable();

  public void getList(Writer writer) throws IOException;

}
