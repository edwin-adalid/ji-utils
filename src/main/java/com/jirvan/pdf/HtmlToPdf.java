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

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.jirvan.lang.NotFoundRuntimeException;
import org.apache.commons.io.IOUtils;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.ImageResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HtmlToPdf {

    public static void main(String[] args) {

        String html = "<html>\n" +
                      "    <body>\n" +
                      "    <img src=\"com/jirvan/JirvanNameLogo 98x38.png\" style=\"border: 0;height: auto;line-height: 100%;outline: none;text-decoration: none;\"></img>\n" +
                      "    <h1>Stuff</h1>\n" +
                      "            <p>The quick brown fox.</p>\n" +
                      "    </body>\n" +
                      "</html>";

        try {
            OutputStream outputStream = new FileOutputStream("/tmp/example.pdf");
            toOutputStream(outputStream, html);
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void toOutputStream(OutputStream outputStream, String html) throws IOException {
        try {
            ITextRenderer renderer = new ITextRenderer();
            UserAgent callback = new UserAgent(renderer.getOutputDevice());
            callback.setSharedContext(renderer.getSharedContext());
            renderer.getSharedContext().setUserAgentCallback(callback);
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static class UserAgent extends ITextUserAgent {


        public UserAgent(ITextOutputDevice outputDevice) {
            super(outputDevice);
        }


        public ImageResource getImageResource(String path) {
            try {
                ImageResource imageResource = (ImageResource) _imageCache.get(path);
                if (imageResource == null) {
                    Image image = getResource(path);
                    scaleToOutputResolution(image);
                    imageResource = new ImageResource(path, new ITextFSImage(image));
                    _imageCache.put(path, imageResource);
                }
                if (imageResource != null) {
                    FSImage image = imageResource.getImage();
                    if (image instanceof ITextFSImage) {
                        image = (FSImage) ((ITextFSImage) imageResource.getImage()).clone();
                    }
                    imageResource = new ImageResource(imageResource.getImageUri(), image);
                } else {
                    imageResource = new ImageResource(path, null);
                }
                return imageResource;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void scaleToOutputResolution(Image image) {
            float dotsPerPixel = getSharedContext().getDotsPerPixel();
            if (dotsPerPixel != 1.0f) {
                image.scaleAbsolute(image.getPlainWidth() * dotsPerPixel, image.getPlainHeight() * dotsPerPixel);
            }
        }

        public static Image getResource(String path) throws IOException, BadElementException {
            InputStream is = HtmlToPdf.class.getClassLoader().getResourceAsStream(path);
            if (is == null) {
                throw new NotFoundRuntimeException("Couldn't find resource \"" + path + "\"");
            }
            try {
                return Image.getInstance(IOUtils.toByteArray(is));
            } finally {
                is.close();
            }
        }

    }

}
