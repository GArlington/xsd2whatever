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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Franck WOLFF
 */
public class JavaFieldProperty extends JavaMember<Field> implements JavaProperty {

    private final JavaMethod readMethod;
    private final JavaMethod writeMethod;

    public JavaFieldProperty(Field field, JavaMethod readMethod, JavaMethod writeMethod) {
        super(field);

        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    public Class<?> getType() {
        return getMember().getType();
    }

    public boolean hasTypePackage() {
        return (getTypePackageName().length() > 0);
    }
    public String getTypePackageName() {
        Package p = getType().getPackage();
        return (p != null ? p.getName() : "");
    }

    public String getTypeName() {
        return getType().getSimpleName();
    }

    public boolean isReadable() {
        return (Modifier.isPublic(getMember().getModifiers()) || readMethod != null);
    }

    public boolean isWritable() {
        return (Modifier.isPublic(getMember().getModifiers()) || writeMethod != null);
    }

    public boolean isExternalizedProperty() {
        return false;
    }

    public boolean isEnum() {
        return getType().isEnum();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return (
            (getMember().isAnnotationPresent(annotationClass)) ||
            (readMethod != null && readMethod.getMember().isAnnotationPresent(annotationClass)) ||
            (writeMethod != null && writeMethod.getMember().isAnnotationPresent(annotationClass))
        );
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return getMember().getAnnotation(annotationClass);
    }

    public JavaMethod getReadMethod() {
        return readMethod;
    }

    public JavaMethod getWriteMethod() {
        return writeMethod;
    }

    public int compareTo(JavaProperty o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof JavaMethodProperty)
            return ((JavaMethodProperty)obj).getName().equals(getName());
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
            " {name=" + getName() +
            ", readable=" + (readMethod != null) +
            ", writable=" + (writeMethod != null) + "}";
    }
}
