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

import java.io.FileDescriptor;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p>
 * Communications port management. {@code CommPortIdentifier} is the central class for controlling access to communications ports.
 * It includes methods for:
 * </p><ul>
 * <li> Determining the communications ports made available by the driver.
 * </li><li> Opening communications ports for I/O operations.
 * </li><li> Determining port ownership.
 * </li><li> Resolving port ownership contention.
 * </li><li> Managing events that indicate changes in port ownership status.
 * </li></ul>
 * <p>
 * An application first uses methods in {@code CommPortIdentifier} to negotiate with the driver to discover which communication
 * ports are available and then select a port for opening. It then uses methods in other classes like {@code CommPort},
 * {@code ParallelPort} and {@code SerialPort} to communicate through the port.
 * </p>
 *
 * @author Jagane Sundar
 * @author Paul Klissner Substantially re-written in 3.0
 * @author wolfi
 */
public class CommPortIdentifier
{

  private final Set<CommPortOwnershipListener> ownershipListener = new CopyOnWriteArraySet<>();
  private final String name;
  private CommPort port;
  private final PortType type;
  private final CommDriver driver;
  private String owner;
  private final Object LOCK = new Object();

  /**
   * Constructor
   *
   * @param name Name to give this port
   * @param port Instantiaton of CommPort implementation
   * @param type SERIAL or PARALLEL
   * @param driver Driver assosciated with this port.
   */
  public CommPortIdentifier(String name,
                            CommPort port,
                            PortType type,
                            CommDriver driver)
  {
    this.port = port;
    if (name == null || name.isEmpty()) {
      this.name = port.getName();
    } else {
      this.name = name;
    }
    this.type = Objects.requireNonNull(type,
                                       "type==null");
    this.driver = Objects.requireNonNull(driver,
                                         "driver==null");
  }

  /**
   * Obtains an enumeration object that contains a {@code CommPortIdentifier} object for each port in the system.
   *
   * @return {@code Iterator} that can be used to enumerate all the ports known to the system
   */
  public static Iterator<CommPortIdentifier> getPortIdentifiers()
  {
    throw new UnsupportedOperationException();

  }

  /**
   * Obtains a {@code CommPortIdentifier} object by using a port name. The port name may have been stored in persistent storage by
   * the application.
   *
   * @param portName name of the port to open
   * @return {@code CommPortIdentifier} object
   * @throws NoSuchPortException if the port does not exist
   */
  public static CommPortIdentifier getPortIdentifier(String portName)
          throws NoSuchPortException
  {
    throw new UnsupportedOperationException();

  }

  /**
   * Obtains the {@code CommPortIdentifier} object corresponding to a port that has already been opened by the application.
   *
   * @param port a CommPort object obtained from a previous open
   * @return {@code CommPortIdentifier} object
   * @throws NoSuchPortException if the port object is invalid
   */
  public static CommPortIdentifier getPortIdentifier(CommPort port)
          throws NoSuchPortException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Adds {@code portName} to the list of ports.
   *
   * @param portName The name of the port being added
   * @param portType The type of the port being added
   * @param driver The driver representing the port being added
   * @since CommAPI 1.1
   * @see CommDriver
   */
  public static void addPortName(String portName,
                                 int portType,
                                 CommDriver driver)
  {

  }

  /**
   * <p>
   * Opens the communications port. {@code open} obtains exclusive ownership of the port. If the port is owned by some other
   * application, a {@code PORT_OWNERSHIP_REQUESTED} event is propagated using the {@code CommPortOwnershipListener} event
   * mechanism. If the application that owns the port calls {@code close} during the event processing, then this {@code open} will
   * succeed.
   *
   * There is one {@code InputStream} and one {@code OutputStream} associated with each port. After a port is opened with
   * {@code open}, then all calls to {@code getInputStream} will return the same stream object until {@code close} is called.
   * </p>
   *
   * @param appname Name of application making this call. This name will become the owner of the port. Useful when resolving
   * ownership contention.
   * @param timeout Time in milliseconds to block waiting for port open.
   * @return {@code CommPort} object
   * @throws PortInUseException if the port is in use by some other application that is not willing to relinquish ownership
   */
  public CommPort open(String appname,
                       int timeout)
          throws PortInUseException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Opens the communications port using a {@code FileDescriptor} object on platforms that support this technique.
   *
   * @param fd The {@code FileDescriptor} associated with this CommPort.
   * @return {@code CommPort} object.
   * @throws UnsupportedCommOperationException on platforms which do not support this functionality.
   */
  public CommPort open(FileDescriptor fd) throws UnsupportedCommOperationException
  {
    throw new UnsupportedCommOperationException();
  }

  /**
   * Returns the name of the port.
   *
   * @return the name of the port
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the port type.
   *
   * @return port type
   */
  public PortType getPortType()
  {
    return type;
  }

  /**
   * Returns the owner of the port.
   *
   * @return current owner of the port.
   */
  public String getCurrentOwner()
  {
    synchronized (LOCK) {
      return owner;
    }
  }

  /**
   * Checks whether the port is owned.
   *
   * @return boolean {@code true} if port is owned by an application, {@code false} if port is not owned.
   */
  public boolean isCurrentlyOwned()
  {
    synchronized (LOCK) {
      return owner != null;
    }
  }

  /**
   * Registers an interested application so that it can receive notification of changes in port ownership. This includes
   * notification of the following events:
   * <ul>
   * <li> {@code PORT_OWNED}: Port became owned
   * </li><li> {@code PORT_UNOWNED}: Port became unowned
   * </li><li> {@code PORT_OWNERSHIP_REQUESTED} If the application owns this port and is willing to give up ownership, then it
   * should call {@code close} now.
   * </li></ul>
   * The {@code ownershipChange} method of the listener registered using {@code addPortOwnershipListener} will be called with one
   * of the above events.
   *
   * @param listener {@code CommPortOwnershipListener} callback object
   */
  public void addPortOwnershipListener(CommPortOwnershipListener listener)
  {
    ownershipListener.add(Objects.requireNonNull(listener,
                                                 "listener==null"));
  }

  /**
   * Deregisters a {@code CommPortOwnershipListener} registered using {@code addPortOwnershipListener}
   *
   * @param listener The CommPortOwnershipListener object that was previously registered using addPortOwnershipListener
   */
  public void removePortOwnershipListener(CommPortOwnershipListener listener)
  {
    ownershipListener.remove(listener);
  }

}
