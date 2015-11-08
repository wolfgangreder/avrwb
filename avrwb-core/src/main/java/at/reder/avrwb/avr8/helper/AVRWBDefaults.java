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
package at.reder.avrwb.avr8.helper;

import java.util.logging.Logger;

/**
 *
 * @author Wolfgang Reder
 */
public final class AVRWBDefaults
{

  public static final Logger LOGGER = Logger.getLogger("at.reder.avrwb");
  public static final double VOLTAGE_MIN = 2.7;
  public static final double VOLTAGE_MAX = 5.5;
  public static final long SPEED_MAX = 20_000_000;
  public static final double TEMP_MIN = -40;
  public static final double TEMP_MAX = 85;

  public static final String MEMNAME_FLASH = "prog";
  public static final String MEMNAME_SRAM = "data";

  public static final String MODULENAME_CPU = "CPU";

  public static final String PROP_CORE_VERSION = "CORE_VERSION";
}
