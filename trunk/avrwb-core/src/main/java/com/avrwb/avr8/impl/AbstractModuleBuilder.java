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
package com.avrwb.avr8.impl;

import com.avrwb.avr8.ModuleBuilder;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.helper.NotFoundStrategy;
import com.avrwb.schema.XmlDevice;
import com.avrwb.schema.XmlModule;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
public abstract class AbstractModuleBuilder<B extends ModuleBuilder> implements ModuleBuilder<B>
{

  protected XmlDevice device;
  protected XmlModule module;
  protected NotFoundStrategy nfStrategy;
  protected SRAM sram;

  protected abstract B getThis();

  @Override
  public B sram(SRAM sram) throws NullPointerException
  {
    Objects.requireNonNull(sram,
                           "sram==null");
    this.sram = sram;
    return getThis();
  }

  @Override
  public B notFoundStrategy(NotFoundStrategy nfStrategy) throws NullPointerException
  {
    Objects.requireNonNull(nfStrategy,
                           "nfStrategy==null");
    this.nfStrategy = nfStrategy;
    return getThis();
  }

  @Override
  public B device(XmlDevice device) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(device,
                           "device==null");
    this.device = device;
    return getThis();
  }

  @Override
  public B moduleDescriptor(XmlModule module) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(module,
                           "module==null");
    this.module = module;
    return getThis();
  }

}
