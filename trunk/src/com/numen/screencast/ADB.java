package com.numen.screencast;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

public class ADB {

	private static final String ADB_PATH = "cmd /c C:\\android\\sdk\\platform-tools\\adb.exe ";
	
	public static synchronized BufferedImage screenCap() {

		Runtime rt = Runtime.getRuntime();
		try {
			Process proc = rt.exec(ADB_PATH + "shell screencap -p"); // 0ms
			InputStream inputStream = proc.getInputStream();
			byte[] bytes = IOUtils.toByteArray(inputStream);
			bytes = com.numen.screencast.IOUtils.fixWindowsWordWrap(bytes);

			InputStream in = new ByteArrayInputStream(bytes);
			return ImageIO.read(in);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void input(int x, int y){

		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec(ADB_PATH + "shell input tap " + x + " " + y);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void input(int x, int y, int dx, int yx){

		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec(ADB_PATH + "shell input swipe " + x + " " + y + " " + dx + " " + yx);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void input(char text){

		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec(ADB_PATH + "shell input text " + text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
