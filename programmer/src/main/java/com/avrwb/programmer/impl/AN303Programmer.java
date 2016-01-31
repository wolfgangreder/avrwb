/*
 * $Id$
 *
 * Copyright (C) 2016 Wolfgang Reder <wolfgang.reder@aon.at>.
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
package com.avrwb.programmer.impl;

import com.avrwb.comm.DataBits;
import com.avrwb.comm.Parity;
import com.avrwb.comm.StopBits;
import com.avrwb.comm.UnsupportedCommOperationException;
import com.avrwb.comm.impl.AwbSerialPort;
import com.avrwb.io.DefaultMemoryChunk;
import com.avrwb.io.MemoryChunkOutputStream;
import com.avrwb.programmer.AvrProgrammer;
import com.avrwb.schema.util.Converter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
public class AN303Programmer implements AvrProgrammer
{

  private final AwbSerialPort port;
  private final InputStream is;
  private final OutputStream os;
  private final LineNumberReader reader;
  private boolean inProgramMode;
  private static final Logger LOGGER = Logger.getLogger("com.avrwb.an303programmer");

  public AN303Programmer(String portName) throws IOException
  {
    LOGGER.setLevel(Level.FINEST);
    port = new AwbSerialPort(portName);
    try {
      port.setSerialPortParams(250_000,
                               DataBits.DATA_8,
                               StopBits.STOP_1,
                               Parity.NONE);
      is = port.getInputStream();
      os = port.getOutputStream();
      reader = new LineNumberReader(new InputStreamReader(is));
      while (!"".equals(sendCommand("\r"))) {
      }
      sendCommand("l0"); // echo off
      sendCommand("p2"); // ss autotoggle off
      sendCommand("m0"); // spi mode 0
      sendCommand("q5"); // 1MHz
    } catch (UnsupportedCommOperationException ex) {
      throw new IOException(ex);
    }
  }

  protected final String sendCommand(String cmd) throws IOException
  {
    LOGGER.fine(() -> ">>> " + cmd);
    os.write(cmd.getBytes());
    os.flush();
    String line = reader.readLine();
    LOGGER.fine(() -> "<<< " + line);
    return line;
  }

  private int readByte(String cmd) throws IOException
  {
    String tmp = sendCommand(cmd);
    if (tmp != null && tmp.length() == 8) {
      return Integer.parseInt(tmp.substring(tmp.length() - 2,
                                            tmp.length()),
                              16);
    }
    return -1;
  }

  private byte[] readBytes(String command) throws IOException
  {
    int delta = command.endsWith("\r") ? 2 : 1;
    int byteCount = (command.length() - delta) / 8;
    String tmp = sendCommand(command);
    byte[] buffer = new byte[byteCount];
    for (int i = 0; i < byteCount; ++i) {
      int offset = (i + 1) * 8;
      String s = tmp.substring(offset - 2,
                               offset);
      buffer[i] = (byte) Integer.parseInt(s,
                                          16);
    }
    return buffer;
  }

  @Override
  public Map<Long, String> getSupportedDevices()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  private static final String ENTER_PROGRAM_MODE = "xac530000\r";
  private static final String READ_SIGNATURE0 = "x30000000\r";
  private static final String READ_SIGNATURE1 = "x30000100\r";
  private static final String READ_SIGNATURE2 = "x30000200\r";

  @Override
  public Long readSignature() throws IOException
  {
    long result = 0;
    int b = readByte(READ_SIGNATURE0);
    if (b == -1) {
      return null;
    }
    result = b;
    b = readByte(READ_SIGNATURE1);
    if (b == -1) {
      return null;
    }
    result = (result << 8) | b;
    b = readByte(READ_SIGNATURE2);
    if (b == -1) {
      return null;
    }
    result = (result << 8) | b;
    return result;
  }

  @Override
  public Long enterProgramMode() throws IOException
  {
    try {
      sendCommand("s1");
      sendCommand("s0");
      Thread.sleep(20);
      boolean success = false;
      int attempt = 0;
      while (!success && attempt <= 32) {
        String result = sendCommand(ENTER_PROGRAM_MODE);
        success = result.length() == 8 && "53".equals(result.substring(4,
                                                                       6));
        if (!success) {
          ++attempt;
          sendCommand("s1");
          sendCommand("s0");
        }
      }
      if (!success) {
        return null;
      }
      inProgramMode = true;
      return readSignature();
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    return 0L;
  }

  @Override
  public void leaveProgramMode() throws IOException
  {
//    sendCommand("s1");
    inProgramMode = false;
  }

  private String buildReadFlashCommand(String prefix,
                                       int byteAddress,
                                       String postFix)
  {
    StringBuilder result = new StringBuilder();
    if (prefix != null) {
      result.append(prefix);
    }
    result.append('2');
    if (byteAddress % 2 != 0) {
      result.append("8");
    } else {
      result.append("0");
    }
    byteAddress /= 2;
    result.append(Converter.printHexString((byteAddress & 0xff00) >> 8,
                                           2,
                                           false));
    result.append(Converter.printHexString(byteAddress & 0xff,
                                           2,
                                           false));
    result.append("00");
    if (postFix != null) {
      result.append(postFix);
    }
    return result.toString();
  }

  @Override
  public void readDevice(MemoryChunkOutputStream stream) throws IOException
  {
    int address = 0;
    int batchSize = 31;
    while (address < 4096) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      int chunkStart = address;
      StringBuilder builder = new StringBuilder("x");
      for (int i = 0; i < batchSize && address < 4096; ++i, ++address) {
        builder.append(buildReadFlashCommand(null,
                                             address,
                                             null));
      }
      if (batchSize * 4 < 128) {
        builder.append("\r");
      }
      byte[] bytes = readBytes(builder.toString());
      bos.write(bytes);
      ByteBuffer buf = ByteBuffer.wrap(bos.toByteArray());
      stream.write(new DefaultMemoryChunk(chunkStart,
                                          buf,
                                          bos.size()));
    }
  }

  @Override
  public void close() throws IOException
  {
    if (inProgramMode) {
      leaveProgramMode();
    }
    port.close();
  }

}
