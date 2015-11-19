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
import com.avrwb.avr8.api.ClockSink;
import com.avrwb.avr8.api.Resetable;
import java.util.List;
import java.util.Map;

/**
 * Basiseigenschaften und funktionalität für Untermodule.
 *
 * @author Wolfgang Reder
 */
public interface Module extends Resetable, ClockSink
{

  /**
   * Kurzname des Moduls (z.B CPU).
   *
   * @return name
   */
  @NotNull
  public String getName();

  /**
   * Vollständiger name des Moduls.
   *
   * @return caption;
   */
  @NotNull
  public String getCaption();

  /**
   * Paramter des Moduls.
   *
   * @return paramMap
   */
  @NotNull
  public Map<String, String> getParam();

  /**
   * Liste der Register des Moduls.
   *
   * @return
   */
  @NotNull
  public List<Register> getRegister();

}
