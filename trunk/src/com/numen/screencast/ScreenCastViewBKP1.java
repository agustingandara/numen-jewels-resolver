package com.numen.screencast;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.io.IOUtils;

public class ScreenCastViewBKP1 extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String ADB_PATH = "C:\\android\\sdk\\platform-tools\\";

	private static final String WINDOW_TITLE = "Numen ScreenCast";

	private static final int WINDOW_WIDTH = 300;
	private static final int WINDOW_MARGIN = 20;
	private static int WINDOW_HEIGHT = 600;

	private BufferedImage image;
	private boolean initialized = false;

	// private boolean fresh = false;

	public ScreenCastViewBKP1() {

		this.setTitle(WINDOW_TITLE);

		Runnable runprocess = new Runnable() {

			@Override
			public void run() {
				// runtime
				while (true) {

					BufferedImage receivedImage = receiveImage();
					if (receivedImage != null) {
						freshImage(receivedImage);
					}
				}
			}
		};

		Thread process = new Thread(runprocess);
		BufferedImage receivedImage = receiveImage();
		if (receivedImage != null) {
			if (!this.initialized) {
				WINDOW_HEIGHT = (int) ((double) (WINDOW_WIDTH / (double) receivedImage.getWidth()) * receivedImage.getHeight());
				this.setSize(WINDOW_WIDTH + (WINDOW_MARGIN * 2), WINDOW_HEIGHT + (WINDOW_MARGIN * 3));
				this.initialized = true;
			}
			freshImage(receivedImage);
			process.start();
		}
	}

	public synchronized void freshImage(BufferedImage _image) {
		this.image = _image;
		// this.fresh = true;
		repaint();
	}

	public synchronized BufferedImage receiveImage() {

		Runtime rt = Runtime.getRuntime();

		try {
			Process proc = rt.exec("cmd /c " + ADB_PATH + "adb.exe shell screencap -p"); // 0ms
			InputStream inputStream = proc.getInputStream();
			byte[] bytes = IOUtils.toByteArray(inputStream);

			// algoritmo fix wordwrapwindows//
			byte b = 0;
			int detection = 0;
			int nuevo = 0;
			for (int original = 0; original < bytes.length; original++) {
				b = bytes[original];
				if (b == 0x0D) detection++;
				else {
					if (b == 0x0A && detection >= 2) nuevo -= 2;
					detection = 0;
				}
				bytes[nuevo] = b;
				nuevo++;
			}
			bytes = Arrays.copyOfRange(bytes, 0, nuevo);
			// algoritmo fix wordwrapwindows//

			InputStream in = new ByteArrayInputStream(bytes);
			return ImageIO.read(in);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void paint(Graphics g) {
		// if(this.fresh){
		g.drawImage(this.image, WINDOW_MARGIN, WINDOW_MARGIN * 2, WINDOW_WIDTH, WINDOW_HEIGHT, this);
		// this.fresh = false;
		// }
	}
}
