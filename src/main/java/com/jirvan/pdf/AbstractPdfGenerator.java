/*

Copyright (c) 2018, Jirvan Pty Ltd
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

package com.jirvan.pdf;

import com.itextpdf.text.DocumentException;
import com.jirvan.util.Io;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractPdfGenerator {

    public void toOutputStream(OutputStream outputStream) throws IOException {
        try {
            ITextRenderer renderer = new ITextRenderer();
            ITextUserAgent callback = getUserAgent(renderer.getOutputDevice());
            callback.setSharedContext(renderer.getSharedContext());
            renderer.getSharedContext().setUserAgentCallback(callback);
            renderer.setDocumentFromString(generateHtml());
            renderer.layout();
            renderer.createPDF(outputStream);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract ITextUserAgent getUserAgent(ITextOutputDevice outputDevice);

    public byte[] toPdf() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            toOutputStream(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void toPdfFile(String pathname) {
        toPdfFile(new File(pathname));
    }

    public void toHtmlFile(String pathname) {
        toHtmlFile(new File(pathname));
    }

    public void toPdfFile(File file) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            toOutputStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void toHtmlFile(File file) {
        Io.toFile(generateHtml(), file, true);
    }

    public String generateHtml() {
        return templateEngine.process(xhtmlTemplate, initializeContext(model));
    }


    //======================== Abstract methods ========================//

    protected abstract Context initializeContext(Object model);


    //======================== Everything below here is protected or private ========================//

    protected AbstractPdfGenerator(Object model, String pathToHtmlTemplate) {
        this.model = model;
        this.xhtmlTemplate = Io.getResourceFileString(this.getClass(), pathToHtmlTemplate);
    }


    //======================== Everything below here is private ========================//

    private static final TemplateEngine templateEngine = initialiseTemplateEngine();

    private Object model;
    private String xhtmlTemplate;

    private static TemplateEngine initialiseTemplateEngine() {
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }


}
