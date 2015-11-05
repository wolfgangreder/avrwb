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
package at.reder.avrwb.core;

import at.reder.avrwb.annotations.Stateless;
import at.reder.avrwb.core.impl.AVRCoreBuilderImpl;
import at.reder.avrwb.core.impl.MemoryBuilderImpl;
import at.reder.avrwb.core.impl.RegisterBitBuilderImpl;
import at.reder.avrwb.core.impl.RegisterBitValueBuilderImpl;
import at.reder.avrwb.core.impl.RegisterBuilderImpl;

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

  public static AVRCoreBuilder getCoreBuilder()
  {
    return new AVRCoreBuilderImpl();
  }

  public static RegisterBuilder getRegisterBuilder()
  {
    return new RegisterBuilderImpl();
  }

  public static RegisterBitBuilder getRegisterBitBuilder()
  {
    return new RegisterBitBuilderImpl();
  }

  public static RegisterBitValueBuilder getRegisterBitValueBuilder()
  {
    return new RegisterBitValueBuilderImpl();
  }

  private InstanceFactories()
  {
  }

}
