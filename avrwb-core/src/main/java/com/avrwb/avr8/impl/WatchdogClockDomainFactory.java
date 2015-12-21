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

import com.avrwb.avr8.api.AVRWBDefaults;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.ClockDomainFactory;
import com.avrwb.avr8.api.ItemNotFoundException;
import com.avrwb.avr8.spi.AbstractClockDomain;
import com.avrwb.schema.XmlClockDomain;
import java.util.Objects;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author wolfi
 */
@ServiceProvider(service = ClockDomainFactory.class, path = "avrwb")
public class WatchdogClockDomainFactory implements ClockDomainFactory
{

  @Override
  public String getImplementationId()
  {
    return AVRWBDefaults.IMPID_CD_WATCHDOG;
  }

  @Override
  public ClockDomain createDomain(XmlClockDomain domainSettings) throws ItemNotFoundException
  {
    return new AbstractClockDomain(Objects.requireNonNull(domainSettings,
                                                          "domainSettings==null"),
                                   getImplementationId());
  }

}
