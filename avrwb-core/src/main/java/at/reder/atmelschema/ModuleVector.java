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
package at.reder.atmelschema;

import at.reder.avrwb.annotations.Immutable;
import at.reder.avrwb.annotations.NotNull;
import java.util.Objects;

@Immutable
public final class ModuleVector
{

  private final String deviceName;
  private final String moduleName;

  public ModuleVector(@NotNull String deviceName,
                      @NotNull String moduleName)
  {
    Objects.requireNonNull(deviceName, "deviceName==null");
    Objects.requireNonNull(moduleName, "moduleName==null");
    this.deviceName = deviceName;
    this.moduleName = moduleName;
  }

  public RegisterVector withRegister(@NotNull String registerName)
  {
    Objects.requireNonNull(registerName, "registerName==null");
    return new RegisterVector(this, registerName);
  }

  public ModuleVector withModule(@NotNull String moduleName)
  {
    Objects.requireNonNull(moduleName, "moduleName==null");
    return new ModuleVector(deviceName, moduleName);
  }

  public String getDeviceName()
  {
    return deviceName;
  }

  public String getModuleName()
  {
    return moduleName;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 41 * hash + Objects.hashCode(this.deviceName);
    hash = 41 * hash + Objects.hashCode(this.moduleName);
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
    final ModuleVector other = (ModuleVector) obj;
    if (!Objects.equals(this.deviceName, other.deviceName)) {
      return false;
    }
    return Objects.equals(this.moduleName, other.moduleName);
  }

  @Override
  public String toString()
  {
    return "ModuleVector{" + deviceName + "/" + moduleName + '}';
  }

}
