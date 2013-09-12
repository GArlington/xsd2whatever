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

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.granite.reflect.JavaMethod.MethodType;

/**
 * @author Franck WOLFF
 */
public class JavaBean extends JavaAbstractType {

    ///////////////////////////////////////////////////////////////////////////
    // Fields.

    protected final Set<JavaImport> imports = new HashSet<JavaImport>();

    protected final JavaType superclass;

    protected final List<JavaInterface> interfaces;
    protected final List<JavaProperty> interfacesProperties;

    protected final SortedMap<String, JavaProperty> properties;
    protected final JavaFieldProperty uid;

    ///////////////////////////////////////////////////////////////////////////
    // Constructor.

    public JavaBean(Class<?> type, URL url) {
        super(type, url);

        // Find superclass (controller filtered).
        this.superclass = getJavaTypeSuperclass(type);

        // Find implemented interfaces (controller filtered).
        this.interfaces = Collections.unmodifiableList(getJavaTypeInterfaces(type));

        // Collect bean properties.
        this.properties = Collections.unmodifiableSortedMap(initProperties());

        // Collect properties from implemented interfaces (ignore all implemented properties).
        Map<String, JavaProperty> allProperties = new HashMap<String, JavaProperty>(this.properties);
        for (JavaType supertype = this.superclass; supertype instanceof JavaBean; supertype = ((JavaBean)supertype).superclass)
            allProperties.putAll(((JavaBean)supertype).properties);
        Map<String, JavaProperty> iPropertyMap = new HashMap<String, JavaProperty>();
        for (JavaInterface interfaze : interfaces) {
            for (JavaProperty property : interfaze.getProperties()) {
                String name = property.getName();
                if (!iPropertyMap.containsKey(name) && !allProperties.containsKey(name))
                    iPropertyMap.put(name, property);
            }
        }
        this.interfacesProperties = getSortedUnmodifiableList(iPropertyMap.values());

        // Find uid (if any).
        JavaFieldProperty tmpUid = null;
        for (JavaProperty property : properties.values()) {
            if (property instanceof JavaFieldProperty) {
                JavaFieldProperty fieldProperty = (JavaFieldProperty)property;
                if (tmpUid == null && isUid(fieldProperty))
                    tmpUid = fieldProperty;
            }
        }
        this.uid = tmpUid;

        // Collect imports.
        if (superclass != null)
            addToImports(getJavaImport(superclass.getType()));
        for (JavaInterface interfaze : interfaces)
            addToImports(getJavaImport(interfaze.getType()));
        for (JavaProperty property : properties.values())
            addToImports(getJavaImport(property.getType()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties.

    public Set<JavaImport> getImports() {
        return imports;
    }
    protected void addToImports(JavaImport javaImport) {
        if (javaImport != null)
            imports.add(javaImport);
    }

    public boolean hasSuperclass() {
        return superclass != null;
    }
    public JavaType getSuperclass() {
        return superclass;
    }

    public boolean hasInterfaces() {
        return interfaces != null && !interfaces.isEmpty();
    }
    public List<JavaInterface> getInterfaces() {
        return interfaces;
    }

    public boolean hasInterfacesProperties() {
        return interfacesProperties != null && !interfacesProperties.isEmpty();
    }
    public List<JavaProperty> getInterfacesProperties() {
        return interfacesProperties;
    }

    public Collection<JavaProperty> getProperties() {
        return properties.values();
    }

    public boolean hasUid() {
        return uid != null;
    }
    public JavaFieldProperty getUid() {
        return uid;
    }

    public boolean hasEnumProperty() {
        for (JavaProperty property : properties.values()) {
            if (property.isEnum())
                return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Utilities.

    protected SortedMap<String, JavaProperty> initProperties() {
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(type);
        SortedMap<String, JavaProperty> propertyMap = new TreeMap<String, JavaProperty>();

        // Standard declared fields.
        for (Field field : type.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                String name = field.getName();
                JavaMethod readMethod = null;
                JavaMethod writeMethod = null;

                if (field.getType().isMemberClass() && !field.getType().isEnum())
                    throw new UnsupportedOperationException("Inner classes are not supported (except enums): " + field.getType());

                if (propertyDescriptors != null) {
                    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                        if (name.equals(propertyDescriptor.getName())) {
                            if (propertyDescriptor.getReadMethod() != null)
                                readMethod = new JavaMethod(propertyDescriptor.getReadMethod(), MethodType.GETTER);
                            if (propertyDescriptor.getWriteMethod() != null)
                                writeMethod = new JavaMethod(propertyDescriptor.getWriteMethod(), MethodType.SETTER);
                            break;
                        }
                    }
                }

                JavaFieldProperty property = new JavaFieldProperty(field, readMethod, writeMethod);
                propertyMap.put(name, property);
            }
        }

        if (propertyDescriptors != null) {
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getReadMethod() != null &&
                    propertyDescriptor.getReadMethod().getDeclaringClass().equals(type) &&
                    !propertyMap.containsKey(propertyDescriptor.getName())) {

                    JavaMethod readMethod = new JavaMethod(propertyDescriptor.getReadMethod(), MethodType.GETTER);
                    JavaMethodProperty property = new JavaMethodProperty(propertyDescriptor.getName(), readMethod, null);
                    propertyMap.put(propertyDescriptor.getName(), property);
                }
            }
        }

        return propertyMap;
    }

    //JG
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return type.isAnnotationPresent(annotationClass);
    }

    //JG
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return type.getAnnotation(annotationClass);
    }
}
