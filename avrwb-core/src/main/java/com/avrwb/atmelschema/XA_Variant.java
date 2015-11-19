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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 *
 * @author wolfi
 */
@XmlEntity
public final class XA_Variant
{

  private int tempMin;
  private int tempMax;
  private long speedMax;
  private float vccMin;
  private float vccMax;
  private String pinout;
  private String _package;
  private String ordercode;

  @XmlAttribute(name = "tempmin")
  @XmlSchemaType(name = "xs:integer")
  public int getTempMin()
  {
    return tempMin;
  }

  public void setTempMin(int tempMin)
  {
    this.tempMin = tempMin;
  }

  @XmlAttribute(name = "tempmax")
  @XmlSchemaType(name = "xs:integer")
  public int getTempMax()
  {
    return tempMax;
  }

  public void setTempMax(int tempMax)
  {
    this.tempMax = tempMax;
  }

  @XmlAttribute(name = "speedmax")
  @XmlSchemaType(name = "xs:integer")
  public long getSpeedMax()
  {
    return speedMax;
  }

  public void setSpeedMax(int speedMax)
  {
    this.speedMax = speedMax;
  }

  @XmlAttribute(name = "vccmin")
  @XmlSchemaType(name = "xs:float")
  public float getVccMin()
  {
    return vccMin;
  }

  public void setVccMin(float vccMin)
  {
    this.vccMin = vccMin;
  }

  @XmlAttribute(name = "vccmax")
  @XmlSchemaType(name = "xs:float")
  public float getVccMax()
  {
    return vccMax;
  }

  public void setVccMax(float vccMax)
  {
    this.vccMax = vccMax;
  }

  @XmlAttribute(name = "pinout")
  public String getPinout()
  {
    return pinout;
  }

  public void setPinout(String pinout)
  {
    this.pinout = pinout;
  }

  @XmlAttribute(name = "package")
  public String getPackage()
  {
    return _package;
  }

  public void setPackage(String _package)
  {
    this._package = _package;
  }

  @XmlAttribute(name = "ordercode")
  public String getOrdercode()
  {
    return ordercode;
  }

  public void setOrdercode(String ordercode)
  {
    this.ordercode = ordercode;
  }

}
