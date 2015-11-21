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

import com.avrwb.annotations.Immutable;
import com.avrwb.annotations.ThreadSave;
import com.avrwb.schema.util.Converter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Wolfgang Reder
 */
@Immutable
@ThreadSave
public final class HexIntAdapter extends XmlAdapter<String, Integer>
{

  private final int minDigits;

  public HexIntAdapter()
  {
    minDigits = 1;
  }

  public HexIntAdapter(int minDigits)
  {
    this.minDigits = minDigits;
  }

  @Override
  public Integer unmarshal(String v)
  {
    return Converter.parseHexString(v);
  }

  @Override
  public String marshal(Integer v)
  {
    if (v == null) {
      return null;
    }
    return Converter.printHexString(v,
                                    minDigits);
  }

}
