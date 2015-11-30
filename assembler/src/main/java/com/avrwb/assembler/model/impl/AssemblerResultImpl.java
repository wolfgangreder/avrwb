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

import com.avrwb.annotations.NotNull;
import com.avrwb.assembler.AssemblerResult;
import com.avrwb.assembler.model.AssemblerSource;
import com.avrwb.assembler.model.Context;
import com.avrwb.assembler.model.FileContext;
import com.avrwb.assembler.model.Inline;
import com.avrwb.assembler.model.Segment;
import com.avrwb.assembler.model.SegmentElement;
import com.avrwb.io.MemoryChunkOutputStream;
import com.avrwb.schema.util.Converter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author wolfi
 */
@Messages({
  "# {0} - addr",
  "# {1} - opcode1",
  "# {2} - opcode2",
  "# {3} - line",
  "List_SEG={0} {1} {2} {3}",
  "# {0} - line",
  "List_INL=                   {0}"})
public class AssemblerResultImpl implements AssemblerResult
{

  private final Context context;
  private final Lookup myLookup;

  public AssemblerResultImpl(@NotNull Context context) throws IOException
  {
    Objects.requireNonNull(context,
                           "context==null");
    this.context = context;
    myLookup = Lookups.singleton(context);
  }

  private void writeIntelHex(List<? extends SegmentElement> elements,
                             MemoryChunkOutputStream os) throws IOException
  {
    for (SegmentElement e : elements) {
      os.write(e);
    }
  }

  private void writeList(Context ctx,
                         Writer writer) throws IOException
  {
    final Map<FileContext, SegmentElement> segments = new HashMap<>();
    final Map<FileContext, Inline> inlines = new HashMap<>();
    final String newLine = System.getProperty("line.separator");
    for (SegmentElement se : ctx.getSegment(Segment.CSEG)) {
      segments.put(se.getFileContext(),
                   se);
    }
    for (SegmentElement se : ctx.getSegment(Segment.ESEG)) {
      segments.put(se.getFileContext(),
                   se);
    }
    for (Inline i : ctx.getInlines()) {
      inlines.put(i.getFileContext(),
                  i);
    }
    writeFile(ctx.getMasterSource(),
              segments,
              inlines,
              newLine,
              writer);
  }

  private void writeCodeSegment(SegmentElement seg,
                                String line,
                                Writer writer) throws IOException
  {
    String op1;
    String op2;
    ByteBuffer data = seg.getData();
    int pos = data.position();
    try {
      data.position(0);
      op1 = Converter.printHexString(data.getShort() & 0xffff,
                                     4,
                                     false);
      if (seg.getSize() == 2) {
        op2 = "    ";
      } else {
        op2 = Converter.printHexString(data.getShort() & 0xffff,
                                       4,
                                       false);
      }
      writer.append(Bundle.List_SEG(Converter.printHexString(seg.getStartAddress() >> 1,
                                                             6,
                                                             false),
                                    op1,
                                    op2,
                                    line));
    } finally {
      data.position(pos);
    }
  }

  private void writeEEpromSegment(SegmentElement seg,
                                  String line,
                                  Writer writer) throws IOException
  {

  }

  private void writeURLStarter(AssemblerSource src,
                               String newLine,
                               Writer writer) throws IOException
  {
    writer.append("URL: ");
    writer.append(src.getSourceURL().toString());
    writer.append(newLine);
  }

  private String getLineNumberString(int line)
  {
    String i = Integer.toUnsignedString(line);
    switch (i.length()) {
      case 1:
        return "     " + i + ": ";
      case 2:
        return "    " + i + ": ";
      case 3:
        return "   " + i + ": ";
      case 4:
        return "  " + i + ": ";
      case 5:
        return " " + i + ": ";
      default:
        return i + ": ";
    }
  }

  private void writeFile(AssemblerSource src,
                         Map<FileContext, SegmentElement> segments,
                         Map<FileContext, Inline> inlines,
                         String newLine,
                         Writer writer) throws IOException
  {
    writeURLStarter(src,
                    newLine,
                    writer);
    try (LineNumberReader reader = new LineNumberReader(src.getReader())) {
      String line;
      while ((line = reader.readLine()) != null) {
        FileContext fctx = new FileContext(src,
                                           reader.getLineNumber());
        writer.append(getLineNumberString(reader.getLineNumber()));
        SegmentElement seg = segments.get(fctx);
        if (seg != null) {
          if (seg.getSegment() == Segment.CSEG) {
            writeCodeSegment(seg,
                             line,
                             writer);
          } else {
            writeEEpromSegment(seg,
                               line,
                               writer);
          }
        } else {
          writer.append("                 ");
          Inline inline = inlines.get(fctx);
          writer.append(line);
          if (inline != null) {
            writer.append(newLine);
            writeFile(inline.getSrc(),
                      segments,
                      inlines,
                      newLine,
                      writer);
            writeURLStarter(src,
                            newLine,
                            writer);
          }
        }
        writer.append(newLine);
      }
    }
  }

  @Override
  public void getCSEG(MemoryChunkOutputStream os) throws IOException
  {
    writeIntelHex(context.getSegment(Segment.CSEG),
                  os);
  }

  @Override
  public void getESEG(MemoryChunkOutputStream os) throws IOException
  {
    writeIntelHex(context.getSegment(Segment.ESEG),
                  os);
  }

  @Override
  public void getList(Writer writer) throws IOException
  {
    writeList(context,
              writer);
  }

  @Override
  public Lookup getLookup()
  {
    return myLookup;
  }

}
