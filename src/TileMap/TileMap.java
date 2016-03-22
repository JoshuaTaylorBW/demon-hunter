package TileMap;

import Main.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

@SuppressWarnings("unused")
public class TileMap {
	
	// position
	private double x;
	private double y;
	
	// bounds
	private int xmin = - 13000;
	private int ymin = 13000;
	private int xmax = -13000;
	private int ymax = 13000;
	
	private double tween;
	
	// map
	private int[][] map;
	public String everything;
	private String[] mapLines = new String[270];
	private int tileSize;
	private int numRows;
	private int numCols;
	private int width;
	private int height;
	
	// tileset
	private BufferedImage tileset;
	private int numTilesAcross;
	private Tile[][] tiles;
	// drawing
	private int rowOffset;
	private int colOffset;
	private int numRowsToDraw;
	private int numColsToDraw;
	
	public TileMap(int tileSize) {
		this.tileSize = tileSize;
		numCols = 0;
		numRows = 0;
		numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
		numColsToDraw = GamePanel.WIDTH / tileSize + 2;
		tween = 0.07;
	}
	
	public void loadTiles(String s) {
		
		try {

			tileset = ImageIO.read(
				getClass().getResourceAsStream(s)
			);
			numTilesAcross = tileset.getWidth() / tileSize;
			System.out.println(numTilesAcross);
			tiles = new Tile[2][numTilesAcross];
			
			BufferedImage subimage;
			for(int col = 0; col < numTilesAcross; col++) {
				subimage = tileset.getSubimage(
							col * tileSize,
							0,
							tileSize,
							tileSize
						);
				tiles[0][col] = new Tile(subimage, Tile.NORMAL);
				
				subimage = tileset.getSubimage(
							col * tileSize,
							tileSize,
							tileSize,
							tileSize
						);
				tiles[1][col] = new Tile(subimage, Tile.BLOCKED);

				
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void loadMap4(MapPiece[][] theMap){

		numCols = 0;
		numRows = 0;
		
		try{
			File newMap = new File("some.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(newMap));
			
			for(int y = 0; y < theMap.length; y++){
				for(int x = 0; x < theMap.length; x++){
					//System.out.println("x " + x + " y " + y);
					String tempLine = "";
					//System.out.println("/piece/" + theMap[x][y].getMapName());
					InputStream in = getClass().getResourceAsStream(""
							+ "/piece/" + theMap[x][y].getMapName());

					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					//reads the [header] line and does nothing with it
					br.readLine();

					//Get height and width of the map
					//columns / width
					String line = br.readLine();
					String[] tokens = line.split("=");
					//System.out.println(tokens[1]);
					//num cols = twenty times PIECES_AMOUNT * 2
					numCols = 20 * 5;
					
					//Skips the next few lines
					String blankLine = " ";
					while(!blankLine.equals("data=")){
						blankLine = br.readLine();
					}

					//rows / height
					numRows = 5 * 15;
					
						//System.out.println("purple perplexion");
					//System.out.println(numCols);
					if(x == 4 && y == 0){
						bw.write(numCols + "\n");
						bw.write(numRows + "\n");
					}
					
					
					
					for(int j = 0; j < 15; j++){
						//System.out.println(j + (y * 15));
						tempLine = br.readLine();
						if(x == 0){
							mapLines[j + (y * 15)] = tempLine;
							if(j % 14 == 0 && j > 0 && x < theMap.length){
								mapLines[j + (y * 15)] = tempLine + ",";
							}else{
								mapLines[j + (y * 15)] = tempLine;
							}
						}else{
							
							if(j % 14 == 0 && j > 0){
								mapLines[j + (y * 15)] += tempLine + ",";
							}else{
								mapLines[j + (y * 15)] += tempLine;
							}
						}
						if(x == 4){
								bw.write(mapLines[j + (y * 15)] + "\r\n");
						}
					
					}
					br.close();
			}
			
			
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
//}}}	
//{{{Make Beginning and Ending in Map

	public void makeMap(String s) {
		
		
		
		try {
			
			BufferedReader br = new BufferedReader(
					new FileReader("some.txt")
				);
			
		StringBuilder sb = new StringBuilder();
			//
			
			
			numCols = Integer.parseInt(br.readLine());
			System.out.println(numCols);
			sb.append(numCols);
			sb.append("\n");
			
			numRows = Integer.parseInt(br.readLine());
			System.out.println(numRows);
			sb.append(numRows);
			sb.append("\n");
			
			map = new int[numRows][numCols];
			width = numCols * tileSize;
			height = numRows * tileSize;
			
			xmin = GamePanel.WIDTH - width;
			xmax = 0;
			ymin = GamePanel.HEIGHT - height;
			ymax = 0;
			
			String delims = ",";
			for(int row = 0; row < numRows; row++) {
				String line = br.readLine();
				sb.append(line);
				sb.append("\n	");
				//line = br.readLine();
				String[] tokens = line.split(delims);
				for(int col = 0; col < numCols; col++) {
					map[row][col] = Integer.parseInt(tokens[col]) - 1;
				}
			}
			everything = sb.toString();
			br.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
//{{{ getter and setters	
	public int getTileSize() { return tileSize; }
	public double getx() { return x; }
	public double gety() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getNumRows() {return numRows;}
	public int getNumCols() {return numCols;}
	public int getType(int row, int col) { // row = y col = x
		int rc = map[row][col];
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		return tiles[r][c].getType();
	}
	public int[][] getMap(){
		return map;
	}
	public void setTween(double d) { tween = d; }
	
	public void scrollUp(){setPosition(getx(), gety() + 480);}
	public void scrollRight(){setPosition(getx() + 640, gety());}
	public void scrollDown(){setPosition(getx(), gety()+480);}
	public void scrollLeft(){setPosition(getx()-640, gety());}
	
	public void setPosition(double x, double y) {
		
		//System.out.println(x);
		//System.out.println(this.x);
		//System.out.println((x - this.x) * tween);
		
		this.x += (x - this.x) * tween;
		this.y += (y - this.y) * tween;
		
		fixBounds();
		
		colOffset = (int)-this.x / tileSize;
		rowOffset = (int)-this.y / tileSize;
		
	}

	
	private void fixBounds() {
		if(x < xmin) x = xmin;
		if(y < ymin) y = ymin;
		if(x > xmax) x = xmax;
		if(y > ymax) y = ymax;
	}
	public void draw(Graphics2D g) {
		
		for(int row = rowOffset;
			row < rowOffset + numRowsToDraw;
			row++) {
			if(row >= numRows) break;
			for(int col = colOffset;
				col < colOffset + numColsToDraw;
				col++) {
				
				if(col >= numCols) break;
				int rc = map[row][col];
				int r = rc / numTilesAcross;
				int c = rc % numTilesAcross;
				
				//System.out.println(r + " " + c);
				
				g.drawImage(
					tiles[r][c].getImage(),
					(int)x + col * tileSize,
					(int)y + row * tileSize,
					null
				);
				if(getType(row, col) == 1){
					g.setColor(Color.YELLOW);
					g.drawRect(
							(int)x + col * tileSize,
							(int)y + row * tileSize,
							tileSize,
							tileSize);
					}
				}
				
			}
		}
}
