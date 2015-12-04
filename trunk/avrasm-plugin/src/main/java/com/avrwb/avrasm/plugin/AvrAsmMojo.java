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
package com.avrwb.avrasm.plugin;

import com.avrwb.assembler.Assembler;
import com.avrwb.assembler.AssemblerConfig;
import com.avrwb.assembler.AssemblerException;
import com.avrwb.assembler.AssemblerResult;
import com.avrwb.assembler.StandardAssemblerConfig;
import com.avrwb.assembler.StandardAssemblerSource;
import com.avrwb.io.IntelHexOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.openide.util.Lookup;

/**
 *
 * @author wolfi
 */
@Mojo(name = "avrasm", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES, requiresDependencyResolution = ResolutionScope.TEST)
public class AvrAsmMojo extends AbstractMojo
{

  /**
   * parameter expression="${basedir}/src/test/resources/avrasm" default="${basedir}/src/test/resources/avrasm"
   */
  @Parameter(defaultValue = "${basedir}/src/test/resources/avrasm")
  private File sourceDir;
  /**
   * parameter expression="${project.build.directory}/test-classes" default="${project.build.directory}/test-classes/avrasm"
   */
  @Parameter(defaultValue = "${project.build.directory}/test-classes/avrasm")
  private String targetDir;

  @Parameter
  private List<String> includePaths;

  @Parameter(defaultValue = "true")
  private boolean generateListFile;
  @Parameter(defaultValue = "true")
  private boolean generateEseg;
  @Parameter(defaultValue = "true")
  private boolean generateCseg;

  private List<Path> splitIncludePath()
  {
    if (includePaths != null && !includePaths.isEmpty()) {
      List<Path> result = new ArrayList<>(includePaths.size());
      for (String s : includePaths) {
        result.add(Paths.get(s));
      }
      return result;
    }
    return Collections.emptyList();
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException
  {
    List<String> filesToCompile = collectFiles(sourceDir,
                                               null);
    Map<File, File> mapping = makeMapping(filesToCompile,
                                          sourceDir,
                                          new File(targetDir));
    StandardAssemblerConfig.AssemblerConfigBuilder configBuilder = new StandardAssemblerConfig.AssemblerConfigBuilder();
    for (Path iPath : splitIncludePath()) {
      configBuilder.includePath(iPath);
    }
    AssemblerConfig asmConfig = configBuilder.build();
    Assembler asm = Lookup.getDefault().lookup(Assembler.class);
    for (Map.Entry<File, File> e : mapping.entrySet()) {
      try {
        if (!isUpToDate(e.getKey(),
                        e.getValue())) {
          compile(e.getKey(),
                  e.getValue(),
                  asm,
                  asmConfig);
        }
      } catch (IOException ex) {
        String msg = "Error while compiling " + e.getKey().toString();
        getLog().error(msg,
                       ex);
        throw new MojoExecutionException(msg,
                                         ex);
      } catch (AssemblerException ex) {
        String msg = "Error while compiling " + e.getKey().toString();
        getLog().error(msg,
                       ex);
        throw new MojoFailureException(msg,
                                       ex);
      }
    }
  }

  private boolean isUpToDate(File source,
                             File target) throws IOException
  {
    if (!generateCseg && !generateEseg && !generateListFile) {
      return true;
    }
    final FileTime sourceTime = Files.getLastModifiedTime(source.toPath());
    FileTime targetTime;
    if (generateCseg) {
      if (!target.exists()) {
        return false;
      }
      targetTime = Files.getLastModifiedTime(target.toPath());
      if (sourceTime.compareTo(targetTime) >= 0) {
        return false;
      }
    }
    if (generateEseg) {
      Path p = Paths.get(target.toString().replace(".hex",
                                                   ".eep"));
      if (!Files.exists(p)) {
        return false;
      }
      targetTime = Files.getLastModifiedTime(p);
      if (sourceTime.compareTo(targetTime) >= 0) {
        return false;
      }
    }
    if (generateListFile) {
      Path p = Paths.get(target.toString().replace(".hex",
                                                   ".lst"));
      if (!Files.exists(p)) {
        return false;
      }
      targetTime = Files.getLastModifiedTime(p);
      if (sourceTime.compareTo(targetTime) >= 0) {
        return false;
      }
    }
    return true;
  }

  private void compile(File source,
                       File target,
                       Assembler asm,
                       AssemblerConfig asmConfig) throws IOException, AssemblerException

  {
    getLog().info(source.toString());
    AssemblerResult result = asm.compile(new StandardAssemblerSource(source.toPath()),
                                         asmConfig);
    File targetParent = target.getParentFile();
    if (generateCseg && result.isCSEGAvailable()) {
      targetParent.mkdirs();
      target.delete();
      try (IntelHexOutputStream os = new IntelHexOutputStream(new FileOutputStream(target))) {
        result.getCSEG(os);
      }
    }
    if (generateEseg && result.isESEGAvailable()) {
      targetParent.mkdirs();
      File eep = new File(target.toString().replace(".hex",
                                                    ".eep"));
      try (IntelHexOutputStream os = new IntelHexOutputStream(new FileOutputStream(eep))) {
        result.getCSEG(os);
      }
    }
    if (generateListFile) {
      target = new File(target.toString().replace("hex",
                                                  "lst"));
      try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target))) {
        result.getList(writer);
      }
    }
  }

  private Map<File, File> makeMapping(List<String> sourceFiles,
                                      File sourcePath,
                                      File targetPath)
  {
    Map<File, File> result = new HashMap<>();
    for (String p : sourceFiles) {
      File source = new File(sourcePath,
                             p);
      File target = new File(targetPath,
                             p.replace(".asm",
                                       ".hex"));
      result.put(source,
                 target);
    }
    return result;
  }

  private List<String> collectFiles(File sourceDir,
                                    String subPath)
  {
    if (sourceDir == null || !sourceDir.canRead() || !sourceDir.isDirectory()) {
      return Collections.emptyList();
    }
    File searchDir;
    if (subPath == null) {
      searchDir = sourceDir;
      subPath = "";
    } else {
      searchDir = new File(sourceDir,
                           subPath);
      subPath = subPath + File.separator;
    }
    List<String> result = new LinkedList<>();
    for (File current : searchDir.listFiles()) {
      if (current.canRead()) {
        if (current.isDirectory()) {
          result.addAll(collectFiles(
                  sourceDir,
                  subPath + current.getName()));
        } else if (current.getName().endsWith(".asm")) {
          result.add(subPath + current.getName());
        }
      }
    }
    return result;
  }

}
