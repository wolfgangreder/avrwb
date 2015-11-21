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
package com.avrwb.avr8.helper;

import com.avrwb.annotations.GuardedBy;
import com.avrwb.annotations.Immutable;
import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.ProvidedModule;
import com.avrwb.schema.AvrCore;
import com.avrwb.schema.AvrFamily;
import com.avrwb.schema.ModuleClass;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author Wolfgang Reder
 */
@Immutable
public final class ModuleKey
{

  @GuardedBy("this")
  private Pattern namePattern;
  private final String name;
  private final AvrCore version;
  private final AvrFamily architecture;
  private final ModuleClass moduleClass;

  public ModuleKey(@NotNull String name,
                   @NotNull AvrCore version,
                   @NotNull AvrFamily family,
                   @NotNull ModuleClass moduleClass)
  {
    Objects.requireNonNull(name,
                           "name==null");
    Objects.requireNonNull(version,
                           "version==null");
    Objects.requireNonNull(family,
                           "family==null");
    Objects.requireNonNull(moduleClass,
                           "moduleclass==null");
    this.name = name;
    this.version = version;
    this.architecture = family;
    this.moduleClass = moduleClass;
  }

  @NotNull
  public String getName()
  {
    return name;
  }

  @NotNull
  public AvrCore getVersion()
  {
    return version;
  }

  @NotNull
  public AvrFamily getArchitecture()
  {
    return architecture;
  }

  @NotNull
  public ModuleKey withName(@NotNull String newName)
  {
    return new ModuleKey(newName,
                         version,
                         architecture,
                         moduleClass);
  }

  @NotNull
  public ModuleKey withVersion(@NotNull AvrCore version)
  {
    return new ModuleKey(name,
                         version,
                         architecture,
                         moduleClass);
  }

  @NotNull
  public ModuleKey withArchitecture(@NotNull AvrFamily arch)
  {
    return new ModuleKey(name,
                         version,
                         arch,
                         moduleClass);
  }

  @NotNull
  public ModuleKey withModuleClass(@NotNull ModuleClass mc)
  {
    return new ModuleKey(name,
                         version,
                         architecture,
                         mc);
  }

  private synchronized Pattern getNamePattern() throws PatternSyntaxException
  {
    if (namePattern == null) {
      namePattern = Pattern.compile(name);
    }
    return namePattern;
  }

  public boolean matches(ProvidedModule pm,
                         boolean nameAsRegExp) throws PatternSyntaxException
  {
    if (pm == null) {
      return false;
    }
    if (!pm.family().equals(architecture)) {
      return false;
    }
    boolean found = false;
    for (String cv : pm.core()) {
      if (cv.equals(version.toString())) {
        found = true;
        break;
      }
    }
    if (!found) {
      return false;
    }
    if (nameAsRegExp) {
      final Pattern np = getNamePattern();
      for (String n : pm.value()) {
        if (np.matcher(n).matches()) {
          return true;
        }
      }
    } else {
      for (String n : pm.value()) {
        if (name.equals(n)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 67 * hash + Objects.hashCode(this.name);
    hash = 67 * hash + Objects.hashCode(this.version);
    hash = 67 * hash + Objects.hashCode(this.architecture);
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
    final ModuleKey other = (ModuleKey) obj;
    if (!Objects.equals(this.name,
                        other.name)) {
      return false;
    }
    if (this.version != other.version) {
      return false;
    }
    return this.architecture == other.architecture;
  }

  @Override
  public String toString()
  {
    return "ModuleKey{" + "name=" + name + ", version=" + version + ", architecture=" + architecture + '}';
  }

}
