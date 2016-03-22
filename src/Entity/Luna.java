package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entity.Enemies.Enemy;
import TileMap.TileMap;

public class Luna extends MapObject{

	private int health;								//Current health
	//private int maxHealth;							//Maximum potential health
	
	private int realMaxEndurance = 2;				/*this is what the endurance really is. we sometimes have to change the maxEndurance
													so that she can move around easier when there are no enemies on screen*/
	private int maxEndurance = 2;					//how many things Luna can do every turn
	private int enduranceLeft;						//how many things Luna can still do in the current turn
	
	private double initialSpeed;
	
	
	private boolean dead;							//is the object dead?
	
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
	
	//I don't know if we'll really need this. the game is turn based...
	private boolean flinching;						//When the player is hit by an enemy it flinches
	private long flinchTimer;						//How long the player is flinching for
	
	//Also to help with drawing. Which action
	//the character is currently doing
	private static final int RIGHT = 0;
	private static final int LEFT = 1;
	private static final int DOWN = 2;
	private static final int UP = 3;
	
	public Luna(TileMap tm) {
		super(tm);
		width = 34;	//width of the sprite
		height = 52;//height of the sprite
		cwidth = 32;//
		cheight = 32;
		
		moveSpeed = .5;
		maxSpeed = 2.0;
		initialSpeed = maxSpeed;
		stopSpeed = 200;
		fallSpeed = 0;
		maxFallSpeed = 0;
		
		facingRight = false;
		facingLeft = false;
		facingUp = false;
		facingDown = true;
		
		enduranceLeft = maxEndurance;
		
	//	health = maxHealth = 1;
		
		try{
			
			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream(
							"/Characters/LunaSheet.png"));
			
			sprites = new ArrayList<BufferedImage[]>();
			for(int y = 0; y < 4; y++){
				//creates the image array to hold the individual sprites
				BufferedImage[] bi = new BufferedImage[numFrames[y]];
				for(int x = 0; x < numFrames[y]; x++){
					if(y == 0 || y == 1){
						bi[x] = spritesheet.getSubimage(
	 							x * 28,
	 							y * 52,
	 							28,
	 							52);
					}else if(y == 2){
						bi[x] = spritesheet.getSubimage(
								x * 34,
								104,
								34,
								56);
					}else if(y == 3){
						bi[x] = spritesheet.getSubimage(
								x * 34,
								160,
								34,
								54);
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

	public int getHealth() {return health;}
	public boolean isDead(){return dead;}
	public void kill(){dead = true;}
	public void revive(){dead = false;}
	public boolean isMoving(){return moving;}
	
	public void setDx(double x){
		dx = x;
	}
	
	//Tile based movement stuff
	public void moveTo(MapObject m){
		newX = m.getx();
		newY = m.gety();
		//moves to the new spot one direction at a time. allows for diagonals :)
		//up
		
			if(newY < gety()){
				up = true;
				moving = true;
				directionMoving = "North";
			}
	
			//down
			if(newY > gety()){
				down = true;
				moving = true;
				directionMoving = "South";
				
			}
			
			if(newX > getx()){
				right = true;
				moving = true;
				directionMoving = "East";
			}
			
			if(newX < getx()){
				left = true;
				moving = true;
				directionMoving = "West";
			}
			
	}
	
	public void stopMoving(){
		//is the character currently moving?
		if(!directionMoving.equals("N")){
			switch(directionMoving){
			
			case "North":
				if(newY > gety()){
					setPosition(getx(), newY);
					up = false;
					moving = false;
					directionMoving = "N";
					
				}
				break;
			case "South":
				if(newY < gety()){
					setPosition(getx(), newY);
					down = false;
					moving = false;
					directionMoving = "N";
					
				}
				break;
			case "East":
				if(newX < getx()){
					setPosition(newX, gety());
					right = false;
					moving = false;
					directionMoving = "N";
					
				}
				break;
			case "West":
				if(newX > getx()){
					setPosition(newX, gety());
					left = false;
					moving = false;
					directionMoving = "N";
					
				}
				break;
			}
			
		}
	}
	
	public int getMaxEndurance(){
		return maxEndurance;
	}
	public int getEnduranceLeft(){
		return enduranceLeft;
	}
	public void setCurrentAction(int action){
		currentAction = action;
	}
	public int getCurrentAction(){
		return currentAction;
	}
	public boolean checkFirst(){
		if(!left && !right){
			return true;
		}else{
			return false;
		}
		
	}
	public void setDirectionMoving(String newDirection){directionMoving = newDirection;}
	
	public void attack(Enemy e){
		int xDifference = Math.abs(getx() - e.getx());
		int yDifference = Math.abs(gety() - e.gety());
		System.out.println(xDifference);
		System.out.println(yDifference);
		
		if((xDifference <= 32 && yDifference <= 16) || (yDifference <= 32 && xDifference <= 16)){
			e.hit(1);
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
	
	public void setMaxEndurance(int newEndurance){
		maxEndurance = newEndurance;
		enduranceLeft = maxEndurance;
	}
	public int getInitialMaxEndurance(){
		return realMaxEndurance;
	}
	public void setMoving(boolean moving2){moving  = moving2;}
	
	public void setMaxSpeed(double newMaxSpeed){maxSpeed = newMaxSpeed;}
	public double getInitialMaxSpeed(){
		return initialSpeed;
	}

	
	
	
	public void setSprites(){
		if(facingDown){	
			if(currentAction != DOWN){
			currentAction = DOWN;
			animation.setFrames(sprites.get(DOWN));
			animation.setDelay(100);
			}
		}else if(facingUp){
			if(currentAction != UP){
				currentAction = UP;
				animation.setFrames(sprites.get(UP));
				animation.setDelay(1000);
				}
		}else if(facingRight){
			if(currentAction != RIGHT){
				currentAction = RIGHT;
				animation.setFrames(sprites.get(RIGHT));
				animation.setDelay(100);
			}
		}else if(facingLeft){
			if(currentAction != LEFT){
				currentAction = LEFT;
				animation.setFrames(sprites.get(LEFT));
				animation.setDelay(20);
			}
		}
	}
	
	public void stopAnimation(){
		if(!up && !left && !right && !down){
			animation.setFrames(sprites.get(DOWN));
			animation.setDelay(10000);
		}
	}
	
	public void update(){
		
		
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(dy == 0 && dx == 0){
			moving =false;
		}
		
		if(down){	
			if(currentAction != DOWN){
				currentAction = DOWN;
				animation.setFrames(sprites.get(DOWN));
				animation.setDelay(200);
				height = 56;
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
				animation.setDelay(150 );
				width = 28;
				height = 52;
			}
		}else if(right){
			if(currentAction != RIGHT){
				currentAction = RIGHT;
				animation.setFrames(sprites.get(RIGHT));
				animation.setDelay(150);
				width = 28;
				height = 52;
			}
		}
		
		animation.update();
		stopMoving();
		
		if(right){ facingRight = true; facingUp = false; facingDown = false; facingLeft = false;}
		if(left){facingRight = false; facingUp = false; facingDown = false; facingLeft = true;}
		if(up){ facingUp = true; facingDown = false; facingRight = false; facingLeft = false;}
		if(down){ facingUp = false; facingDown = true; facingRight = false; facingLeft = false;}	
		
	}
	
	public void draw(Graphics2D g){
		setMapPosition();
		
		if(flinching){
			long elapsed =
					(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0){
				return;
			}
		}
		
//		Rectangle r = getRectangle();
//		r.x += xmap;
//		r.y += ymap;
//		g.draw(r);
//		
		super.draw(g);
	}
	
}
