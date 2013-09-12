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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.granite.Parser;
import org.granite.GroovyRenderer;
import org.granite.token.Token;
import org.granite.reflect.JavaType;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import org.codehaus.groovy.runtime.InvokerHelper;

public class ClassTranslator {

    private final File classTemplate;
    private final File enumTemplate;
    private final String version = "0.0.1";

    public ClassTranslator(File classTemplate, File enumTemplate) {
        this.classTemplate = classTemplate;
        this.enumTemplate = enumTemplate;
    }

    public void translate(Class<?> clazz, File outputDir, String extension) throws Exception {
        File template = (clazz.isEnum()) ? this.enumTemplate : this.classTemplate;
        JavaType javaType = TranslatorUtils.getJavaType(clazz);
        Map<String, Object> bindings = new HashMap<String, Object>();
        bindings.put("gVersion", version);
        bindings.put("jClass", javaType);
        ByteArrayOutputStreamWrapper osw = new ByteArrayOutputStreamWrapper(8192);
        executeGroovy(bindings, new PrintWriter(new OutputStreamWriter(osw)), template);
        OutputStream os = null;
        try {
            File file = getOutputFile(javaType, outputDir, extension);
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            os = new BufferedOutputStream(new FileOutputStream(file));
            os.write(osw.getBytes(), 0, osw.size());
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            if (os != null) {
                try { os.close(); } catch (Exception e) { }
            }
        }
    }

    private File getOutputFile(JavaType javaType, File outputDir, String extension) {
        StringBuilder sb = new StringBuilder()
            .append(outputDir.getAbsolutePath())
            .append(File.separatorChar)
            .append(javaType.getQualifiedName().replace('.', File.separatorChar))
            .append(extension);
        return new File(sb.toString());
    }

    private void executeGroovy(Map<String, Object> bindings, Writer writer, File template) throws Exception {
        InputStream is = null;
        URI uri = template.toURI();
        Script script = null;
        try {
            is = getInputStream(uri, getClass().getClassLoader());
            Reader reader = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()));
            Parser parser = new Parser();
            List<Token> tokens = parser.parse(reader);
            GroovyRenderer renderer = new GroovyRenderer();
            String source = renderer.renderSource(tokens);
            GroovyShell shell = new GroovyShell();
            script = shell.parse(source);
        } catch (Exception e) {
            throw new Exception("Could not compile template: " + uri + ":\n" + e.getMessage());
        } finally {
            if (is != null) {
                try { is.close(); } catch (Exception e) { }
            }
        }
        try {
            Binding binding = (bindings == null ? new Binding() : new Binding(bindings));
            Script scriptInstance = InvokerHelper.createScript(script.getClass(), binding);
            scriptInstance.setProperty("out", writer);
            scriptInstance.run();
            writer.flush();
          } catch (Exception e) {
              throw new Exception("Could not execute template: " + uri + ":\n" + e.getMessage());
          }
    }

    private InputStream getInputStream(URI uri, ClassLoader loader) throws IOException {
        InputStream is = null;
        String scheme = uri.getScheme();
        if ("class".equals(scheme)) {
            if (loader != null) {
                is = loader.getResourceAsStream(uri.getSchemeSpecificPart());
            }
            if (is == null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(uri.getSchemeSpecificPart());
                if (is == null) {
                    throw new IOException("Resource not found exception: " + uri);
                }
            }
        } else if (scheme == null || scheme.length() <= 1) {
            is = new FileInputStream(uri.toString());
        } else {
            is = uri.toURL().openStream();
        }
        return is;
    }

    static class ByteArrayOutputStreamWrapper extends ByteArrayOutputStream {
      public ByteArrayOutputStreamWrapper() { }
      public ByteArrayOutputStreamWrapper(int size) {
        super(size);
      }

      public byte[] getBytes() {
          return buf;
      }
    }
}
