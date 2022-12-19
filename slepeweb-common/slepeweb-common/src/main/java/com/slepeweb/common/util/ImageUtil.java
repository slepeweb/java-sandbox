package com.slepeweb.common.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageUtil {
	//private static Logger LOG = Logger.getLogger(ImageUtil.class);
	
	/*
	 * NOTE TODO: This scaling method sometimes (if not always!) converts a portrait image to landscape.
	 * One article on the web claims it's because getScaledInstance() doesn't retain EXIF metadata.
	 * Haven't progressed this further. Current remedy is to use ubuntu's 'Image Viewer' app
	 * to rotate the image.
	 */
	public static InputStream scaleImage(InputStream in, int width, int height, String mimeType) 
			throws IOException {
		
		BufferedImage sourceImage = ImageIO.read(in);
		Image img = sourceImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);		
		BufferedImage scaledImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = scaledImage.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		
		int c = mimeType.lastIndexOf("/");
		String scaledImageType = mimeType.toLowerCase().substring(c + 1);
		if (scaledImageType.equals("jpeg")) {
			scaledImageType = "jpg";
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(scaledImage, scaledImageType, baos);
	    
	    return pipe(baos);
	}
	
	public static ByteArrayInputStream pipe(ByteArrayOutputStream baos) throws IOException {
		return new ByteArrayInputStream(baos.toByteArray());		
	}
	
	/*
	 * 
	 * Use ffmpeg in the os, to avoid loading masses of jar files into the project:
	 * 
	 * ffmpeg -skip_frame nokey -i test.mp4 -vsync vfr -frame_pts true out-%02d.jpg
	 * 
	public static InputStream grabFirstVideoFrame(String inf, String ouf, String type) {
		InputStream in = null;
		
		try {
			in = new FileInputStream(inf);
			return grabFirstVideoFrame(in, type);
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to open one/both files; in = %s, out = %s", inf, ouf), e);
		}
		finally {
			if (in != null) {
				try {in.close();}
				catch (Exception e) {}
			}
		}
		
		return null;
	}
	*/
	
	/*
	public static InputStream grabFirstVideoFrame(InputStream in, String type) {
		
		try {
			@SuppressWarnings("resource")
			FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(in);
			grabber.start();
			@SuppressWarnings("resource")
			Java2DFrameConverter c = new Java2DFrameConverter();
			Frame f = grabber.grabKeyFrame();
			
			String rotation = grabber.getVideoMetadata("rotate");
			int theta = StringUtils.isNotBlank(rotation) ? Integer.parseInt(rotation) : 0;
			BufferedImage img = c.convert(f);
			
			if (Math.abs(theta) > 0) {
				img = rotateImage(img, theta);
				LOG.info(String.format("... rotated frame by %d degrees", theta));
			}
	
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ImageIO.write(img, type, baos);
			
			grabber.stop();
			return pipe(baos);
		}
		catch (Exception e) {
			LOG.error("Failed to grab frame from video", e);
		}
		
		return null;
	}
	*/
	
	@SuppressWarnings("unused")
	private static BufferedImage rotateImage(BufferedImage image, int degrees) {
		double rads = Math.toRadians(degrees);
		double sin = Math.abs(Math.sin(rads));
		double cos = Math.abs(Math.cos(rads));
		int w = (int) Math.floor(image.getWidth() * cos + image.getHeight() * sin);
		int h = (int) Math.floor(image.getHeight() * cos + image.getWidth() * sin);
		
		BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
		AffineTransform at = new AffineTransform();
		at.translate(w / 2, h / 2);
		at.rotate(rads,0, 0);
		at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
		
		AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		rotateOp.filter(image,rotatedImage);

		return rotatedImage;
	}
	
	public static void main(String[] args) {
	}

}
