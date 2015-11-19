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
package com.avrwb.atmelschema;

import com.avrwb.annotations.Immutable;
import com.avrwb.annotations.NotNull;
import java.util.Objects;

@Immutable
public final class RegisterVector
{

  private final ModuleVector module;
  private final String registerName;

  public RegisterVector(@NotNull ModuleVector module,
                        @NotNull String registerName)
  {
    Objects.requireNonNull(module, "module==null");
    Objects.requireNonNull(registerName, "registerName==null");
    this.module = module;
    this.registerName = registerName;
  }

  public RegisterVector(@NotNull String deviceName,
                        @NotNull String moduleName,
                        @NotNull String registerName)
  {
    this(new ModuleVector(deviceName, moduleName), registerName);
  }

  @NotNull
  public ModuleVector getModule()
  {
    return module;
  }

  @NotNull
  public String getRegisterName()
  {
    return registerName;
  }

  @NotNull
  public RegisterBitGrpVector withBitGrp(@NotNull String groupName)
  {
    return new RegisterBitGrpVector(this, groupName);
  }

  @NotNull
  public RegisterVector withRegister(@NotNull String registerName)
  {
    return new RegisterVector(module, registerName);
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 41 * hash + Objects.hashCode(this.module);
    hash = 41 * hash + Objects.hashCode(this.registerName);
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
    final RegisterVector other = (RegisterVector) obj;
    if (!Objects.equals(this.module, other.module)) {
      return false;
    }
    return Objects.equals(this.registerName, other.registerName);
  }

  @Override
  public String toString()
  {
    return "RegisterVector{" + module.getDeviceName() + "/" + module.getModuleName() + "/" + registerName + '}';
  }

}
