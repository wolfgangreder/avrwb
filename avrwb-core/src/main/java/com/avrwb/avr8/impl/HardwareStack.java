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
package com.avrwb.avr8.impl;

import com.avrwb.atmelschema.util.HexIntAdapter;
import com.avrwb.avr8.Stack;
import com.avrwb.avr8.helper.AVRWBDefaults;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

/**
 *
 * @author wolfi
 */
public final class HardwareStack implements Stack
{

  private static final int STACK_SIZE = 3;
  final Deque<Integer> stack = new ArrayDeque<>(STACK_SIZE + 1);

  @Override
  public int push(int byteToPush)
  {
    int oldByte = stack.getFirst();
    stack.push(byteToPush);
    if (stack.size() > STACK_SIZE) {
      AVRWBDefaults.LOGGER.log(Level.WARNING,
                               "stack overflow while pushing {0}",
                               new Object[]{HexIntAdapter.toHexString(byteToPush,
                                                                      2)});
      stack.removeLast();
    }
    return oldByte;
  }

  @Override
  public int pop()
  {
    if (stack.isEmpty()) {
      AVRWBDefaults.LOGGER.log(Level.WARNING,
                               "stack underoverflow");
      return 0;

    }
    return stack.pop();
  }

  @Override
  public int getSP()
  {
    return stack.size();
  }

}
