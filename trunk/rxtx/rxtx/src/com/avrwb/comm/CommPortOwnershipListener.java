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
package com.avrwb.comm;

import java.util.EventListener;

/**
 *
 * <p>
 * Propagates various communications port ownership events. When a port is opened, a {@code CommPortOwnership} event of type
 * {@code PORT_OWNED} will be propagated. When a port is closed, a {@code CommPortOwnership} event of type {@code PORT_UNOWNED}
 * will be propagated.
 * </p><p>
 * Multiple applications that are seeking ownership of a communications port can resolve their differences as follows:
 * </p><ul>
 * <li>
 * ABCapp calls {@code open} and takes ownership of port.
 * </li><li>
 * XYZapp calls {@code open} sometime later.
 * </li><li>
 * While processing XYZapp's {@code open}, {@code CommPortIdentifier} will propagate a {@code CommPortOwnership} event with the
 * event type {@code PORT_OWNERSHIP_REQUESTED}.
 * </li><li>
 * If ABCapp is registered to listen to these events and if it is willing to give up ownership, it calls {@code close} from within
 * the event callback.
 * </li><li>
 * After the event has been fired, {@code CommPortIdentifier} checks to see if ownership was given up, and if so, turns over
 * ownership of the port to XYZapp by returning success from {@code open}.
 * </li></ul>
 * Note: When a {@code close} is called from within a {@code CommPortOwnership} event callback, a new {@code CommPortOwnership}
 * event will
 * <i>not</i> be generated.
 *
 * @author Jagane Sundar
 * @author wolfi
 * @see CommPort
 * @see CommPortIdentifier
 * @see java.util.EventListener
 */
public interface CommPortOwnershipListener extends EventListener
{

  public static enum Type
  {
    /**
     * The port just went from unowned to owned state, when an application successfully called {@code CommPortIdentifier.open}.
     */
    PORT_OWNED,
    /**
     * Ownership contention.
     */
    PORT_OWNERSHIP_REQUESTED,
    /**
     * The port just went from owned to unowned state, when the port's owner called {@code CommPort.close}.
     */
    PORT_UNOWNED;
  }

  /**
   * Propagates a CommPortOwnership event.
   *
   * @param type type of event
   */
  public void ownershipChange(Type type);

}
