/*
 * $Id$
 *
 * Author Wolfgang Reder (w.reder@mountain-sd.at)
 *
 * Copyright (c) 2015 Mountain Software Design KG
 *
 * Diese Datei unterliegt der Mountain Software Design Sourcecode Lizenz.
 */
package com.avrwb.avr8.api.instructions;

public class BaseInstructionContext
{

  private final int opcode;
  private final String mnemonic;
  private String currentDeviceStateMessage;
  protected long finishCycle = -1;

  public BaseInstructionContext(int opcode,
                                String mnemonic)
  {
    this.opcode = opcode;
    this.mnemonic = mnemonic;
  }

}
