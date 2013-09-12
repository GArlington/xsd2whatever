<%
    StringBuilder sb = new StringBuilder();
    sb.append("Ext.define('" + jClass.packageName + "." + jClass.name + "', {\n\n");
    sb.append("    statics: {\n");

    int ctr = 0;
    for (jEnumValue in jClass.enumValues) {
        String eName = jEnumValue.name;
        if (eName.toLowerCase() == "string" ||
            eName.toLowerCase() == "date" ||
            eName.toLowerCase() == "boolean") {
            eName = "_" + eName;
        }
        String pName = eName.replace(" ", "_");
        sb.append("        " + pName + ":{index:" + ctr + ",name:'" + pName + "',toString:function(){return '" + pName + "'}},\n");
        ctr++;
    }

    sb.append("    }\n\n");
    sb.append("});");

%>/*
  This file was auto-generated. Do not modify
  XSD2Whatever - version: ${gVersion}
*/

${sb.toString()}