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
package com.avrwb.atmelschema.util;

import com.avrwb.avr8.MemoryAccess;
import com.avrwb.avr8.MemoryAccessSet;
import com.avrwb.annotations.Stateless;
import com.avrwb.annotations.ThreadSave;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Wolfgang Reder
 */
@ThreadSave
@Stateless
public final class NoExecuteMemoryAccessAdapter extends XmlAdapter<String, MemoryAccessSet>
{

  @Override
  public MemoryAccessSet unmarshal(String v) throws IllegalArgumentException
  {
    if (v == null) {
      return null;
    }
    MemoryAccessSet result = new MemoryAccessSet();
    for (char ch : v.toCharArray()) {
      MemoryAccess tmp = MemoryAccess.valueOf(ch);
      if (tmp != MemoryAccess.EXECUTE) {
        result.add(tmp);
      }
    }
    return result;
  }

  @Override
  public String marshal(MemoryAccessSet v)
  {
    if (v == null) {
      return null;
    }
    StringBuilder b = new StringBuilder();
    for (MemoryAccess a : v) {
      if (a != MemoryAccess.EXECUTE) {
        b.append(a.sign());
      }
    }
    return b.toString();
  }

}
