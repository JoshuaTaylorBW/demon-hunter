package TileMap;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import Main.GamePanel;
import State.GameStateManager;

public class MapPiece {

	// position
	private double x;
	private double y;
	private double initX;
	private double initY;
	private int beenPlaced = 0;				//int to check the first time setPosition is called for each piece
	
	// map
	private int[][] map;
	private int tileSize = 32;
	private int numRows;
	private int numCols;
	private String[] openings;
	private String mapName;
	
	// tileset
	private BufferedImage tileset;
	private int numTilesAcross;
	private Tile[][] tiles;

	//Spot On Map And Openings
	
	private String[] nameIndivs;

	private boolean centerOnly;


	private String northOpens="";
	private String eastOpens="";
	private String southOpens="";
	private String westOpens="";
	
	String nOpens = "", eOpens = "", sOpens = "", wOpens = "";
	
	// drawing
	private int rowOffset;
	public int getNumTilesAcross() {
		return numTilesAcross;
	}
	public void setNumTilesAcross(int numTilesAcross) {
		this.numTilesAcross = numTilesAcross;
	}

	private int colOffset;
	private int numRowsToDraw;
	private int numColsToDraw;
	
	private int whichDirection = 0;

	public MapPiece(MapPiece nPiece, MapPiece ePiece, MapPiece sPiece, MapPiece wPiece){
		findInitOpenings(nPiece, ePiece, sPiece, wPiece);
		mapName = findGoodFile(nOpens, eOpens, sOpens, wOpens);
		init();
	}
	public MapPiece(String locNOpens, String locEOpens, String locSOpens, String locWOpens){
		mapName = findGoodFile(locNOpens,locEOpens,locSOpens,locWOpens);
		init();
	}
	public MapPiece(String mapName){
		this.mapName = mapName;
		init();
		
	}

	public void init(){
		nameIndivs = mapName.split("(?!^)");
		
		findOpenings();
		//System.out.println(mapName);
		findFolder();
		numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
		numColsToDraw = GamePanel.WIDTH / tileSize + 2;

		loadTiles("/Tileset/TiledTilesetBlue.png");
		readFlare("/piece/" + mapName);
		if(GameStateManager.printing){
			
			System.out.println("North " + northOpens);
			System.out.println("East " + eastOpens);
			System.out.println("South " + southOpens);
			System.out.println("West " + westOpens);
		}
	}
	
	//Use file name to find where the piece opens up for other maps.
	public void findOpenings(){
		int startPoint = 0;//Where we start reading the string array from
		if(centerOnly){
			startPoint = 1;
		}else{
			startPoint = 2;
		}

		String openings = "";
		while(whichDirection < 4){
			for(int i = startPoint; i < nameIndivs.length; i++){
				if(nameIndivs[i].equals("a")){
					openings = "";
					openings = nameIndivs[i+1];
					startPoint++;
					addDirection(whichDirection-1, openings);
					i++;
				}else{
					openings = "";
					openings = nameIndivs[i];
					startPoint++;
					addDirection(whichDirection, openings);
					whichDirection++;
				}
			}
		}
	}
	//Only used in constructor to change the opens strings variables
	public void findInitOpenings(MapPiece n, MapPiece e, MapPiece s, MapPiece w){
		if(n != null){
			nOpens = n.getSouthOpens();
		}else{
			nOpens = "A";
		}
		if(e != null){
			eOpens = e.getWestOpens();
		}else{
			eOpens = "A";
		}
		if(s != null){
			sOpens = s.getNorthOpens();
		}else{
			sOpens = "A";
		}
		if(w != null){
			wOpens = w.getEastOpens();
		}else{
			wOpens = "A";
		}
	}
/*************************************************************************************************/
	public void addDirection(int direction, String opens){
		switch(direction){
			case 0:		northOpens += opens;
					break;
			case 1:		eastOpens += opens;
					break;
			case 2:		southOpens += opens; 
					break;
			case 3:		westOpens += opens;
					break;
		}	
	}

	public void loadTiles(String s) {
		try {

			tileset = ImageIO.read(
				getClass().getResourceAsStream(s)
			);
			numTilesAcross = tileset.getWidth() / tileSize;

			
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
	
	// This reads flare files from tiled
	public void readFlare(String s){
		try {
			
			InputStream in = getClass().getResourceAsStream(s);
			BufferedReader br = new BufferedReader(
						new InputStreamReader(in));

			//reads the [header] line and does nothing with it
			br.readLine();

			//Get height and width of the map
			//columns / width
			String line = br.readLine();
			String[] tokens = line.split("=");
			numCols = Integer.parseInt(tokens[1]);
			

			//rows / height
			line = br.readLine();
			tokens = line.split("=");
			numRows = Integer.parseInt(tokens[1]);

			//System.out.println(numCols + " " + numRows);

			//Skips the next few lines
			String tempLine = " ";
			while(!tempLine.equals("data=")){
				tempLine = br.readLine();
			}

			//creates the map array and the size of the map
			//in terms of pixels.
			map = new int[numRows][numCols];
			//the minimum and maximum places the map can scroll
			//might not be needed here..
			
			//adds the numbers from the flare map file
			//to the game screen
			for(int row = 0; row < numRows; row++) {
				line = br.readLine();
				tokens = line.split(",");
				for(int col = 0; col < numCols; col++) {
					map[row][col] = Integer.parseInt(
							tokens[col]) - 1;
					
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
	
	public int getType(int row, int col) {
		int rc = map[row][col];
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		return tiles[r][c].getType();
	}
	
	//getters and setters
	public String[] getOpenings(){
		return openings;
	}
	public void printOpenings(){
		System.out.println(northOpens);
		System.out.println(eastOpens);
		System.out.println(southOpens);
		System.out.println(westOpens);
	}
	public int getNumRows(){
		return numRows;
	}
	public String getNorthOpens(){
		return northOpens;
	}
	public String getEastOpens(){
		return eastOpens;
	}public String getSouthOpens(){
		return southOpens;
	}public String getWestOpens(){
		return westOpens;
	}
	public void setMapName(String name){
		mapName = name;
	}
	public String getMapName(){
		return mapName;
	}
	public int[][] getMap(){
		return map;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	
	
	public void setPosition(double x, double y) {
		
		//System.out.println(x);
		//System.out.println(this.x);
		//System.out.println((x - this.x) * tween);
		
//		this.x += (x - this.x) * tween;
//		this.y += (y - this.y) * tween;
		
		this.x = x;
		this.y = y;
		
		if(beenPlaced == 0){
			initX = this.x;
			//System.out.println("iX " + initX);
			initY = this.y;
			//System.out.println("iY " + initY);
			beenPlaced++;
		}
		
//		fixBounds();
//		
		colOffset = (int)-this.x / tileSize;
		rowOffset = (int)-this.y / tileSize;
		
	}
	public double getInitX() {
		return initX;
	}
	public void setInitX(double initX) {
		this.initX = initX;
	}
	public double getInitY() {
		return initY;
	}
	public void setInitY(double initY) {
		this.initY = initY;
	}
	
	//Things to build pieces based on second constructor :)
	public String findFolder(){
		String almostDir = new File("").getAbsolutePath();
		String[] partsToDir = almostDir.split("/");
		String dir = "";
		for(int i = 0; i < partsToDir.length - 1; i++){
			
				if(i > 0){
				dir = dir + "/" + partsToDir[i];
				}else{
					dir = dir + partsToDir[i];
				}
		}
		dir = dir.substring(1);

		return dir;
	}
	public String findGoodFile(String tempNOpens, String tempEOpens, String tempSOpens, String tempWOpens){
		
		File folder = new File("/" + findFolder()+"/Demons/bin/piece");

		File[] listOfFiles = folder.listFiles();
		System.out.println("Found " + listOfFiles.length + " files");
		ArrayList<String> matchingFiles = new ArrayList<String>();
		String fileName;
		
		for(int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].isFile()){
				fileName = listOfFiles[i].getName();
				String[] split = fileName.split("(?!^)");

				if(directionMatch(tempNOpens, split[split.length - 8]) &&
					directionMatch(tempEOpens, split[split.length - 7]) &&
					directionMatch(tempSOpens, split[split.length - 6]) &&
					directionMatch(tempWOpens, split[split.length - 5])){
						if(fileName != "CONNNN.txt"){
							matchingFiles.add(fileName);
						}
					}
				}
			}
		Random r = new Random();
		int whichFile = r.nextInt(matchingFiles.size());
		String textFile = matchingFiles.get(whichFile);
		
		return textFile;
		}	
	
	public String returnLine(int whichLine){
		String newLine = "";
		for(int i = 0; i < 20; i++){
			newLine = newLine +  map[whichLine][i] + " ";
		}
		//System.out.println(newLine);
		return newLine;
		
	}
	
	public String findGoodFile(MapPiece n, MapPiece e, MapPiece s, MapPiece w){
	
		String nOpens = "", eOpens = "", sOpens = "", wOpens = "";
	
		File folder = new File("/" + findFolder() + "/Demons/bin/piece");
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> matchingFiles = new ArrayList<String>();
		String fileName;
		
		for(int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].isFile()){
				fileName = listOfFiles[i].getName();
				String[] split = fileName.split("(?!^)");

				if(directionMatch(nOpens, split[split.length - 8]) &&
					directionMatch(eOpens, split[split.length - 7]) &&
					directionMatch(sOpens, split[split.length - 6]) &&
					directionMatch(wOpens, split[split.length - 5])){
						if(fileName != "CONNNN.txt"){
							matchingFiles.add(fileName);
						}
					}
				}
			}
		Random r = new Random();
		int whichFile = r.nextInt(matchingFiles.size());
		String textFile = matchingFiles.get(whichFile);
		
		return textFile;
		}	
	
	public boolean directionMatch(String open, String newFile){
		if(open.equals("A")){return true;}
		if(open.equals(newFile)){return true;}
		return false;
		
	}

	public void draw(Graphics2D g) {
		if(beenPlaced == 0){
			//System.out.println(mapName);
		}
		for(
				int row = rowOffset;
				row < rowOffset + numRowsToDraw;
				row++) {
				
				if(row >= numRows) break;
				
				for(
					int col = colOffset;
					col < colOffset + numColsToDraw;
					col++) {
					
					if(col >= numCols) break;
					
					int rc = map[row][col];
					int r = rc / numTilesAcross;
					int c = rc % numTilesAcross;
					
					g.drawImage(
						tiles[r][c].getImage(),
						(int)x + col * tileSize,
						(int)y + row * tileSize,
						null
					);
					
				}
				
			}
		}
}
