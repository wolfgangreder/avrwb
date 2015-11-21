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
package com.avrwb.schema.util;

import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NullAllowed;
import com.avrwb.annotations.ThreadSave;
import com.avrwb.schema.XmlPart;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 * Statische Methoden zum laden und speichern von Device Beschreibungen.
 *
 * @author wolfi
 */
@ThreadSave
public final class DeviceStreamer
{

  public static enum Version
  {
    V_1_0;
  }
  private static JAXBContext context;
  private static final Map<Version, Schema> SCHEMA_CACHE = new HashMap<>();

  private static Schema loadSchema(DeviceStreamer.Version version) throws IOException, SAXException
  {
    InputStream is = getSchemaStream(version);
    if (is != null) {
      SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      return sf.newSchema(new StreamSource(is));
    }
    return null;
  }

  public static InputStream getSchemaStream(DeviceStreamer.Version version)
  {
    String fileName = null;
    switch (version) {
      case V_1_0:
        fileName = "partdescriptor-1.0.xsd";
    }
    if (fileName != null) {
      return DeviceStreamer.class.getResourceAsStream("/com/avrwb/schema/" + fileName);
    }
    return null;

  }

  @NotNull
  public static Schema getSchema(DeviceStreamer.Version version) throws IOException, SAXException
  {
    synchronized (SCHEMA_CACHE) {
      Schema result;
      if (!SCHEMA_CACHE.containsKey(version)) {
        result = loadSchema(version);
        SCHEMA_CACHE.put(version,
                         result);
      } else {
        result = SCHEMA_CACHE.get(version);
      }

      return result;
    }
  }

  @NotNull
  public static synchronized JAXBContext getContext() throws IOException
  {
    if (context == null) {
      try {
        context = JAXBContext.newInstance(XmlPart.class);
      } catch (JAXBException ex) {
        throw new IOException(ex);
      }
    }
    return context;
  }

  /**
   * Liest eine MCU Beschreibung ein.
   *
   * @param is Eingabestrom
   * @param schemaVersion wenn die Beschreibungsdatei Validiert werden soll, ungleich {@code null}.
   * @return MCU Beschreibung
   * @throws IOException if any
   * @throws org.xml.sax.SAXException if any
   */
  @NotNull
  public static XmlPart loadDevice(@NotNull InputStream is,
                                   @NullAllowed("no schema validation") DeviceStreamer.Version schemaVersion) throws IOException,
                                                                                                                     SAXException
  {
    Objects.requireNonNull(is,
                           "is==null");
    try {
      Unmarshaller um = getContext().createUnmarshaller();
      if (schemaVersion != null) {
        Schema schema = getSchema(schemaVersion);
        if (schema == null) {
          throw new IOException("cannot load schema");
        }
        um.setSchema(schema);
      }
      return (XmlPart) um.unmarshal(is);
    } catch (JAXBException ex) {
      if (ex.getCause() instanceof IOException) {
        throw (IOException) ex.getCause();
      } else if (ex.getCause() instanceof SAXException) {
        throw (SAXException) ex.getCause();
      }
      throw new IOException(ex);
    }
  }

  public static void storeDevice(@NotNull XmlPart part,
                                 @NotNull OutputStream os) throws IOException
  {
    Objects.requireNonNull(part,
                           "part==null");
    Objects.requireNonNull(os,
                           "os==null");
    try {
      Marshaller m = getContext().createMarshaller();
      m.marshal(part,
                os);
    } catch (JAXBException ex) {
      if (ex.getCause() instanceof IOException) {
        throw (IOException) ex.getCause();
      } else {
        throw new IOException(ex);
      }
    }
  }

  private DeviceStreamer()
  {
  }

}
