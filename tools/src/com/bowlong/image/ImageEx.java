package com.bowlong.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.SwingUtilities;

public class ImageEx {
	public static final BufferedImage load(String ref) throws IOException {
		File f = new File(ref);
		return load(f);
	}

	public static final BufferedImage create(int w, int h) {
		BufferedImage aimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		return aimg;
	}

	public static final BufferedImage create(int w, int h, int type) {
		BufferedImage aimg = new BufferedImage(w, h, type);
		return aimg;
	}

	public static final BufferedImage load(File f) throws IOException {
		return ImageIO.read(f);
	}

	public static final BufferedImage load(URL url) throws IOException {
		return ImageIO.read(url);
	}

	public static final BufferedImage load(InputStream is) throws IOException {
		return ImageIO.read(is);
	}

	public static final boolean save(BufferedImage img, String format, File f)
			throws IOException {
		return ImageIO.write(img, format, f);
	}

	public static final boolean save(BufferedImage img, String format, String s)
			throws IOException {
		File f = new File(s);
		return save(img, format, f);
	}

	public static final boolean save(BufferedImage img, String format,
			OutputStream os) throws IOException {
		return ImageIO.write(img, format, os);
	}

	public static final BufferedImage translucentImage(BufferedImage loaded,
			float transperancy) {
		// Create the image using the
		BufferedImage aimg = new BufferedImage(loaded.getWidth(),
				loaded.getHeight(), Transparency.TRANSLUCENT);
		// Get the images graphics
		Graphics2D g = aimg.createGraphics();
		// Set the Graphics composite to Alpha
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				transperancy));
		// Draw the LOADED img into the prepared reciver image
		g.drawImage(loaded, null, 0, 0);
		// let go of all system resources in this Graphics
		g.dispose();
		// Return the image
		return aimg;
	}

	public static final BufferedImage makeColorToColor(BufferedImage image,
			Color color, Color newColor) {
		BufferedImage dimg = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = dimg.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.drawImage(image, null, 0, 0);
		g.dispose();
		for (int i = 0; i < dimg.getHeight(); i++) {
			for (int j = 0; j < dimg.getWidth(); j++) {
				if (dimg.getRGB(j, i) == color.getRGB()) {
					dimg.setRGB(j, i, newColor.getRGB());
				}
			}
		}
		return dimg;
	}

	public static final BufferedImage makeColorTransparent(BufferedImage image,
			Color color) {
		BufferedImage dimg = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = dimg.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.drawImage(image, null, 0, 0);
		g.dispose();
		for (int i = 0; i < dimg.getHeight(); i++) {
			for (int j = 0; j < dimg.getWidth(); j++) {
				if (dimg.getRGB(j, i) == color.getRGB()) {
					dimg.setRGB(j, i, 0x8F1C1C);
				}
			}
		}
		return dimg;
	}

	public static final BufferedImage horizontalflip(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(w, h, img.getType());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
		g.dispose();
		return dimg;
	}

	public static final BufferedImage verticalflip(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(w, h, img.getColorModel()
				.getTransparency());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(img, 0, 0, w, h, 0, h, w, 0, null);
		g.dispose();
		return dimg;
	}

	public static final BufferedImage rotate(BufferedImage img, int angle) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(w, h, img.getType());
		Graphics2D g = dimg.createGraphics();
		g.rotate(Math.toRadians(angle), w / 2, h / 2);
		g.drawImage(img, null, 0, 0);
		return dimg;
	}

	public static final BufferedImage resize2default(BufferedImage img,
			int newW, int newH) {
		BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(img.getScaledInstance(newW, newH, Image.SCALE_DEFAULT), 0,
				0, newW, newH, null);
		g.dispose();
		return dimg;
	}

	public static final BufferedImage resize2smooth(BufferedImage img,
			int newW, int newH) {
		BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH), 0,
				0, newW, newH, null);
		g.dispose();
		return dimg;
	}

	public static final Image resize(BufferedImage img, int width, int height,
			int hints) {
		return img.getScaledInstance(width, height, hints);
	}

	public static final Image scaleSmooth(BufferedImage img, int newW, int newH) {
		return img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	}

	public static final Image scaleDefault(BufferedImage img, int newW, int newH) {
		return img.getScaledInstance(newW, newH, Image.SCALE_DEFAULT);
	}

	public static final Image scaleFast(BufferedImage img, int newW, int newH) {
		return img.getScaledInstance(newW, newH, Image.SCALE_FAST);
	}

	public static final BufferedImage clip(Image img, int x, int y, int w, int h) {
		BufferedImage dimg = new BufferedImage(w, h,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = dimg.createGraphics();
		int dx1 = 0;
		int dy1 = 0;
		int dx2 = w;
		int dy2 = h;
		int sx1 = x;
		int sy1 = y;
		int sx2 = x + w;
		int sy2 = y + h;
		g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
		g.dispose();
		return dimg;
	}

	public static final BufferedImage[] splitImage(BufferedImage img, int cols,
			int rows) {
		int w = img.getWidth() / cols;
		int h = img.getHeight() / rows;
		int num = 0;
		BufferedImage imgs[] = new BufferedImage[w * h];
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				imgs[num] = new BufferedImage(w, h, img.getType());
				// Tell the graphics to draw only one block of the image
				Graphics2D g = imgs[num].createGraphics();
				g.drawImage(img, 0, 0, w, h, w * x, h * y, w * x + w,
						h * y + h, null);
				g.dispose();
				num++;
			}
		}
		return imgs;
	}

	public static final BufferedImage byteArrayToImage(byte[] img)
			throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(img);
		BufferedImage src = ImageIO.read(bais);

		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		dest.getGraphics().drawImage(src, 0, 0, src.getWidth(),
				src.getHeight(), null);

		bais = null;
		src = null;
		return dest;
	}

	public static final byte[] bufferedImageToByteArray(BufferedImage img,
			String format) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, format, baos);
		baos.flush();
		baos.close();

		return baos.toByteArray();
	}

	public static final byte[] imageToByteArray(byte[] b, String format)
			throws IOException {

		BufferedImage img = byteArrayToImage(b);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, format, baos);
		baos.flush();
		baos.close();

		return baos.toByteArray();
	}

	public static final BufferedImage ImageToType(BufferedImage img,
			String format) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, format, baos);
		baos.flush();
		baos.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		return ImageIO.read(bais);
	}

	public static final byte[] imageToType(byte[] img, String format)
			throws IOException {
		BufferedImage bi = byteArrayToImage(img);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, format, baos);
		baos.flush();
		baos.close();
		return baos.toByteArray();
	}

	public static final BufferedImage byteArrayToImageType(byte[] img,
			String format) throws IOException {
		byte[] b = imageToType(img, format);
		return byteArrayToImage(b);
	}

	public static final BufferedImage drawImage(BufferedImage src,
			BufferedImage added, int x, int y, String ref) throws IOException {

		BufferedImage dimg = new BufferedImage(src.getWidth(), src.getHeight(),
				src.getType());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(src, 0, 0, src.getWidth(), src.getHeight(), null);
		g.drawImage(added, x, y, added.getWidth(), added.getHeight(), null);
		g.dispose();

		return dimg;
	}

	public static final BufferedImage drawTransparencyImage(BufferedImage bg,
			BufferedImage img, int x, int y, float transperancy) {
		BufferedImage dimg = new BufferedImage(bg.getWidth(), bg.getHeight(),
				bg.getType());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(bg, 0, 0, bg.getWidth(), bg.getHeight(), null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				transperancy));
		g.drawImage(img, null, x, y);
		g.dispose();
		// Return the image
		return dimg;
	}

	public static final BufferedImage drawString(BufferedImage src, String str,
			int x, int y, Color rgb) throws IOException {

		BufferedImage dimg = new BufferedImage(src.getWidth(), src.getHeight(),
				src.getType());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(src, 0, 0, src.getWidth(), src.getHeight(), null);

		Color old = g.getColor();
		g.setColor(rgb);
		g.drawString(str, x, y);
		g.setColor(old);
		g.dispose();

		return src;
	}

	public static final BufferedImage drawString(BufferedImage src, String str,
			int x, int y, Color rgb, Font font) {
		Graphics2D g = src.createGraphics();

		Color oColor = g.getColor();
		Font oFont = g.getFont();
		g.setFont(font);
		g.setColor(rgb);
		g.drawString(str, x, y);
		g.setFont(oFont);
		g.setColor(oColor);
		g.dispose();

		return src;
	}

	public static final BufferedImage drawLine(BufferedImage src, int x1,
			int y1, int x2, int y2, Color rgb) throws IOException {

		Graphics2D g = src.createGraphics();
		Color old = g.getColor();
		g.setColor(rgb);
		g.drawLine(x1, y1, x2, y2);
		g.setColor(old);
		g.dispose();

		return src;
	}

	public static final BufferedImage drawTransparencyString(BufferedImage src,
			String str, int x, int y, Color rgb, float transperancy)
			throws IOException {

		BufferedImage dimg = new BufferedImage(src.getWidth(), src.getHeight(),
				src.getType());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(src, 0, 0, src.getWidth(), src.getHeight(), null);

		Color old = g.getColor();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				transperancy));
		g.drawString(str, x, y);
		g.setColor(old);
		g.dispose();

		return src;
	}

	public static final void drawImageToGraphics2D(Graphics2D g,
			BufferedImage img, int x, int y) {
		g.drawImage(img, x, y, img.getWidth(), img.getHeight(), null);
		g.dispose();
	}

	public static final void drawStringToGraphics2D(Graphics2D g, String str,
			int x, int y, Color rgb) {
		Color old = g.getColor();
		g.drawString(str, x, y);
		g.setColor(old);
		g.dispose();
	}

	public static final byte[] scaleByWidth(byte[] img, int w, String format)
			throws Exception {
		BufferedImage i = byteArrayToImage(img);
		BufferedImage i2 = scaleByWidth(i, w);
		return bufferedImageToByteArray(i2, format);
	}

	public static final BufferedImage scaleByWidth(BufferedImage img, int w)
			throws IOException {
		double width = img.getWidth();
		double height = img.getHeight();

		double ws = w / width;
		double hs = (ws * height);
		int h = (int) hs;

		return resize2smooth(img, w, h);
	}

	public static final BufferedImage scaleByHeight(BufferedImage img, int h)
			throws IOException {
		double width = img.getWidth();
		double height = img.getHeight();

		double hs = h / height;
		double ws = (hs * width);
		int w = (int) ws;

		return resize2smooth(img, w, h);
	}

	public static final Rectangle getRect(BufferedImage img) throws IOException {
		Rectangle rect = new Rectangle(0, 0, img.getWidth(), img.getHeight());
		return rect;
	}

	public static final Rectangle getRect(byte[] img) throws IOException {
		BufferedImage bi = byteArrayToImage(img);
		return getRect(bi);
	}

	// 获得字符串的宽度
	public static final int getStringWidth(Graphics g, String str) {
		FontMetrics fm = g.getFontMetrics();
		return SwingUtilities.computeStringWidth(fm, str);
	}

	// 获得字符串的宽度
	public static final int getStringWidth(Graphics g, Font font, String str) {
		FontMetrics fm = g.getFontMetrics(font);
		return SwingUtilities.computeStringWidth(fm, str);
	}

	// 获得字符串的高宽
	public static final Rectangle getStringRect(Graphics g, Font font,
			String str) {
		Rectangle ret = new Rectangle();
		double width;
		double height;
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D r2d = fm.getStringBounds(str, g);
		width = r2d.getWidth();
		height = r2d.getHeight();
		ret.width = (int) width;
		ret.height = (int) height;
		return ret;
	}

	public static final Rectangle getStringRect(Graphics g, String str) {
		return getStringRect(g, g.getFont(), str);
	}

	// public static final String BMP = "BMP";
	// public static final String JPG = "JPG";
	// public static final String PNG = "PNG";
	// public static final String JPEG = "JPEG";
	// public static final String WBMP = "WBMP";
	// public static final String GIF = "GIF";

	public void bufferedImageTobytes(BufferedImage img, String fmt,
			float quality, OutputStream out) throws IOException {
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(fmt);
		ImageWriter writer = (ImageWriter) iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(quality);

		iwp.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
		ColorModel colorModel = ColorModel.getRGBdefault();
		// 指定压缩时使用的色彩模式
		iwp.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel,
				colorModel.createCompatibleSampleModel(16, 16)));

		IIOImage iIamge = new IIOImage(img, null, null);
		writer.setOutput(ImageIO.createImageOutputStream(out));
		writer.write(null, iIamge, iwp);
	}

	public static void main(String[] args) throws IOException {
	}

}
