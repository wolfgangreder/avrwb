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
package com.avrwb.atmelschema;

import com.avrwb.annotations.XmlEntity;
import com.avrwb.avr8.Architecture;
import com.avrwb.avr8.Family;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 *
 * @author Wolfgang Reder
 */
@XmlEntity
public final class XA_Device
{

  private String name;
  private Architecture architecture;
  private Family family;
  private final List<XA_AddressSpace> adressSpaces = new ArrayList<>();
  private final List<XA_DeviceModule> modules = new ArrayList<>();

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

  @XmlAttribute(name = "architecture")
  public Architecture getArchitecture()
  {
    return architecture;
  }

  public void setArchitecture(Architecture architecture)
  {
    this.architecture = architecture;
  }

  @XmlAttribute(name = "family")
  public Family getFamily()
  {
    return family;
  }

  public void setFamily(Family family)
  {
    this.family = family;
  }

  @XmlElement(name = "address-space")
  @XmlElementWrapper(name = "address-spaces")
  public List<XA_AddressSpace> getAdressSpaces()
  {
    return adressSpaces;
  }

  @XmlElement(name = "module")
  @XmlElementWrapper(name = "peripherals")
  public List<XA_DeviceModule> getModules()
  {
    return modules;
  }

  @Override
  public String toString()
  {
    return "XA_Device{" + "name=" + name + ", architecture=" + architecture + ", family=" + family + '}';
  }

}
