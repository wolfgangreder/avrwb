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
 * <p>ParallelPortEvent class.</p>
 *
 * @author Trent Jarvi
 * @version %I%, %G%
 * @since JDK1.0
 */
public class ParallelPortEvent extends EventObject
{
	/** Constant <code>PAR_EV_ERROR=1</code> */
	static public final int  PAR_EV_ERROR   =1;
	/** Constant <code>PAR_EV_BUFFER=2</code> */
	static public final int  PAR_EV_BUFFER  =2;


	private boolean OldValue;
	private boolean NewValue;
	private int eventType;
	/*public int eventType           =0; depricated */

	/**
	 * <p>Constructor for ParallelPortEvent.</p>
	 *
	 * @param srcport a {@link gnu.io.ParallelPort} object.
	 * @param eventtype a int.
	 * @param oldvalue a boolean.
	 * @param newvalue a boolean.
	 */
	public ParallelPortEvent(ParallelPort srcport, int eventtype, 
		boolean oldvalue, boolean newvalue)
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
