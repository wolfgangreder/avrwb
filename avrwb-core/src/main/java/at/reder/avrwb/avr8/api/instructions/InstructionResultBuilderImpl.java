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
package at.reder.avrwb.avr8.api.instructions;

import at.reder.avrwb.annotations.NotThreadSave;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.api.InstructionResult;
import at.reder.avrwb.avr8.api.InstructionResultBuilder;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author wolfi
 */
@NotThreadSave
public final class InstructionResultBuilderImpl implements InstructionResultBuilder
{

  private boolean finished;
  private int nextIP = -1;
  private final SortedSet<Integer> modifiedDataAdresses = new TreeSet<>();
  private final Device device;

  public InstructionResultBuilderImpl(Device device)
  {
    Objects.requireNonNull(device,
                           "device==null");
    this.device = device;
  }

  @Override
  public InstructionResultBuilder finished(boolean finished)
  {
    this.finished = finished;
    return this;
  }

  @Override
  public InstructionResultBuilder nextIp(int nextIP) throws IllegalArgumentException
  {
    if (nextIP < device.getFlash().getStart() || nextIP >= device.getFlash().getSize()) {
      throw new IllegalArgumentException("nextIP out of range");
    }
    this.nextIP = nextIP;
    return this;
  }

  @Override
  public InstructionResultBuilder addModifiedDataAddresses(int modifiedAddress) throws IllegalArgumentException
  {
    if (modifiedAddress < device.getSRAM().getStart() || modifiedAddress >= device.getSRAM().getSize()) {
      throw new IllegalArgumentException("modifiedAddress out of range");
    }
    modifiedDataAdresses.add(modifiedAddress);
    return this;
  }

  @Override
  public InstructionResult build() throws IllegalStateException
  {
    if (finished && nextIP < 0) {
      throw new IllegalStateException("nextIP out of range");
    }
    return new InstructionResultImpl(finished,
                                     nextIP,
                                     modifiedDataAdresses);
  }

}
