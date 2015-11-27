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
package com.avrwb.assembler.model.impl;

import com.avrwb.annotations.NotNull;
import com.avrwb.assembler.AssemblerResult;
import com.avrwb.assembler.model.Context;
import com.avrwb.io.MemoryChunkInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author wolfi
 */
public class AssemblerResultImpl implements AssemblerResult
{

  private final Context context;
  private final Lookup myLookup;

  public AssemblerResultImpl(@NotNull Context context)
  {
    Objects.requireNonNull(context,
                           "context==null");
    this.context = context;
    myLookup = Lookups.singleton(context);
  }

  @Override
  public MemoryChunkInputStream getCSEG() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public MemoryChunkInputStream getESEG() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Reader getList() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Lookup getLookup()
  {
    return myLookup;
  }

}
