package com.slepeweb.common.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageUtil {

	public static void streamScaled(InputStream in, OutputStream out, int width, int height, String mediaType) 
			throws IOException {
		
		// Create the thumbnail
		BufferedImage src = ImageIO.read(in);
		Image img = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);		
		BufferedImage thumb = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = thumb.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		// Standardize on media type
		int c = mediaType.lastIndexOf("/");
		String thumbType = mediaType.toLowerCase().substring(c + 1);
		if (thumbType.equals("jpeg")) {
			thumbType = "jpg";
		}
		
	    ImageIO.write(thumb, thumbType, out);
	}
	
	public static ByteArrayInputStream pipe(ByteArrayOutputStream baos) throws IOException {
		return new ByteArrayInputStream(baos.toByteArray());		
	}
}
