package com.numen.screencast;

import java.util.Arrays;

public class IOUtils {

	public static byte[] fixWindowsWordWrap(byte[] bytes) {
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
		return Arrays.copyOfRange(bytes, 0, nuevo);
	}

}
