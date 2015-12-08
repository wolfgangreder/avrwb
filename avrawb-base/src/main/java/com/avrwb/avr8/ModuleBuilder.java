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

import com.avrwb.annotations.NotNull;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.avr8.helper.NotFoundStrategy;
import com.avrwb.schema.XmlDevice;
import com.avrwb.schema.XmlModule;

/**
 *
 * @author Wolfgang Reder
 * @param <B> implementierende Klasse
 */
public interface ModuleBuilder<B extends ModuleBuilder>
{

  @NotNull
  public B notFoundStrategy(@NotNull NotFoundStrategy nfStrategy) throws NullPointerException;

  @NotNull
  public B device(@NotNull XmlDevice device) throws NullPointerException, IllegalArgumentException;

  @NotNull
  public B moduleDescriptor(@NotNull XmlModule module) throws NullPointerException, IllegalArgumentException;

  @NotNull
  public B sram(@NotNull SRAM sram) throws NullPointerException;

  @NotNull
  public Module build() throws IllegalStateException, ItemNotFoundException, NullPointerException;

}
