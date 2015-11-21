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
package com.avrwb.schema;

import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.schema.util.DeviceStreamer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author wolfi
 */
public class ReadPartFilesTestNG
{

  private boolean isPartFile(Path file,
                             BasicFileAttributes attr)
  {
    return (attr.isRegularFile() && Files.isReadable(file));
  }

  private List<Path> scanPath(String partDirectory) throws IOException
  {
    Path file = Paths.get(partDirectory);
    List<Path> pathList = new LinkedList<>();
    Files.find(file,
               1,
               this::isPartFile).forEach((Path f) -> pathList.add(f));
    return pathList;
  }

  @DataProvider(name = "partFiles")
  public Object[][] readPartFileNames() throws IOException
  {
    String baseDir = System.getProperty("basedir");
    List<Path> pathList = scanPath(baseDir + "/src/main/resources/com/avrwb/devices");
    Object[][] result = new Object[pathList.size()][];
    int i = 0;
    Iterator<Path> iter = pathList.iterator();
    while (iter.hasNext()) {
      Path current = iter.next();
      result[i++] = new Object[]{current, DeviceStreamer.Version.V_1_0};
    }
    return result;
  }

  @DataProvider(name = "failPartFiles")
  public Object[][] readFailPartFileNames() throws IOException
  {
    String baseDir = System.getProperty("basedir");
    List<Path> pathList = scanPath(baseDir + "/src/test/resources/com/avrwb/devices");
    Object[][] result = new Object[pathList.size() * 2][1];
    int i = 0;
    Iterator<Path> iter = pathList.iterator();
    while (iter.hasNext()) {
      Path current = iter.next();
      result[i++] = new Object[]{current, DeviceStreamer.Version.V_1_0};
      result[i++] = new Object[]{current, null};
    }
    return result;
  }

  @Test(dataProvider = "partFiles")
  public void testPartFiles(Path partFile,
                            DeviceStreamer.Version schemaVersion) throws IOException, SAXException
  {
    String context = partFile.toString();
    try (InputStream fis = Files.newInputStream(partFile,
                                                StandardOpenOption.READ)) {
      XmlPart part = DeviceStreamer.loadDevice(fis,
                                               schemaVersion);
      assertNotNull(part,
                    context);
      XmlDevice device = part.getDevice();
      assertNotNull(device,
                    context);
      assertNotNull(device.getAddressSpaces(),
                    context);
      assertNotNull(device.getAvrCore(),
                    context);
      assertNotNull(device.getFamily(),
                    context);
      assertNotNull(device.getModules(),
                    context);
      assertNotNull(device.getName(),
                    context);
      assertFalse(device.getName().trim().isEmpty(),
                  context);
      assertNotNull(device.getAddressSpaces().getAddressSpace(),
                    context);
      assertFalse(device.getAddressSpaces().getAddressSpace().isEmpty(),
                  context);
      Set<String> memIds = new HashSet<>();
      Set<SegmentType> memTypes = new HashSet<>();
      for (XmlAddressSpace space : device.getAddressSpaces().getAddressSpace()) {
        assertNotNull(space.getId(),
                      context);
        String spaceContext = context + "|" + space.getId();
        memIds.add(space.getId());
        assertNotNull(space,
                      spaceContext);
        assertNotNull(space.getByteOrder(),
                      spaceContext);
        assertNotNull(space.getName(),
                      spaceContext);
        assertNotNull(space.getSegment(),
                      spaceContext);
        assertFalse(space.getSegment().isEmpty(),
                    spaceContext);
        for (XmlSegment seg : space.getSegment()) {
          assertNotNull(seg,
                        spaceContext);
          assertNotNull(seg.getName(),
                        spaceContext);
          assertFalse(seg.getName().trim().isEmpty(),
                      spaceContext);
          String segContext = spaceContext + "|" + seg.getName();
          assertNotNull(seg.getAccess(),
                        segContext);
          assertNotNull(seg.getSize(),
                        segContext);
          assertTrue(seg.getSize() > 0,
                     segContext);
          assertNotNull(seg.getStart(),
                        segContext);
          assertTrue(seg.getStart() >= 0,
                     segContext);
          assertNotNull(seg.getType(),
                        segContext);
          memTypes.add(seg.getType());
        }
      }
      assertTrue(memIds.contains(AVRWBDefaults.MEMID_EEPROM),
                 context);
      assertTrue(memIds.contains(AVRWBDefaults.MEMID_FLASH),
                 context);
      assertTrue(memIds.contains(AVRWBDefaults.MEMID_SRAM),
                 context);
      assertTrue(memTypes.contains(SegmentType.REGISTER));
      assertTrue(memTypes.contains(SegmentType.IO));
      assertTrue(memTypes.contains(SegmentType.FLASH));
      Set<ModuleClass> moduleClasses = new HashSet<>();
      XmlRegister sreg = null;
      assertNotNull(device.getModules().getModule(),
                    context);
      for (XmlModule mod : device.getModules().getModule()) {
        assertNotNull(mod,
                      context);
        assertNotNull(mod.getId(),
                      context);
        assertFalse(mod.getId().trim().isEmpty(),
                    context);
        String modContext = context + "|" + mod.getId();
        assertNotNull(mod.getName(),
                      modContext);
        assertFalse(mod.getName().trim().isEmpty(),
                    modContext);
        assertNotNull(mod.getClazz(),
                      modContext);
        moduleClasses.add(mod.getClazz());
        assertNotNull(mod.getInterrupt());
        for (XmlInterrupt itr : mod.getInterrupt()) {
          assertNotNull(itr,
                        modContext);
          assertNotNull(itr.getName(),
                        modContext);
          String itrContext = modContext + "|" + itr.getName();
          assertFalse(itr.getName().trim().isEmpty(),
                      itrContext);
          assertNotNull(itr.getVector(),
                        itrContext);
          assertTrue(itr.getVector() >= 0,
                     itrContext);
        }
        assertNotNull(mod.getRegister(),
                      modContext);
        for (XmlRegister reg : mod.getRegister()) {
          assertNotNull(reg,
                        modContext);
          assertNotNull(reg.getName(),
                        modContext);
          String regContext = modContext + "|" + reg.getName();
          assertFalse(reg.getName().trim().isEmpty(),
                      regContext);
          assertNotNull(reg.getSize(),
                        regContext);
          assertTrue(reg.getSize() > 0,
                     regContext);
          if (reg.getBitmask() != null) {
            assertTrue(reg.getBitmask() > 0,
                       regContext);
          }
          assertNotNull(reg.getIoAddress(),
                        regContext);
          assertTrue(reg.getIoAddress() >= 0,
                     regContext);
          assertNotNull(reg.getRamAddress(),
                        regContext);
          assertTrue(reg.getRamAddress() >= 0,
                     regContext);
          assertNotNull(reg.getBitgroup(),
                        regContext);
          if (reg.getName().equals("SREG")) {
            sreg = reg;
          }
          for (XmlBitgroup bg : reg.getBitgroup()) {
            assertNotNull(bg,
                          regContext);
            assertNotNull(bg.getName(),
                          regContext);
            String bgContext = regContext + "|" + bg.getName();
            assertFalse(bg.getName().trim().isEmpty(),
                        bgContext);
            assertNotNull(bg.getBitmask(),
                          bgContext);
            assertTrue(bg.getBitmask() > 0,
                       bgContext);
            if (bg.getBitvalues() != null) {
              assertNotNull(bg.getBitvalues().getBitvalue(),
                            bgContext);
              for (XmlBitvalue bv : bg.getBitvalues().getBitvalue()) {
                assertNotNull(bv,
                              bgContext);
                assertNotNull(bv.getName(),
                              bgContext);
                String bvContext = bgContext + "|" + bv.getName();
                assertFalse(bv.getName().trim().isEmpty(),
                            bvContext);
                assertNotNull(bv.getBitValue(),
                              bvContext);
                assertTrue(bv.getBitValue() >= 0,
                           bvContext);
              }
            }
          }
        }
      }
      assertTrue(moduleClasses.contains(ModuleClass.CPU),
                 context);
      assertNotNull(sreg,
                    context);
    }
  }

  @Test(dataProvider = "failPartFiles")
  public void testFailPartFiles(Path partFile,
                                DeviceStreamer.Version schemaVersion)
  {
    String context = partFile.toString();
    if (schemaVersion != null) {
      context += "with schema " + schemaVersion.name();
    }
    try (InputStream fis = Files.newInputStream(partFile,
                                                StandardOpenOption.READ)) {
      DeviceStreamer.loadDevice(fis,
                                schemaVersion);
    } catch (SAXException | IOException ex) {
      if (schemaVersion == null) {
        fail(context,
             ex);
      } else {
        return;
      }
    }
    if (schemaVersion != null) {
      fail(context + " schema validation failed");
    }
  }

  @Test(dataProvider = "partFiles")
  public void storePartFile(Path partFile,
                            DeviceStreamer.Version schemaVersion) throws IOException, SAXException
  {
    if (schemaVersion == null) {
      String context = partFile.toString();
      XmlPart part;
      try (InputStream fis = Files.newInputStream(partFile,
                                                  StandardOpenOption.READ)) {
        part = DeviceStreamer.loadDevice(fis,
                                         DeviceStreamer.Version.V_1_0);
      }
      assertNotNull(part,
                    context);
      DeviceStreamer.storeDevice(part,
                                 System.out);
    }
  }

}
