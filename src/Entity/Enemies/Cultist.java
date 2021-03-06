package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entity.Animation;
import Entity.Luna;
import Entity.MapObject;
import TileMap.TileMap;

@SuppressWarnings("unused")
public class Cultist extends Enemy{
	//things that relate to drawing the character
	private ArrayList<BufferedImage[]> sprites;		//Arraylist that holds sprites arrays
	private final int[] numFrames = {
			4, 4, 4, 4
	};

	//These are things to make
	//the character stop moving
	private String directionMoving = "N";			//The string that tells which way we're moving currently. If it's N that means we're not moving
	private int newX = 0; 							//The goal X
	private int newY = 0;							//the goal Y
	private boolean moving = false;					//is the character moving right now?

	private Luna luna;
	private TileMap tm;
	
	//Also to help with drawing. Which action
	//the character is currently doing
	private static final int RIGHT = 0;
	private static final int LEFT = 1;
	private static final int UP = 2;
	private static final int DOWN = 3;

	public Cultist(TileMap tm) {
		super(tm);
		this.tm = tm;
		name = "Cultist";
		width = 34;
		height = 52;
		cwidth = 32;
		cheight = 32;

		moveSpeed = .5;
		maxSpeed = 2.0;
		stopSpeed = 0.4;
		fallSpeed = 0;
		maxFallSpeed = 0;

		facingRight = false;
		facingLeft = false;
		facingUp = false;
		facingDown = true;

		maxHealth = 1;
		health = 1;

		try{

			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream(
							"/Characters/CultistSheet.png"));

			sprites = new ArrayList<BufferedImage[]>();
			for(int y = 0; y < 4; y++){
				//creates the image array to hold the individual sprites
				BufferedImage[] bi = new BufferedImage[numFrames[y]];
				for(int x = 0; x < numFrames[y]; x++){
					if(y == 0 || y == 1){
						bi[x] = spritesheet.getSubimage(
								x * 34,
								y * 58,
								34,
								58);
					}else if(y == 2){
						bi[x] = spritesheet.getSubimage(
								x * 32,
								116,
								32,
								58);
					}else if(y == 3){
						bi[x] = spritesheet.getSubimage(
								x * width,
								178,
								width,
								52);
					}
				}
				sprites.add(bi);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		animation = new Animation();
		currentAction = DOWN;
		animation.setFrames(sprites.get(DOWN));
		animation.setDelay(200);


	}
	
	public void setLuna(Luna l){luna = l;}

	public void move(){
		//pathScore = (movement cost) + (estimated movement cost to final destination)

		int newX = getx();				//the horizontal position of the new spot
		int newY = gety();				//the vertical position of the new spot
		double pathScore = 0;			//This is the best score and the final spot we will move to.
		int bestPath = 0;			//this is the spot in the arrayList where the best path is stored
		int movementCost = 0;			//how much endurance would it cost to get to this spot
		int estimatedMovementCost = 0;	//how much endurance would it probably take to get to the final destination (the player)

		ArrayList<int[]> movableSpots = new ArrayList<int[]>();	//these are the spots that are movable.. not blocked.

		//Look at all spots to see which spots are open and where they are
		//to go left or to go up we have to subtract two for some reason...?
		for(int y = -2; y <= 1; y++){
			for(int x = -2; x <= 1; x++){
				if(y == 0 && x == 0){

				}else{
					if(y !=  -1 && x != -1){


						if(tm.getType(getCurrRow() + y, getCurrCol() + x) == 0){  //if the spot is movable
							
							if(x != -2){
								newX = x;
							}else{
								newX = -1;
							}

							if(y != -2){
								newY = y;
							}else{
								newY = -1;
							}

							movementCost = Math.abs(newX) + Math.abs(newY);

							//Manhattan method. Distance from goal in tiles. no diagonals. (enemy x - player x) + (enemy y - player y)
							estimatedMovementCost = (Math.abs(luna.getx() - (getx() + (newX * 32))) / 32) + 
									(Math.abs(luna.gety() - (gety() + (newY * 32))) / 32);
							
							//add the spot to movableSpots. each array in the list has four ints
							//the four ints are: The spot's x postition, the spot's y position
							//the movement cost, and the final estimated movement cost
							if( x != y && (x != 0 ||y != 0)){
								int[] moves = new int[]{newX, newY, movementCost, estimatedMovementCost};
								movableSpots.add(moves);
							}
						}
					}
				}
			}
		}

		pathScore = movableSpots.get(0)[2] + movableSpots.get(0)[3];
		for(int i = 0; i < movableSpots.size(); i++){
			if(movableSpots.get(i)[2] + movableSpots.get(i)[3] < pathScore){
				pathScore = movableSpots.get(i)[2] + movableSpots.get(i)[3];
				bestPath = i;
			}
		}
		moveTo((movableSpots.get(bestPath)[0] * 32) + getx(), (movableSpots.get(bestPath)[1] * 32) + gety());
	}

	public void moveTo(int x, int y){
		newX = x;
		newY = y;
		//moves to the new spot one direction at a time. allows for diagonals :)
		//up
		
			if(newY < gety()){
				up = true;
				setMoving(true);
				directionMoving = "North";
			}
	
			//down
			if(newY > gety()){
				down = true;
				setMoving(true);
				directionMoving = "South";
				
			}
			
			if(newX > getx()){
				right = true;
				setMoving(true);
				directionMoving = "East";
			}
			
			if(newX < getx()){
				left = true;
				setMoving(true);
				directionMoving = "West";
			}
			
	}

	public void stopMoving(){
			//is the character currently moving?
			if(!directionMoving.equals("N")){
				switch(directionMoving){
				
				case "North":
					if(newY > gety()){
						setPosition(getx(), newY + 4);
						up = false;
						setMoving(false);
						directionMoving = "N";
						
					}
					break;
				case "South":
					if(newY < gety()){
						setPosition(getx(), newY - 4);
						down = false;
						setMoving(false);
						directionMoving = "N";
					}
					break;
				case "East":
					if(newX < getx()){
						setPosition(newX - 4, gety());
						right = false;
						setMoving(false);
						directionMoving = "N";
					}
					break;
				case "West":
					if(newX > getx()){
						setPosition(newX + 4, gety());
						left = false;
						setMoving(false);
						directionMoving = "N";
						
					}
					break;
				}
			}
		}
	
	public void getNextPosition(){
			if(up){
				dy -= moveSpeed;
				if(dy < -maxSpeed){
					dy = -maxSpeed;
				}
			}else if(down){
				dy += moveSpeed;
				if(dy > maxSpeed){
					dy = maxSpeed;
				}
			} else {
				if(dy > 0){
					dy -= stopSpeed;
					if(dy < 0){
						dy = 0;
					}
				}else if(dy < 0){
					dy += stopSpeed;
					if(dy > 0){
						dy = 0;
					}
				}	
			}
			if(right){
				dx += moveSpeed;
				if(dx > -maxSpeed){
					dx = maxSpeed;
				}
			}else if(left){
				dx -= moveSpeed;
				if(dx < maxSpeed){
					dx = -maxSpeed;
				}
			}else{
				if(dx > 0){
					dx -= stopSpeed;
					if(dx < 0){
						dx = 0;
					}
				}else if(dx < 0){
					dx += stopSpeed;
					if(dx > 0){
						dx = 0;
					}
				}
			}
		}
		
	public void update(){
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(dy == 0 && dx == 0){
			setMoving(false);
		}
		
		if(down){	
			if(currentAction != DOWN){
				currentAction = DOWN;
				animation.setFrames(sprites.get(DOWN));
				animation.setDelay(200);
				height = 52;
				width = 34;
			}
		}else if(up){
			if(currentAction != UP){
				currentAction = UP;
				animation.setFrames(sprites.get(UP));
				animation.setDelay(200);
				height = 52;
				width = 34;
			}
		}else if(left){
			if(currentAction != LEFT){
				currentAction = LEFT;
				animation.setFrames(sprites.get(LEFT));
				animation.setDelay(200);
				width = 34;
				height = 58;
			}
		}else if(right){
			if(currentAction != RIGHT){
				currentAction = RIGHT;
				animation.setFrames(sprites.get(RIGHT));
				animation.setDelay(200);
				width = 34;
				height = 58;
			}
		}
		
		animation.update();
		stopMoving();
	}
		
	public void draw(Graphics2D g){
		checkTileMapCollision();
//
//		Rectangle r = getRectangle();
//		r.x += xmap;
//		r.y += ymap;
//		g.draw(r);

		super.draw(g);
	}

	public boolean isMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

}
