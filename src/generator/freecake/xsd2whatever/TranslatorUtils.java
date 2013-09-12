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

import org.granite.reflect.JavaEnum;
import org.granite.reflect.JavaBean;
import org.granite.reflect.JavaType;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslatorUtils {

    public static Map<Class<?>, File> inputClasses = new HashMap<Class<?>, File>();

    public static URL findResource(Class<?> clazz) {
        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        if (clazz.isPrimitive()) {
            return null;
        }
        ClassLoader loader = (clazz.getClassLoader() != null ? clazz.getClassLoader() : ClassLoader.getSystemClassLoader());
        return loader.getResource(clazz.getName().replace('.', '/').concat(".class"));
    }

    public static JavaType getJavaType(Class<?> clazz) {
        JavaType javaType = null;
        URL url = TranslatorUtils.findResource(clazz);
        if (clazz.isEnum()) {
            List<String> eVals = new ArrayList<String>();
            try{
                Object[] consts = clazz.getEnumConstants();
                for (Object obj : consts) {
                    Class<?> sub = obj.getClass();
                    Method mth = sub.getDeclaredMethod("value");
                    eVals.add((String) mth.invoke(obj));
                }
            } catch (Exception e) { }
            javaType = new JavaEnum(clazz, url, eVals);
        } else {
            javaType = new JavaBean(clazz, url);
        }
        return javaType;
    }

}