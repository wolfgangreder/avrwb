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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 *
 * @author wolfi
 */
public class LowerCaseReader extends Reader
{

  private final Reader parent;

  public LowerCaseReader(InputStream is,
                         Charset charset)
  {
    this.parent = new InputStreamReader(is,
                                        charset);
  }

  public LowerCaseReader(Reader parent)
  {
    this.parent = parent;
  }

  @Override
  public int read(char[] cbuf,
                  int off,
                  int len) throws IOException
  {
    int read = parent.read(cbuf,
                           off,
                           len);
    for (int i = off; i < off + read; ++i) {
      cbuf[i] = Character.toLowerCase(cbuf[i]);
    }
    return read;
  }

  @Override
  public void close() throws IOException
  {
    parent.close();
  }

}
