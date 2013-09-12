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
*/

package org.granite.token;

import java.io.StringReader;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Franck WOLFF
 */
public class Directive extends Token {

    String name = null;
    final Map<String, String> attributes = new HashMap<String, String>();

    public Directive(int index, String content) throws Exception {
        super(index, content);
        parse(content);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    private void parse(String content)
        throws SAXException, ParserConfigurationException, IOException {
        InputSource is = new InputSource(new StringReader('<' + content + "/>"));
        SAXParserFactory.newInstance().newSAXParser().parse(is, new Handler());
    }

    @Override
    public String toString() {
        return this.getClass().getName() + '[' + name + ", " + attributes + ']';
    }

    class Handler extends DefaultHandler {
        @Override
        public void startElement(String namespaceURI,
                                 String localName,
                                 String qName,
                                 Attributes atts)
            throws SAXException {
            if (name != null)
                throw new SAXException("illegal nested element: " + qName);
            name = qName;
            for (int i = 0; i < atts.getLength(); i++)
                attributes.put(atts.getQName(i), atts.getValue(i));
        }
    }
}
