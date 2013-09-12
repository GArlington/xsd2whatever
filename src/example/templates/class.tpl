<%

    StringBuilder sb = new StringBuilder();
    sb.append("Ext.define('" + jClass.packageName + "." + jClass.name + "', {\n");

    if (jClass.hasSuperclass()) {
        sb.append("    extend: '" + jClass.superclass.packageName + "." + jClass.superclass.name + "',\n\n");
    } else {
        sb.append("    extend: 'Ext.data.Model',\n\n");
    }

    sb.append("    fields: [\n");
    for (jProperty in jClass.properties) {
        String propName = jProperty.name;
        if (jProperty.isAnnotationPresent(javax.xml.bind.annotation.XmlElement)) {
            propName = jProperty.getAnnotation(javax.xml.bind.annotation.XmlElement).name();
        } else if (jProperty.isAnnotationPresent(javax.xml.bind.annotation.XmlAttribute)) {
            propName = jProperty.getAnnotation(javax.xml.bind.annotation.XmlAttribute).name();
        }
        if (propName.equals("##default")) {
              propName = jProperty.name;
        }
        //if (propName == propValue ||
        if (propName == "Event" ||
            propName == "delete" ||
            propName == "class" ||
            propName == "default" ||
            propName == "value") {
            propName = "_" + propName;
        }
        if (jProperty.isAnnotationPresent(javax.xml.bind.annotation.XmlValue)) {
            propName = "value";
        }

        propName = propName.replace("-", "_");
        sb.append("            '" + propName + "',\n");
    }
    sb.append("            ]\n\n");

    sb.append("});");
%>/*
  This file was auto-generated. Do not modify
  XSD2Whatever - version: ${gVersion}
*/

${sb.toString()}