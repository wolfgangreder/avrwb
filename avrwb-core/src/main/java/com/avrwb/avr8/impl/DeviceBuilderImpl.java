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

import com.avrwb.annotations.NullAllowed;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.DeviceBuilder;
import com.avrwb.avr8.Variant;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.avr8.helper.NotFoundStrategy;
import com.avrwb.schema.XmlDevice;
import com.avrwb.schema.XmlPart;
import java.util.Objects;
import java.util.logging.Logger;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Wolfgang Reder
 */
@Messages({"DeviceBuilderImpl_no_device=Cannot find any device."})
public final class DeviceBuilderImpl implements DeviceBuilder
{

  private NotFoundStrategy strategy = NotFoundStrategy.WARNING;
  private XmlPart part;
  private Variant variant;
  @SuppressWarnings("NonConstantLogger")
  private Logger deviceLogger;

  @Override
  public DeviceBuilder deviceLogger(@NullAllowed Logger deviceLogger)
  {
    this.deviceLogger = deviceLogger;
    return this;
  }

  @Override
  public DeviceBuilder variant(Variant variant) throws NullPointerException
  {
    Objects.requireNonNull(variant,
                           "variant==null");
    this.variant = variant;
    return this;
  }

  @Override
  public DeviceBuilder fromDescriptor(XmlPart file) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(file,
                           "file==null");
    this.part = file;
    return this;
  }

  @Override
  public DeviceBuilder notFoundStrategy(NotFoundStrategy strategy) throws NullPointerException
  {
    Objects.requireNonNull(strategy,
                           "strategy==null");
    this.strategy = strategy;
    return this;
  }

  @Override
  public Device build() throws NullPointerException, IllegalStateException, ItemNotFoundException
  {
    Objects.requireNonNull(part,
                           "part==null");
    Objects.requireNonNull(strategy,
                           "strategy==null");
    XmlDevice dev = part.getDevice();
    Objects.requireNonNull(dev,
                           "device==null");
    if (variant == null && !part.getDevice().getVariants().getVariant().isEmpty()) {
      variant = new VariantBuilderImpl().xmlVariant(part.getDevice().getVariants().getVariant().get(0)).build();
    }
    return new DeviceImpl(part.getDevice(),
                          variant,
                          strategy,
                          deviceLogger);
  }

}
