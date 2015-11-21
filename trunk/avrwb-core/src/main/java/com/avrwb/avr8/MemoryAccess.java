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

import com.avrwb.annotations.Stateless;
import com.avrwb.annotations.ThreadSave;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Wolfgang Reder
 */
public enum MemoryAccess
{
  READ('r'),
  WRITE('w'),
  EXECUTE('x');

  @Stateless
  @ThreadSave
  public static final class Adapter extends XmlAdapter<String, MemoryAccess>
  {

    @Override
    public MemoryAccess unmarshal(String v)
    {
      if (v == null) {
        return null;
      }
      switch (v) {
        case "R":
        case "r":
          return READ;
        case "W":
        case "w":
          return WRITE;
        case "X":
        case "x":
          return EXECUTE;
        default:
          return valueOf(v);
      }
    }

    @Override
    public String marshal(MemoryAccess v)
    {
      if (v == null) {
        return null;
      }
      return Character.toString(v.sign());
    }

  }
  private final char sign;

  private MemoryAccess(char sign)
  {
    this.sign = sign;
  }

  public char sign()
  {
    return sign;
  }

  public static MemoryAccess valueOf(char ch)
  {
    switch (ch) {
      case 'R':
      case 'r':
        return READ;
      case 'W':
      case 'w':
        return WRITE;
      case 'X':
      case 'x':
        return EXECUTE;
    }
    throw new IllegalArgumentException();
  }

}
