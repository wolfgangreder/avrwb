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
package com.avrwb.avr8;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class FractNGTest
{

  public FractNGTest()
  {
  }

  @Test
  public void testGetByte()
  {
    Fract8 f = Fract8.valueOfByte(0);
    assertEquals(f.getByte(),
                 0);
    f = Fract8.valueOfByte(20);
    assertEquals(f.getByte(),
                 20);
    f = Fract8.valueOfByte(178);
    assertEquals(f.getByte(),
                 178);
  }

  @Test
  public void testGetFloat()
  {
    Fract8 f = Fract8.valueOfByte(0);
    assertEquals(f.floatValue(),
                 0f);
    f = Fract8.valueOfByte(20);
    assertEquals(f.floatValue(),
                 1 / 8f + 1 / 32f,
                 1e-6f);
    f = Fract8.valueOfByte(178);
    assertEquals(f.floatValue(),
                 1.390625f,
                 1e-6);
  }

  @Test
  public void testMult()
  {
    Fract8 f1 = Fract8.valueOfByte(0x40);
    Fract8 f2 = Fract8.valueOfByte(0x40);
    Fract16 result = f1.mult(f2);
    assertEquals(result.floatValue(),
                 1 / 4f,
                 1e-6f);
    Fract8 f8 = result.toFract8();
    assertEquals(f8.floatValue(),
                 1 / 4f,
                 1e-6f);
    f1 = Fract8.valueOfByte(0x01);
    f2 = Fract8.valueOfByte(0x80);
    result = f1.mult(f2);
    assertEquals(result.floatValue(),
                 f1.floatValue(),
                 1e-6);
    f1 = Fract8.valueOfByte(0x02);
    f2 = Fract8.valueOfByte(0x40);
    result = f1.mult(f2);
    assertEquals(result.floatValue(),
                 1 / 128f,
                 1e-6);
  }

  @Test
  public void testFloatCTOR()
  {
    Fract8 f1 = Fract8.valueOfFloat(0.25f);
    assertEquals(f1.getByte(),
                 0x20);
  }

}
