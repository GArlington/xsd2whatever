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


/**
 * @author Franck WOLFF
 */
public class JavaMethodProperty implements JavaProperty {

    private final String name;
    private final JavaMethod readMethod;
    private final JavaMethod writeMethod;
    private final Class<?> type;

    public JavaMethodProperty(String name, JavaMethod readMethod, JavaMethod writeMethod) {
        if (name == null || (readMethod == null && writeMethod == null))
            throw new NullPointerException("Invalid parameters");
        this.name = name;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        this.type = (
            readMethod != null ?
            readMethod.getMember().getReturnType() :
            writeMethod.getMember().getParameterTypes()[0]
        );
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isReadable() {
        return (readMethod != null);
    }

    public boolean isWritable() {
        return (writeMethod != null);
    }

    public boolean isExternalizedProperty() {
        return false;
    }

    public boolean isEnum() {
        return type.isEnum();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return (
            (readMethod != null && readMethod.getMember().isAnnotationPresent(annotationClass)) ||
            (writeMethod != null && writeMethod.getMember().isAnnotationPresent(annotationClass))
        );
    }

    public JavaMethod getReadMethod() {
        return readMethod;
    }

    public JavaMethod getWriteMethod() {
        return writeMethod;
    }

    public int compareTo(JavaProperty o) {
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof JavaMethodProperty)
            return ((JavaMethodProperty)obj).name.equals(name);
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
