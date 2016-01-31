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

import java.io.Serializable;

/**
 * Thrown when the specified port is in use.
 *
 * @author Jagane Sundar
 * @author wolfi
 */
public class PortInUseException extends Exception implements Serializable
{

  private final String currentOwner;

  public PortInUseException(String currentOwner)
  {
    this.currentOwner = currentOwner;
  }

  public PortInUseException(String currentOwner,
                            String message)
  {
    super(message);
    this.currentOwner = currentOwner;
  }

  /**
   * Describes the current owner of the communications port.
   *
   * @return currentOwner
   */
  public String getCurrentOwner()
  {
    return currentOwner;
  }

}
