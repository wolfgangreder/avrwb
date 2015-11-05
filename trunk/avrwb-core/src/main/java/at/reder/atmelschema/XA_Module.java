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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Wolfgang Reder
 */
@XmlEntity
public final class XA_Module
{

  public static final class Param
  {

    @XmlAttribute(name = "name")
    public String name;
    @XmlAttribute(name = "value")
    public String value;

    public Param()
    {
    }

    public Param(String name, String value)
    {
      this.name = name;
      this.value = value;
    }

    @Override
    public String toString()
    {
      return "Param{" + "name=" + name + ", value=" + value + '}';
    }

  }
  private String name;
  private String caption;
  private final List<XA_RegisterGroup> registerGroups = new ArrayList<>();
  private final List<XA_ValueGroup> valueGroups = new ArrayList<>();
  private final List<Param> parameter = new LinkedList<>();
  private Map<String, String> paramMap;

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

  @XmlElement(name = "register-group")
  public List<XA_RegisterGroup> getRegisterGroups()
  {
    return registerGroups;
  }

  @XmlElement(name = "value-group")
  public List<XA_ValueGroup> getValueGroups()
  {
    return valueGroups;
  }

  @XmlTransient
  public Map<String, String> getParameter()
  {
    if (paramMap == null) {
      paramMap = new HashMap<>();
      for (Param p : parameter) {
        if (p != null && p.name != null && p.value != null) {
          paramMap.put(p.name, p.value);
        }
      }
    }
    return paramMap;
  }

  @XmlElement(name = "param")
  @XmlElementWrapper(name = "parameters")
  public List<Param> getParameterList()
  {
    paramMap = null;
    return parameter;
  }

  @Override
  public String toString()
  {
    return "XA_Module{" + "name=" + name + '}';
  }

}
