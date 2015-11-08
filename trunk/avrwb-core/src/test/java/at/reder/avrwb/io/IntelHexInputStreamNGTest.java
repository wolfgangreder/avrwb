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
package at.reder.avrwb.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class IntelHexInputStreamNGTest
{

  public IntelHexInputStreamNGTest()
  {
  }

  private String readFileToString(InputStream is) throws IOException
  {
    StringBuilder builder = new StringBuilder();
    try (InputStreamReader reader = new InputStreamReader(is,
                                                          StandardCharsets.US_ASCII)) {
      char[] buffer = new char[256];
      int read;
      while ((read = reader.read(buffer)) > 0) {
        builder.append(buffer,
                       0,
                       read);
      }
    }
    return builder.toString();
  }

  private void readCompare(URL url,
                           int bufSize) throws IOException
  {
    File tmpFile = File.createTempFile("test",
                                       ".tmp");
    tmpFile.deleteOnExit();
    try {
      try (IntelHexOutputStream hos = new IntelHexOutputStream(new FileOutputStream(tmpFile));
              IntelHexInputStream his = new IntelHexInputStream(url.openStream())) {
        final ByteBuffer buffer = ByteBuffer.allocate(bufSize);
        MemoryChunk chunk;
        while ((chunk = his.read(buffer)) != null) {
          hos.write(chunk);
        }
      }
      String expected = readFileToString(url.openStream());
      String actual = readFileToString(new FileInputStream(tmpFile));
      assertEquals(expected.length(),
                   actual.length());
      for (int i = 0; i < expected.length(); ++i) {
        char e = expected.charAt(i);
        char a = actual.charAt(i);
        assertEquals("Position " + i,
                     e,
                     a);
      }
    } finally {
      tmpFile.delete();
    }
  }

  private void readCompare(URL url) throws IOException
  {
    File tmpFile = File.createTempFile("test",
                                       ".tmp");
    tmpFile.deleteOnExit();
    try {
      try (IntelHexOutputStream hos = new IntelHexOutputStream(new FileOutputStream(tmpFile));
              IntelHexInputStream his = new IntelHexInputStream(url.openStream())) {
        MemoryChunk chunk;
        while ((chunk = his.read()) != null) {
          hos.write(chunk);
        }
      }
      String expected = readFileToString(url.openStream());
      String actual = readFileToString(new FileInputStream(tmpFile));
      assertEquals(expected.length(),
                   actual.length());
      for (int i = 0; i < expected.length(); ++i) {
        char e = expected.charAt(i);
        char a = actual.charAt(i);
        assertEquals("Position " + i,
                     e,
                     a);
      }
    } finally {
      tmpFile.delete();
    }
  }

  @Test
  public void test_spi2uarteep() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.eep"));
  }

  @Test
  public void test_spi2uarteep_16() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.eep"),
                16);
  }

  @Test
  public void test_spi2uarteep_10() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.eep"),
                10);
  }

  @Test
  public void test_spi2uarteep_1() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.eep"),
                1);
  }

  @Test
  public void test_spi2uarteep_17() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.eep"),
                17);
  }

  @Test
  public void test_spi2uarteep_4096() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.eep"),
                4096);
  }

  @Test
  public void test_spi2uarthex_16() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.hex"),
                16);
  }

  @Test
  public void test_spi2uarthex() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.hex"));
  }

  @Test
  public void test_spi2uarthex_10() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.hex"),
                10);
  }

  @Test
  public void test_spi2uarthex_1() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.hex"),
                1);
  }

  @Test
  public void test_spi2uarthex_17() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.hex"),
                17);
  }

  @Test
  public void test_spi2uarthex_4096() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/spi2uart.hex"),
                4096);
  }

  @Test(enabled = false) // der test lässt sich momentan nicht validieren
  public void test_stellbadenhex() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/stellbaden.hex"),
                16);
  }

  @Test
  public void test_stellbadeneep() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/stellbaden.eep"));
  }

  @Test
  public void test_stellbadeneep_16() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/stellbaden.eep"),
                16);
  }

  @Test
  public void test_stellbadeneep_10() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/stellbaden.eep"),
                10);
  }

  @Test
  public void test_stellbadeneep_1() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/stellbaden.eep"),
                1);
  }

  @Test
  public void test_stellbadeneep_17() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/stellbaden.eep"),
                17);
  }

  @Test
  public void test_stellbadeneep_4096() throws IOException
  {
    readCompare(IntelHexInputStream.class.getResource("/hexfiles/stellbaden.eep"),
                4096);
  }

}
