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
package at.reder.avrwb.avr8.impl;

import at.reder.atmelschema.XA_AvrToolsDeviceFile;
import at.reder.atmelschema.XA_Device;
import at.reder.atmelschema.XA_Variant;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.DeviceBuilder;
import at.reder.avrwb.avr8.helper.ItemNotFoundException;
import at.reder.avrwb.avr8.helper.NotFoundStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Wolfgang Reder
 */
@Messages({"DeviceBuilderImpl_no_device=Cannot find any device."})
public final class DeviceBuilderImpl implements DeviceBuilder
{

  private NotFoundStrategy strategy = NotFoundStrategy.WARNING;
  private XA_Variant variant;
  private XA_AvrToolsDeviceFile file;
  private String deviceName;
  private Function<Collection<? extends XA_Device>, XA_Device> deviceSelector;

  @Override
  public DeviceBuilder deviceSelector(Function<Collection<? extends XA_Device>, XA_Device> ds)
  {
    this.deviceSelector = ds;
    return this;
  }

  @Override
  public DeviceBuilder deviceName(String deviceName)
  {
    this.deviceName = deviceName;
    return this;
  }

  @Override
  public DeviceBuilder notFoundStrategy(NotFoundStrategy strategy) throws NullPointerException
  {
    Objects.requireNonNull(strategy, "strategy==null");
    this.strategy = strategy;
    return this;
  }

  @Override
  public DeviceBuilder fromDescriptor(InputStream strm,
                                      Function<Collection<? extends XA_Variant>, XA_Variant> pickVariant) throws
          NullPointerException,
          IllegalArgumentException,
          IOException
  {
    return fromDescriptor(XA_AvrToolsDeviceFile.load(strm), pickVariant);
  }

  @Override
  public DeviceBuilder fromDescriptor(XA_AvrToolsDeviceFile file,
                                      Function<Collection<? extends XA_Variant>, XA_Variant> pickVariant) throws
          NullPointerException,
          IllegalArgumentException
  {
    Objects.requireNonNull(file, "file==null");
    Function<Collection<? extends XA_Variant>, XA_Variant> pv;
    if (pickVariant != null) {
      pv = pickVariant;
    } else {
      pv = (Collection<? extends XA_Variant> vc) -> {
        return vc.isEmpty() ? vc.iterator().next() : null;
      };
    }
    this.file = file;
    variant = pv.apply(file.getVariants());
    return this;
  }

  @Override
  public Device build() throws NullPointerException, IllegalStateException, ItemNotFoundException
  {
    Objects.requireNonNull(file, "file==null");
    Objects.requireNonNull(strategy, "strategy==null");
    XA_Device dev = null;
    if (deviceName == null || deviceName.trim().isEmpty()) {
      if (deviceSelector == null) {
        if (!file.getDevices().isEmpty()) {
          deviceName = file.getDevices().get(0).getName();
        }
      } else {
        dev = deviceSelector.apply(file.getDevices());
      }
    }
    if (dev == null) {
      for (XA_Device d : file.getDevices()) {
        if (deviceName.equals(d.getName())) {
          dev = d;
          break;
        }
      }
    }
    if (dev == null) {
      throw new ItemNotFoundException(Bundle.DeviceBuilderImpl_no_device());
    }
    return new DeviceImpl(file, variant, dev, strategy);
  }

}
