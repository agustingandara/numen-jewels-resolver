package com.numen.screencast;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class JewelsPatterns {
	
	public static final float PRINT_SCALE = 0.6f;
	public static final int WINDOW_MARGIN = 20;

	private static final int JW_MARGIN_L = 10;
	private static final int JW_MARGIN_R = 10;
	private static final int JW_MARGIN_T = 68;
	private static final int JW_MARGIN_B = 222;
	
	private static final int JW_COLUMNS = 7;
	private static final int JW_ROWS = 9;
	
	private static int stage[][] = new int[JW_COLUMNS+2][JW_ROWS+2];
	private static boolean firstAnalize = false;
	
	private static int wbox = 0;
	private static int hbox = 0;
	
	private static boolean detect = false;
	
	public static synchronized void analize(BufferedImage bufferedImage) {
		int wsplit = bufferedImage.getWidth()-JW_MARGIN_L-JW_MARGIN_R;
		int hsplit = bufferedImage.getHeight()-JW_MARGIN_T-JW_MARGIN_B;
		wbox = wsplit / JW_COLUMNS;
		hbox = hsplit / JW_ROWS;
		
		int column = 0;
		int row = 0;
		while(row < JW_ROWS){
			while(column < JW_COLUMNS){
				//get centre pixel of each box
				stage[column+1][row+1] = bufferedImage.getRGB(
						JW_MARGIN_L + wbox / 2 + wbox * column,
						JW_MARGIN_T + hbox / 2 + hbox * row);
				column++;
			}
			column = 0;
			row++;
		}
		firstAnalize = true;
	}
	
	public static void drawAnalize(Graphics g, ScreenCastView screenCastView){
		if(!firstAnalize) return;
		
		int column = 0;
		int row = 0;
		while(row < JW_ROWS){
			while(column < JW_COLUMNS){
				//get centre pixel of each box
				//stage[column][row]
				g.setColor(new Color(stage[column+1][row+1]));
				g.drawRect(
						WINDOW_MARGIN + (int) ((JW_MARGIN_L + column * wbox) * PRINT_SCALE) , 
						WINDOW_MARGIN * 2 + (int) ((JW_MARGIN_T + row * hbox) * PRINT_SCALE), 
						WINDOW_MARGIN + (int) ((JW_MARGIN_L + column * wbox + wbox) * PRINT_SCALE), 
						WINDOW_MARGIN * 2 + (int) ((JW_MARGIN_T + row * hbox + hbox) * PRINT_SCALE));
				column++;
			}
			column = 0;
			row++;
		}
	}
	
	/* entorno 0 left 1 top 2 rigth 3 bottom
	 * 
	 */
	
	public static synchronized void resolve(){
		detect = false;
		int column = JW_COLUMNS -1;
		int row = JW_ROWS-1;
		while(row > -1 && !detect){
			while(column > -1 && !detect){
				
				resolveAlgoritmEntorno(column, row);
				column--;
			}
			column = JW_COLUMNS -1;
			row--;
		}
	}
	
	private static void resolveAlgoritmEntorno(int column, int row){
		//first algoritm entorno
		boolean entorno[][] = new boolean[3][3];
		
		int columnentorno = 0;
		int rowentorno = 0;
		while(rowentorno < 3 && !detect){
			while(columnentorno < 3 && !detect){
				
				if(columnentorno != 1 || rowentorno != 1){
					//get centre pixel of each box
					entorno[columnentorno][rowentorno] = compareColor(
									stage[column+1][row+1], 
									stage[column+columnentorno][row+rowentorno]);
					
					//validate solution 1
					if(entorno[0][0] && entorno[0][2]){			//left
						solution(row, column, row, column-1);
						detect = true;
					}else if(entorno[0][0] && entorno[2][0]){	//top
						solution(row, column, row-1, column);
						detect = true;
					}else if(entorno[2][2] && entorno[0][2]){	//bottom
						solution(row, column, row+1, column);
						detect = true;
					}else if(entorno[2][2] && entorno[2][0]){	//right
						solution(row, column, row, column+1);
						detect = true;
					}
					
					//validate solution 2
					if(entorno[1][0]){										//top
						resolveAlgoritmCamp(column, row, column, row-1);
					}else if(entorno[0][1] ){								//left
						resolveAlgoritmCamp(column, row, column-1, row);
					}else if(entorno[2][1]){								//right
						resolveAlgoritmCamp(column, row, column+1, row);
					}else if(entorno[1][2]){								//bottom
						resolveAlgoritmCamp(column, row, column, row+1);
					}
				}
				
				columnentorno++;
			}
			columnentorno = 0;
			rowentorno++;
		}
		//end of first algoritm entorno
	}
	
	private static void resolveAlgoritmCamp(int column1, int row1, int column2, int row2){
		
		if(column1 == column2){	//vertical
			int majorrow = row1 < row2 ? row2 : row1;
			int minorrow = row1 > row2 ? row2 : row1;
			
			if(minorrow > 2){
				if(compareColor(stage[column1+1][minorrow+1], stage[column2+1][minorrow-1])){    //top centre
					solution(minorrow-1, column1, minorrow-2, column2);
					detect = true;
				}
			}else if(minorrow > 1){
				if(compareColor(stage[column1+1][minorrow+1], stage[column2][minorrow])){	//top left
					solution(minorrow-1, column1, minorrow-1, column2-1);
					detect = true;
				}else if(compareColor(stage[column1+1][minorrow+1], stage[column2+2][minorrow])){//top right
					solution(minorrow-1, column1, minorrow-1, column2+1);
					detect = true;
				}
			}else if(majorrow < JW_ROWS + 2){
				if(compareColor(stage[column1+1][majorrow+1], stage[column2+1][majorrow+3])){	//bottom centre
					solution(majorrow+1, column1, majorrow+2, column2);
					detect = true;	
				}
			}else if(majorrow < JW_ROWS + 1){
				if(compareColor(stage[column1+1][majorrow+1], stage[column2][majorrow+2])){	//bottom left
					solution(majorrow+1, column1, majorrow+1, column2-1);
					detect = true;
				} else if(compareColor(stage[column1+1][majorrow+1], stage[column2+2][majorrow+2])){//bottom right
					solution(majorrow, column1, majorrow+1, column2+1);
					detect = true;
				}
			}
			
		}else if(row1 == row2){	//horizontal
			int majorcolumn = column1 < column2 ? column2 : column1;
			int minorcolumn = column1 > column2 ? column2 : column1;
			
			if(minorcolumn > 2){
				if(compareColor(stage[minorcolumn+1][row1+1], stage[minorcolumn-1][row2+1])){    //top centre
					solution(row1, minorcolumn-1, row2, minorcolumn-2);
					detect = true;
				}
			}else if(minorcolumn > 1){
				if(compareColor(stage[minorcolumn+1][row1+1], stage[minorcolumn][row2])){	//top left
					solution(row1, minorcolumn-1, row2-1, minorcolumn-1);
					detect = true;
				}else if(compareColor(stage[minorcolumn+1][row1+1], stage[minorcolumn][row2+2])){//top right
					solution(row1, minorcolumn-1, row2+1, minorcolumn-1);
					detect = true;
				}
			}else if(majorcolumn < JW_COLUMNS + 2){
				if(compareColor(stage[majorcolumn+1][row1+1], stage[majorcolumn+3][row2+1])){	//bottom centre
					solution(row1, majorcolumn+1, row2, majorcolumn+2);
					detect = true;	
				}
			}else if(majorcolumn < JW_COLUMNS + 1){
				if(compareColor(stage[majorcolumn+1][row1+1], stage[majorcolumn+2][row2])){	//bottom left
					solution(row1, majorcolumn+1, row2-1, majorcolumn+1);
					detect = true;
				} else if(compareColor(stage[majorcolumn+1][row1+1], stage[majorcolumn+2][row2+2])){//bottom right
					solution(row1, majorcolumn+1, row2+1, majorcolumn+1);
					detect = true;
				}
			}
			
		}
	}
	
	public static void solution(int rowfrom, int columnfrom, int rowto, int columnto){
		ADB.input(
				JW_MARGIN_L + (columnfrom) * wbox + (wbox / 2), 
				JW_MARGIN_T + (rowfrom) * hbox + (hbox / 2), 
				JW_MARGIN_L + (columnto) * wbox + (wbox / 2), 
				JW_MARGIN_T + (rowto) * hbox + (hbox / 2));
		
	}
	
	private static final int COLOR_COMP_ERR = 10;
	
	public static boolean compareColor(int color1, int color2){
		Color c1 = new Color(color1);
		Color c2 = new Color(color2);
		if(	c1.getRed() < c2.getRed() + COLOR_COMP_ERR &&
			c1.getRed() > c2.getRed() - COLOR_COMP_ERR &&
			c1.getGreen() < c2.getGreen() + COLOR_COMP_ERR &&
			c1.getGreen() > c2.getGreen() - COLOR_COMP_ERR &&
			c1.getBlue() < c2.getBlue() + COLOR_COMP_ERR &&
			c1.getBlue() > c2.getBlue() - COLOR_COMP_ERR)
			return true;
			
		return false;
	}
}
