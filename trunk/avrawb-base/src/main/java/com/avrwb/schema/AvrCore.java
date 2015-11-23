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
package com.avrwb.schema;

import com.avrwb.annotations.Immutable;
import com.avrwb.annotations.ThreadSave;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pseudoenum zur Darstellung der Coreversionen.
 *
 * @author wolfi
 */
@Immutable
public final class AvrCore
{

  private static final ConcurrentHashMap<String, AvrCore> INSTANCES = new ConcurrentHashMap<>();
  public static final AvrCore ANY = valueOf("");
  public static final AvrCore V0 = valueOf("V0");
  public static final AvrCore V0E = valueOf("V0E");
  public static final AvrCore V1 = valueOf("V1");
  public static final AvrCore V2 = valueOf("V2");
  public static final AvrCore V2E = valueOf("V2E");
  public static final AvrCore V3 = valueOf("V3");
  public static final AvrCore V3X = valueOf("V3X");
  public static final AvrCore I6000 = valueOf("I6000");

  @ThreadSave
  public static AvrCore valueOf(String str)
  {
    if (str == null) {
      return null;
    }
    return INSTANCES.computeIfAbsent(str,
                                     (String k) -> new AvrCore(k));
  }

  public static String printAvrCore(AvrCore core)
  {
    if (core != null) {
      return core.toString();
    }
    return null;
  }

  private final String value;

  private AvrCore(String value)
  {
    this.value = value;
  }

  public String name()
  {
    return value;
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(Object core)
  {
    return this == core;
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }

  @Override
  public String toString()
  {
    return value;
  }

}
