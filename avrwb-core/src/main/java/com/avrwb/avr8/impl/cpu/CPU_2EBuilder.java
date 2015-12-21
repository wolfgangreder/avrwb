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
package com.avrwb.avr8.impl.cpu;

import com.avrwb.avr8.CPU;
import com.avrwb.avr8.CPUBuilder;
import com.avrwb.avr8.api.ItemNotFoundException;
import com.avrwb.avr8.impl.AbstractModuleBuilder;
import com.avrwb.schema.ModuleClass;
import com.avrwb.schema.XmlModule;

/**
 *
 * @author Wolfgang Reder
 */
final class CPU_2EBuilder extends AbstractModuleBuilder<CPU_2EBuilder> implements CPUBuilder<CPU_2EBuilder>
{

  @Override
  protected CPU_2EBuilder getThis()
  {
    return this;
  }

  @Override
  protected XmlModule checkModuleClass(XmlModule module) throws IllegalArgumentException
  {
    if (module.getClazz() != ModuleClass.CPU) {
      throw new IllegalArgumentException("module" + module.getId() + " is no cpu");
    }
    return module;
  }

  @Override
  public CPU build() throws IllegalStateException, ItemNotFoundException, NullPointerException
  {
    return new CPU_2E(device,
                      module,
                      sram,
                      nfStrategy);
  }

}
