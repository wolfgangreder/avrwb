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

import com.avrwb.annotations.Invariants;
import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NotThreadSave;
import com.avrwb.avr8.Register;

/**
 *
 * @author wolfi
 */
@NotThreadSave
public interface InstructionResultBuilder
{

  @NotNull
  public InstructionResultBuilder finished(boolean finished,
                                           @Invariants(minValue = "0", maxValue = "FLASH_END") int nextIP) throws
          IllegalArgumentException;

  public boolean isFinished();

  @NotNull
  public InstructionResultBuilder addModifiedRegister(@NotNull Register modifiedRegister) throws NullPointerException;

  @NotNull
  public InstructionResultBuilder addModifiedDataAddresses(@Invariants(minValue = "0", maxValue = "RAM_END") int modifiedAddress)
          throws IllegalArgumentException;

  @NotNull
  public InstructionResult build() throws IllegalStateException;

}
