package GameStates;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import Entity.Luna;
import Entity.Rock;
import Entity.Stairs;
import Entity.Enemies.Cultist;
import Entity.Enemies.Diagon;
import Entity.Enemies.Enemy;
import Entity.Enemies.Lava;
import Entity.Spots.MovableSpot;
import Main.GamePanel;
import Main.Keys;
import Main.Mouse;
import State.GameState;
import State.GameStateManager;
import TileMap.Background;
import TileMap.MapPiece;
import TileMap.TileMap;

public class DungeonState extends GameState{

	public static int PIECES_AMOUNT = 9; 			// The amount of pieces it takes to build the level. must be a perfect square
	public static int PIECES_MADE = 0;

	//MAP STUFF
	private MapPiece[][] theMap;					//The map while its being built
	private TileMap map;							//the finished map.
	private double mapX, mapY;						//Where the map is currently
	private double realMapX, realMapY;				//Where the map should be when not scrolling.
	private boolean scrolling;
	private boolean mapReady = false;				//is everything spawned and set in the right place?
	
	
	//THE MOUSE
	private Mouse mouse;							//this is the mouse...

	
	//ENEMY STUFF
	private ArrayList<Enemy> enemies;				//the arrayList that holds all the enemies and things

	
	//LUNA STUFF
	private Luna luna;								//Luna is the main character and is the character the player plays as
	private int lunaReady = 0;						//if 0 creates all the things that need to made at beginning of luna turn
	private boolean lunaTurn = true;
	private ArrayList<MovableSpot> movables;		//the arraylist of all the spots the player could currently move to
	private int playerMoved = 0;					//has the player moved in a direction yet? no = 0 yes = 1
	
	
	//DRAWING STUFF
	private Background grid;
	private Font newFont;
	private Background bg;							//The background image. (Not necessary in this game besides to be used to prevent blurring
	private int displayMapX;						//which map is being displayed in the x direction (for mouse movement)
	private int displayMapY;						//which map is being displayed in the y direction (for mouse movement)
	
	
	//OTHER MAP STUFF
	private Stairs stairs;
	private ArrayList<Lava> lavas = new ArrayList<Lava>();
	private ArrayList<Rock> rocks = new ArrayList<Rock>();
	private Rock rock;
	
	
	public DungeonState(GameStateManager gsm){
		this.gsm = gsm;
		init();
	}

	public void init(){
		bg = new Background("/Background/Black.png", 0);
		bg.setPosition(0, 0);
		grid = new Background("/Background/Grid.png", 0);
		bg.setPosition(0, 0);
		newFont = importFont();
		
		buildMap();
		
		movables = new ArrayList<MovableSpot>();

		luna = new Luna(map);
		
		
		
//		lava = new Lava(map);
//		lava.setPosition(1632, 1212);
//		lava2.setPosition(1632, 1244);
		/*
		 * To start centered: 1600, 1216
		 * To start north: 1600, 1024
		 * To start south: 1600, 1408
		 * To start east: 1888, 1216
		 * To start west: 1344, 1216
		 */

		luna.setPosition(1600, 1408);
		
		spawnEnemies();
		printMap();
		spawnStairs();
		
		mouse = new Mouse(map);	
		mouse.setPosition(100, 100);



		mapReady = true;
		

	}
	
	//initial spawning things
	public void buildMap(){
		theMap = new MapPiece[5][5];

		fillEmpties();
		MapPiece piecey = new MapPiece("COMMMM.txt");
		mapX = -1280; mapY = -960;
		realMapX = -1280; realMapY = -960;
		scrolling = false;
		theMap[2][2] = piecey;
		displayMapX = 2;
		displayMapY = 2;

		piecey.setPosition(mapX, mapY);
		secondPiece(piecey,2, 2,"N"); 
		secondPiece(piecey,2, 2,"E"); 
		secondPiece(piecey,2, 2,"S"); 
		secondPiece(piecey,2, 2,"W");
		mapBuilder(1,1);
		mapBuilder(1,3);
		mapBuilder(3,3);
		mapBuilder(3,1);

		endPieces();

		map = new TileMap(32);

		loadTileset();
		map.loadMap4(theMap);

		map.makeMap("some.txt");
		map.setPosition(-1920, -1440);
		map.setTween(1);

	}
	public void loadTileset(){
		if(gsm.currentLevel == 1 || gsm.currentLevel == 2){
			map.loadTiles("/Tileset/Grass.png");
		}else if(gsm.currentLevel == 3 || gsm.currentLevel >= 4){
			map.loadTiles("/Tileset/First.png");
		}
	}
	public void spawnEnemies(){
		//How we will do this in notepad
		//((mapx + 1) * 640) - (32 * (numberOfTiles from right side of screen you want the enemy to be))


		enemies = new ArrayList<Enemy>();
//		rock = new Rock(map);
//		rock.setPosition(1632, 1212);
//		rock.setLuna(luna);
//		rocks.add(rock);
//		rock.setOtherRocks(rocks);
//		rock.setLavas(lavas);
//		rock.setPositionInRocks(rocks.size()-1);
		
		
		spawnEnemies(findGoodEnemyFile(theMap[1][1]), theMap[1][1], 1, 1);
		spawnEnemies(findGoodEnemyFile(theMap[1][2]), theMap[1][2], 1, 2);
		spawnEnemies(findGoodEnemyFile(theMap[1][3]), theMap[1][3], 1, 3);

		spawnEnemies(findGoodEnemyFile(theMap[2][1]), theMap[2][1], 2, 1);
		spawnEnemies(findGoodEnemyFile(theMap[2][2]), theMap[2][2], 2, 2);
		spawnEnemies(findGoodEnemyFile(theMap[2][3]), theMap[2][3], 2, 3);

		spawnEnemies(findGoodEnemyFile(theMap[3][1]), theMap[3][1], 3, 1);
		spawnEnemies(findGoodEnemyFile(theMap[3][2]), theMap[3][2], 3, 2);
		spawnEnemies(findGoodEnemyFile(theMap[3][3]), theMap[3][3], 3, 3);


	}
	public void spawnStairs(){
		ArrayList<Integer> xPossibles = new ArrayList<Integer>();
		ArrayList<Integer> yPossibles = new ArrayList<Integer>();

		for(int col = 0; col < theMap.length; col++){
			for(int row = 0; row < theMap.length; row++){
				String[] mapChars = theMap[row][col].getMapName().split("(?!^)");

				if(theMap[row][col].getMapName().equals("CONNNN.txt") &&
						(!mapChars[mapChars.length - 9].equals("W") && 
								!mapChars[mapChars.length - 10].equals("H"))){
				}else{
					xPossibles.add(row);
					System.out.println(col);
					yPossibles.add(col);

					System.out.println(theMap[row][col].getMapName());


				}
			}
		}

		Random r = new Random();
		int whichPiece = r.nextInt(xPossibles.size());
		int whereX = r.nextInt(18);	// + 32
		int whereY = r.nextInt(13);
		System.out.println(whichPiece);
		int finalX = ((xPossibles.get(whichPiece)) * 640) + ((32 * whereX)) + 32;
		System.out.println(xPossibles.get(whichPiece) + "Which piece");
		//		System.out.println((xPossibles.get(whichPiece) * 640));
		int finalY = (((yPossibles.get(whichPiece)) * 480)) + ((32 * whereY) + 32);
		//System.out.println(yPossibles.get(whichPiece) + "here");

		stairs = new Stairs(map);
		stairs.setStateManager(gsm);
		stairs.setPosition(finalX, finalY);
		System.out.println(stairs.getx());
		System.out.println(stairs.gety());

	}

	
	//UPDATED STUFF
	public void update(){

		luna.update();
		for(int i = 0; i < rocks.size(); i++){
			rocks.get(i).update();
		}
//		for(int i = 0; i < lavas.size(); i++){
//			lavas.get(i).update();
//		}
		//		System.out.println(luna.getx());
		//		System.out.println(luna.gety());
		for(int i = 0; i < enemies.size(); i++){
			Enemy e = enemies.get(i);
			if(!e.notOnScreen()){
				e.update();
			}
			if(e.contains(luna)){
					gsm.setState(GameStateManager.LOADINGSTATE);
			}
			if(e.isDead()){
				enemies.remove(e);
			}
		}
		
		mouse.update();
		

		if(stairs.intersects(luna)){
			gsm.addScore(500);
			stairs.nextLevel();
		}

		if(!luna.isMoving() && luna.getx() % 32 == 0 && luna.gety() % 32 == 0){
			movableSpots();
		}

		handleInput();

		map.setPosition(mapX, mapY);
		screenScrolling();
	}
	public void screenScrolling(){
		//moves the tilemap up and down when the player gets to the sides of the map.
		if(!scrolling){
			//move up
			if(luna.getScreenY() == 32){
	
				//move the map up 480
				mapY += 480;
				//move luna up 2 tiles so that she doesnt mess up the downward scrolling
				luna.setPosition(luna.getx(), luna.gety() - 64);
	
				//Sets the real map variables so that we can reset the maps position when done scrolling.
				realMapY = mapY;
				realMapX = mapX;
					
	
				//this is stuff to clear the arraylist of any movable spots and to create the new ones
				movables.clear();
				lunaReady = 0;
				movableSpots();
	
				//Mouse Stuff
				displayMapY--;
			}
	
			//move down
			if(luna.getScreenY() >= 480 && luna.getScreenY() <= 512){
				//move the map up 480
				mapY -= 480;
				System.out.println(mapY);
				//move luna 2 tiles so that she doesnt mess up the scrolling
				luna.setPosition(luna.getx(), luna.gety() + 64);
	
				//Sets the real map variables so that we can reset the maps position when done scrolling.
				realMapY = mapY;
				realMapX = mapX;
	
				//this is stuff to clear the arraylist of any movable spots and to create the new ones
				movables.clear();
				lunaReady = 0;
				movableSpots();
	
				//Mouse Stuff
				displayMapY++;
			}
	
			//move right
	
			//to prevent moving at init. look to next comment for further explanation
			if(!luna.isMoving() && mapReady){
				//the 3000 is to prevent the screen form scrolling when the map
				//initially spawns because Lunas init screen x is around 5000 and
				//doesnt fix itself until it gets to around 3000 :)
				if(luna.getScreenX() == 640 && luna.getScreenX() < 3000){
					//move the map right 640
					mapX -= 640;
					//move luna 2 tiles so that she doesnt mess up the scrolling
					luna.setPosition(luna.getx() + 64, luna.gety());
	
					//Sets the real map variables so that we can reset the maps position when done scrolling.
					realMapY = mapY;
					realMapX = mapX;
	
					//this is stuff to clear the arraylist of any movable spots and to create the new ones
					movables.clear();
					lunaReady = 0;
					movableSpots();
	
					//Mouse Stuff
					displayMapX++;
				}
			}	
	
			//move left
			if(!luna.isMoving() && mapReady){
				if(luna.getScreenX() == 32 && luna.getScreenX() < 3000){
					//move the map right 640
					mapX += 640;
					//move luna 2 tiles so that she doesnt mess up the scrolling
					luna.setPosition(luna.getx() - 64, luna.gety());
	
					//Sets the real map variables so that we can reset the maps position when done scrolling.
					realMapY = mapY;
					realMapX = mapX;
					
					//this is stuff to clear the arraylist of any movable spots and to create the new ones
					movables.clear();
					lunaReady = 0;
					movableSpots();
	
					//Mouse Stuff
					displayMapX--;
				}
			}
		}

		if(enemiesOnScreen()){
			luna.setMaxEndurance(luna.getInitialMaxEndurance());
			luna.setMaxSpeed(luna.getInitialMaxSpeed());
		}

	}
	
	//INPUT
	public void handleInput(){

		if(Keys.isPressed(Keys.LEFT)){
			scrolling = true;
			mapX += 10;
		}
		if(Keys.isPressed(Keys.RIGHT)){
			scrolling = true;
			mapX -= 10;
		}
		if(Keys.isPressed(Keys.UP)){
			scrolling = true;
			mapY += 10;
		}
		if(Keys.isPressed(Keys.DOWN)){
			scrolling = true;
			mapY -= 10;
		}
		if(Keys.isPressed(Keys.BUTTON1)){
			mapY = realMapY;
			mapX = realMapX;
			scrolling = false;
		}



	}
	public void mouseClicked(MouseEvent e){
		mouse.setPosition((
				e.getX() / GamePanel.SCALE) + (640 * displayMapX)
				- (mouse.getWidth() / 2),
				(e.getY() / GamePanel.SCALE) + (480 * displayMapY) 
				- (mouse.getHeight() / 2));


		for(int j = 0; j < enemies.size(); j++){
			Enemy en = enemies.get(j);

			if(mouse.intersects(en)){
				luna.attack(en);
				gsm.addScore(100);
				movables.clear();
				lunaReady = 0;	
			}	

		}

		for(int i = 0; i < movables.size(); i++){
			MovableSpot ms = movables.get(i);
			if(mouse.intersects(ms)){
				luna.moveTo(ms);
				movables.clear();
				lunaReady = 0;
			}
		}



	}
	public void movableSpots(){
		//creates the squares of movable spots in the arrayList movables
		//and puts them where they need to be
		
		if(lunaReady == 0 && mapReady){
			if(enemiesOnScreen()){
				
				for(int i = 0; i < enemies.size(); i++){
					Enemy e = enemies.get(i);
					if(!e.notOnScreen()){
						e.move();
					}
				}
			
			}else{
				luna.setMaxEndurance(10);
				luna.setMaxSpeed(7.0);
			}
			lunaReady++;
			MovableSpot ms;
			for(int i = 0; i < 4; i++){
				boolean spawnable = true;
				for(int j = 0; j < luna.getEnduranceLeft(); j++){
					switch(i){
					//movable spots going north
					
					case 0:
						
						ms = new MovableSpot(map, luna);
						ms.setPosition(luna.getx(), luna.gety() - (32 * j) - 32);
						for(int l = 0; l < lavas.size(); l++){
							if(ms.intersects(lavas.get(l))){
								spawnable = false;
							}
						}
						if(spawnable){
							movables.add(ms);
						}

						break;
						
						
					case 1:
						ms = new MovableSpot(map, luna);
						ms.setPosition(luna.getx() + (32 * j) + 32, luna.gety());
						for(int l = 0; l < lavas.size(); l++){
							if(ms.intersects(lavas.get(l))){
								spawnable = false;
							}
						}
						if(spawnable){
							movables.add(ms);
						}
						break;
						
						
					case 2:
						ms = new MovableSpot(map, luna);
						ms.setPosition(luna.getx(), luna.gety() + (32 * j) + 32);
						
						for(int l = 0; l < lavas.size(); l++){
							if(ms.intersects(lavas.get(l))){
								spawnable = false;
							}
						}
						if(spawnable){
							movables.add(ms);
						}


						break;
					case 3:
						ms = new MovableSpot(map, luna);
						ms.setPosition(luna.getx() - (32 * j) - 32, luna.gety());
						
						for(int l = 0; l < lavas.size(); l++){
							if(ms.intersects(lavas.get(l))){
								spawnable = false;
							}
						}
						if(spawnable){
							movables.add(ms);
						}

						break;
					}
				}
			}
		}
	}
	
	public void draw(Graphics2D g){
		g.setColor(Color.WHITE);
		g.setFont(newFont);
		map.draw(g);
		
		stairs.draw(g);
		if(!scrolling){
			

			grid.draw(g);
		}
		
		
		
		if(!luna.isMoving()){
			for(int i = 0; i < movables.size(); i++){
				movables.get(i).draw(g);
			}
		}
		for(int i = 0; i < enemies.size(); i++){
			if(enemies.get(i).gety() < luna.gety()){
				enemies.get(i).draw(g);
			}
		}
		for(int i = 0; i < lavas.size(); i++){
			lavas.get(i).draw(g);
		}
		luna.draw(g);
		for(int i = 0; i < enemies.size(); i++){
			if(enemies.get(i).gety() >= luna.gety()){
				enemies.get(i).draw(g);
			}
		}

		mouse.draw(g);

		g.drawString("S C O R E :  "+ gsm.getScore(), 25, 30);
		for(int i = 0; i < rocks.size(); i++){
			rocks.get(i).draw(g);
		}
		
	}	
	public Font importFont() {
		//imports the font used for scores.
		Font customFont = null;
		try{
			InputStream myStream = new BufferedInputStream(new FileInputStream("Resources/Font/DIMIS___.TTF"));
			  customFont = Font.createFont(Font.TRUETYPE_FONT, myStream).deriveFont(18f);
              GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
              //register the font
              ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Resources/Font/DIMIS___.TTF")));
              return customFont;
       	}catch(Exception e){
			e.printStackTrace();
		}
		return customFont;
	}
	
	
	
	//INITIAL MAP CREATION
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
	public String makeFirstPiece(String placement){

		File folder = new File("/" + findFolder() + "/bin/piece");
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> matchingFiles = new ArrayList<String>();
		String[] place = placement.split("(?!^)");
		String fileName;

		for(int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].isFile()){
				fileName = listOfFiles[i].getName();
				String[] split = fileName.split("(?!^)");
				if(split[0].equals(place[0]) && split[1].equals(place[1])){
					matchingFiles.add(fileName);
				}
			}
		}
		Random r = new Random();
		int whichFile = r.nextInt(matchingFiles.size());
		String textFile = matchingFiles.get(whichFile);

		PIECES_MADE++;

		return textFile;
	}	
	public void secondPiece(MapPiece m, int x, int y, String direction){
		//m is the map we're building off of, x and y are positions
		//of original map in theMap array. direction is n,e,s, or w for where
		//to put the map piece in relation to the original piece
		
		MapPiece newMP;

		//These strings are what our openings will be. we set them to
		//nothing right here to avoid initialization errors

		switch(direction){
		case "N":
			if(!m.getNorthOpens().equals("N")){
				if(y != 0){
					newMP = new MapPiece("A", "A",m.getNorthOpens(), "A");
					theMap[x][y-1] = newMP;

					PIECES_MADE++;
					//if there are no north opens set the north openings to none.
				}
			}
			break;
		case "E":
			if(!m.getEastOpens().equals("N")){
				if(y == 0){
					newMP = new MapPiece("N", "A", "A", m.getEastOpens());
				}else{
					newMP = new MapPiece("A", "A", "A", m.getEastOpens());
				}

				theMap[x + 1][y] = newMP;
				PIECES_MADE++;
			}
			break;
		case "S":
			if(!m.getSouthOpens().equals("N")){

				if(x == 0){
					newMP = new MapPiece(m.getSouthOpens(), "A", "A", "N");
				}else{
					newMP = new MapPiece(m.getSouthOpens(), "A", "A", "A");
				}


				theMap[x][y + 1] = newMP;
				PIECES_MADE++;
			}
			break;
		case "W":
			if(!m.getWestOpens().equals("N")){
				if(x == 0){return;}
				newMP = new MapPiece("A", m.getWestOpens(), "A", "A");

				theMap[x - 1][y] = newMP;
				PIECES_MADE++;
			}
			break;
		}
	}	
	public void mapBuilder(int x, int y){
		//m is the piece we're building off of, x and y are where the new
		//piece is located in theMap array.. 
		//Ex: top left piece is [0][0] and the one to the right of it is [1][0]
		
		/*we initially set these to the old pieces position 
		so that we don't always have to set both variables...*/

		String north = "", east = "", south = "", west = "";

		MapPiece newMp;

		//Here we create the strings for the openings of the final piece
		if(y == 0){north = "N";}else{
			north = theMap[x][y-1].getSouthOpens();
			//System.out.println("The piece at " + x + " " + y + "Has a north opening of " + north);
		}
		if(x == 4){east = "N";}else{
			east = theMap[x+1][y].getWestOpens();
			//System.out.println("The piece at " + x + " " + y + "Has a east opening of " + east);
		}
		if(y == 4){south = "N";}else{
			south = theMap[x][y + 1].getNorthOpens();
			//System.out.println("The piece at " + x + " " + y + "Has a south opening of " + south);
		}
		if(x == 0){west = "N";}else{
			west = theMap[x-1][y].getEastOpens();
			//System.out.println("The piece at " + x + " " + y + "Has a west opening of " + west);
		}
		//create a new mapPiece using the openings we just found
		newMp = new MapPiece(north, east, south, west);
		//put the piece into the array in the correct location
		theMap[x][y] = newMp;

	}
	public void endPieces(){
		for(int i = 0; i < 5; i++){

			theMap[i][0] = new MapPiece("N", "N", theMap[i][1].getNorthOpens(), "N");
			theMap[0][i] = new MapPiece("N", theMap[1][i].getWestOpens(), "N", "N");
			theMap[i][4] = new MapPiece(theMap[i][3].getSouthOpens(), "N", "N", "N");
			theMap[4][i] = new MapPiece("N", "N", "N", theMap[3][i].getEastOpens());


			//System.out.println(theMap[i][0].getMapName());

		}
	}
	public void fillEmpties(){
		for(int y = 0; y < 5; y++){
			for(int x = 0; x < 5; x++){
				theMap[x][y] = new MapPiece("CONNNN.txt");
			}
		}
	}
	public String getMapName(String s){
		String[] breaker = s.split(".txt");
		return breaker[0];
	}	
	public void printMap(){
		//Prints the maps individual pieces names in a grid into the console
		
		for(int col = 0; col < theMap.length; col++){
			for(int row = 0; row < theMap.length; row++){
				System.out.print(theMap[row][col].getMapName() + "  ");
			}
			System.out.println("");
		}
	}
	
	//GETTERS AND SETTERS
	public boolean enemiesOnScreen(){
			//this checks if there are any enemies on the screen
			//because if there aren't we want to give luna 
			//unlimited movement
			
			// enemiesHere is an integer that holds the amount of enemies on the screen at this point
			int enemiesHere = 0;	
			for(int i = 0; i < enemies.size(); i++){
				Enemy e = enemies.get(i);
				//if the enemy is on screen add to enemiesHere
				if(!e.notOnScreen()){
					enemiesHere++;
				}
			}
			//if there are enemies on screen return true
			if(enemiesHere > 0){
				return true;
			}
			//otherwise return false
			return false;
		}
	public boolean readyToMove(){
			if(playerMoved == 0){

				playerMoved++;
				return true;
			}
			return false;
		}
	
	//ENEMY SPAWNING STUFF
	public String findGoodEnemyFile(MapPiece currentPiece){
		String textFile = "Empty/NONONE.txt";
		try{
			File folder = new File("/" + findFolder() + "/Demons/bin/piece/SpawningPatterns/");
			System.out.println("The Path" + folder.getAbsolutePath());
			File[] listOfFiles = folder.listFiles();

			
			System.out.println("Found NOw " + listOfFiles.length + " files");
			ArrayList<String> matchingFiles = new ArrayList<String>();
			String fileName;

			String[] mapChars = currentPiece.getMapName().split("(?!^)");
			
			for(int i = 1; i < 65; i++) {
				System.out.println(i + " " + listOfFiles.length);
				if(listOfFiles[i].isFile()){
					fileName = listOfFiles[i].getName();
					System.out.println(fileName + " " + listOfFiles.length);
					String[] fileIndivs = fileName.split("(?!^)");
					//level finds what level the spawn pattern was made for
					String level = (fileIndivs[fileIndivs.length - 8] + fileIndivs[fileIndivs.length - 7]);
					int currentLevel = Integer.parseInt(level);
					if(currentLevel == gsm.getCurrentLevel()){
						//if the map piece is not a hallway or special piece
						if(!mapChars[mapChars.length - 9].equals("W") && !mapChars[mapChars.length - 9].equals("B")){
							if(!mapChars[mapChars.length - 10].equals("H") && !mapChars[mapChars.length - 10].equals("M")){//not middle blocked
								//if the spawning pattern was made for "open" pieces

								matchingFiles.add(fileName);

							}
						}
					}
				}
			}
			Random r = new Random();
			System.out.println(matchingFiles.size());
			int whichFile = r.nextInt(matchingFiles.size());
			textFile = matchingFiles.get(whichFile);

		}catch(IllegalArgumentException e){
			System.out.println("hallway");
		}
		System.out.println(currentPiece.getMapName());
		System.out.println(textFile);
		return textFile;
	}	
	public void spawnEnemies(String textFile, MapPiece currentPiece, int mapX, int mapY){
		try{
			BufferedReader br = new BufferedReader(
					new FileReader("/" + findFolder() + "/Demons/bin/piece/SpawningPatterns/" + textFile)
					);


			String enemyType = br.readLine();

			//are we spawning cultists??
			if(enemyType.equals("Cultist")){
				int cultistAmount = Integer.parseInt(br.readLine());
				Cultist c;
				for(int i = 0; i < cultistAmount; i++){
					String[] coors = br.readLine().split(",");//string array coordinates is the next line
					c =  new Cultist(map);
					c.setLuna(luna);
					//position stuff
					int xTile = Math.abs(Integer.parseInt(coors[0]) - 20);	//which tile on the x axis
					int yTile = Math.abs(Integer.parseInt(coors[1]) - 15);  //which tile on the y axis
					double xPos = ((mapX + 1) * 640) - (32 * xTile); //actual position on x axis
					double yPos = ((mapY + 1) * 480) - (32 * yTile); //actual position on y axis
					c.setPosition(xPos, yPos);
					enemies.add(c);
				}
				System.out.println("X " + mapX + " Y " + mapY);

			}
			enemyType = br.readLine();
			if(enemyType.equals("Diagon")){
				int diagonAmount = Integer.parseInt(br.readLine());
				Diagon d;
				for(int i = 0; i < diagonAmount; i++){
					String[] coors = br.readLine().split(",");//string array coordinates is the next line
					d =  new Diagon(map);
					d.setLuna(luna);
					//position stuff
					int xTile = Math.abs(Integer.parseInt(coors[0]) - 20);	//which tile on the x axis
					int yTile = Math.abs(Integer.parseInt(coors[1]) - 15);  //which tile on the y axis
					double xPos = ((mapX + 1) * 640) - (32 * xTile); //actual position on x axis
					double yPos = ((mapY + 1) * 480) - (32 * yTile); //actual position on y axis
					System.out.println(xPos + "   " + yPos);
					d.setPosition(xPos, yPos);
					enemies.add(d);
				}			
			}
			enemyType = br.readLine();
			if(enemyType.equals("Lava")){
				int squaresOfLava = Integer.parseInt(br.readLine());
				Lava l;
				for(int i = 0; i < squaresOfLava; i++){
					String[] squares = br.readLine().split(" x ");	//this splits the line into the two coordinates
					String[] topLeft = squares[0].split(",");		//this splits the first coordinate into x and y
					String[] bottomRight = squares[1].split(",");	//this splits the second coordinate into x and y
					int xTile = 0;//which tile on the x axis
					int yTile = 0;//which tile on the y axis
					for(int y = Integer.parseInt(topLeft[1]); y <= Integer.parseInt(bottomRight[1]); y++){//this goes from the furthest north y to the furthest south
						for(int x = Integer.parseInt(topLeft[0]); x <= Integer.parseInt(bottomRight[0]); x++){//this goes from the furthest west y to the furthest east
							l = new Lava(map);
							xTile = Math.abs(x - 20);
							yTile = Math.abs(y - 15);
							double xPos = ((mapX + 1) * 640) - (32 * xTile);
							double yPos = ((mapY + 1) * 480) - (32 * yTile) - 4;
							l.setPosition(xPos, yPos);
							lavas.add(l);
						}
					}
				}
			}
			enemyType = br.readLine();
			if(enemyType.equalsIgnoreCase("Rock")){
				int rockAmount = Integer.parseInt(br.readLine());
				Rock r;
				for(int i = 0; i < rockAmount; i++){
					String[] coors = br.readLine().split(",");//string array coordinates is the next line
					r =  new Rock(map);
					r.setLuna(luna);
					r.setOtherRocks(rocks);
					r.setLavas(lavas);
					//position stuff
					int xTile = Math.abs(Integer.parseInt(coors[0]) - 20);	//which tile on the x axis
					int yTile = Math.abs(Integer.parseInt(coors[1]) - 15);  //which tile on the y axis
					double xPos = ((mapX + 1) * 640) - (32 * xTile); //actual position on x axis
					double yPos = ((mapY + 1) * 480) - (32 * yTile); //actual position on y axis
					System.out.println(xPos + "   " + yPos);
					r.setPosition(xPos, yPos);
					rocks.add(r);
					r.setPositionInRocks(rocks.size()-1);
				}			
			}
			
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	
}
