package com.jirvan.pdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.jirvan.lang.NotFoundRuntimeException;
import org.apache.commons.io.IOUtils;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.ImageResource;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractPdfGeneratorWithUserAgent extends AbstractPdfGenerator {

    public AbstractPdfGeneratorWithUserAgent(Object model, String pathToHtmlTemplate) {
        super(model, pathToHtmlTemplate);
    }

    protected ITextUserAgent getUserAgent(ITextOutputDevice outputDevice) {
        return new UserAgent(outputDevice);
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
            InputStream is = ClassLoader.getSystemResourceAsStream(path);
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
