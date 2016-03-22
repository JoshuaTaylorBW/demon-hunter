package Entity.Spots;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entity.Animation;
import Entity.Luna;
import Entity.MapObject;
import TileMap.TileMap;

public class MovableSpot extends MapObject{

	public Luna luna;
	public int lunaX, lunaY;
	public int lunaEndurance;
	public TileMap tm;
	private ArrayList<BufferedImage[]> sprites;		//Arraylist that holds sprites arrays
	private final int[] numFrames = {
			1, 1, 1, 1
	};
	
	public MovableSpot(TileMap tm, Luna luna){
		super(tm);
		this.luna = luna;
		lunaX = luna.getx();
		lunaY = luna.gety();
		
		width = 32;
		height = 32;
		cwidth = 32;
		cheight = 32;
		facingRight = false;
		facingLeft = false;
		try{
			
			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream(
							"/Tileset/Singles/MovableAreas.png"));
			
			sprites = new ArrayList<BufferedImage[]>();
			for(int y = 0; y < 1; y++){
				//creates the image array to hold the individual sprites
				BufferedImage[] bi = new BufferedImage[numFrames[y]];
				
						bi[0] = spritesheet.getSubimage(
	 							0,
	 							0,
	 							32,
	 							32);
						
						sprites.add(bi);
					}
				
			}catch(Exception e){
			e.printStackTrace();
		}
		animation = new Animation();
		currentAction = 0;
		animation.setFrames(sprites.get(0));
		animation.setDelay(400);
		
	}
	
	
	
	public void draw(Graphics2D g){
		setMapPosition();
		super.draw(g);
	}
	
}
