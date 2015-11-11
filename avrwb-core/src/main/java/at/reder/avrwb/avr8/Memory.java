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
import at.reder.avrwb.annotations.NullAllowed;
import at.reder.avrwb.avr8.api.Resetable;
import at.reder.avrwb.io.MemoryChunk;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 *
 * @author Wolfgang Reder
 */
public interface Memory extends Resetable
{

  @NotNull
  public String getId();

  @NotNull
  public String getName();

  @NotNull
  public ByteOrder getByteOrder();

  public int getSize();

  public int getStart();

  public int getByteAt(int address);

  public void setByteAt(int address,
                        int value);

  public int getWordAt(int address);

  public void setWordAt(int address,
                        int value);

  public long getDWordAt(int address);

  public void setDWordAt(int address,
                         long value);

  public long getQWordAt(int address);

  public void setQWordAt(int address,
                         long value);

  public float getFloatAt(int address);

  public void setFloatAt(int address,
                         float f);

//  public double getDoubleAt(int address);
//
//  public void setDoubleAt(int address, double d);
  /**
   * Liest Characters vom Speicher. Bei einem Addressüberlauf (sowohl beim Speicher, als auch dem Buffer) wird das Lesen
   * abgebrochen.
   *
   * @see java.nio.charset.StandardCharsets#US_ASCII
   * @param address Adresse ab der gelesen werden soll.
   * @param buffer Buffer in den geschrieben wird.
   * @param offset Offset ab dem in den Buffer geschrieben wird.
   * @param numCharacters max. Anzahl der Zeichen die zu lesen sind; wenn -1 dann wird bis zum ersten 0 gelesen.
   * @param charSet zu verwendendes Characterset
   * @return die Anzahl der gelesenen Zeichen
   */
  public int readCharacters(int address,
                            @NotNull char[] buffer,
                            int offset,
                            int numCharacters,
                            @NullAllowed("StandardCharsets.US_ASCII") Charset charSet);

  /**
   * Liest einen String ab der Adresse {@code address}. Das lesen wird abgebrochen, wenn eine 0 auftritt, oder das Ende des
   * Speichers aufgetreten ist.
   *
   * @see java.nio.charset.StandardCharsets#US_ASCII
   * @see java.lang.StringBuilder
   * @param address Adresse ab der gelesen wird
   * @param charSet zu verwendendes Characterset.
   * @return Neuer StringBuilder
   */
  @NotNull
  public StringBuilder readString(int address,
                                  @NullAllowed("StandardCharsets.US_ASCII") Charset charSet);

  /**
   * Schreibt characters zum Speicher. Bei einem Adressüberlauf (sowohl beim Speicher, als auch bem Buffer) wird das Schreiben
   * abgebrochen.
   *
   * @see java.nio.charset.StandardCharsets#US_ASCII
   * @param address Adresse ab der geschrieben werden soll.
   * @param buffer Buffer von dem gelesen werden soll.
   * @param offset Offset ab dem vom Buffer gelesen werden soll.
   * @param toWrite Anzahl der characters die zu schreiben sind; bei -1 wird bis zum ersten 0 geschrieben
   * @param charSet zu verwendendes Characterset.
   * @return die Anzahl der geschriebenen Zeichen.
   */
  public int writeCharacters(int address,
                             @NotNull char[] buffer,
                             int offset,
                             int toWrite,
                             @NullAllowed("StandardCharsets.US_ASCII") Charset charSet);

  /**
   * Kopiert den Inhalt von {@code chunk} ab der aktuellen Position den Buffers, in den internen Speicher.
   *
   * @param chunk Speicherbereich bit den zu schreibenden Daten.
   * @return {@code true} wenn der vollständige Inhalte von {@code chunk} geschreiben werden konnte.
   */
  public boolean initialize(@NotNull MemoryChunk chunk);

  /**
   * Initialisiert alle Adressen mit {@code initVal}.
   *
   * @param intVal defaultValue
   */
  public void initialize(int intVal);

}