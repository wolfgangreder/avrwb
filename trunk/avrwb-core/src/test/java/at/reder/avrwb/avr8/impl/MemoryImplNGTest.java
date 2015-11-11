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
package at.reder.avrwb.avr8.impl;

import at.reder.atmelschema.XA_AddressSpace;
import at.reder.atmelschema.XA_AvrToolsDeviceFile;
import at.reder.avrwb.io.DefaultMemoryChunk;
import at.reder.avrwb.io.IntelHexInputStream;
import at.reder.avrwb.io.MemoryChunk;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class MemoryImplNGTest
{

  private final Charset utf8;
  private static URL mega8URL;
  private static XA_AvrToolsDeviceFile mega8;
  private static XA_AddressSpace dataSpace;
  private MemoryImpl memory;

  public MemoryImplNGTest()
  {
    utf8 = Charset.forName("utf-8");
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    mega8URL = MemoryImplNGTest.class.getResource("/com/atmel/devices/ATmega8.xml");
    mega8 = XA_AvrToolsDeviceFile.load(mega8URL);
    for (XA_AddressSpace space : mega8.getDevice("ATmega8").getAdressSpaces()) {
      if ("data".equals(space.getId())) {
        dataSpace = space;
        break;
      }
    }
    assertNotNull("dataSpace==null",
                  dataSpace);
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
    memory = (MemoryImpl) new MemoryBuilderImpl().fromAddressSpace(dataSpace).build();
  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
    memory = null;
  }

  @Test
  public void testInitialize()
  {
    ByteBuffer bb = ByteBuffer.wrap("Wolfgang Reder".getBytes(utf8));
    MemoryChunk chunk = new DefaultMemoryChunk(0,
                                               bb,
                                               bb.capacity());
    assertTrue(memory.initialize(chunk));
    bb = ByteBuffer.allocate(memory.getSize() + 1);
    chunk = new DefaultMemoryChunk(0,
                                   bb,
                                   bb.capacity());
    assertFalse(memory.initialize(chunk));
  }

  @Test
  public void testGetId()
  {
    assertEquals("data",
                 memory.getId());
  }

  @Test
  public void testGetName()
  {
    assertEquals("data",
                 memory.getName());
  }

  @Test
  public void testGetByteOrder()
  {
    assertEquals(ByteOrder.LITTLE_ENDIAN,
                 memory.getByteOrder());
  }

  @Test
  public void testGetSize()
  {
    assertEquals(0x460,
                 memory.getSize());
  }

  @Test
  public void testGetStart()
  {
    assertEquals(0,
                 memory.getStart());
  }

  @Test
  public void testReadCharactersUntilEOS_1()
  {
    ByteBuffer bb = ByteBuffer.wrap("Reder Wolfgang\0Austria".getBytes(utf8));
    MemoryChunk chunk = new DefaultMemoryChunk(0,
                                               bb,
                                               bb.capacity());
    memory.initialize(chunk);
    int address = 0;
    char[] buffer = new char[64];
    int offset = 0;
    int numCharacters = -1;
    int expResult = 14;
    int result = memory.readCharacters(address,
                                       buffer,
                                       offset,
                                       numCharacters,
                                       utf8);
    assertEquals(expResult,
                 result);
    assertEquals("Reder Wolfgang",
                 new String(buffer,
                            0,
                            result));
  }

  @Test
  public void testReadCharactersUntilEOS_2()
  {
    ByteBuffer bb = ByteBuffer.wrap("Reder Wolfgang\nAustria".getBytes(utf8));
    MemoryChunk chunk = new DefaultMemoryChunk(0,
                                               bb,
                                               bb.capacity());
    memory.initialize(chunk);
    int address = 0;
    char[] buffer = new char[22];
    int offset = 0;
    int numCharacters = -1;
    int expResult = 22;
    int result = memory.readCharacters(address,
                                       buffer,
                                       offset,
                                       numCharacters,
                                       utf8);
    assertEquals(expResult,
                 result);
    assertEquals("Reder Wolfgang\nAustria",
                 new String(buffer,
                            0,
                            result));
  }

  @Test
  public void testReadCharactersUntilEOS_3()
  {
    ByteBuffer bb = ByteBuffer.wrap("R€der Wolfgang\nAustria".getBytes(utf8));
    MemoryChunk chunk = new DefaultMemoryChunk(0,
                                               bb,
                                               bb.capacity());
    memory.initialize(chunk);
    int address = 0;
    char[] buffer = new char[20];
    int offset = 0;
    int numCharacters = -1;
    int expResult = 20;
    int result = memory.readCharacters(address,
                                       buffer,
                                       offset,
                                       numCharacters,
                                       utf8);
    assertEquals(expResult,
                 result);
    assertEquals("R€der Wolfgang\nAustr",
                 new String(buffer,
                            0,
                            result));
  }

  @Test
  public void testReadCharactersUntilEOS_4()
  {
    ByteBuffer bb = ByteBuffer.wrap("R€der Wolfgang\nAustria".getBytes(utf8));
    MemoryChunk chunk = new DefaultMemoryChunk(0,
                                               bb,
                                               bb.capacity());
    memory.initialize(chunk);
    int address = 0;
    char[] buffer = new char[memory.getSize() + 2];
    int offset = 0;
    int numCharacters = -1;
    int expResult = 22;
    int result = memory.readCharacters(address,
                                       buffer,
                                       offset,
                                       numCharacters,
                                       utf8);
    assertEquals(expResult,
                 result);
    assertEquals("R€der Wolfgang\nAustria",
                 new String(buffer,
                            0,
                            result));
  }

  @Test
  public void testReadCharactersUntilLimit_1()
  {
    ByteBuffer bb = ByteBuffer.wrap("R€der Wolfgang\nAustria".getBytes(utf8));
    MemoryChunk chunk = new DefaultMemoryChunk(0,
                                               bb,
                                               bb.capacity());
    memory.initialize(chunk);
    int address = 0;
    char[] buffer = new char[memory.getSize() + 2];
    int offset = 0;
    int numCharacters = 5;
    int expResult = 5;
    int result = memory.readCharacters(address,
                                       buffer,
                                       offset,
                                       numCharacters,
                                       utf8);
    assertEquals(expResult,
                 result);
    assertEquals("R€der",
                 new String(buffer,
                            0,
                            result));
  }

  @Test
  public void testReadCharactersUntilLimit_2()
  {
    ByteBuffer bb = ByteBuffer.wrap("R€der Wolfgang\0Austria".getBytes(utf8));
    memory.initialize(new DefaultMemoryChunk(0,
                                             bb,
                                             bb.capacity()));
    int address = 0;
    char[] buffer = new char[memory.getSize() + 2];
    int offset = 0;
    int numCharacters = 20;
    int expResult = 20;
    int result = memory.readCharacters(address,
                                       buffer,
                                       offset,
                                       numCharacters,
                                       utf8);
    assertEquals(expResult,
                 result);
    assertEquals("R€der Wolfgang\0Austr",
                 new String(buffer,
                            0,
                            result));
  }

  @Test
  public void testReadCharactersUntilLimit_3()
  {
    ByteBuffer bb = ByteBuffer.wrap("R€der Wolfgang\0Austria".getBytes(utf8));
    MemoryChunk chunk = new DefaultMemoryChunk(0,
                                               bb,
                                               bb.capacity());
    memory.initialize(chunk);
    int address = 0;
    char[] buffer = new char[memory.getSize() + 2];
    int offset = 0;
    int numCharacters = 25;
    int expResult = 25;
    int result = memory.readCharacters(address,
                                       buffer,
                                       offset,
                                       numCharacters,
                                       utf8);
    assertEquals(expResult,
                 result);
    assertEquals("R€der Wolfgang\0Austria\0\0\0",
                 new String(buffer,
                            0,
                            result));
  }

  @Test
  public void testReadCharactersUntilLimit_4()
  {
    ByteBuffer bb = ByteBuffer.wrap("R€der Wolfgang\0Austria".getBytes(utf8));
    MemoryChunk chunk = new DefaultMemoryChunk(0,
                                               bb,
                                               bb.capacity());
    memory.initialize(chunk);
    int address = 0;
    char[] buffer = new char[20];
    int offset = 0;
    int numCharacters = 25;
    int expResult = 20;
    int result = memory.readCharacters(address,
                                       buffer,
                                       offset,
                                       numCharacters,
                                       utf8);
    assertEquals(expResult,
                 result);
    assertEquals("R€der Wolfgang\0Austr",
                 new String(buffer,
                            0,
                            result));
  }

  @Test
  public void testWriteCharacters_1()
  {
    int address = 0;
    String str = "R€der Wolfgang\0Austria";
    int offset = 0;
    int toWrite = -1;
    int expResult = 16;
    int result = memory.writeCharacters(address,
                                        str.toCharArray(),
                                        offset,
                                        toWrite,
                                        utf8);
    assertEquals(expResult,
                 result);
    char[] buffer = new char[24];
    result = memory.readCharacters(address,
                                   buffer,
                                   offset,
                                   toWrite,
                                   utf8);
    expResult = 14;
    assertEquals(expResult,
                 result);
    assertEquals("R€der Wolfgang",
                 new String(buffer,
                            0,
                            result));
  }

  @Test
  public void testWriteCharacters_2()
  {
    int address = memory.getSize() - 10;
    String str = "R€der Wolfgang\0Austria";
    int offset = 0;
    int toWrite = -1;
    int expResult = 10;
    int result = memory.writeCharacters(address,
                                        str.toCharArray(),
                                        offset,
                                        toWrite,
                                        utf8);
    assertEquals(expResult,
                 result);
    char[] buffer = new char[24];
    result = memory.readCharacters(address,
                                   buffer,
                                   offset,
                                   toWrite,
                                   utf8);
    expResult = 8;
    assertEquals(expResult,
                 result);
    assertEquals("R€der Wo",
                 new String(buffer,
                            0,
                            result));
  }

  @Test
  public void readString_1()
  {
    int address = memory.getSize() - 10;
    memory.writeCharacters(address,
                           "R€der Wolfgang".toCharArray(),
                           0,
                           -1,
                           utf8);
    StringBuilder str = memory.readString(address,
                                          utf8,
                                          4);
    assertNotNull(str);
    assertEquals("R€der Wo",
                 str.toString());
    address = 0;
    memory.writeCharacters(address,
                           "R€der Wolfgang\0Austria".toCharArray(),
                           0,
                           22,
                           utf8);
    str = memory.readString(address,
                            utf8,
                            4);
    assertEquals("R€der Wolfgang",
                 str.toString());
  }

  @Test
  public void readString_2()
  {
    int address = memory.getSize() - 10;
    memory.writeCharacters(address,
                           "R€der Wolfgang".toCharArray(),
                           0,
                           -1,
                           utf8);
    StringBuilder str = memory.readString(address,
                                          utf8,
                                          1024);
    assertNotNull(str);
    assertEquals("R€der Wo",
                 str.toString());
    address = 0;
    memory.writeCharacters(address,
                           "R€der Wolfgang\0Austria".toCharArray(),
                           0,
                           22,
                           utf8);
    str = memory.readString(address,
                            utf8,
                            1024);
    assertEquals("R€der Wolfgang",
                 str.toString());
  }

  @Test
  public void testGetFloat() throws IOException
  {

    MemoryChunk eeprom;
    try (IntelHexInputStream ihs = new IntelHexInputStream(getClass().getResourceAsStream("/hexfiles/awb_memorytest.eep"))) {
      eeprom = ihs.read();
    }
    assertNotNull("eeprom==null",
                  eeprom);
    memory.initialize(eeprom);
    float expected = 2.7182818284590452353602874713526624977572470937000f;
    float actual = memory.getFloatAt(4);
    assertEquals(expected,
                 actual,
                 1e-6);
    expected = 3.1415926535897932384626433832795028841971693993751f;
    actual = memory.getFloatAt(0);
    assertEquals(expected,
                 actual,
                 1e-6);
    expected = 0;
    actual = memory.getFloatAt(0xc);
    assertEquals(expected,
                 actual,
                 1e-6);
    expected = Float.NaN;
    actual = memory.getFloatAt(0x8);
    assertEquals(expected,
                 actual);
    expected = Float.POSITIVE_INFINITY;
    actual = memory.getFloatAt(0x10);
    assertEquals(expected,
                 actual);
  }

  @Test
  public void testBytes()
  {
    memory.setByteAt(0,
                     0xff);
    assertEquals(0xff,
                 memory.getByteAt(0));
    memory.setByteAt(0,
                     1);
    assertEquals(1,
                 memory.getByteAt(0));
    memory.setByteAt(0,
                     0x7f);
    assertEquals(0x7f,
                 memory.getByteAt(0));
    memory.setByteAt(0,
                     0x80);
    assertEquals(0x80,
                 memory.getByteAt(0));
  }

}
