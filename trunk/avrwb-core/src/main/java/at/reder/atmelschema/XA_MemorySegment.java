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
import at.reder.avrwb.avr8.MemoryAccessSet;
import at.reder.avrwb.avr8.MemoryType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 *
 * @author Wolfgang Reder
 */
@XmlEntity
public final class XA_MemorySegment
{

  private boolean external;
  private String name;
  private MemoryAccessSet memoryAccess;
  private boolean executeable;
  private int start;
  private int size;
  private MemoryType type;
  private int pageSize;

  @XmlAttribute(name = "external")
  public Boolean isExternal()
  {
    return external;
  }

  public void setExternal(Boolean external)
  {
    this.external = external != null && external;
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

  @XmlAttribute(name = "rw")
  public MemoryAccessSet getMemoryAccess()
  {
    return memoryAccess;
  }

  public void setMemoryAccess(MemoryAccessSet memoryAccess)
  {
    this.memoryAccess = memoryAccess;
  }

  @XmlAttribute(name = "exec")
  public Boolean isExecuteable()
  {
    return executeable;
  }

  public void setExecuteable(Boolean executeable)
  {
    this.executeable = executeable != null && executeable;
  }

  @XmlAttribute(name = "start")
  @XmlSchemaType(name = "xs:integer")
  public Integer getStart()
  {
    return start;
  }

  public void setStart(Integer start)
  {
    this.start = start != null ? start : 0;
  }

  @XmlAttribute(name = "size")
  @XmlSchemaType(name = "xs:integer")
  public Integer getSize()
  {
    return size;
  }

  public void setSize(Integer size)
  {
    this.size = size != null ? size : 0;
  }

  public MemoryType getType()
  {
    return type;
  }

  @XmlAttribute(name = "type")
  @XmlSchemaType(name = "xs:integer")
  public void setType(MemoryType type)
  {
    this.type = type;
  }

  @XmlAttribute(name = "pagesize")
  @XmlSchemaType(name = "xs:integer")
  public Integer getPageSize()
  {
    return pageSize;
  }

  public void setPageSize(Integer pageSize)
  {
    this.pageSize = pageSize != null ? pageSize : 0;
  }

  @Override
  public String toString()
  {
    return "XA_MemorySegment{" + "name=" + name + '}';
  }

}
