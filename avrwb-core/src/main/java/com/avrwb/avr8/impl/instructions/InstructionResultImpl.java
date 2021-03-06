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
package com.avrwb.avr8.impl.instructions;

import com.avrwb.annotations.Immutable;
import com.avrwb.avr8.api.InstructionResult;
import com.avrwb.avr8.api.SimulationEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author wolfi
 */
@Immutable
final class InstructionResultImpl implements InstructionResult
{

  private final boolean finished;
  private final int nextIP;
  private final Set<Integer> modifiedDataAddresses;
  private final List<SimulationEvent> events;

  public InstructionResultImpl(boolean finished,
                               int nextIP,
                               Collection<Integer> modifiedDataAddresses,
                               Collection<? extends SimulationEvent> events)
  {
    this.finished = finished;
    this.nextIP = nextIP;
    if (modifiedDataAddresses == null || modifiedDataAddresses.isEmpty()) {
      this.modifiedDataAddresses = Collections.emptySet();
    } else {
      this.modifiedDataAddresses = Collections.unmodifiableSet(new HashSet<>(modifiedDataAddresses));
    }
    if (events == null || events.isEmpty()) {
      this.events = Collections.emptyList();
    } else {
      this.events = Collections.unmodifiableList(new ArrayList<>(events));
    }
  }

  @Override
  public boolean isExecutionFinished()
  {
    return finished;
  }

  @Override
  public int getNextIP()
  {
    return nextIP;
  }

  @Override
  public Set<Integer> getModifiedDataAddresses()
  {
    return modifiedDataAddresses;
  }

  @Override
  public List<SimulationEvent> getEvents()
  {
    return events;
  }

}
