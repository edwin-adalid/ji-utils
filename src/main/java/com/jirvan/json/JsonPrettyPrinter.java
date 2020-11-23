/*

Copyright (c) 2014, Jirvan Pty Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Jirvan Pty Ltd nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.jirvan.json;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;

public class JsonPrettyPrinter extends DefaultPrettyPrinter {

    public JsonPrettyPrinter() {
        _objectIndenter = new LocalIndenter(4);
        _arrayIndenter = new LocalIndenter(4);
    }

    @Override
    public JsonPrettyPrinter createInstance() {
        return new JsonPrettyPrinter();
    }

    @Override public void writeArrayValueSeparator(JsonGenerator jg) throws IOException {
        jg.writeRaw(',');
        _arrayIndenter.writeIndentation(jg, _nesting);
    }

    @Override public void writeEndArray(JsonGenerator jg, int nrOfValues) throws IOException {
        if (!_arrayIndenter.isInline()) {
            --_nesting;
        }
        if (nrOfValues > 0) {
            _arrayIndenter.writeIndentation(jg, _nesting);
        } else {
            jg.writeRaw(' ');
        }
        jg.writeRaw(']');
    }

    @Override public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
        if (_spacesInObjectEntries) {
            jg.writeRaw(": ");
        } else {
            jg.writeRaw(':');
        }
    }

    public static class LocalIndenter implements Indenter {

        private int indentationSpaces;

        public LocalIndenter(int indentationSpaces) {
            this.indentationSpaces = indentationSpaces;
        }

        //@Override
        public boolean isInline() { return false; }

        //@Override
        public void writeIndentation(JsonGenerator jg, int level) throws IOException {
            jg.writeRaw("\n");
            for (int i = 0; i < level * indentationSpaces; i++) {
                jg.writeRaw(' ');
            }
        }
    }

}