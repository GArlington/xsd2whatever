/*
  Copyright (C) 2012 Jason Gardner

  This library is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or (at your
  option) any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
  for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, see <http://www.gnu.org/licenses/>.

  "XSD2Whatever" - builds upon and incorprates many pieces of the GraniteDS
  project and, therefore, assumes the same licensing model. www.graniteds.org
*/
package freecake.xsd2whatever;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class XsdModelGeneratorTask extends Task {

    private String outputdir = ".";
    private Path classpath = null;
    private List<FileSet> inputFiles = new ArrayList<FileSet>();
    private String classTemplate = "";
    private String enumTemplate = "";
    private String extension = "unknown";

    @Override
    public void execute() throws BuildException {
        log("Output Directory: " + outputdir, Project.MSG_INFO);
        log("Classpath: " + classpath, Project.MSG_INFO);
        log("Scanning for input files...", Project.MSG_INFO);

        AntClassLoader clazzldr = new AntClassLoader(XsdModelGeneratorTask.class.getClassLoader(), getProject(), classpath, true);
        try {
            clazzldr.setThreadContextLoader();
            for (FileSet fset : inputFiles) {
                DirectoryScanner dirscan = fset.getDirectoryScanner(getProject());
                dirscan.setCaseSensitive(true);
                dirscan.scan();
                String[] filenames = dirscan.getIncludedFiles();
                for (String filename : filenames) {
                    if (filename.endsWith(".class") &&
                        !filename.contains("package-info") &&
                        !filename.contains("ObjectFactory")) {
                        try {
                            File file = new File(dirscan.getBasedir(), filename);
                            if (!file.exists()) {
                                throw new FileNotFoundException(file.toString());
                            }
                            String clazzname = filename.substring(0, filename.length() - 6).replace(File.separatorChar, '.');
                            Class<?> clazz = clazzldr.loadClass(clazzname);
                            if (!clazz.isMemberClass() || clazz.isEnum()) {
                                TranslatorUtils.inputClasses.put(clazz, file);
                            }
                        } catch (Exception ex) {
                            throw new BuildException("Error loading class: " + filename, ex);
                        }
                    }
                }
            }

            log("Running the Generator...", Project.MSG_INFO);
            File classTplFile = new File(getProject().getProperty("basedir"), classTemplate);
            File enumTplFile = new File(getProject().getProperty("basedir"), enumTemplate);
            ClassTranslator translator = new ClassTranslator(classTplFile, enumTplFile);
            File outputdirFile = new File(outputdir);
            for (Map.Entry<Class<?>, File> entry : TranslatorUtils.inputClasses.entrySet()) {
                try {
                    translator.translate(entry.getKey(), outputdirFile, "." + extension);
                } catch (Exception ex2) {
                    throw new BuildException("Error generating: " + entry.getKey() + ":\n" + ex2.getMessage());
                }
            }
            log("Generation complete");
        } finally {
            clazzldr.resetThreadContextLoader();
        }
    }

    public void setOutputdir(String outputdir) {
        this.outputdir = outputdir;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void addFileset(FileSet fileSet) {
        this.inputFiles.add(fileSet);
    }

    public void setClasspath(Path path) {
        if (classpath == null) {
            classpath = path;
        } else {
            classpath.append(path);
        }
    }

    public Path createClasspath() {
        if (classpath == null) {
            classpath = new Path(getProject());
        }
        return classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    public void setClassTemplate(String ct) {
        this.classTemplate = ct;
    }

    public void setEnumTemplate(String et) {
        this.enumTemplate = et;
    }
}
