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
package at.reder.avrwb.avr8.api;

import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.annotations.Stateless;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.DeviceBuilder;
import at.reder.avrwb.avr8.MemoryBuilder;
import at.reder.avrwb.avr8.RegisterBitGrpBuilder;
import at.reder.avrwb.avr8.RegisterBitGrpValueBuilder;
import at.reder.avrwb.avr8.RegisterBuilder;
import at.reder.avrwb.avr8.api.instructions.InstructionResultBuilderImpl;
import at.reder.avrwb.avr8.impl.DeviceBuilderImpl;
import at.reder.avrwb.avr8.impl.MemoryBuilderImpl;
import at.reder.avrwb.avr8.impl.RegisterBitBuilderImpl;
import at.reder.avrwb.avr8.impl.RegisterBitGrpValueBuilderImpl;
import at.reder.avrwb.avr8.impl.RegisterBuilderImpl;

/**
 *
 * @author Wolfgang Reder
 */
@Stateless
public final class InstanceFactories
{

  public static MemoryBuilder getMemoryBuilder()
  {
    return new MemoryBuilderImpl();
  }

  public static RegisterBuilder getRegisterBuilder()
  {
    return new RegisterBuilderImpl();
  }

  public static RegisterBitGrpBuilder getRegisterBitBuilder()
  {
    return new RegisterBitBuilderImpl();
  }

  public static RegisterBitGrpValueBuilder getRegisterBitValueBuilder()
  {
    return new RegisterBitGrpValueBuilderImpl();
  }

  public static DeviceBuilder getDeviceBuilder()
  {
    return new DeviceBuilderImpl();
  }

  public static InstructionResultBuilder getInstructionResultBuilder(@NotNull Device device) throws NullPointerException
  {
    return new InstructionResultBuilderImpl(device);
  }

  private InstanceFactories()
  {
  }

}
