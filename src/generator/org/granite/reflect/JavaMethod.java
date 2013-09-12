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

import java.lang.reflect.Method;

/**
 * @author Franck WOLFF
 */
public class JavaMethod extends JavaMember<Method> {

    public enum MethodType {
        GETTER,
        SETTER,
        OTHER
    }

    private final boolean override;
    private final MethodType type;

    public JavaMethod(Method method, MethodType type) {
        super(method);

        boolean override = false;
        for (Class<?> superclass = method.getDeclaringClass().getSuperclass();
            superclass != null;
            superclass = superclass.getSuperclass()) {

            try {
                Method superMethod = superclass.getMethod(method.getName(), method.getParameterTypes());
                // Strict return type checking (isAssignableFrom?)...
                if (superMethod.getReturnType() == method.getReturnType()) {
                    override = true;
                    break;
                }
            } catch (NoSuchMethodException e) {
                // continue...
            }
        }
        this.override = override;

        this.type = type;
    }

    public boolean isOverride() {
        return override;
    }

    public MethodType getType() {
        return type;
    }

    public String getTypeName() {
        return type.name();
    }
}
