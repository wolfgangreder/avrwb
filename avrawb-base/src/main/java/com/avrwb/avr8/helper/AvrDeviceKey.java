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
package com.avrwb.avr8.helper;

import com.avrwb.schema.AvrCore;
import com.avrwb.schema.AvrFamily;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class AvrDeviceKey
{

  private final AvrFamily family;
  private final AvrCore core;
  private final String id;

  public AvrDeviceKey(AvrFamily family,
                      AvrCore core,
                      String id)
  {
    this.family = family;
    this.core = core;
    this.id = id;
  }

  public AvrFamily getFamily()
  {
    return family;
  }

  public AvrCore getCore()
  {
    return core;
  }

  public String getId()
  {
    return id;
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 89 * hash + Objects.hashCode(this.family);
    hash = 89 * hash + Objects.hashCode(this.core);
    hash = 89 * hash + Objects.hashCode(this.id);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AvrDeviceKey other = (AvrDeviceKey) obj;
    if (!Objects.equals(this.id,
                        other.id)) {
      return false;
    }
    if (this.family != other.family) {
      return false;
    }
    return this.core == other.core;
  }

  @Override
  public String toString()
  {
    return "AVRDeviceKey{" + "family=" + family + ", core=" + core + ", id=" + id + '}';
  }

}
