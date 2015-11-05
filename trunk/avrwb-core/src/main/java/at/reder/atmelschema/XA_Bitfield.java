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

@XmlEntity
public final class XA_Bitfield
{

  private String name;
  private String caption;
  private int mask;
  private String values;

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

  @XmlAttribute(name = "caption")
  @XmlSchemaType(name = "xs:string")
  public String getCaption()
  {
    return caption;
  }

  public void setCaption(String caption)
  {
    this.caption = caption;
  }

  @XmlAttribute(name = "mask")
  @XmlSchemaType(name = "xs:integer")
  public Integer getMask()
  {
    return mask;
  }

  public void setMask(Integer mask)
  {
    this.mask = mask != null ? mask : 0;
  }

  @XmlAttribute(name = "values")
  @XmlSchemaType(name = "xs:string")

  public String getValues()
  {
    return values;
  }

  public void setValues(String values)
  {
    this.values = values;
  }

  @Override
  public String toString()
  {
    return "XA_Bitfield{" + "name=" + name + '}';
  }

}
