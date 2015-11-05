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
package at.reder.atmelschema;

import at.reder.avrwb.annotations.XmlEntity;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 *
 * @author Wolfgang Reder
 */
@XmlEntity
public final class XA_AddressSpace
{

  private ByteOrder byteorder;
  private String name;
  private String id;
  private int start;
  private int size;
  private final List<XA_MemorySegment> segments = new ArrayList<>();

  @XmlAttribute(name = "endianness")
  public ByteOrder getByteOrder()
  {
    return byteorder;
  }

  public void setByteOrder(ByteOrder bo)
  {
    this.byteorder = bo;
  }

  @XmlAttribute(name = "name")
  @XmlSchemaType(name = "xs:string")
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @XmlAttribute(name = "id")
  @XmlID
  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  @XmlAttribute(name = "start")
  @XmlSchemaType(name = "xs:integer")
  public Integer getStart()
  {
    return start;
  }

  public void setStart(Integer start)
  {
    this.start = start;
  }

  @XmlAttribute(name = "size")
  @XmlSchemaType(name = "xs:integer")
  public Integer getSize()
  {
    return size;
  }

  public void setSize(Integer size)
  {
    this.size = size;
  }

  @XmlElement(name = "memory-segment")
  public List<XA_MemorySegment> getSegments()
  {
    return segments;
  }

  @Override
  public String toString()
  {
    return "XA_AddressSpace{" + "name=" + name + ", id=" + id + '}';
  }

}
