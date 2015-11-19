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

import com.avrwb.annotations.ProvidedModule;
import com.avrwb.annotations.ProvidedModules;
import com.avrwb.avr8.AVRCoreVersion;
import com.avrwb.avr8.Architecture;
import com.avrwb.avr8.CPUBuilder;
import com.avrwb.avr8.CPUBuilderFactory;
import com.avrwb.avr8.ModuleBuilderFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
@ServiceProvider(service = ModuleBuilderFactory.class, path = "avrwb")
@ProvidedModules({
  @ProvidedModule(core = AVRCoreVersion.V2E, architecture = Architecture.AVR8, value = "CPU")
})
public final class CPU_2EBuilderFactory implements CPUBuilderFactory
{

  @Override
  public CPUBuilder createBuilder()
  {
    return new CPU_2EBuilder();
  }

}
