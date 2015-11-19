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
package com.avrwb.avr8;

import com.avrwb.atmelschema.XA_AvrToolsDeviceFile;
import com.avrwb.atmelschema.XA_Device;
import com.avrwb.atmelschema.XA_Variant;
import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NullAllowed;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.avr8.helper.NotFoundStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 *
 * @author Wolfgang Reder
 */
public interface DeviceBuilder
{

  @NotNull
  public DeviceBuilder deviceLogger(@NullAllowed("sublogger .dev.<devname> to global logger") Logger deviceLogger);

  @NotNull
  public DeviceBuilder deviceSelector(Function<Collection<? extends XA_Device>, XA_Device> ds);

  @NotNull
  public DeviceBuilder deviceName(String deviceName);

  @NotNull
  public DeviceBuilder notFoundStrategy(@NotNull NotFoundStrategy strategy) throws NullPointerException;

  @NotNull
  public DeviceBuilder fromDescriptor(@NotNull InputStream strm,
                                      @NullAllowed("first") Function<Collection<? extends XA_Variant>, XA_Variant> pickVariant)
          throws NullPointerException,
                 IllegalArgumentException,
                 IOException;

  @NotNull
  public DeviceBuilder fromDescriptor(@NotNull XA_AvrToolsDeviceFile file,
                                      @NullAllowed("first") Function<Collection<? extends XA_Variant>, XA_Variant> pickVariant)
          throws NullPointerException, IllegalArgumentException;

  @NotNull
  public Device build() throws NullPointerException, IllegalStateException, ItemNotFoundException;

}
