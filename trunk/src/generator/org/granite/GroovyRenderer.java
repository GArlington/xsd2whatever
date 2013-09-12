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

package org.granite;

import java.util.List;

import org.granite.token.Comment;
import org.granite.token.Expression;
import org.granite.token.Scriplet;
import org.granite.token.TemplateText;
import org.granite.token.Token;

/**
 * @author Franck WOLFF
 */
public class GroovyRenderer {

    private String source = null;

    public String renderSource(List<Token> tokens) {
        return renderSource(tokens, null);
    }

    public String renderSource(List<Token> tokens, String out) {
        StringBuilder sb = new StringBuilder(1024);

        for (Token token : tokens) {
            if (token instanceof TemplateText) {
                String content = token.getContent();

                if (content.length() > 0) {
                    int iLast = content.length() - 1;
                    sb.append("print(\"");

                    for (int i = 0; i < content.length(); i++) {
                        char c = content.charAt(i);

                        switch (c) {
                        case '\\':
                            sb.append("\\\\");
                            break;
                        case '"' :
                            sb.append("\\\"");
                            break;
                        case '\n':
                            sb.append("\\n\");\n");
                            if (i < iLast)
                                sb.append("print(\"");
                            break;
                        case '\f':
                            sb.append("\\f");
                            break;
                        default  :
                            sb.append(c);
                            break;
                        }
                    }

                    if (content.charAt(iLast) != '\n')
                        sb.append("\");\n");
                }
            }
            else if (token instanceof Expression)
                sb.append("print(").append(token.getContent()).append(");\n");
            else if (token instanceof Scriplet)
                sb.append(token.getContent()).append('\n');
            else if (!(token instanceof Comment))
                throw new UnsupportedOperationException("Unsupported token (not implemented): " + token);
        }

        source = sb.toString();

        return source;
    }

    public String getSource() {
        return source;
    }
}
