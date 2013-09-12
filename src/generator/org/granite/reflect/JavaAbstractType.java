/*
  GRANITE DATA SERVICES
  Copyright (C) 2007-2008 ADEQUATE SYSTEMS SARL

  This file is part of Granite Data Services.

  Granite Data Services is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or (at your
  option) any later version.

  Granite Data Services is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
  for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, see <http://www.gnu.org/licenses/>.

  =============================================================================

  Modifications have been made to this file: Jason Gardner - 09/01/2013
  The original file and project can be found @ www.graniteds.org
*/

package org.granite.reflect;

import freecake.xsd2whatever.TranslatorUtils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * @author Franck WOLFF
 */
public abstract class JavaAbstractType implements JavaType {

    ///////////////////////////////////////////////////////////////////////////
    // Fields.

    protected final Class<?> type;
    protected final URL url;

    private long lastModified = Long.MIN_VALUE;

    ///////////////////////////////////////////////////////////////////////////
    // Constructor.

    protected JavaAbstractType(Class<?> type, URL url) {
        if (type == null)
            throw new IllegalArgumentException("Parameter type cannot be null");

        this.type = type;
        this.url = url;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties.

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        if (type.isMemberClass())
            return type.getEnclosingClass().getSimpleName() + '$' + type.getSimpleName();
        return type.getSimpleName();
    }

    public Package getPackage() {
        return type.getPackage();
    }

    public String getPackageName() {
        return (type.getPackage() != null ? type.getPackage().getName() : "");
    }

    public String getQualifiedName() {
        if (type.getPackage() == null)
            return getName();
        return new StringBuilder().append(getPackageName()).append('.').append(getName()).toString();
    }

    public URL getUrl() {
        return url;
    }

    public long getLastModified() {
        if (lastModified == Long.MIN_VALUE) {
            try {
                lastModified = lastModified(url);
            } catch (IOException e) {
                lastModified = -1L;
            }
        }
        return lastModified;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Utility.

    protected <T extends Collection<?>> T removeNull(T coll) {
        coll.remove(null);
        return coll;
    }

    protected PropertyDescriptor[] getPropertyDescriptors(Class<?> type) {
        PropertyDescriptor[] propertyDescriptors = getProperties(type);
        return (propertyDescriptors != null ? propertyDescriptors : new PropertyDescriptor[0]);
    }

    protected List<JavaProperty> getSortedUnmodifiableList(Collection<JavaProperty> coll) {
        List<JavaProperty> list = (coll instanceof List ? (List<JavaProperty>)coll : new ArrayList<JavaProperty>(coll));
        Collections.sort(list);
        return Collections.unmodifiableList(list);
    }

    protected List<JavaInterface> getJavaTypeInterfaces(Class<?> clazz) {
        List<JavaInterface> interfazes = new ArrayList<JavaInterface>();
        //TODO
        /*
        for (Class<?> interfaze : clazz.getInterfaces()) {
            if (getConfig().isGenerated(interfaze))
                interfazes.add((JavaInterface).getJavaType(interfaze));
        }
        */
        return interfazes;
    }

    protected JavaType getJavaTypeSuperclass(Class<?> clazz) {
        Class<?> superclass = clazz.getSuperclass();
        if ((superclass != null) && TranslatorUtils.inputClasses.containsKey(superclass))
            return TranslatorUtils.getJavaType(superclass);
        return null;
    }

    protected PropertyDescriptor[] getProperties(Class<?> clazz) {
        try {
            return Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        } catch (Exception e) {
            throw new RuntimeException("Could not introspect properties of class: " + clazz, e);
        }
    }

    protected boolean isUid(JavaFieldProperty fieldProperty) {
        /*
        return getConfig().getUid() == null
            ? "uid".equals(fieldProperty.getName())
            : getConfig().getUid().equals(fieldProperty.getName());
        */
        return "uid".equals(fieldProperty.getName());
    }

    protected JavaImport getJavaImport(Class<?> clazz) {
        URL url = TranslatorUtils.findResource(clazz);
        JavaImport javaImport = new JavaImport(clazz, url);
        return javaImport;
    }

    private long lastModified(URL url) throws IOException {
        long lastModified = -1L;
        if (url != null) {
            URLConnection connection = url.openConnection();
            if (connection instanceof JarURLConnection) {
                JarEntry entry = ((JarURLConnection)connection).getJarEntry();
                if (entry != null)
                    lastModified = entry.getTime();
            }
            if (lastModified == -1L)
                lastModified = connection.getLastModified();
        }
        return (lastModified == 0L ? -1L : lastModified);
    }

}
