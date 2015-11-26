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
package com.avrwb.assembler.model;

import com.avrwb.annotations.NotNull;
import com.avrwb.assembler.AssemblerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class Context
{

  private final Map<String, Alias> constMap = new HashMap<>();
  private final Map<String, Alias> varMap = new HashMap<>();
  private final Deque<Expression> expStack = new LinkedList<>();
  private final Map<Integer, List<SegmentElement>> cseg = new HashMap<>();
  private final Map<Integer, List<SegmentElement>> dseg = new HashMap<>();
  private final Map<Integer, List<SegmentElement>> eseg = new HashMap<>();
  private int currentCPosition;
  private int currentDPosition;
  private int currentEPosition;

  public boolean isExpressionStackEmpty()
  {
    return expStack.isEmpty();
  }

  public void pushExpression(@NotNull Expression exp)
  {
    Objects.requireNonNull(exp,
                           "exp==null");
    expStack.push(exp);
  }

  public Expression popExpression() throws AssemblerException
  {
    try {
      return expStack.pop();
    } catch (NoSuchElementException ex) {
      throw new AssemblerException(ex.getMessage());
    }
  }

  public Alias getAlias(@NotNull String key)
  {
    Objects.requireNonNull(key,
                           "key==null");
    Alias result = constMap.get(key);
    if (result == null) {
      result = varMap.get(key);
    }
    return result;
  }

  public Alias addAlias(@NotNull Alias alias) throws AssemblerException
  {
    Objects.requireNonNull(alias,
                           "alias==null");
    Alias current = getAlias(alias.getName());
    if (current != null && current.isConst()) {
      throw new AssemblerException("alias already defined");
    }
    if (alias.isConst()) {
      Alias tmp = constMap.putIfAbsent(alias.getName(),
                                       alias);
      if (tmp != null) {
        throw new IllegalStateException("alias collision");
      }
    } else {
      varMap.put(alias.getName(),
                 alias);
    }
    return current;
  }

  public int getCurrentCPosition()
  {
    return currentCPosition;
  }

  public void setCurrentCPosition(int currentCPosition)
  {
    this.currentCPosition = currentCPosition;
  }

  public int getCurrentDPosition()
  {
    return currentDPosition;
  }

  public void setCurrentDPosition(int currentDPosition)
  {
    this.currentDPosition = currentDPosition;
  }

  public int getCurrentEPosition()
  {
    return currentEPosition;
  }

  public void setCurrentEPosition(int currentEPosition)
  {
    this.currentEPosition = currentEPosition;
  }

  public void addToCSEG(@NotNull SegmentElement element)
  {
    Objects.requireNonNull(element,
                           "element==null");
    cseg.computeIfAbsent(element.getOffset(),
                         (Integer p) -> new ArrayList<>()).add(element);
  }

  public Map<Integer, List<SegmentElement>> getCSEG()
  {
    return Collections.unmodifiableMap(cseg);
  }

  public void addToDSEG(@NotNull SegmentElement element)
  {
    Objects.requireNonNull(element,
                           "element==null");
    dseg.computeIfAbsent(element.getOffset(),
                         (Integer p) -> new ArrayList<>()).add(element);
  }

  public Map<Integer, List<SegmentElement>> getDSEG()
  {
    return Collections.unmodifiableMap(dseg);
  }

  public void addToESEG(@NotNull SegmentElement element)
  {
    Objects.requireNonNull(element,
                           "element==null");

    eseg.computeIfAbsent(element.getOffset(),
                         (Integer p) -> new ArrayList<>()).add(element);
  }

  public Map<Integer, List<SegmentElement>> getESEG()
  {
    return Collections.unmodifiableMap(eseg);
  }

}
