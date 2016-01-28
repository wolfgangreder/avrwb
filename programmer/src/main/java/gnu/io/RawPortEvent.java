/* Non functional contact taj@www.linux.org.uk for details */

/*-------------------------------------------------------------------------
|   rxtx is a native interface to serial ports in java.
|   Copyright 1997-2004 by Trent Jarvi taj@www.linux.org.uk.
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
import java.util.*;

/**
 * <p>RawPortEvent class.</p>
 *
 * @author Trent Jarvi
 * @version %I%, %G%
 * @since JDK1.0
 */
public class RawPortEvent extends EventObject
{
	/** Constant <code>DATA_AVAILABLE=1</code> */
	public static final int DATA_AVAILABLE      =1;
	/** Constant <code>OUTPUT_BUFFER_EMPTY=2</code> */
	public static final int OUTPUT_BUFFER_EMPTY =2;
	/** Constant <code>CTS=3</code> */
	public static final int CTS                 =3;
	/** Constant <code>DSR=4</code> */
	public static final int DSR                 =4;
	/** Constant <code>RI=5</code> */
	public static final int RI                  =5;
	/** Constant <code>CD=6</code> */
	public static final int CD                  =6;
	/** Constant <code>OE=7</code> */
	public static final int OE                  =7;
	/** Constant <code>PE=8</code> */
	public static final int PE                  =8;
	/** Constant <code>FE=9</code> */
	public static final int FE                  =9;
	/** Constant <code>BI=10</code> */
	public static final int BI                 =10;

	private boolean OldValue;
	private boolean NewValue;
	private int eventType;
	/*public int eventType           =0; depricated */

	/**
	 * <p>Constructor for RawPortEvent.</p>
	 *
	 * @param srcport a {@link gnu.io.RawPort} object.
	 * @param eventtype a int.
	 * @param oldvalue a boolean.
	 * @param newvalue a boolean.
	 */
	public RawPortEvent(RawPort srcport, int eventtype, boolean oldvalue, boolean newvalue)
	{
		super( srcport );	
		OldValue=oldvalue;
		NewValue=newvalue;
		eventType=eventtype;
	}
	/**
	 * <p>Getter for the field <code>eventType</code>.</p>
	 *
	 * @return a int.
	 */
	public int getEventType()
	{
		return(eventType);
	}
	/**
	 * <p>getNewValue.</p>
	 *
	 * @return a boolean.
	 */
	public boolean getNewValue()
	{
		return( NewValue );
	}
	/**
	 * <p>getOldValue.</p>
	 *
	 * @return a boolean.
	 */
	public boolean getOldValue()
	{
		return( OldValue );
	}
}
