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
package com.avrwb.schema.util;

import java.nio.ByteOrder;

/**
 *
 * @author wolfi
 */
public final class Converter
{

  private Converter()
  {
  }

  public static Boolean parseBoolean(String v)
  {
    if (v == null) {
      return null;
    }
    try {
      return Integer.parseInt(v) != 0;
    } catch (NumberFormatException ex) {

    }
    return Boolean.parseBoolean(v);
  }

  public static String printBoolean(Boolean b)
  {
    if (b == null) {
      return null;
    }
    return b.toString();
  }

  public static ByteOrder parseByteOrder(String s)
  {
    if (s == null) {
      return null;
    }
    switch (s) {
      case "little-endian":
        return ByteOrder.LITTLE_ENDIAN;
      case "big-endian":
        return ByteOrder.BIG_ENDIAN;
      default:
        throw new IllegalArgumentException("unknown byte order " + s);
    }
  }

  public static String printByteOrder(ByteOrder bo)
  {
    if (bo == null) {
      return null;
    }
    if (bo == ByteOrder.BIG_ENDIAN) {
      return "big-endian";
    } else {
      return "little-endian";
    }
  }

  public static String printHexString(Integer i)
  {
    if (i == null) {
      return null;
    }
    return printHexString(i,
                          1);
  }

  public static String printHexString(int i,
                                      int minDigits)
  {
    String tmp = Integer.toHexString(i);
    if (tmp.length() < minDigits) {
      StringBuilder b = new StringBuilder(minDigits);
      for (int n = tmp.length(); n < minDigits; ++n) {
        b.append("0");
      }
      tmp = b.append(tmp).toString();
    }
    return "0x" + tmp;

  }

  public static Integer parseHexString(String str)
  {
    if (str != null) {
      return Integer.decode(str);
    }
    return null;
  }

}
