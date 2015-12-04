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
import com.avrwb.assembler.model.impl.RegisterExpression;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
    private final List<Path> includePaths = new LinkedList<>();

    public AssemblerConfigBuilder includePath(Path path)
    {
      Objects.requireNonNull(path,
                             "path==null");
      includePaths.add(path);
      return this;
    }

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
                                         defaultAliases,
                                         includePaths);
    }

  }

  private static List<Alias> getRegisterAliases()
  {
    List<Alias> result = new ArrayList<>(32);
    for (int i = 0; i < 32; ++i) {
      result.add(new AliasImpl("r" + i,
                               true,
                               new RegisterExpression(i,
                                                      null)));
    }
    return result;
  }

  private static final AssemblerConfig DEFAULT_INSTANCE = new StandardAssemblerConfig(new StandardFileResolver(),
                                                                                      StandardCharsets.ISO_8859_1,
                                                                                      ByteOrder.LITTLE_ENDIAN,
                                                                                      Collections.EMPTY_LIST,
                                                                                      Collections.EMPTY_LIST);
  private final FileResolver fileResolver;
  private final Charset targetCharset;
  private final ByteOrder byteOrder;
  private final List<Alias> defaultAliases;
  private final List<Path> includePaths;

  public static AssemblerConfig getDefaultInstance()
  {
    return DEFAULT_INSTANCE;
  }

  public StandardAssemblerConfig(FileResolver fileResolver,
                                 Charset targetCharset,
                                 ByteOrder byteOrder,
                                 Collection<? extends Alias> defAliases,
                                 Collection<? extends Path> includePaths)
  {
    this.fileResolver = fileResolver;
    this.targetCharset = targetCharset;
    this.byteOrder = byteOrder;
    List<Alias> tmpAliases = new ArrayList<>();
    if (defAliases != null && !defAliases.isEmpty()) {
      tmpAliases.addAll(defAliases);
    }
    tmpAliases.addAll(getRegisterAliases());
    this.defaultAliases = Collections.unmodifiableList(tmpAliases);
    if (includePaths == null || includePaths.isEmpty()) {
      this.includePaths = Collections.emptyList();
    } else {
      this.includePaths = Collections.unmodifiableList(new ArrayList<>(includePaths));
    }
  }

  @Override
  public List<Path> getIncludePaths()
  {
    return includePaths;
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
