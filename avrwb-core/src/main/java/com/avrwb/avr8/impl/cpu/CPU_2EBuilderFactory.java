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

import com.avrwb.annotations.ProvidedModule;
import com.avrwb.annotations.ProvidedModules;
import com.avrwb.avr8.CPUBuilder;
import com.avrwb.avr8.CPUBuilderFactory;
import com.avrwb.avr8.ModuleBuilderFactory;
import com.avrwb.schema.AvrFamily;
import com.avrwb.schema.ModuleClass;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
@ServiceProvider(service = ModuleBuilderFactory.class, path = "avrwb")
@ProvidedModules({
  @ProvidedModule(family = AvrFamily.MEGA,
                  core = {"V2E"},
                  moduleClass = ModuleClass.CPU,
                  value = {"CPU"},
                  implementationId = "V2E-CPU-0"),
  @ProvidedModule(family = AvrFamily.TINY,
                  core = {"V2"},
                  moduleClass = ModuleClass.CPU,
                  value = {"CPU"},
                  implementationId = "V2-CPU-0")
})
public final class CPU_2EBuilderFactory implements CPUBuilderFactory
{

  @Override
  public CPUBuilder createBuilder()
  {
    return new CPU_2EBuilder();
  }

}
