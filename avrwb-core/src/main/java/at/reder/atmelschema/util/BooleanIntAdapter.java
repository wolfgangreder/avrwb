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
package at.reder.atmelschema.util;

import at.reder.avrwb.annotations.Stateless;
import at.reder.avrwb.annotations.ThreadSave;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Wandelt einen Integer in einen Boolean um. {@code unmarshal} liefert nur dann true, wenn die zahl &lt;&gt; 0 ist.
 *
 * @author Wolfgang Reder
 */
@Stateless
@ThreadSave
public final class BooleanIntAdapter extends XmlAdapter<String, Boolean>
{

  @Override
  public Boolean unmarshal(String v)
  {
    if (v == null) {
      return null;
    }
    try {
      return Integer.parseInt(v) != 0;
    } catch (NumberFormatException ex) {

    }
    return false;
  }

  @Override
  public String marshal(Boolean v)
  {
    if (v == null) {
      return null;
    }
    return v ? "1" : "0";
  }

}
