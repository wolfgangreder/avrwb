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
@XmlJavaTypeAdapters({
  @XmlJavaTypeAdapter(value = Family.Adapter.class, type = Family.class),
  @XmlJavaTypeAdapter(value = ByteOrderAdapter.class, type = ByteOrder.class),
  @XmlJavaTypeAdapter(value = BooleanIntAdapter.class, type = Boolean.class),
  @XmlJavaTypeAdapter(value = NoExecuteMemoryAccessAdapter.class, type = MemoryAccessSet.class),
  @XmlJavaTypeAdapter(value = HexIntAdapter.class, type = Integer.class)
})
package com.avrwb.atmelschema;

import com.avrwb.atmelschema.util.BooleanIntAdapter;
import com.avrwb.atmelschema.util.ByteOrderAdapter;
import com.avrwb.atmelschema.util.HexIntAdapter;
import com.avrwb.atmelschema.util.NoExecuteMemoryAccessAdapter;
import com.avrwb.avr8.Family;
import com.avrwb.avr8.MemoryAccessSet;
import java.nio.ByteOrder;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
