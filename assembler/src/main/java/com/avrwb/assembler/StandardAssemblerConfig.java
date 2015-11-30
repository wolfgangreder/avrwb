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

import com.avrwb.annotations.NotNull;
import com.avrwb.assembler.model.Alias;
import com.avrwb.assembler.model.impl.AliasImpl;
import com.avrwb.assembler.model.impl.IntExpression;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
    private final List<Alias> defaultAliases = new LinkedList<>();

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

    public AssemblerConfigBuilder defaultAlias(@NotNull Alias alias)
    {
      Objects.requireNonNull(alias);
      defaultAliases.add(alias);
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
                                         bo,
                                         defaultAliases);
    }

  }

  private static List<Alias> getRegisterAliases()
  {
    List<Alias> result = new ArrayList<>(32);
    for (int i = 0; i < 32; ++i) {
      result.add(new AliasImpl("r" + i,
                               true,
                               new IntExpression(i,
                                                 null)));
    }
    return result;
  }

  private static final AssemblerConfig DEFAULT_INSTANCE = new StandardAssemblerConfig(new StandardFileResolver(),
                                                                                      StandardCharsets.ISO_8859_1,
                                                                                      ByteOrder.BIG_ENDIAN,
                                                                                      getRegisterAliases());
  private final FileResolver fileResolver;
  private final Charset targetCharset;
  private final ByteOrder byteOrder;
  private final List<Alias> defaultAliases;

  public static AssemblerConfig getDefaultInstance()
  {
    return DEFAULT_INSTANCE;
  }

  public StandardAssemblerConfig(FileResolver fileResolver,
                                 Charset targetCharset,
                                 ByteOrder byteOrder,
                                 Collection<? extends Alias> defAliases)
  {
    this.fileResolver = fileResolver;
    this.targetCharset = targetCharset;
    this.byteOrder = byteOrder;
    if (defAliases == null || defAliases.isEmpty()) {
      defaultAliases = Collections.emptyList();
    } else {
      defaultAliases = Collections.unmodifiableList(new ArrayList<>(defAliases));
    }
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

  @Override
  public List<Alias> getDefaultAliases()
  {
    return defaultAliases;
  }

}
