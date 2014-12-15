package com.numen.screencast;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ScreenCastViewBKP extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String ADB_PATH = "C:\\android\\sdk\\platform-tools\\";

	private static final String WINDOW_TITLE = "Numen ScreenCast";

	private static final int WINDOW_WIDTH = 300;
	private static final int WINDOW_MARGIN = 20;
	private static int WINDOW_HEIGHT = 600;

	private BufferedImage image;
	private boolean initialized = false;
	private boolean fresh = false;
	
	public ScreenCastViewBKP() {
		
		this.setTitle(WINDOW_TITLE);

		Runnable runprocess = new Runnable() {
			
			@Override
			public void run() {
				// runtime
				while(true){

					/*try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
					
					BufferedImage receivedImage = receiveImage();
					if(receivedImage!=null){
						freshImage(receivedImage);
					}
				}				
			}
		};

		Thread process = new Thread(runprocess);
		BufferedImage receivedImage = receiveImage();
		if(receivedImage!=null){
			if(!this.initialized){ 
				WINDOW_HEIGHT = (int) ((double) (WINDOW_WIDTH / (double)receivedImage.getWidth()) * receivedImage.getHeight());
				this.setSize(WINDOW_WIDTH + (WINDOW_MARGIN*2), WINDOW_HEIGHT + (WINDOW_MARGIN*3));
				this.initialized = true;
			}
			freshImage(receivedImage);
			process.start();
		}
	}

	public synchronized void freshImage(BufferedImage _image){
		this.image = _image;
		this.fresh = true;
		repaint();
	}
	
	public synchronized BufferedImage receiveImage(){

		Runtime rt = Runtime.getRuntime();
		
		try {
			Process proc = rt.exec("cmd /c "+ADB_PATH+"adb.exe shell screencap -p /mnt/sdcard/numenscreen.png");	//0ms
			int response = proc.waitFor();
			System.out.println("SnapScreen -1/0: " + response);
			if (response != -1) {
				proc = rt.exec("cmd /c "+ADB_PATH+"adb.exe pull /mnt/sdcard/numenscreen.png C:\\tmp\\numenscreen.png");//100ms
				response = proc.waitFor();
				System.out.println("PullPC -1/0: " + response);
				if (response != -1) {
					
					return ImageIO.read(new File("C:\\tmp\\numenscreen.png"));
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void paint(Graphics g) {
		if(this.fresh){
			g.drawImage(this.image, WINDOW_MARGIN, WINDOW_MARGIN * 2, WINDOW_WIDTH, WINDOW_HEIGHT, this);
			this.fresh = false;
		}
	}
}
