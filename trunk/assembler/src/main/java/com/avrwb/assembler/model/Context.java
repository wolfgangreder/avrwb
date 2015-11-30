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
import com.avrwb.assembler.AssemblerConfig;
import com.avrwb.assembler.AssemblerError;
import com.avrwb.assembler.SourceContext;
import com.avrwb.assembler.StandardAssemblerConfig;
import com.avrwb.avr8.helper.AVRWBDefaults;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
public final class Context
{

  private final Level DEBUGLEVEL = Level.FINEST;
  private static final Logger LOGGER = AVRWBDefaults.LOGGER;
  private final Map<String, Alias> constMap = new HashMap<>();
  private final Map<String, Alias> varMap = new HashMap<>();
  private final LinkedList<Expression> expStack = new LinkedList<>();
  private final Map<Segment, List<SegmentElement>> segments = new HashMap<>();
  private final Map<Segment, LongAdder> segmentPositions = new HashMap<>();
  private Segment currentSegment = Segment.CSEG;
  private final AssemblerConfig config;
  private final InternalAssembler assembler;
  public final LinkedList<AssemblerSource> sourceStack = new LinkedList<>();
  public AssemblerSource masterSource;
  private final List<Inline> inlines = new LinkedList<>();

  public Context(@NotNull InternalAssembler assembler,
                 AssemblerConfig config)
  {
    Objects.requireNonNull(assembler,
                           "assembler==null");
    this.assembler = assembler;
    this.config = config != null ? config : StandardAssemblerConfig.getDefaultInstance();
    for (Alias a : this.config.getDefaultAliases()) {
      addAlias(a,
               null);
    }
  }

  private void debugLog(Supplier<String> msg)
  {
    LOGGER.log(DEBUGLEVEL,
               msg);
  }

  public void addInline(Inline inline)
  {
    Objects.requireNonNull(inline,
                           "inline==null");
    inlines.add(inline);
  }

  public List<Inline> getInlines()
  {
    return Collections.unmodifiableList(inlines);
  }

  public void pushSource(AssemblerSource src)
  {
    Objects.requireNonNull(src,
                           "src==null");
    sourceStack.push(src);
    if (masterSource == null) {
      masterSource = src;
    }
  }

  public AssemblerSource getMasterSource()
  {
    return masterSource;
  }

  public AssemblerSource popSource()
  {
    return sourceStack.pop();
  }

  public AssemblerSource getCurrentSource()
  {
    return sourceStack.getFirst();
  }

  public AssemblerConfig getConfig()
  {
    return config;
  }

  public InternalAssembler getAssembler()
  {
    return assembler;
  }

  public boolean isExpressionStackEmpty()
  {
    return expStack.isEmpty();
  }

  public void pushExpression(@NotNull Expression exp)
  {
    Objects.requireNonNull(exp,
                           "exp==null");
    debugLog(() -> MessageFormat.format("pushing expression {0}",
                                        exp));
    expStack.push(exp);
  }

  public void forEachExpression(@NotNull Consumer<? super Expression> action,
                                boolean deleteProcessed)
  {
    Objects.requireNonNull(action,
                           "action==null");
    if (expStack.isEmpty()) {
      return;
    }
    ListIterator<Expression> iter = expStack.listIterator(expStack.size());
    while (iter.hasPrevious()) {
      action.accept(iter.previous());
    }
    if (deleteProcessed) {
      expStack.clear();
    }
  }

  public Expression popExpression(SourceContext sourceContext) throws AssemblerError
  {
    try {
      Expression result = expStack.pop();
      debugLog(() -> MessageFormat.format("poping expression {0}",
                                          result));
      return result;
    } catch (NoSuchElementException ex) {
      throw new AssemblerError(ex.getMessage(),
                               sourceContext);
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

  public Alias addAlias(@NotNull Alias alias,
                        SourceContext sourceContext) throws AssemblerError
  {
    Objects.requireNonNull(alias,
                           "alias==null");
    Alias current = getAlias(alias.getName());
    if (current != null && current.isConst()) {
      throw new AssemblerError("alias already defined:" + alias.getName(),
                               sourceContext);
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

  public int getCurrentPosition(@NotNull Segment seg)
  {
    Objects.requireNonNull(seg,
                           "seg==null");
    return segmentPositions.computeIfAbsent(seg,
                                            (Segment s) -> new LongAdder()).intValue();
  }

  public int getCurrentPosition()
  {
    return getCurrentPosition(currentSegment);
  }

  public void setCurrentPosition(@NotNull Segment seg,
                                 int currentPosition)
  {
    Objects.requireNonNull(seg,
                           "seg==null");
    LongAdder adder = segmentPositions.computeIfAbsent(seg,
                                                       (Segment s) -> new LongAdder());
    adder.reset();
    adder.add(currentPosition);
  }

  private int addToSegment(@NotNull SegmentElement element,
                           Segment segment,
                           SourceContext sourceContext)
  {
    Objects.requireNonNull(element,
                           "element==null");
    List<SegmentElement> seg = segments.computeIfAbsent(segment,
                                                        (Segment s) -> new ArrayList<>());
    int maxAddress = element.getStartAddress() + element.getSize();
    if (seg.isEmpty()) {
      seg.add(element);
    } else {
      int index = -(Collections.binarySearch(seg,
                                             element) + 1);
      if (index == seg.size()) {
        SegmentElement prev = seg.get(index - 1);
        if ((prev.getStartAddress() + prev.getSize()) > element.getStartAddress()) {
          throw new AssemblerError("address collision in " + segment + " at offset 0x" + Integer.
                  toHexString(element.getStartAddress()),
                                   sourceContext);
        }
        seg.add(index,
                element);
      } else {
        SegmentElement next = seg.get(index);
        if (next.getStartAddress() < maxAddress) {
          throw new AssemblerError("address collision in " + segment + " at offset 0x" + Integer.toHexString(element.
                  getStartAddress()),
                                   sourceContext);
        }
        if (index != 0) {
          SegmentElement prev = seg.get(index - 1);
          if ((prev.getStartAddress() + prev.getSize()) > element.getStartAddress()) {
            throw new AssemblerError("address collision in " + segment + " at offset 0x" + Integer.
                    toHexString(element.getStartAddress()),
                                     sourceContext);
          }
        }
      }
    }
    return element.getSize();
  }

  public void addToSeg(@NotNull SegmentElement element,
                       SourceContext sourceContext)
  {
    Objects.requireNonNull(element,
                           "element==null");
    addToSeg(currentSegment,
             element,
             sourceContext);
  }

  public void addToSeg(@NotNull Segment seg,
                       @NotNull SegmentElement element,
                       SourceContext sourceContext)
  {
    Objects.requireNonNull(seg,
                           "seg==null");
    Objects.requireNonNull(element,
                           "element==null");
    LongAdder adder = segmentPositions.computeIfAbsent(seg,
                                                       (Segment s) -> new LongAdder());
    adder.add(addToSegment(element,
                           seg,
                           sourceContext));

  }

  public List<SegmentElement> getSegment(@NotNull Segment seg)
  {
    Objects.requireNonNull(seg,
                           "seg==null");
    return Collections.unmodifiableList(segments.computeIfAbsent(seg,
                                                                 (Segment s) -> new ArrayList<>()));
  }

  public Segment getCurrentSegment()
  {
    return currentSegment;
  }

  public void setCurrentSegment(@NotNull Segment currentSegment)
  {
    Objects.requireNonNull(currentSegment,
                           "currentSegment==null");
    this.currentSegment = currentSegment;
  }

}
