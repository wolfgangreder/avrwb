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
package com.avrwb.comm.impl;

import com.avrwb.avr8.helper.Utils;
import com.avrwb.comm.DataBits;
import com.avrwb.comm.FlowControl;
import com.avrwb.comm.Parity;
import com.avrwb.comm.SerialPort;
import com.avrwb.comm.SerialPortEventListener;
import com.avrwb.comm.StopBits;
import com.avrwb.comm.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.TooManyListenersException;

/**
 *
 * @author wolfi
 */
public final class AwbSerialPort extends SerialPort
{

  static {
    System.load(Utils.findNativeLibrary("rxtxSerial"));
  }

  private final class MyOutputStream extends OutputStream
  {

    @Override
    public void write(int b) throws IOException
    {
      sendByte(b);
    }

    @Override
    public void write(byte[] b,
                      int off,
                      int len) throws IOException
    {
      sendBytes(b,
                off,
                len);
    }

    @Override
    public void close() throws IOException
    {
      AwbSerialPort.this.flush();
    }

    @Override
    public void flush() throws IOException
    {
      AwbSerialPort.this.flush();
    }

  }

  private final class MyInputStream extends InputStream
  {

    @Override
    public int read() throws IOException
    {
      byte[] b = new byte[1];
      int result = read(b,
                        0,
                        1);
      if (result == 1) {
        return b[0];
      }
      return -1;
    }

    @Override
    public int read(byte[] b,
                    int off,
                    int len) throws IOException
    {
      try {
        return readBytes(b,
                         off,
                         len,
                         readTimeOut * 1000);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
      return -1;
    }

    @Override
    public void close() throws IOException
    {
      flush();
    }

    @Override
    public int available() throws IOException
    {
      return bytesAvailable();
    }

  }
  private final Object lock = new Object();
  private long fd = -1;
  private int readTimeOut = -1;
  private final MyOutputStream os;
  private final InputStream is;
  private int baudRate;
  private DataBits dataBits;
  private StopBits stopBits;
  private Parity parity;

  public AwbSerialPort(String portName) throws IOException
  {
    super(portName);
    fd = nativeOpen(portName);
    if (fd != -1) {
      os = new MyOutputStream();
      is = new MyInputStream();
    } else {
      throw new IOException("cannot open port " + portName);
    }
  }

  private native long nativeOpen(String portName) throws IOException;

  public boolean isOpen()
  {
    synchronized (lock) {
      return fd != -1;
    }
  }

  @Override
  public int getBaudRate()
  {
    synchronized (lock) {
      return baudRate;
    }
  }

  private native void readPortParams() throws IOException; // called from setSerialPortParams and nativeOpen

  @Override
  public native void setSerialPortParams(int baudrate,
                                         DataBits dataBits,
                                         StopBits stopBits,
                                         Parity parity) throws UnsupportedCommOperationException, IOException;

  private native void sendByte(int b) throws IOException;

  private native void sendBytes(byte[] data,
                                int offset,
                                int len) throws IOException;

  private native int readBytes(byte[] buffer,
                               int offset,
                               int len,
                               int microsToWait) throws IOException, InterruptedException;

  private native void flush() throws IOException;

  private native int bytesAvailable() throws IOException;

  @Override
  public DataBits getDataBits()
  {
    synchronized (lock) {
      return dataBits;
    }
  }

  @Override
  public StopBits getStopBits()
  {
    synchronized (lock) {
      return stopBits;
    }
  }

  @Override
  public Parity getParity()
  {
    synchronized (lock) {
      return parity;
    }
  }

  @Override
  public InputStream getInputStream()
  {
    return is;
  }

  @Override
  public OutputStream getOutputStream()
  {
    return os;
  }

  @Override
  public void close() throws IOException
  {
    flush();
    fd = nativeClose(fd);
  }

  private native long nativeClose(long fd);

  @Override
  public void enableReceiveTimeout(int rcvTimeout)
  {
    synchronized (lock) {
      this.readTimeOut = rcvTimeout;
    }
  }

  @Override
  public void disableReceiveTimeout()
  {
    synchronized (lock) {
      readTimeOut = -1;
    }
  }

  @Override
  public boolean isReceiveTimeoutEnabled()
  {
    synchronized (lock) {
      return this.readTimeOut != -1;
    }
  }

  @Override
  public int getReceiveTimeout()
  {
    synchronized (lock) {
      return readTimeOut;
    }
  }

  @Override
  public void sendBreak(int millis)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setFlowControlMode(Set<FlowControl> flowcontrol) throws UnsupportedCommOperationException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Set<FlowControl> getFlowControlMode()
  {
    java.lang.Thread.currentThread().interrupt();
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setDTR(boolean dtr)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isDTR()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setRTS(boolean rts)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isRTS()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isCTS()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isDSR()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isRI()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isCD()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void addEventListener(SerialPortEventListener lsnr) throws TooManyListenersException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void removeEventListener()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void notifyOnDataAvailable(boolean enable)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void notifyOnOutputEmpty(boolean enable)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void notifyOnCTS(boolean enable)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void notifyOnDSR(boolean enable)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void notifyOnRingIndicator(boolean enable)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void notifyOnCarrierDetect(boolean enable)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void notifyOnOverrunError(boolean enable)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void notifyOnParityError(boolean enable)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void notifyOnFramingError(boolean enable)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void notifyOnBreakInterrupt(boolean enable)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void enableReceiveThreshold(int thresh) throws UnsupportedCommOperationException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void disableReceiveThreshold()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isReceiveThresholdEnabled()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getReceiveThreshold()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void enableReceiveFraming(int framingByte) throws UnsupportedCommOperationException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void disableReceiveFraming()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isReceiveFramingEnabled()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getReceiveFramingByte()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setInputBufferSize(int size)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getInputBufferSize()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setOutputBufferSize(int size)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getOutputBufferSize()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
