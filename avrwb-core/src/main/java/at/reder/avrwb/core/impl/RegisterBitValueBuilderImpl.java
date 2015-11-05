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
package at.reder.avrwb.core.impl;

import at.reder.atmelschema.XA_ValueGroup;
import at.reder.avrwb.annotations.Invariants;
import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.core.RegisterBitValue;
import at.reder.avrwb.core.RegisterBitValueBuilder;
import java.util.Objects;

/**
 * Standardimplmenentierung von RegisterBitValueBuilder
 *
 * @see at.reder.avrwb.core.RegisterBitValueBuilder
 * @see at.reder.avrwb.core.InstanceFactories
 * @author Wolfgang Reder
 */
public final class RegisterBitValueBuilderImpl implements RegisterBitValueBuilder
{

  private String name;
  private String caption;
  private int value;

  @Override
  @NotNull
  public RegisterBitValueBuilder fromDescriptor(@NotNull XA_ValueGroup valueGroup) throws NullPointerException
  {
    Objects.requireNonNull(valueGroup, "valueGroup==null");
    name = valueGroup.getName();
    caption = valueGroup.getCaption();
    value = valueGroup.
  }

  @Override
  @NotNull
  public RegisterBitValueBuilder name(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                                     IllegalArgumentException
  {

  }

  @Override
  @NotNull
  public RegisterBitValueBuilder caption(@NotNull String caption) throws NullPointerException
  {

  }

  @Override
  @NotNull
  public RegisterBitValueBuilder value(int value)
  {

  }

  @Override
  @NotNull
  public RegisterBitValue build() throws NullPointerException, IllegalStateException
  {

  }

}
