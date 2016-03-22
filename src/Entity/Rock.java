package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entity.Enemies.Lava;
import TileMap.TileMap;

	//the rock is a movable rock used for puzzles. :)

public class Rock extends MapObject {
	
	private ArrayList<BufferedImage[]> sprites;		//Arraylist that holds sprites arrays
	private Luna luna;
	
	private ArrayList<Rock> otherRocks;
	private int positionInRocks;						//this is so that we dont check colliding against ourselves
	private ArrayList<Lava> lavas;						//this is the list of lavas so that we can check we're not colliding with the lava.
	
	private double lastPositionX;						//so that we can reset if we intersect something we're not supposed to
	private double lastPositionY;						//so that we can reset if we intersect something we're not supposed to
	
	private double lunaLastPositionX;					//so that we can reset if we intersect something we're not supposed to
	private double lunaLastPositionY;					//so that we can reset if we intersect something we're not supposed to
	
	private boolean restarted = true;
	
	public Rock(TileMap tm){
		super(tm);
		
		width = 32;
		height = 28;
		cwidth = 16;
		cheight = 16;
		
		moveSpeed = 0;
		maxSpeed = 0.0;
		stopSpeed = 0.0;
		fallSpeed = 0;
		maxFallSpeed = 0;

		facingRight = false;
		facingLeft = false;
		facingUp = false;
		facingDown = true;

	
		
		try{

			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream(
							"/Tileset/Singles/Rock.png"));

			sprites = new ArrayList<BufferedImage[]>();
			BufferedImage[] bi = new BufferedImage[1];
				for(int x = 0; x < 1; x++){
					bi[x] = spritesheet.getSubimage(
							x * 32,
							0,
							32,
							32);
				}
				sprites.add(bi);
		}catch(Exception e){
			e.printStackTrace();
		}
		animation = new Animation();
		currentAction = 0;
		animation.setFrames(sprites.get(0));
		animation.setDelay(100);
	}
	
	public void setLuna(Luna l){luna = l;}
	public void setOtherRocks(ArrayList<Rock> rocks){otherRocks = rocks;}
	public void setLavas(ArrayList<Lava> lavas2){lavas = lavas2;}
	public void setPositionInRocks(int pos){positionInRocks = pos;}
	
	public void update(){
		checkTileMapCollision();
		animation.update();
		checkPush();
	}
	
	public void checkPush(){
		if(restarted){
			lastPositionX = getx();
			lastPositionY = gety();
			lunaLastPositionX =luna.getCurrCol() * 32;
			lunaLastPositionY = luna.getCurrRow() * 32;
			restarted = false;
		}
	
		if(intersects(luna)){
			System.out.println(lunaLastPositionY);
			//NORTH
			if(luna.isFacingUp()){
				if(!checkWall(currRow - 1, currCol)){
					setPosition(x, y-32);
					restarted = true;
				}else{
					luna.setPosition(lunaLastPositionX, lunaLastPositionY);
					luna.setUp(false);
					luna.setMoving(false);
					luna.setDirectionMoving("N");
					restarted = true;
				}
				
			//EAST	
			}else if(luna.isFacingRight()){
				if(!checkWall(currRow, currCol + 1)){
					setPosition(x+32,y);
					restarted = true;
				}else{
					luna.setPosition(lastPositionX-32, lunaLastPositionY);
					luna.setRight(false);
					luna.setMoving(false);
					luna.setDirectionMoving("N");
					restarted = true;
				}
				
			//SOUTH
			}else if(luna.isFacingDown()){
				if(!checkWall(currRow + 1, currCol)){
					setPosition(x, y+32);
					restarted = true;
				}else{
					luna.setPosition(lunaLastPositionX, lastPositionY-28);
					luna.setDown(false);
					luna.setMoving(false);
					luna.setDirectionMoving("N");
					restarted = true;
				}
				
			//WEST
			}else if(luna.isFacingLeft()){
				if(!checkWall(currRow, currCol-2)){
					setPosition(x-32, y);
					restarted = true;
				}else{
					luna.setPosition(lastPositionX+32, lastPositionY+4);
					luna.setLeft(false);
					luna.setMoving(false);
					luna.setDirectionMoving("N");
					restarted = true;
				}
			}
			checkOtherRocks();
			checkForLava();
		}
	}
	public void checkOtherRocks(){
		for(int i = 0; i < otherRocks.size(); i++){
			if(i != positionInRocks){
				if(intersects(otherRocks.get(i))){
					setPosition(lastPositionX, lastPositionY);
					luna.setPosition(lunaLastPositionX, lunaLastPositionY);
					System.out.println("rocks");
					stopLuna();
				}
			}
		}
	}
	public void checkForLava(){
		for(int i = 0; i < lavas.size(); i++){
			if(intersects(lavas.get(i))){
				setPosition(lastPositionX, lastPositionY);
				System.out.println("lavas");
				luna.setPosition(lunaLastPositionX, lunaLastPositionY);
				stopLuna();
			}
		}
	}
	
	public void stopLuna(){
		if(luna.isFacingUp()){
			luna.setUp(false);
			luna.setMoving(false);
			luna.setDirectionMoving("N");
		}else if(luna.isFacingRight()){
			luna.setRight(false);
			luna.setMoving(false);
			luna.setDirectionMoving("N");
		}else if(luna.isFacingDown()){
			luna.setDown(false);
			luna.setMoving(false);
			luna.setDirectionMoving("N");
		}else if(luna.isFacingLeft()){
			luna.setLeft(false);
			luna.setMoving(false);
			luna.setDirectionMoving("N");
		}
	}
	
	//row = y col = x
	public boolean checkWall(int row, int col){
		//if collides with other rocks
		
		
		//if collides with wall
		if(tileMap.getType(row, col) == 1){
			return true;
		}
		return false;
		
		
	}
	
	public void draw(Graphics2D g){
		checkTileMapCollision();
		
			Rectangle r = getRectangle();
			r.x += xmap;
			r.y += ymap;
			g.draw(r);
	//	
			super.draw(g);
	}
	
}
