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
import com.avrwb.assembler.AssemblerException;
import com.avrwb.assembler.AssemblerResult;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Memory;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.api.instructions.helper.StringAssemblerSource;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.avr8.helper.NotFoundStrategy;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.avr8.impl.SREGImpl;
import com.avrwb.avr8.spi.InstanceFactories;
import com.avrwb.schema.XmlPart;
import com.avrwb.schema.util.DeviceStreamer;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Set;
import org.openide.util.Lookup;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;
import org.testng.annotations.BeforeClass;

public class AbstractInstructionTest
{

  protected static Assembler assembler;
  protected static XmlPart part;

  protected static class MemoryChangeHandler
  {

    private final Set<Integer> expected;
    private final Memory mem;
    private final String msg;

    public MemoryChangeHandler(Memory mem,
                               Set<Integer> expected,
                               String msg)
    {
      this.mem = mem;
      this.expected = expected;
      this.msg = msg;
    }

    private String getIntList(Collection<Integer> coll)
    {
      StringBuilder tmp = new StringBuilder();
      coll.stream().sorted().forEach((Integer i) -> {
        tmp.append("0x");
        tmp.append(Integer.toHexString(i));
        tmp.append(',');
      });
      if (tmp.length() > 0) {
        tmp.setLength(tmp.length() - 1);
      }
      return tmp.toString();
    }

    public void onMemoryChanged(Memory mem,
                                Set<Integer> addresses)
    {
      if (mem != this.mem) {
        fail(msg + " memory ist not expected instance");
      }
      if (expected.isEmpty()) {
        fail(msg + " dont expect memory change");
      }
      if (!expected.equals(addresses)) {
        fail(msg + "expected change of [" + getIntList(expected) + "] but got [" + getIntList(addresses) + "]");
      }
      expected.clear();
    }

  }

  protected Device getDevice(XmlPart p) throws NullPointerException, IllegalStateException, ItemNotFoundException
  {
    Device device = InstanceFactories.getDeviceBuilder().fromDescriptor(p).notFoundStrategy(NotFoundStrategy.ERROR).build();
    assertNotNull(device,
                  "testDevice==null");
    return device;
  }

  protected Device getDevice(String cmd) throws ItemNotFoundException, IOException, AssemblerException, SimulationException
  {
    return getDevice(cmd,
                     false);
  }

  protected Device getDevice(XmlPart p,
                             String cmd) throws ItemNotFoundException, IOException, AssemblerException, SimulationException
  {
    return getDevice(p,
                     cmd,
                     false);
  }

  protected Device getDevice(String cmd,
                             boolean list) throws ItemNotFoundException, IOException, AssemblerException, SimulationException
  {
    return getDevice(part,
                     cmd,
                     list);
  }

  protected Device getDevice(XmlPart part,
                             String cmd,
                             boolean list) throws ItemNotFoundException, IOException, AssemblerException, SimulationException
  {
    Device result = getDevice(part);
    AssemblerResult asr = compile(cmd);
    if (list) {
      try (OutputStreamWriter w = new OutputStreamWriter(System.out)) {
        asr.getList(w);
      }
    }
    result.getFlash().initialize(asr.getCSEG());
    result.reset(ResetSource.POWER_UP);
    return result;
  }

  protected AssemblerResult compile(String cmd) throws IOException, AssemblerException
  {
    return assembler.compile(new StringAssemblerSource(cmd + "\nnop",
                                                       cmd),
                             null);
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    assembler = Lookup.getDefault().lookup(Assembler.class);
    assertNotNull(assembler,
                  "assembler==null");
    part = DeviceStreamer.loadDevice(LdiNGTest.class.getResource("/com/avrwb/devices/ATmega16bit.xml"),
                                     DeviceStreamer.Version.V_1_0);
    assertNotNull(part,
                  "tmp==null");
  }

  public void assertSREG(int actual,
                         int expected,
                         String message)
  {
    if (actual != expected) {
      if (message != null) {
        fail(message + ": expected " + SREGImpl.getSREGString(expected) + " but found " + SREGImpl.getSREGString(actual));
      } else {
        fail(message + ": expected " + SREGImpl.getSREGString(expected) + " but found " + SREGImpl.getSREGString(actual));
      }
    }
  }

}
