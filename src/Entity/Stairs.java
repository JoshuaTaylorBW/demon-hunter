package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import State.GameStateManager;
import TileMap.TileMap;

public class Stairs extends MapObject{

	public GameStateManager stateManager;	//Our state manager. allows us to go back to loading state
	
	TileMap tm;					//The Tilemap this is placed on
	
	private ArrayList<BufferedImage[]> sprites;		//Arraylist that holds sprites arrays
	private final int[] numFrames = {1};
	
	public Stairs(TileMap tm){
		super(tm);
		this.tm = tm;
		
		width = 32;
		height = 32;
		cwidth = 16;
		cheight = 16;
		
		try{
			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream(
							"/Characters/StairSheet.png"));
			
			sprites = new ArrayList<BufferedImage[]>();
			BufferedImage[] bi = new BufferedImage[numFrames[0]];
				for(int x = 0; x < numFrames[0]; x++){
						bi[x] = spritesheet.getSubimage(
	 							0,
	 							0,
	 							32,
	 							32);
			}
				sprites.add(bi);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites.get(0));
		animation.setDelay(400);
		
		}

	public void setStateManager(GameStateManager gsm){
		stateManager = gsm;
	}
	
	//Adds a level to the gsm's currentLevel and calls gsm's nextLevel() method
	public void nextLevel(){
		stateManager.nextLevel();
	}
	
	public void draw(Graphics2D g){
		setMapPosition();
		super.draw(g);
		
	}
	
}
