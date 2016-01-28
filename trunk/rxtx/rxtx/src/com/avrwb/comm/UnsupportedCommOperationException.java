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
 * Thrown when a driver doesn't allow the specified operation.
 *
 * @author Jagane Sundar
 * @author wolfi
 * @see CommPort
 */
public class UnsupportedCommOperationException extends Exception implements Serializable
{

  /**
   * Constructs {@code an UnsupportedCommOperationException} with no detail message.
   */
  public UnsupportedCommOperationException()
  {
  }

  /**
   * Constructs an {@code UnsupportedCommOperationException} with the specified detail message.
   *
   * @param message the detail message.
   */
  public UnsupportedCommOperationException(String message)
  {
    super(message);
  }

}
