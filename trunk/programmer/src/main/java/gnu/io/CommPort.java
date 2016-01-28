/*-------------------------------------------------------------------------
 |   rxtx is a native interface to serial ports in java.
 |   Copyright 1997-2004 by Trent Jarvi taj@www.linux.org.uk
 |
 |   This library is free software; you can redistribute it and/or
 |   modify it under the terms of the GNU Library General Public
 |   License as published by the Free Software Foundation; either
 |   version 2 of the License, or (at your option) any later version.
 |
 |   This library is distributed in the hope that it will be useful,
 |   but WITHOUT ANY WARRANTY; without even the implied warranty of
 |   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 |   Library General Public License for more details.
 |
 |   You should have received a copy of the GNU Library General Public
 |   License along with this library; if not, write to the Free
 |   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 --------------------------------------------------------------------------*/
package gnu.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Trent Jarvi
 * @version %I%, %G%
 * @since JDK1.0
 */
/**
 * CommPort
 *
 * @author reder
 * @version $Id$Id
 */
public abstract class CommPort extends Object implements AutoCloseable
{

  protected String name;

  /**
   * <p>enableReceiveFraming.</p>
   *
   * @param f a int.
   * @throws gnu.io.UnsupportedCommOperationException if any.
   */
  public abstract void enableReceiveFraming(int f)
          throws UnsupportedCommOperationException;

  /**
   * <p>disableReceiveFraming.</p>
   */
  public abstract void disableReceiveFraming();

  /**
   * <p>isReceiveFramingEnabled.</p>
   *
   * @return a boolean.
   */
  public abstract boolean isReceiveFramingEnabled();

  /**
   * <p>getReceiveFramingByte.</p>
   *
   * @return a int.
   */
  public abstract int getReceiveFramingByte();

  /**
   * <p>disableReceiveTimeout.</p>
   */
  public abstract void disableReceiveTimeout();

  /**
   * <p>enableReceiveTimeout.</p>
   *
   * @param time a int.
   * @throws gnu.io.UnsupportedCommOperationException if any.
   */
  public abstract void enableReceiveTimeout(int time)
          throws UnsupportedCommOperationException;

  /**
   * <p>isReceiveTimeoutEnabled.</p>
   *
   * @return a boolean.
   */
  public abstract boolean isReceiveTimeoutEnabled();

  /**
   * <p>getReceiveTimeout.</p>
   *
   * @return a int.
   */
  public abstract int getReceiveTimeout();

  /**
   * <p>enableReceiveThreshold.</p>
   *
   * @param thresh a int.
   * @throws gnu.io.UnsupportedCommOperationException if any.
   */
  public abstract void enableReceiveThreshold(int thresh)
          throws UnsupportedCommOperationException;

  /**
   * <p>disableReceiveThreshold.</p>
   */
  public abstract void disableReceiveThreshold();

  /**
   * <p>getReceiveThreshold.</p>
   *
   * @return a int.
   */
  public abstract int getReceiveThreshold();

  /**
   * <p>isReceiveThresholdEnabled.</p>
   *
   * @return a boolean.
   */
  public abstract boolean isReceiveThresholdEnabled();

  /**
   * <p>setInputBufferSize.</p>
   *
   * @param size a int.
   */
  public abstract void setInputBufferSize(int size);

  /**
   * <p>getInputBufferSize.</p>
   *
   * @return a int.
   */
  public abstract int getInputBufferSize();

  /**
   * <p>setOutputBufferSize.</p>
   *
   * @param size a int.
   */
  public abstract void setOutputBufferSize(int size);

  /**
   * <p>getOutputBufferSize.</p>
   *
   * @return a int.
   */
  public abstract int getOutputBufferSize();

  /** {@inheritDoc} */
  @Override
  public void close()
  {
    printDebugMessage("CommPort:close()");

    try {
      CommPortIdentifier cp = CommPortIdentifier.getPortIdentifier(this);
      if (cp != null) {
        CommPortIdentifier.getPortIdentifier(this).internalClosePort();
      }
    } catch (NoSuchPortException e) {
    }
  }

  ;

	/**
	 * <p>getInputStream.</p>
	 *
	 * @return a {@link java.io.InputStream} object.
	 * @throws java.io.IOException if any.
	 */
	public abstract InputStream getInputStream() throws IOException;

  /**
   * <p>getOutputStream.</p>
   *
   * @return a {@link java.io.OutputStream} object.
   * @throws java.io.IOException if any.
   */
  public abstract OutputStream getOutputStream() throws IOException;

  /**
   * <p>Getter for the field <code>name</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getName()
  {
    printDebugMessage("CommPort:getName()");
    return (name);
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    printDebugMessage("CommPort:toString()");
    return (name);
  }

  private static void printDebugMessage(String msg)
  {
    Logger.getLogger("gnu.io").log(Level.FINE, msg);
  }

}
