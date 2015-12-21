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
package com.avrwb.avr8.impl;

import com.avrwb.annotations.NotNull;
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.SimulationContext;
import com.avrwb.avr8.api.SimulationController;
import com.avrwb.avr8.api.SimulationEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author wolfi
 */
public class StandardSimulationContext implements SimulationContext, SimulationController
{

  private final LinkedList<SimulationEvent> events = new LinkedList<>();
  private final Device device;
  private final PriorityQueue<ClockDomain> domainQueue;
  private final CPU cpu;
  private boolean running;
  private final AtomicBoolean pauseRequest = new AtomicBoolean();

  public StandardSimulationContext(@NotNull Device device)
  {
    this.device = Objects.requireNonNull(device,
                                         "device==null");
    domainQueue = new PriorityQueue<>((ClockDomain o1, ClockDomain o2)
            -> Double.compare(o1.getNextEventTime(),
                              o2.getNextEventTime()));
    domainQueue.addAll(device.getClockDomains());
    cpu = device.getCPU();
  }

  @Override
  public Device getDevice()
  {
    return device;
  }

  @Override
  public ClockDomain getCPUDomain()
  {
    return device.getCPUDomain();
  }

  @Override
  public List<SimulationEvent> getEvents()
  {
    return events;
  }

  @Override
  public void addEvent(SimulationEvent event)
  {
    events.add(Objects.requireNonNull(event,
                                      "event==null"));
  }

  @Override
  public void resetEvents()
  {
    events.clear();
  }

  @Override
  public SimulationController getController()
  {
    return this;
  }

  @Override
  public synchronized boolean isRunning()
  {
    return running;
  }

  @Override
  public synchronized void pause()
  {
    if (isRunning()) {
      pauseRequest.set(true);
    }
  }

  private synchronized void beginRunning()
  {
    if (isRunning()) {
      throw new IllegalStateException();
    }
    running = true;
  }

  private synchronized void endRunning()
  {
    running = false;
    pauseRequest.set(false);
  }

  @Override
  @SuppressWarnings("empty-statement")
  public void runToNextIp()
  {
    beginRunning();
    try {
      long ctr = cpu.getExecutionCounter();
      while (!pauseRequest.get() && cpu.getExecutionCounter() == ctr) {
        while (!pauseRequest.get() && !internalStep());
      }
    } finally {
      endRunning();
    }
  }

  private boolean internalStep()
  {
    boolean result;
    ClockDomain cd = domainQueue.remove();
    try {
      result = cd.getId().equals(device.getCPUDomain().getId());
      cd.run(this);
    } finally {
      domainQueue.add(cd);
    }
    return result;
  }

  @Override
  public void step()
  {
    beginRunning();
    try {
      internalStep();
    } finally {
      endRunning();
    }
  }

  @Override
  public void runTo(int ip)
  {
    beginRunning();
    try {
      while (!pauseRequest.get() && cpu.getIP() != ip) {
        runToNextIp();
      }
    } finally {
      endRunning();
    }
  }

  @Override
  @SuppressWarnings("empty-statement")
  public void stepCPU()
  {
    beginRunning();
    try {
      while (!pauseRequest.get() && !internalStep());
    } finally {
      endRunning();
    }
  }

}
