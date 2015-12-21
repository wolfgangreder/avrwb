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
package com.avrwb.avr8.impl.timer;

import com.avrwb.avr8.api.ItemNotFoundException;
import com.avrwb.avr8.impl.AbstractModuleBuilder;
import com.avrwb.schema.ModuleClass;
import com.avrwb.schema.XmlModule;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public class Timer_0Builder extends AbstractModuleBuilder<Timer_0Builder>
{

  @Override
  protected XmlModule checkModuleClass(XmlModule module) throws IllegalArgumentException
  {
    if (module.getClazz() != ModuleClass.TIMER) {
      throw new IllegalArgumentException("invalid module class " + module.getClazz());
    }
    return module;
  }

  @Override
  protected Timer_0Builder getThis()
  {
    return this;
  }

  @Override
  public Timer_0 build() throws IllegalStateException, ItemNotFoundException, NullPointerException
  {
    Objects.requireNonNull(device,
                           "device==null");
    Objects.requireNonNull(module,
                           "module==null");
    Objects.requireNonNull(sram,
                           "sram==null");
    if (module.getClazz() != ModuleClass.TIMER) {
      throw new IllegalStateException("invalid module class");
    }
    return new Timer_0(device,
                       module,
                       sram,
                       nfStrategy);
  }

}
