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
package com.avrwb.assembler.model.impl;

import com.avrwb.assembler.SourceContext;
import com.avrwb.assembler.model.Segment;
import com.avrwb.assembler.model.SegmentElement;

/**
 *
 * @author wolfi
 */
public abstract class AbstractSegmentElement implements SegmentElement
{

  private final int offset;
  private final SourceContext fileContext;
  private final Segment segment;

  protected AbstractSegmentElement(Segment segment,
                                   int offset,
                                   SourceContext fileContext)
  {
    this.segment = segment;
    this.offset = offset;
    this.fileContext = fileContext;
  }

  @Override
  public Segment getSegment()
  {
    return segment;
  }

  @Override
  public SourceContext getSourceContext()
  {
    return fileContext;
  }

  @Override
  public int getStartAddress()
  {
    return offset;
  }

  @Override
  public int getSize()
  {
    return getData().capacity();
  }

  @Override
  public int compareTo(SegmentElement o)
  {
    return Integer.compareUnsigned(offset,
                                   o.getStartAddress());
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 79 * hash + this.offset;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof SegmentElement)) {
      return false;
    }
    final SegmentElement other = (SegmentElement) obj;
    return this.offset == other.getStartAddress();
  }

}
