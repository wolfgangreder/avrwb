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

import com.avrwb.annotations.GuardedBy;
import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NullAllowed;
import com.avrwb.annotations.ProvidedModule;
import com.avrwb.annotations.ProvidedModules;
import com.avrwb.annotations.ThreadSave;
import com.avrwb.avr8.ModuleBuilderFactory;
import com.avrwb.avr8.helper.ModuleKey;
import com.avrwb.schema.AvrCore;
import com.avrwb.schema.AvrFamily;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.lookup.Lookups;

/**
 * Klasse zum Auflösen von Modulimplementationen.
 *
 * @author Wolfgang Reder
 */
@ThreadSave
public final class ModuleResolver
{

  private static final class InstanceHolder
  {

    private static final ModuleResolver INSTANCE = new ModuleResolver();
  }
  private final Lookup.Result<ModuleBuilderFactory> lookupResult;
  @GuardedBy("lookupResult")
  private final Map<AvrFamily, Map<ModuleKey, ModuleBuilderFactory>> modules = new HashMap<>();

  /**
   * Liefert die Standardinstanz
   *
   * @return instanz
   */
  @NotNull
  public static ModuleResolver getInstance()
  {
    return InstanceHolder.INSTANCE;
  }

  private ModuleResolver()
  {
    Lookup lookup = Lookups.forPath("avrwb");
    lookupResult = lookup.lookupResult(ModuleBuilderFactory.class);
    lookupResult.addLookupListener(this::lookupResultChanged);
  }

  private void lookupResultChanged(LookupEvent ev)
  {
    synchronized (lookupResult) {
      modules.clear();
    }
  }

  private List<ProvidedModule> getProviedModules(Class<?> clazz)
  {
    ProvidedModules pm = clazz.getAnnotation(ProvidedModules.class);
    if (pm != null) {
      return Arrays.asList(pm.value());
    }
    ProvidedModule p = clazz.getAnnotation(ProvidedModule.class);
    if (p != null) {
      return Collections.singletonList(p);
    }
    return Collections.emptyList();
  }

  private void processFactory(ModuleBuilderFactory mf)
  {
    List<ProvidedModule> m = getProviedModules(mf.getClass());
    for (ProvidedModule pm : m) {
      Map<ModuleKey, ModuleBuilderFactory> am = modules.computeIfAbsent(pm.family(),
                                                                        (AvrFamily a) -> new HashMap<>());
      for (String n : pm.value()) {
        for (String sc : pm.core()) {
          AvrCore c = AvrCore.valueOf(sc);
          am.put(new ModuleKey(n,
                               c,
                               pm.family(),
                               pm.moduleClass()),
                 mf);
        }
      }
    }
  }

  private void checkMap()
  {
    synchronized (lookupResult) {
      if (modules.isEmpty()) {
        for (ModuleBuilderFactory f : lookupResult.allInstances()) {
          processFactory(f);
        }
      }
    }
  }

  /**
   * Sucht nach einer Factory für das durch {@code moduleKey} spezifierte Modul.
   *
   * @param moduleKey moduleKey
   * @return ModuleBuilderFactory oder {@code null} wenn nicht gefunden.
   */
  @NullAllowed("not found")
  public ModuleBuilderFactory findModuleBuilder(@NotNull ModuleKey moduleKey)
  {
    Objects.requireNonNull(moduleKey,
                           "moduleKey==null");
    checkMap();
    Map<ModuleKey, ModuleBuilderFactory> map = modules.get(moduleKey.getFamily());
    ModuleBuilderFactory result = null;
    if (map != null) {
      result = map.get(moduleKey);
      if (result == null) {
        result = map.get(moduleKey.withVersion(AvrCore.ANY));
      }
    }
    return result;
  }

}
