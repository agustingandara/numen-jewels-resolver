package com.numen.screencast;

import javax.swing.JFrame;

public class Main {


	public static void main (String [ ] args) 
    { 
		ScreenCastView window = new ScreenCastView();
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } 
}
