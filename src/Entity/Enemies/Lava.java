package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entity.Animation;
import Entity.Luna;
import TileMap.TileMap;


public class Lava extends Enemy{
	private int health;								//Current health
	private int maxHealth;							//Maximum potential health
	private int maxEndurance = 7;					//how many things Luna can do every turn
	private int enduranceLeft;						//how many things Luna can still do in the current turn
	
	private TileMap tiley;							//the tilemap the object is currently on

	private boolean dead;							//is the object dead?
	
	private Luna myLuna;

	//things that relate to drawing the character
	private ArrayList<BufferedImage[]> sprites;		//Arraylist that holds sprites arrays
	private final int[] numFrames = {
			4
	};
	public Lava(TileMap tm) {
		super(tm);
		name = "Lava";
		
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

		enduranceLeft = maxEndurance;

		maxHealth = 1;
		health = 1;

		try{

			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream(
							"/Tileset/Singles/Lava.png"));

			sprites = new ArrayList<BufferedImage[]>();
			BufferedImage[] bi = new BufferedImage[numFrames[0]];
				for(int x = 0; x < numFrames[0]; x++){
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
	
	public void update(){
		checkTileMapCollision();
		animation.update();
	}
	
	public void draw(Graphics2D g){
		checkTileMapCollision();
	//
//		Rectangle r = getRectangle();
//		r.x += xmap;
//		r.y += ymap;
//		g.draw(r);
//	
		super.draw(g);
	}
	
}
