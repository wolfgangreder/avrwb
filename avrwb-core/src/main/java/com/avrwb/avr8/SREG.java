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

/**
 * Registerspezialisierung für das SREG
 *
 * @author wolfi
 */
public interface SREG extends Register
{

  public static final int C = 0;
  public static final int MASK_C = 1;
  public static final int Z = 1;
  public static final int MASK_Z = 2;
  public static final int N = 2;
  public static final int MASK_N = 4;
  public static final int V = 3;
  public static final int MASK_V = 8;
  public static final int S = 4;
  public static final int MASK_S = 16;
  public static final int H = 5;
  public static final int MASK_H = 32;
  public static final int T = 6;
  public static final int MASK_T = 64;
  public static final int I = 7;
  public static final int MASK_I = 128;

  public boolean getC();

  public boolean setC(boolean newState);

  public boolean getZ();

  public boolean setZ(boolean newState);

  public boolean getN();

  public boolean setN(boolean newState);

  public boolean getV();

  public boolean setV(boolean newState);

  public boolean getS();

  public boolean setS(boolean newState);

  public boolean getH();

  public boolean setH(boolean newState);

  public boolean getT();

  public boolean setT(boolean newState);

  public boolean getI();

  public boolean setI(boolean newState);

  public void fixSignBit();

}
