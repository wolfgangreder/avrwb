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

import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.XmlEntity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author wolfi
 */
@XmlRootElement(name = "avr-tools-device-file")
@XmlEntity
public final class XA_AvrToolsDeviceFile
{

  private static JAXBContext ctx;

  private static synchronized JAXBContext getContext() throws JAXBException
  {
    if (ctx == null) {
      ctx = JAXBContext.newInstance(XA_AvrToolsDeviceFile.class);
    }
    assert ctx != null;
    return ctx;
  }

  public static XA_AvrToolsDeviceFile load(@NotNull URL url) throws IOException
  {
    Objects.requireNonNull(url);
    try (InputStream is = url.openStream()) {
      return load(is);
    }
  }

  public static XA_AvrToolsDeviceFile load(@NotNull InputStream is) throws IOException
  {
    Objects.requireNonNull(is);
    try {
      Unmarshaller um = getContext().createUnmarshaller();
      Object tmp = um.unmarshal(is);
      if (tmp instanceof XA_AvrToolsDeviceFile) {
        return (XA_AvrToolsDeviceFile) tmp;
      } else if (tmp == null) {
        return null;
      } else {
        throw new IOException("unknown schema");
      }
    } catch (JAXBException ex) {
      throw new IOException(ex);
    }
  }

  private final List<XA_Variant> variants = new ArrayList<>();
  private final List<XA_Device> devices = new ArrayList<>();
  private final List<XA_Module> modules = new ArrayList<>();

  @XmlElement(name = "variant")
  @XmlElementWrapper(name = "variants")
  public List<XA_Variant> getVariants()
  {
    return variants;
  }

  @XmlElement(name = "device")
  @XmlElementWrapper(name = "devices")
  public List<XA_Device> getDevices()
  {
    return devices;
  }

  @XmlElement(name = "module")
  @XmlElementWrapper(name = "modules")
  public List<XA_Module> getModules()
  {
    return modules;
  }

  public void store(@NotNull URL url) throws IOException
  {
    Objects.requireNonNull(url, "url==null");
    URLConnection conn = url.openConnection();
    conn.setDoOutput(true);
    try (OutputStream os = conn.getOutputStream()) {
      store(os);
    }
  }

  public void store(@NotNull OutputStream os) throws IOException
  {
    Objects.requireNonNull(os, "os==null");
    try {
      Marshaller m = getContext().createMarshaller();
      m.marshal(this, os);
    } catch (JAXBException ex) {
      throw new IOException(ex);
    }
  }

  public XA_DeviceModule getDeviceModule(@NotNull ModuleVector moduleVector)
  {
    Objects.requireNonNull(moduleVector, "moduleVector==null");
    XA_Device device = getDevice(moduleVector.getDeviceName());
    if (device == null) {
      return null;
    }
    for (XA_DeviceModule mod : device.getModules()) {
      ModuleVector v = moduleVector.withModule(mod.getName());
      if (v.equals(moduleVector)) {
        return mod;
      }
    }
    return null;
  }

  public XA_Device getDevice(@NotNull String deviceName)
  {
    Objects.requireNonNull(deviceName, "deviceName==null");
    for (XA_Device dev : getDevices()) {
      if (deviceName.equals(dev.getName())) {
        return dev;
      }
    }
    return null;
  }

  /**
   * Findet ein bestimmtes IORegister
   *
   * @param registerVector Vektor zum register
   * @return Den Registerdeskriptor oder {@code null} wenn das Register nicht gefunden werden kann.
   */
  public XA_Register findRegister(@NotNull RegisterVector registerVector)
  {
    Objects.requireNonNull(registerVector, "registerVector==null");
    XA_DeviceModule deviceModule = getDeviceModule(registerVector.getModule());
    if (deviceModule == null) {
      return null;
    }
    final Set<String> registerGroupNames = new HashSet<>();
    for (XA_DeviceModuleInstance i : deviceModule.getInstances()) {
      for (XA_DeviceModuleRegisterGroup g : i.getRegisterGroup()) {
        registerGroupNames.add(g.getNameInModule());
      }
    }
    for (XA_Module i : getModules()) {
      for (XA_RegisterGroup g : i.getRegisterGroups()) {
        if (registerGroupNames.contains(g.getName())) {
          for (XA_Register r : g.getRegister()) {
            RegisterVector v = registerVector.withRegister(r.getName());
            if (v.equals(registerVector)) {
              return r;
            }
          }
        }
      }
    }
    return null;
  }

  public XA_Module findModule(@NotNull ModuleVector moduleVector)
  {
    Objects.requireNonNull(moduleVector, "moduleVector==null");
    XA_DeviceModule deviceModule = getDeviceModule(moduleVector);
    if (deviceModule == null) {
      return null;
    }
    for (XA_Module i : getModules()) {
      if (deviceModule.getName().equals(i.getName())) {
        return i;
      }
    }
    return null;
  }

  @NotNull
  public List<XA_ValueGroup> findValueGroups(@NotNull RegisterVector registerVector)
  {
    XA_Register register = findRegister(registerVector);
    if (register == null) {
      return Collections.emptyList();
    }
    XA_Module module = findModule(registerVector.getModule());
    return module.getValueGroups();
  }

  public XA_Bitfield findBitField(@NotNull RegisterBitGrpVector grp)
  {
    XA_Module module = findModule(grp.getRegister().getModule());
    if (module != null) {
      for (XA_RegisterGroup rg : module.getRegisterGroups()) {
        for (XA_Register reg : rg.getRegister()) {
          if (grp.getRegister().getRegisterName().equals(reg.getName())) {
            for (XA_Bitfield bf : reg.getBitfields()) {
              if (grp.getName().equals(bf.getName())) {
                return bf;
              }
            }
          }
        }
      }
    }
    return null;
  }

  public XA_ValueGroup findValueGroup(@NotNull RegisterBitGrpVector registerBitGrpVector)
  {
    for (XA_ValueGroup g : findValueGroups(registerBitGrpVector.getRegister())) {
      if (registerBitGrpVector.getName().equals(g.getName())) {
        return g;
      }
    }
    return null;
  }

}
