/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avrwb.comm;

import com.avrwb.comm.impl.AwbSerialPort;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

/**
 *
 * @author wolfi
 */
public class Rxtx
{

  @SuppressWarnings("CallToPrintStackTrace")
  private static void threadFunc(AwbSerialPort port)
  {
    try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(port.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    } catch (Error | Exception ex) {
      ex.printStackTrace();
    }
//    System.err.println("thread done");
//    try (InputStreamReader reader = new InputStreamReader(port.getInputStream())) {
//      int b;
//      while (true) {
//        b = reader.read();
//        if (b != -1) {
//          if (b != '\r') {
//            System.out.print((char) b);
//          }
//        } else {
//          System.out.println("-1 read");
//        }
//      }
//    } catch (Error | Exception ex) {
//      ex.printStackTrace();
//    }
  }

  protected static String sendCommand(OutputStream os,
                                      LineNumberReader reader,
                                      String cmd) throws IOException
  {
    os.write(cmd.getBytes());
    os.flush();
    return reader.readLine();
  }

  /**
   * @param args the command line arguments
   */
  @SuppressWarnings("CallToPrintStackTrace")
  public static void main(String[] args)
  {
    try (final AwbSerialPort port = new AwbSerialPort(args[0])) {
      final Thread th = new Thread(() -> {
        threadFunc(port);
      });

      port.setSerialPortParams(250_000,
                               DataBits.DATA_8,
                               StopBits.STOP_1,
                               Parity.NONE);
//      th.setDaemon(true);
//      th.start();
      try (OutputStream os = port.getOutputStream();
              LineNumberReader reader = new LineNumberReader(new InputStreamReader(port.getInputStream()))) {
        String line = sendCommand(os,
                                  reader,
                                  "l0p2m0q3s1");
        line = sendCommand(os,
                           reader,
                           "s0");
        Thread.sleep(20);
        line = sendCommand(os,
                           reader,
                           "xac530000\r");
        System.out.print("1:");
        System.out.println(line);
        line = sendCommand(os,
                           reader,
                           "x30000000\r");
        System.out.print("2:");
        System.out.println(line);
        line = sendCommand(os,
                           reader,
                           "x30000100\r");
        System.out.print("3:");
        System.out.println(line);
        line = sendCommand(os,
                           reader,
                           "x30000200\r");
        System.out.print("4:");
        System.out.println(line);
        // os.write("s1\r\n".getBytes());
      }
      Thread.sleep(100_000);
    } catch (Error | Exception ex) {
      ex.printStackTrace();
    }
  }

}
