package com.numen.screencast;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class ScreenCastView extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String WINDOW_TITLE = "Numen ScreenCast";

	private static int WINDOW_WIDTH = 0;
	private static int WINDOW_HEIGHT = 0;

	private BufferedImage image;
	private boolean initialized = false;

	public ScreenCastView() {

		this.setTitle(WINDOW_TITLE);

		Runnable runprocess = new Runnable() {

			@Override
			public void run() {
				// runtime
				while (true) {

					BufferedImage receivedImage = ADB.screenCap();
					if (receivedImage != null) {
						freshImage(receivedImage);
					}
				}
			}
		};

		Thread process = new Thread(runprocess);
		BufferedImage receivedImage = ADB.screenCap();
		if (receivedImage != null) {
			if (!this.initialized) {
				WINDOW_HEIGHT = (int) (receivedImage.getHeight() * JewelsPatterns.PRINT_SCALE);
				WINDOW_WIDTH = (int) (receivedImage.getWidth() * JewelsPatterns.PRINT_SCALE);
				this.setSize(WINDOW_WIDTH + JewelsPatterns.WINDOW_MARGIN * 2, WINDOW_HEIGHT+ JewelsPatterns.WINDOW_MARGIN * 3);
				this.initialized = true;
			}
			freshImage(receivedImage);
			process.start();
		}
		
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				ADB.input(arg0.getKeyChar());
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {}
			
			@Override
			public void keyPressed(KeyEvent arg0) {}
		});
	}

	public synchronized void freshImage(BufferedImage _image) {
		this.image = _image;
		JewelsPatterns.analize(this.image);
		JewelsPatterns.resolve();
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(this.image, JewelsPatterns.WINDOW_MARGIN, JewelsPatterns.WINDOW_MARGIN * 2, WINDOW_WIDTH, WINDOW_HEIGHT, this);
		JewelsPatterns.drawAnalize(g, this);
		//super.paint(g);
	}
}
