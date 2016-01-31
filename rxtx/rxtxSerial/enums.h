/*
 * $Id$
 *
 * Copyright (C) 2016 Wolfgang Reder <wolfgang.reder@aon.at>.
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
#ifndef ENUMS_H
#define ENUMS_H

enum  DataBits {
  DATA_5,
  DATA_6,
  DATA_7,
  DATA_8,
  DATA_9
} ;

typedef enum {
  STOP_1,
  STOP_1_5,
  STOP_2
} StopBits;

typedef enum {
  NONE,
  EVEN,
  ODD,
  MARK,
  SPACE
} Parity;
#endif /* ENUMS_H */

