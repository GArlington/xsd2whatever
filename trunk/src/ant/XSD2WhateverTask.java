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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import com.sun.tools.xjc.XJC2Task;
import freecake.xsd2whatever.XsdModelGeneratorTask;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

public class XSD2WhateverTask extends Task {
    private String modelNamespace;
    private String outputDir;
    private String classTemplate;
    private String enumTemplate;
    private String extension;
    private Vector fileSets = new Vector();
    private boolean keepJava = false;

    public void execute() throws BuildException {

        log("Processing XSD files...");
        java.io.File classes = new java.io.File(outputDir + java.io.File.separator + "classes");
        java.io.File gensrc = new java.io.File(outputDir + java.io.File.separator + "generated");

        String delResult;

        //Delete existing dirs
        if (classes.exists()) {
            if ((delResult = deleteDir(classes)) != null) {
                throw new BuildException("Unable to delete directory '" + delResult + "'!  Check file permissions.");
            }
        }
        if (gensrc.exists()) {
            if ((delResult = deleteDir(gensrc)) != null) {
                throw new BuildException("Unable to delete directory '" + delResult + "'!  Check file permissions.");
            }
        }

        //Create dirs
        if (classes.mkdir() == false) {
            throw new BuildException("Unable to create directory '" + classes.getAbsolutePath() + "'!  Check file permissions.");
        }
        gensrc.mkdir();

        Project project = getProject();

        try {
            //run xjc and generate java files from xsd
            XJC2Task xjc = new XJC2Task();
            xjc.setTaskName("xsd2whatever");
            xjc.setProject(project);
            xjc.setDestdir(gensrc);
            xjc.setExtension(true);
            xjc.setPackage(modelNamespace);
            for (Iterator itFSets = fileSets.iterator();itFSets.hasNext();) {
                FileSet fs = (FileSet)itFSets.next();
                xjc.addConfiguredSchema(fs);
            }
            FileSet f3 = new FileSet();
            f3.setDir(new File(outputDir));
            f3.setIncludes("**/*.java");
            xjc.addConfiguredProduces(f3);
            xjc.execute();

            //compile the java files
            Javac javac = new Javac();
            javac.setFork(true);
            javac.setIncludejavaruntime(true);
            javac.setIncludeantruntime(true);
            javac.setTaskName("xsd2whatever");
            javac.setProject(project);
            javac.setSource("1.6");
            javac.setTarget("1.6");
            javac.setDebug(true);
            Path sourcePath = new Path(project, gensrc.getAbsolutePath());
            javac.setSrcdir(sourcePath);
            javac.setDestdir(classes);
            javac.execute();

            //run the java classes through the generator
            XsdModelGeneratorTask gen = new XsdModelGeneratorTask();
            gen.setTaskName("xsd2whatever");
            gen.setProject(project);
            gen.setClassTemplate(classTemplate);
            gen.setEnumTemplate(enumTemplate);
            gen.setExtension(extension);
            gen.setOutputdir(gensrc + java.io.File.separator + extension);
            FileSet f4 = new FileSet();
            f4.setDir(classes);
            f4.setIncludes("**/*.class");
            gen.addFileset(f4);
            Path cp = new Path(project, classes.getAbsolutePath());
            gen.setClasspath(cp);
            gen.execute();

        } catch (Exception e) {
            throw new BuildException(e);
        }

        //clean up classes dir
        if ((delResult = deleteDir(classes)) != null) {
            throw new BuildException("Unable to delete directory '" + delResult + "'!  Check file permissions.");
        }
        for (String child : gensrc.list()) {
            if (!child.equals(extension) && keepJava == false) {
                //clean up java files
                File cf = new File(gensrc,child);
                if ((delResult = deleteDir(cf)) != null) {
                    throw new BuildException("Unable to delete directory '" + delResult + "'!  Check file permissions.");
                }
            }
        }
    }

    public void setModelnamespace(String mn) {
        this.modelNamespace = mn;
    }

    public void setOutputdir(String od) {
        this.outputDir = od;
    }

    public void setClassTemplate(String ct) {
        this.classTemplate = ct;
    }

    public void setEnumTemplate(String et) {
        this.enumTemplate = et;
    }

    public void setExtension(String ex) {
        this.extension = ex;
    }

    public void addFileset(FileSet fs) {
        this.fileSets.add(fs);
    }

    public void setKeepJava(boolean keepJava) {
        this.keepJava = keepJava;
    }

//====================================================================================================================================

    private String deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                File childFile = new File(dir, aChildren);
                String failStr = deleteDir(childFile);
                if (failStr != null) {
                    return failStr;
                }
            }
        }
        boolean success2 = dir.delete();
        if (!success2) {
            return dir.getAbsolutePath();
        }
        return null;
    }
}
