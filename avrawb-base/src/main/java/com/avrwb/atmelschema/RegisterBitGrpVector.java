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
import com.avrwb.annotations.Invariants;
import com.avrwb.annotations.NotNull;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
@Immutable
public final class RegisterBitGrpVector
{

  private final RegisterVector register;
  private final String name;

  public RegisterBitGrpVector(@NotNull RegisterVector register,
                              @NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                             IllegalArgumentException
  {
    Objects.requireNonNull(register, "registervector==null");
    Objects.requireNonNull(name, "name==null");
    if (name.trim().isEmpty()) {
      throw new IllegalArgumentException("name is empty");
    }
    this.register = register;
    this.name = name;
  }

  @NotNull
  public RegisterVector getRegister()
  {
    return register;
  }

  @NotNull
  public String getName()
  {
    return name;
  }

  @NotNull
  public RegisterBitGrpVector withBitGrp(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                                        IllegalArgumentException
  {
    return new RegisterBitGrpVector(register, name);
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 47 * hash + Objects.hashCode(this.register);
    hash = 47 * hash + Objects.hashCode(this.name);
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
    final RegisterBitGrpVector other = (RegisterBitGrpVector) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    return Objects.equals(this.register, other.register);
  }

  @Override
  public String toString()
  {
    return "RegisterBitGrpVector{" + "register=" + register + ", name=" + name + '}';
  }

}
