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
package com.avrwb.assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.RecognitionException;

/**
 *
 * @author wolfi
 */
public class SyntaxException extends AssemblerException
{

  private final List<RecognitionException> errors;

  private SyntaxException(RecognitionException err,
                          Collection<? extends RecognitionException> errors)
  {
    super(AssemblerError.constructMessage(err),
          err,
          AssemblerError.createSourceContext(err));
    this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
  }

  public SyntaxException(Collection<? extends RecognitionException> errors)
  {
    this(errors.iterator().next(),
         errors);
  }

  public SyntaxException(RecognitionException err)
  {
    this(Collections.singleton(err));
  }

  public SyntaxException(String msg,
                         SourceContext sourceContext)
  {
    super(msg,
          sourceContext);
    errors = Collections.emptyList();
  }

  public SyntaxException(Throwable th,
                         SourceContext sourceContext)
  {
    super(th,
          sourceContext);
    if (th instanceof RecognitionException) {
      errors = Collections.singletonList((RecognitionException) th);
    } else {
      errors = Collections.emptyList();
    }
  }

  public SyntaxException(String msg,
                         Throwable th,
                         SourceContext sourceContext)
  {
    super(msg,
          th,
          sourceContext);
    if (th instanceof RecognitionException) {
      errors = Collections.singletonList((RecognitionException) th);
    } else {
      errors = Collections.emptyList();
    }
  }

}
