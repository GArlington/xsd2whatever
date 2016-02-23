## Templates ##
Templates are groovy scripts designed to work with XSD2Whatever.  The generator requires a _classTemplate_ and, optionally, an _enumTemplate_ (if your XSDs contain enumerations).

The generator handles many features of XSD, but not the complete specification.  It will likely evolve over time to provide more functionality.

For information on groovy template syntax, check out [Groovy Templates](http://groovy.codehaus.org/Groovy+Templates)

## Variables ##
When the templates are executed, they are initialized with a set of variables that can be evaluated to produce the desired output.

  * **gVersion** - Version number of the generator
  * **jClass** - Object describing the Java class that was generated via JAXB-XJC.  This object (hopefully) contains all of the information about the XSD entity to generate the desired output.  **jClass** has the following methods (depending on the context):


---

**ALL**

---

<font color='purple'><code>Class</code></font> **getType()**<br>
<font color='purple'><code>String</code></font> <b>getName()</b><br>
<font color='purple'><code>Package</code></font> <b>getPackage()</b><br>
<font color='purple'><code>String</code></font> <b>getPackageName()</b><br>
<font color='purple'><code>String</code></font> <b>getQualifiedName()</b><br>
<font color='purple'><code>URL</code></font> <b>getUrl()</b><br>
<hr />
<b>Class</b>
<hr />
<font color='purple'><code>Set&lt;JavaImport&gt;</code></font> <b>getImports()</b><br>
<font color='purple'><code>boolean</code></font> <b>hasSuperclass()</b><br>
<font color='purple'><code>JavaType</code></font> <b>getSuperclass()</b><br>
<font color='purple'><code>boolean</code></font> <b>hasInterfaces()</b><br>
<font color='purple'><code>List&lt;JavaInterface&gt;</code></font> <b>getInterfaces()</b><br>
<font color='purple'><code>boolean</code></font> <b>hasInterfacesProperties()</b><br>
<font color='purple'><code>List&lt;JavaProperty&gt;</code></font> <b>getInterfacesProperties()</b><br>
<font color='purple'><code>Collection&lt;JavaProperty&gt;</code></font> <b>getProperties()</b><br>
<font color='purple'><code>boolean</code></font> <b>hasEnumProperty()</b><br>
<font color='purple'><code>boolean</code></font> <b>isAnnotationPresent(Class<code>&lt;? extends Annotation&gt;</code> annotationClass)</b><br>
<font color='purple'><code>&lt;T extends Annotation&gt; T</code></font> <b>getAnnotation(Class<code>&lt;T&gt;</code> annotationClass)</b><br>
<hr />
<b>Enum</b>
<hr />
<font color='purple'><code>JavaEnumValue</code></font> <b>getFirstEnumValue()</b><br>
<font color='purple'><code>List&lt;JavaEnumValue&gt;</code></font> <b>getEnumValues()</b><br>