# XSD->Whatever #

## Generate code from XSD schemas using templates ##

### Background ###
For years, one design pattern I've used over and over is contract-driven communication interfaces.  Often this is accomplished by defining an XSD, which each side of the interface can build towards.

Way back when,  I was working in Java and was able to leverage the great JAXB tools and take it a step further, and generate model/POJO classes from the XSDs, and let JAXB auto serialize them, leaving me to only ever deal with generated Java objects and XSDs when doing XML communication (and also JSON, with some modifications).

Later, I began working in Actionscript/Flex quite a bit, and was seeking a similar utility.  In the end, I ended up leveraging the class generator portion of GraniteDS, writing a bunch of custom templates for it, and then writing XML/JSON serializers to tie it all together.  It was all a bit hacky, but I was able to handle AS2/AS3 messaging with Java/.NET backends with ease.

Then, I hacked on it even more, having the project spit out javascript files to do the same thing with an ExtJS project, and again for C++ files for a Qt project.

So, I finally decided I should try to boil it down to the bare bones pieces that I actually needed and try to make the project more generic.  This project is the result of that, and at this point, has very basic code generation capabilities.

### License ###
This project draws heavily from the GraniteDS project, and therefore, assumes the same LGPL license.  It also leverages Antlr/ASM (BSD), Groovy (Apache2), and JAXB (CDDL).  If you need data services for Flex development, be sure to check out [GraniteDS](http://www.graniteds.org)

### How It Works ###
The generator is invoked as an Ant task, or alternatively, from a command shell.  Provided with an XSD(s) and template(s), the generator will produce a new file for each declared XSD entity, using the template's output.

The templates are essentially groovy scripts that are fed a "JavaType" object at runtime, which describes the XSD entity, allowing the script to generate the desired output.

The project contains two Ant build scripts: one builds the generator, and a second build script (build-example.xml)  shows an example usage of the generator.

When writing your own template, refer to this wiki page for information on the template API: [TemplateAPI](TemplateAPI.md)