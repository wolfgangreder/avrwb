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
import java.io.IOException;
import java.nio.ByteOrder;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Wolfgang Reder
 */
@Stateless
public final class ByteOrderAdapter extends XmlAdapter<String, ByteOrder>
{

  @Override
  public ByteOrder unmarshal(String v) throws IOException
  {
    if (v == null) {
      return null;
    }
    switch (v.toUpperCase()) {
      case "LITTLE":
        return ByteOrder.LITTLE_ENDIAN;
      case "BIG":
        return ByteOrder.BIG_ENDIAN;
      default:
        throw new IOException("unknown byteorder " + v);
    }
  }

  @Override
  public String marshal(ByteOrder v)
  {
    if (v == null) {
      return null;
    }
    if (v == ByteOrder.LITTLE_ENDIAN) {
      return "little";
    } else if (v == ByteOrder.BIG_ENDIAN) {
      return "big";
    } else {
      throw new IllegalArgumentException("unknown byteorder " + v);
    }
  }

}
