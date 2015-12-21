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

import com.avrwb.annotations.ProvidedModule;
import com.avrwb.annotations.ProvidedModules;
import com.avrwb.avr8.ModuleBuilder;
import com.avrwb.avr8.ModuleBuilderFactory;
import com.avrwb.schema.AvrFamily;
import com.avrwb.schema.ModuleClass;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author wolfi
 */
@ServiceProvider(service = ModuleBuilderFactory.class, path = "avrwb")
@ProvidedModules({
  @ProvidedModule(moduleClass = ModuleClass.X_PRESCALER,
                  family = AvrFamily.TINY,
                  core = "ANY",
                  value = "TIMER_PRESCALER",
                  implementationId = "TIMER_PRESCALER"),
  @ProvidedModule(moduleClass = ModuleClass.X_PRESCALER,
                  family = AvrFamily.BASIC,
                  core = "ANY",
                  value = "TIMER_PRESCALER",
                  implementationId = "TIMER_PRESCALER"),
  @ProvidedModule(moduleClass = ModuleClass.X_PRESCALER,
                  family = AvrFamily.MEGA,
                  core = "ANY",
                  value = "TIMER_PRESCALER",
                  implementationId = "TIMER_PRESCALER")
})
public final class TimerPrescalerBuilderFactory implements ModuleBuilderFactory
{

  @Override
  public ModuleBuilder createBuilder()
  {
    return new TimerPrescalerBuilder();
  }

}
