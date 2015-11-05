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
package at.reder.avrwb.avr8;

import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.annotations.NotThreadSave;
import at.reder.avrwb.annotations.NullAllowed;
import at.reder.avrwb.annotations.Stateless;
import at.reder.avrwb.annotations.ThreadSave;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Wolfgang Reder
 */
@NotThreadSave
public final class MemoryAccessSet implements Set<MemoryAccess>
{

  private final Set<MemoryAccess> set = EnumSet.noneOf(MemoryAccess.class);

  @ThreadSave
  @Stateless
  public static final class Adapter extends XmlAdapter<String, MemoryAccessSet>
  {

    @Override
    public MemoryAccessSet unmarshal(String v) throws IllegalArgumentException
    {
      if (v == null) {
        return null;
      }
      MemoryAccessSet result = new MemoryAccessSet();
      for (char ch : v.toCharArray()) {
        result.add(MemoryAccess.valueOf(ch));
      }
      return result;
    }

    @Override
    public String marshal(MemoryAccessSet v)
    {
      if (v == null) {
        return null;
      }
      StringBuilder b = new StringBuilder();
      for (MemoryAccess a : v) {
        b.append(a.sign());
      }
      return b.toString();
    }

  }

  public MemoryAccessSet()
  {

  }

  public MemoryAccessSet(@NullAllowed("empty") Collection<? extends MemoryAccess> coll)
  {
    if (coll != null) {
      set.addAll(coll);
    }
  }

  /**
   * Liefert eine veränderbare Kopie.
   *
   * @return kopie
   */
  @NotNull
  public Set<MemoryAccess> toSet()
  {
    if (set.isEmpty()) {
      return EnumSet.noneOf(MemoryAccess.class);
    } else {
      return EnumSet.copyOf(set);
    }
  }

  @Override
  public int size()
  {
    return set.size();
  }

  @Override
  public boolean isEmpty()
  {
    return set.isEmpty();
  }

  @Override
  public boolean contains(Object o)
  {
    return set.contains(o);
  }

  @Override
  public Iterator<MemoryAccess> iterator()
  {
    return set.iterator();
  }

  @Override
  public Object[] toArray()
  {
    return set.toArray();
  }

  @Override
  @SuppressWarnings("SuspiciousToArrayCall")
  public <T> T[] toArray(T[] a)
  {
    return set.toArray(a);
  }

  @Override
  public boolean add(MemoryAccess e)
  {
    return set.add(e);
  }

  @Override
  public boolean remove(Object o)
  {
    return set.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    return set.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends MemoryAccess> c)
  {
    return set.addAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    return set.retainAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    return set.removeAll(c);
  }

  @Override
  public void clear()
  {
    set.clear();
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 53 * hash + Objects.hashCode(this.set);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final MemoryAccessSet other = (MemoryAccessSet) obj;
    return this.set.equals(other.set);
  }

}
