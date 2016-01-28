/* ------------------------------------------------------------------------
 * |   Zystem is a native interface for message reporting in java.
 * |   Copyright 2002 by Trent Jarvi taj@www.linux.org.uk.
 * |
 * |   This library is free software; you can redistribute it and/or
 * |   modify it under the terms of the GNU Library General Public
 * |   License as published by the Free Software Foundation; either
 * |   version 2 of the License, or (at your option) any later version.
 * |
 * |   This library is distributed in the hope that it will be useful,
 * |   but WITHOUT ANY WARRANTY; without even the implied warranty of
 * |   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * |   Library General Public License for more details.
 * |
 * |   You should have received a copy of the GNU Library General Public
 * |   License along with this library; if not, write to the Free
 * |   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * -------------------------------------------------------------------------- */
package gnu.io;

import java.io.RandomAccessFile;

/**
 * <p>Zystem class.</p>
 *
 * @author reder
 * @version $Id$Id
 */
public class Zystem
{

  /** Constant <code>SILENT_MODE=0</code> */
  public static final int SILENT_MODE = 0;
  /** Constant <code>FILE_MODE=1</code> */
  public static final int FILE_MODE = 1;
  /** Constant <code>NET_MODE=2</code> */
  public static final int NET_MODE = 2;
  /** Constant <code>MEX_MODE=3</code> */
  public static final int MEX_MODE = 3;
  /** Constant <code>PRINT_MODE=4</code> */
  public static final int PRINT_MODE = 4;
  /** Constant <code>J2EE_MSG_MODE=5</code> */
  public static final int J2EE_MSG_MODE = 5;
  /** Constant <code>J2SE_LOG_MODE=6</code> */
  public static final int J2SE_LOG_MODE = 6;

  static int mode;

  static {
    /*
     * The rxtxZystem library uses Python code and is not
     * included with RXTX. A seperate library will be released
     * to avoid potential license conflicts.
     *
     * Trent Jarvi taj@www.linux.org.uk
     */

    //System.loadLibrary( "rxtxZystem" );
    mode = SILENT_MODE;
  }

  private static String target;

  /**
   * <p>Constructor for Zystem.</p>
   *
   * @param m a int.
   * @throws gnu.io.UnSupportedLoggerException if any.
   */
  public Zystem(int m) throws UnSupportedLoggerException
  {
    mode = m;
    startLogger("asdf");
  }

  /**
   * Constructor.
   * Mode is taken from the java system property "gnu.io.log.mode". The available values are :<ul>
   * <li> SILENT_MODE No logging
   * <li> FILE_MODE log to file
   * <li> NET_MODE
   * <li> MEX_MODE
   * <li> PRINT_MODE
   * <li> J2EE_MSG_MODE
   * <li> J2SE_LOG_MODE log to java.util.logging
   * </ul>
   *
   * @throws gnu.io.UnSupportedLoggerException err
   */
  public Zystem() throws UnSupportedLoggerException
  {
    String s = System.getProperty("gnu.io.log.mode");
    if (s != null) {
      switch (s) {
        case "SILENT_MODE":
          mode = SILENT_MODE;
          break;
        case "FILE_MODE":
          mode = FILE_MODE;
          break;
        case "NET_MODE":
          mode = NET_MODE;
          break;
        case "MEX_MODE":
          mode = MEX_MODE;
          break;
        case "PRINT_MODE":
          mode = PRINT_MODE;
          break;
        case "J2EE_MSG_MODE":
          mode = J2EE_MSG_MODE;
          break;
        case "J2SE_LOG_MODE":
          mode = J2SE_LOG_MODE;
          break;
        default:
          try {
            mode = Integer.parseInt(s);
          } catch (NumberFormatException e) {
            mode = SILENT_MODE;
          }
          break;
      }
    } else {
      mode = SILENT_MODE;
    }
    startLogger("asdf");
  }

  /**
   * <p>startLogger.</p>
   *
   * @throws gnu.io.UnSupportedLoggerException if any.
   */
  public void startLogger() throws UnSupportedLoggerException
  {
    if (mode == SILENT_MODE || mode == PRINT_MODE) {
      //nativeNetInit( );
      return;
    }
    throw new UnSupportedLoggerException("Target Not Allowed");
  }

  /* accept the host or file to log to. */
  /**
   * <p>startLogger.</p>
   *
   * @param t a {@link java.lang.String} object.
   * @throws gnu.io.UnSupportedLoggerException if any.
   */
  public void startLogger(String t) throws UnSupportedLoggerException
  {
    target = t;
    /*
     * if ( mode == NET_MODE )
     * {
     * nativeNetInit( );
     * }
     * if ( nativeInit( ) )
     * {
     * throw new UnSupportedLoggerException(
     * "Port initializion failed" );
     * }
     */
    return;
  }

  /**
   * <p>finalize.</p>
   */
  public void finalize()
  {
    /*
     * if ( mode == NET_MODE )
     * {
     * nativeNetFinalize( );
     * }
     * nativeFinalize();
     */
    mode = SILENT_MODE;
    target = null;
  }

  /**
   * <p>filewrite.</p>
   *
   * @param s a {@link java.lang.String} object.
   */
  public void filewrite(String s)
  {
    try {
      RandomAccessFile w = new RandomAccessFile(target,
                                                "rw");;
      w.seek(w.length());
      w.writeBytes(s);
      w.close();
    } catch (Exception e) {
      System.out.println("Debug output file write failed");
    }
  }

  /**
   * <p>report.</p>
   *
   * @param s a {@link java.lang.String} object.
   * @return a boolean.
   */
  public boolean report(String s)
  {
    if (mode == NET_MODE) {
      //	return( nativeNetReportln( s ) );
    } else if (mode == PRINT_MODE) {
      System.out.println(s);
      return (true);
    } else if (mode == MEX_MODE) {
      //	return( nativeMexReport( s ) );
    } else if (mode == SILENT_MODE) {
      return (true);
    } else if (mode == FILE_MODE) {
      filewrite(s);
    } else if (mode == J2EE_MSG_MODE) {
      return (false);
    } else if (mode == J2SE_LOG_MODE) {
      java.util.logging.Logger.getLogger("gnu.io").fine(s);
      return (true);
    }
    return (false);
  }

  /**
   * <p>reportln.</p>
   *
   * @return a boolean.
   */
  public boolean reportln()
  {
    boolean b;
    if (mode == NET_MODE) {
      //	b= nativeNetReportln( "\n" );
      //	return(b);
    } else if (mode == PRINT_MODE) {
      System.out.println();
      return (true);
    } else if (mode == MEX_MODE) {
      //	b = nativeMexReportln( "\n" );
      //	return(b);
    } else if (mode == SILENT_MODE) {
      return (true);
    } else if (mode == FILE_MODE) {
      filewrite("\n");
    } else if (mode == J2EE_MSG_MODE) {
      return (false);
    }
    return (false);
  }

  /**
   * <p>reportln.</p>
   *
   * @param s a {@link java.lang.String} object.
   * @return a boolean.
   */
  public boolean reportln(String s)
  {
    boolean b;
    if (mode == NET_MODE) {
      //	b= nativeNetReportln( s + "\n" );
      //	return(b);
    } else if (mode == PRINT_MODE) {
      System.out.println(s);
      return (true);
    } else if (mode == MEX_MODE) {
      //	b = nativeMexReportln( s + "\n" );
      //	return(b);
    } else if (mode == SILENT_MODE) {
      return (true);
    } else if (mode == FILE_MODE) {
      filewrite(s + "\n");
    } else if (mode == J2EE_MSG_MODE) {
      return (false);
    } else if (mode == J2SE_LOG_MODE) {
      return (true);
    }
    return (false);
  }

  /*
   * private native boolean nativeInit( );
   * private native void nativeFinalize();
   */

 /* open and close the socket */
 /*
   * private native boolean nativeNetInit( );
   * private native void nativeNetFinalize();
   */

 /* dumping to a remote machine */
 /*
   * public native boolean nativeNetReport( String s );
   * public native boolean nativeNetReportln( String s );
   */

 /* specific to Matlab */
 /*
   * public native boolean nativeMexReport( String s );
   * public native boolean nativeMexReportln( String s );
   */
}
