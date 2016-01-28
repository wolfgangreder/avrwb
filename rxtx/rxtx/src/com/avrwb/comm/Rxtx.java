/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avrwb.comm;

import com.avrwb.comm.impl.AwbSerialPort;

/**
 *
 * @author wolfi
 */
public class Rxtx
{

  /**
   * @param args the command line arguments
   */
  @SuppressWarnings("CallToPrintStackTrace")
  public static void main(String[] args)
  {
    try (AwbSerialPort port = new AwbSerialPort("/dev/ttyUSB1")) {
//      port.setSerialPortParams(250_000,
//                               DataBits.DATA_8,
//                               StopBits.STOP_1,
//                               Parity.NONE);
//      try (OutputStream os = port.getOutputStream()) {
//        os.write(0xcc);
//      }
      port.close();
    } catch (Error | Exception ex) {
      ex.printStackTrace();
    }
  }

}
