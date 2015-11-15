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
package at.reder.avrwb.avr8;

import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class AVRDeviceKey
{

  private final Family family;
  private final AVRCoreVersion core;
  private final Architecture architecture;
  private final String id;

  public AVRDeviceKey(Family family,
                      Architecture architecture,
                      AVRCoreVersion core,
                      String id)
  {
    this.family = family;
    this.core = core;
    this.id = id;
    this.architecture = architecture;
  }

  public Family getFamily()
  {
    return family;
  }

  public Architecture getArchitecture()
  {
    return architecture;
  }

  public AVRCoreVersion getCore()
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
    hash = 89 * hash + Objects.hashCode(this.architecture);
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
    final AVRDeviceKey other = (AVRDeviceKey) obj;
    if (!Objects.equals(this.id,
                        other.id)) {
      return false;
    }
    if (this.family != other.family) {
      return false;
    }
    if (this.core != other.core) {
      return false;
    }
    return this.architecture == other.architecture;
  }

  @Override
  public String toString()
  {
    return "AVRDeviceKey{" + "family=" + family + ", core=" + core + ", architecture=" + architecture + ", id=" + id + '}';
  }

}
