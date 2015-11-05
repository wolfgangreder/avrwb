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
package com.atmel.schema;

import at.reder.atmelschema.RegisterVector;
import at.reder.atmelschema.XA_AddressSpace;
import at.reder.atmelschema.XA_AvrToolsDeviceFile;
import at.reder.atmelschema.XA_Bitfield;
import at.reder.atmelschema.XA_Device;
import at.reder.atmelschema.XA_DeviceModule;
import at.reder.atmelschema.XA_DeviceModuleInstance;
import at.reder.atmelschema.XA_DeviceModuleRegisterGroup;
import at.reder.atmelschema.XA_MemorySegment;
import at.reder.atmelschema.XA_Module;
import at.reder.atmelschema.XA_Register;
import at.reder.atmelschema.XA_RegisterGroup;
import at.reder.atmelschema.XA_Value;
import at.reder.atmelschema.XA_ValueGroup;
import at.reder.atmelschema.XA_Variant;
import at.reder.avrwb.avr8.Architecture;
import at.reder.avrwb.avr8.Family;
import at.reder.avrwb.avr8.MemoryAccess;
import at.reder.avrwb.avr8.MemoryAccessSet;
import at.reder.avrwb.avr8.MemoryType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class AvrToolsDevice_mega8_NGTest
{

  private URL mega8URL;
  private XA_AvrToolsDeviceFile mega8;

  public AvrToolsDevice_mega8_NGTest()
  {
  }

  @Test
  public void testStreaming() throws IOException
  {
    mega8URL = getClass().getResource("/com/atmel/devices/ATmega8.xml");
    mega8 = XA_AvrToolsDeviceFile.load(mega8URL);
  }

  @Test(expectedExceptions = NullPointerException.class)
  @SuppressWarnings("null")
  public void testStreamingFailURL() throws IOException
  {
    URL u = null;
    XA_AvrToolsDeviceFile.load(u);
  }

  @Test(expectedExceptions = NullPointerException.class)
  @SuppressWarnings("null")
  public void testStreamingFailStream() throws IOException
  {
    InputStream u = null;
    XA_AvrToolsDeviceFile.load(u);
  }

  @Test(dependsOnMethods = {"testStreaming"})
  public void testVariants()
  {
    List<XA_Variant> varList = mega8.getVariants();
    assertNotNull(varList);
    assertEquals(1, varList.size());
    XA_Variant var = varList.get(0);
    assertNotNull(var);
    assertEquals("standard", var.getOrdercode());
    assertEquals("", var.getPackage());
    assertEquals("", var.getPinout());
    assertEquals(0, var.getSpeedMax());
    assertEquals(0, var.getTempMax());
    assertEquals(0, var.getTempMin());
    assertEquals(5.5f, var.getVccMax(), 1e-6);
    assertEquals(2.7f, var.getVccMin(), 1e-6);
  }

  @Test(dependsOnMethods = {"testStreaming"})
  public void testDevices()
  {
    List<XA_Device> devList = mega8.getDevices();
    assertNotNull(devList);
    assertEquals(1, devList.size());
    assertFalse(devList.contains(null));
    XA_Device dev = devList.get(0);
    assertNotNull(dev);
    assertEquals(Family.megaAVR, dev.getFamily());
    assertEquals(Architecture.AVR8, dev.getArchitecture());
    assertEquals("ATmega8", dev.getName());
  }

  private void assertSpace(XA_AddressSpace space,
                           ByteOrder byteOrder,
                           String id,
                           String name,
                           Integer start,
                           Integer size,
                           int numSegements)
  {
    assertNotNull(space);
    assertEquals(byteOrder, space.getByteOrder());
    assertEquals(id, space.getId());
    assertEquals(name, space.getName());
    assertNotNull(space.getStart());
    assertEquals(start, space.getStart());
    assertNotNull(space.getSize());
    assertEquals(size, space.getSize());
    assertNotNull(space.getSegments());
    assertEquals(numSegements, space.getSegments().size());
    assertFalse(space.getSegments().contains(null));
  }

  private void assertSegment(XA_MemorySegment seg,
                             String name,
                             MemoryAccessSet access,
                             Integer start,
                             Integer size,
                             Boolean exec,
                             MemoryType type,
                             Integer pageSize,
                             Boolean external)
  {
    assertNotNull(seg);
    assertEquals(name, seg.getName());
    assertEquals(access, seg.getMemoryAccess());
    assertNotNull(seg.getStart());
    assertEquals(start, seg.getStart());
    assertNotNull(seg.getSize());
    assertEquals(size, seg.getSize());
    assertNotNull(seg.isExecuteable());
    assertEquals(exec, seg.isExecuteable());
    assertNotNull(seg.getType());
    assertEquals(type, seg.getType());
    assertNotNull(seg.getPageSize());
    assertEquals(pageSize, seg.getPageSize());
    assertNotNull(seg.isExternal());
    assertEquals(external, seg.isExternal());
  }

  private void assertDeviceModule(XA_DeviceModule module,
                                  String name,
                                  int numInstances)
  {
    assertNotNull(module);
    assertEquals(name, module.getName());
    assertNotNull(module.getInstances());
    assertEquals(numInstances, module.getInstances().size());
    assertFalse(module.getInstances().contains(null));
  }

  private void assertDeviceModuleInstance(XA_DeviceModuleInstance inst,
                                          String name,
                                          int numGroups)
  {
    assertNotNull(inst);
    assertEquals(name, inst.getName());
    assertNotNull(inst.getRegisterGroup());
    assertEquals(numGroups, inst.getRegisterGroup().size());
    assertFalse(inst.getRegisterGroup().contains(null));
  }

  private void assertDeviceRegisterGroup(XA_DeviceModuleRegisterGroup grp,
                                         String name,
                                         String addressSpace,
                                         String nameInModule,
                                         Integer offset)
  {
    assertNotNull(grp);
    assertEquals(name, grp.getName());
    assertEquals(addressSpace, grp.getAddressSpace());
    assertEquals(nameInModule, grp.getNameInModule());
    assertNotNull(grp.getOffset());
    assertEquals(offset, grp.getOffset());
  }

  @Test(dependsOnMethods = {"testStreaming", "testDevices"})
  public void testAdressSpacesDevice0()
  {
    XA_Device device = mega8.getDevices().get(0);
    List<XA_AddressSpace> spaces = device.getAdressSpaces();
    final MemoryAccessSet rw = new MemoryAccessSet(Arrays.asList(MemoryAccess.READ, MemoryAccess.WRITE));
    final MemoryAccessSet r = new MemoryAccessSet(Collections.singleton(MemoryAccess.READ));
    assertNotNull(spaces);
    assertEquals(8, spaces.size());
    assertSpace(spaces.get(0), ByteOrder.LITTLE_ENDIAN, "prog", "prog", 0, 0x2000, 5);
    assertSegment(spaces.get(0).getSegments().get(0), "FLASH", rw, 0, 0x2000, true, MemoryType.flash, 0x40, false);
    assertSegment(spaces.get(0).getSegments().get(3), "BOOT_SECTION_3", rw, 0x1c00, 0x0400, true, MemoryType.flash, 0x40, false);
    assertSpace(spaces.get(1), ByteOrder.LITTLE_ENDIAN, "signatures", "signatures", 0, 0x3, 1);
    assertSegment(spaces.get(1).getSegments().get(0), "SIGNATURES", r, 0, 3, false, MemoryType.signatures, 0, false);
    assertSpace(spaces.get(2), ByteOrder.LITTLE_ENDIAN, "fuses", "fuses", 0, 0x2, 1);
    assertSpace(spaces.get(3), ByteOrder.LITTLE_ENDIAN, "lockbits", "lockbits", 0, 1, 1);
    assertSpace(spaces.get(4), ByteOrder.LITTLE_ENDIAN, "data", "data", 0, 0x460, 3);
    assertSpace(spaces.get(5), ByteOrder.LITTLE_ENDIAN, "eeprom", "eeprom", 0, 0x200, 1);
    assertSpace(spaces.get(6), ByteOrder.LITTLE_ENDIAN, "io", "io", 0, 0x40, 0);
    assertSpace(spaces.get(7), ByteOrder.LITTLE_ENDIAN, "osccal", "osccal", 0, 0x4, 1);
    List<XA_DeviceModule> modules = device.getModules();
    assertNotNull(modules);
    assertFalse(modules.contains(null));
    assertEquals(17, modules.size());
    assertDeviceModule(modules.get(0), "ANALOG_COMPARATOR", 1);
    assertDeviceModuleInstance(modules.get(0).getInstances().get(0), "ANALOG_COMPARATOR", 1);
    assertDeviceRegisterGroup(modules.get(0).getInstances().get(0).getRegisterGroup().get(0),
                              "ANALOG_COMPARATOR",
                              "data",
                              "ANALOG_COMPARATOR",
                              0);
  }

  private void assertModule(XA_Module module,
                            String name,
                            String caption,
                            int numRegisterGroup,
                            int numValueGroup)
  {
    assertNotNull(module);
    assertEquals(name, module.getName());
    assertEquals(caption, module.getCaption());
    assertNotNull(module.getRegisterGroups());
    assertFalse(module.getRegisterGroups().contains(null));
    assertEquals(numRegisterGroup, module.getRegisterGroups().size());
    assertNotNull(module.getValueGroups());
    assertFalse(module.getValueGroups().contains(null));
    assertEquals(numValueGroup, module.getValueGroups().size());
  }

  @Test(dependsOnMethods = {"testStreaming"})
  public void testModules()
  {
    List<XA_Module> modules = mega8.getModules();
    assertNotNull(modules);
    assertFalse(modules.contains(null));
    assertEquals(17, modules.size());
    assertModule(modules.get(3), "SPI", "", 1, 1);
  }

  private void assertRegisterGroup(XA_RegisterGroup grp,
                                   String name,
                                   String caption,
                                   int numRegister)
  {
    assertNotNull(grp);
    assertEquals(name, grp.getName());
    assertEquals(caption, grp.getCaption());
    assertNotNull(grp.getRegister());
    assertFalse(grp.getRegister().contains(null));
    assertEquals(numRegister, grp.getRegister().size());
  }

  private void assertRegister(XA_Register reg,
                              String name,
                              String caption,
                              Integer offset,
                              Integer size,
                              int numBitfields,
                              String ocd_rw,
                              Integer mask)
  {
    assertNotNull(reg);
    assertEquals(name, reg.getName());
    assertEquals(caption, reg.getCaption());
    assertNotNull(reg.getOffset());
    assertEquals(offset, reg.getOffset());
    assertNotNull(reg.getSize());
    assertEquals(size, reg.getSize());
    assertNotNull(reg.getBitfields());
    assertFalse(reg.getBitfields().contains(null));
    assertEquals(numBitfields, reg.getBitfields().size());
    assertEquals(ocd_rw, reg.getOcd_rw());
    assertEquals(mask, reg.getMask());
  }

  private void assertBitfield(XA_Bitfield field,
                              String caption,
                              String name,
                              Integer mask,
                              String values)
  {
    assertNotNull(field);
    assertEquals(name, field.getName());
    assertEquals(caption, field.getCaption());
    assertNotNull(field.getMask());
    assertEquals(mask, field.getMask());
    assertEquals(values, field.getValues());
  }

  private void assertValueGroup(XA_ValueGroup grp,
                                String caption,
                                String name,
                                int numValues)
  {
    assertNotNull(grp);
    assertEquals(caption, grp.getCaption());
    assertEquals(name, grp.getName());
    assertNotNull(grp.getValues());
    assertFalse(grp.getValues().contains(null));
    assertEquals(numValues, grp.getValues().size());
  }

  private void assertValue(XA_Value val,
                           String caption,
                           String name,
                           Integer value)
  {
    assertNotNull(val);
    assertEquals(caption, val.getCaption());
    assertEquals(name, val.getName());
    assertNotNull(val.getValue());
    assertEquals(value, val.getValue());
  }

  @Test(dependsOnMethods = {"testStreaming", "testModules"})
  public void testFUSE()
  {
    final XA_Module fuse = mega8.getModules().get(0);
    assertModule(fuse, "FUSE", "", 1, 3);
    final XA_RegisterGroup grp = fuse.getRegisterGroups().get(0);
    assertRegisterGroup(grp, "FUSE", "", 2);
    XA_Register reg = grp.getRegister().get(0);
    assertRegister(reg, "HIGH", "", 1, 1, 7, null, 0xff);
    assertBitfield(reg.getBitfields().get(0), "Reset Disabled (Enable PC6 as i/o pin)", "RSTDISBL", 0x80, null);
    reg = grp.getRegister().get(1);
    assertRegister(reg, "LOW", "", 0, 1, 3, null, 0xff);
    assertBitfield(reg.getBitfields().get(2), "Select Clock Source", "SUT_CKSEL", 0x3F, "ENUM_SUT_CKSEL");
    XA_ValueGroup valGroup = fuse.getValueGroups().get(0);
    assertValueGroup(valGroup, "", "ENUM_BODLEVEL", 2);
    assertValue(valGroup.getValues().get(0), "Brown-out detection at VCC=4.0 V", "4V0", 0);
    assertValue(valGroup.getValues().get(1), "Brown-out detection at VCC=2.7 V", "2V7", 1);
  }

  @Test(dependsOnMethods = {"testStreaming", "testModules"})
  public void testSPI()
  {
    final XA_Module spi = mega8.getModules().get(3);
    assertModule(spi, "SPI", "", 1, 1);
    final XA_RegisterGroup grp = spi.getRegisterGroups().get(0);
    assertRegisterGroup(grp, "SPI", "", 3);
    XA_Register reg = grp.getRegister().get(0);
    assertRegister(reg, "SPDR", "SPI Data Register", 0x2f, 1, 0, "", 0xff);
    reg = grp.getRegister().get(1);
    assertRegister(reg, "SPSR", "SPI Status Register", 0x2e, 1, 3, "R", 0xff);
  }

  @Test(dependsOnMethods = {"testStreaming"})
  public void testStreamOut() throws IOException
  {
    mega8.store(System.out);
  }

  @Test(dependsOnMethods = {"testStreaming"})
  public void testFileOut() throws IOException
  {
    File file = new File(mega8URL.getPath()).getParentFile();
    file = new File(file, "m8.xml.gz");
    try (OutputStream fos = new GZIPOutputStream(new FileOutputStream(file))) {
      mega8.store(fos);
    }
  }

  @Test(dependsOnMethods = {"testStreaming"})
  public void testFindRegister()
  {
    XA_Register register = mega8.findRegister(new RegisterVector("ATmega8", "CPU", "SREG"));
    assertNotNull(register);
    assertEquals("SREG", register.getName());
    assertEquals(0x5f, (int) register.getOffset());
    register = mega8.findRegister(new RegisterVector("ATmega8", "PORTB", "DDRB"));
    assertNotNull(register);
    assertEquals("DDRB", register.getName());
    assertEquals(0x37, (int) register.getOffset());
  }

  @Test(dependsOnMethods = {"testStreaming"})
  @SuppressWarnings("null")
  public void testModuleParameter()
  {
    XA_Module cpu = null;
    Iterator<XA_Module> iter = mega8.getModules().iterator();
    while (cpu == null && iter.hasNext()) {
      XA_Module current = iter.next();
      if ("CPU".equals(current.getName())) {
        cpu = current;
      }
    }
    assertNotNull(cpu);
    assertNotNull(cpu.getParameter());
    String expected = "V2E";
    String actual = cpu.getParameter().get("CORE_VERSION");
    assertEquals(expected, actual);
  }

}
