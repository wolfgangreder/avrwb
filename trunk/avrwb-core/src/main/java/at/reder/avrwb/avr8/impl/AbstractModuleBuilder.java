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
package at.reder.avrwb.avr8.impl;

import at.reder.atmelschema.ModuleVector;
import at.reder.atmelschema.XA_AvrToolsDeviceFile;
import at.reder.atmelschema.XA_Module;
import at.reder.avrwb.avr8.ModuleBuilder;
import at.reder.avrwb.avr8.helper.NotFoundStrategy;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
public abstract class AbstractModuleBuilder<B extends ModuleBuilder> implements ModuleBuilder<B>
{

  protected XA_AvrToolsDeviceFile file;
  protected ModuleVector moduleVector;
  protected XA_Module module;
  protected NotFoundStrategy nfStrategy;

  protected abstract B getThis();

  @Override
  public B notFoundStrategy(NotFoundStrategy nfStrategy) throws NullPointerException
  {
    Objects.requireNonNull(nfStrategy, "nfStrategy==null");
    this.nfStrategy = nfStrategy;
    return getThis();
  }

  @Override
  public B moduleVector(ModuleVector mv) throws NullPointerException
  {
    Objects.requireNonNull(mv, "mv==null");
    moduleVector = mv;
    return getThis();
  }

  @Override
  public B descriptor(XA_AvrToolsDeviceFile file) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(file, "file==null");
    this.file = file;
    return getThis();
  }

  @Override
  public B moduleDescriptor(XA_Module module)
  {
    this.module = module;
    return getThis();
  }

}
