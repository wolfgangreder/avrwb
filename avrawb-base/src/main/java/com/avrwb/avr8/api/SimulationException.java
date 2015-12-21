/*
 * $Id$
 *
 * Copyright (C) 2015 Wolfgang Reder <wolfgang.reder@aon.at>.
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
package com.avrwb.avr8.api;

import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * @author wolfi
 */
public class SimulationException extends Exception
{

  private final Supplier<String> messageFunc;

  public SimulationException(Supplier<String> messageFunc)
  {
    super(Objects.requireNonNull(messageFunc.get(),
                                 "messageFunc==null"));
    this.messageFunc = messageFunc;
  }

  public SimulationException(String message,
                             Supplier<String> messageFunc)
  {
    super(message);
    this.messageFunc = Objects.requireNonNull(messageFunc,
                                              "messageFunc==null");
  }

  public Supplier< String> getMessageFunction()
  {
    return messageFunc;
  }

  @Override
  public String getLocalizedMessage()
  {
    return messageFunc.get();
  }

}
