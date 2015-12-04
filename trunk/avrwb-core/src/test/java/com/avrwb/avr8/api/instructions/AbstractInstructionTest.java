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
package com.avrwb.avr8.api.instructions;

import com.avrwb.assembler.Assembler;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.avr8.helper.NotFoundStrategy;
import com.avrwb.avr8.spi.InstanceFactories;
import com.avrwb.schema.XmlPart;
import com.avrwb.schema.util.DeviceStreamer;
import org.openide.util.Lookup;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.BeforeClass;

public class AbstractInstructionTest
{

  protected static Assembler assembler;
  protected static XmlPart part;

  protected Device getDevice() throws NullPointerException, IllegalStateException, ItemNotFoundException
  {
    Device device = InstanceFactories.getDeviceBuilder().fromDescriptor(part).notFoundStrategy(NotFoundStrategy.ERROR).build();
    assertNotNull(device,
                  "testDevice==null");
    return device;
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    assembler = Lookup.getDefault().lookup(Assembler.class);
    assertNotNull(assembler,
                  "assembler==null");
    part = DeviceStreamer.loadDevice(LdiNGTest.class.getResource("/com/avrwb/devices/ATtiny4313.xml"),
                                     DeviceStreamer.Version.V_1_0);
    assertNotNull(part,
                  "tmp==null");
  }

}
