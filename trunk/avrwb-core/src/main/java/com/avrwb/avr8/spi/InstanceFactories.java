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
package com.avrwb.avr8.spi;

import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.Stateless;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.DeviceBuilder;
import com.avrwb.avr8.MemoryBuilder;
import com.avrwb.avr8.RegisterBitGrpBuilder;
import com.avrwb.avr8.RegisterBitGrpValueBuilder;
import com.avrwb.avr8.RegisterBuilder;
import com.avrwb.avr8.VariantBuilder;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.impl.instructions.InstructionResultBuilderImpl;
import com.avrwb.avr8.impl.DeviceBuilderImpl;
import com.avrwb.avr8.impl.MemoryBuilderImpl;
import com.avrwb.avr8.impl.RegisterBitBuilderImpl;
import com.avrwb.avr8.impl.RegisterBitGrpValueBuilderImpl;
import com.avrwb.avr8.impl.RegisterBuilderImpl;
import com.avrwb.avr8.impl.VariantBuilderImpl;

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

  public static VariantBuilder getVariantBuilder()
  {
    return new VariantBuilderImpl();
  }

  private InstanceFactories()
  {
  }

}
