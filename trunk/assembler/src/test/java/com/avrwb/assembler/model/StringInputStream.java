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
package com.avrwb.assembler.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author wolfi
 */
public class StringInputStream extends InputStream
{

  private final InputStream wrapped;

  public StringInputStream(String str)
  {
    wrapped = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public int read() throws IOException
  {
    return wrapped.read();
  }

  @Override
  public boolean markSupported()
  {
    return wrapped.markSupported();
  }

  @Override
  public synchronized void reset() throws IOException
  {
    wrapped.reset();
  }

  @Override
  public synchronized void mark(int readlimit)
  {
    wrapped.mark(readlimit);
  }

  @Override
  public void close() throws IOException
  {
    wrapped.close();
  }

  @Override
  public int available() throws IOException
  {
    return wrapped.available();
  }

  @Override
  public long skip(long n) throws IOException
  {
    return wrapped.skip(n);
  }

  @Override
  public int read(byte[] b,
                  int off,
                  int len) throws IOException
  {
    return wrapped.read(b,
                        off,
                        len);
  }

  @Override
  public int read(byte[] b) throws IOException
  {
    return wrapped.read(b);
  }

}
