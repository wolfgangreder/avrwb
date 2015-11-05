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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 *
 * @author Wolfgang Reder
 */
@XmlEntity
public class XA_DeviceModuleRegisterGroup
{

  private String name;
  private String nameInModule;
  private int offset;
  private String addressSpace;

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

  @XmlAttribute(name = "name-in-module")
  @XmlSchemaType(name = "xs:string")
  public String getNameInModule()
  {
    return nameInModule;
  }

  public void setNameInModule(String nameInModule)
  {
    this.nameInModule = nameInModule;
  }

  @XmlAttribute(name = "offset")
  @XmlSchemaType(name = "xs:integer")
  public Integer getOffset()
  {
    return offset;
  }

  public void setOffset(Integer offset)
  {
    this.offset = offset != null ? offset : 0;
  }

  @XmlAttribute(name = "address-space")
  @XmlSchemaType(name = "xs:string")
  public String getAddressSpace()
  {
    return addressSpace;
  }

  public void setAddressSpace(String addressSpace)
  {
    this.addressSpace = addressSpace;
  }

  @Override
  public String toString()
  {
    return "XA_DeviceModuleRegisterGroup{" + "name=" + name + ", nameInModule=" + nameInModule + '}';
  }

}
