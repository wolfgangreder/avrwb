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

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author wolfi
 */
public class StandardAssemblerConfig implements AssemblerConfig
{

  public static final class AssemblerConfigBuilder
  {

    private FileResolver resolver;
    private Charset charset;
    private ByteOrder bo;

    public AssemblerConfigBuilder fileResolver(FileResolver fr)
    {
      resolver = fr;
      return this;
    }

    public AssemblerConfigBuilder targetCharset(Charset ch)
    {
      charset = ch;
      return this;
    }

    public AssemblerConfigBuilder targetByteOrder(ByteOrder bo)
    {
      this.bo = bo;
      return this;
    }

    public AssemblerConfig build()
    {
      if (resolver == null) {
        resolver = new StandardFileResolver();
      }
      if (charset == null) {
        charset = StandardCharsets.ISO_8859_1;
      }
      if (bo == null) {
        bo = ByteOrder.LITTLE_ENDIAN;
      }
      return new StandardAssemblerConfig(resolver,
                                         charset,
                                         bo);
    }

  }
  private static final AssemblerConfig DEFAULT_INSTANCE = new StandardAssemblerConfig(new StandardFileResolver(),
                                                                                      StandardCharsets.ISO_8859_1,
                                                                                      ByteOrder.LITTLE_ENDIAN);
  private final FileResolver fileResolver;
  private final Charset targetCharset;
  private final ByteOrder byteOrder;

  public static AssemblerConfig getDefaultInstance()
  {
    return DEFAULT_INSTANCE;
  }

  public StandardAssemblerConfig(FileResolver fileResolver,
                                 Charset targetCharset,
                                 ByteOrder byteOrder)
  {
    this.fileResolver = fileResolver;
    this.targetCharset = targetCharset;
    this.byteOrder = byteOrder;
  }

  @Override
  public FileResolver getFileResolver()
  {
    return fileResolver;
  }

  @Override
  public Charset getTargetCharset()
  {
    return targetCharset;
  }

  @Override
  public ByteOrder getTargetByteOrder()
  {
    return byteOrder;
  }

}
