package Main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import Entity.MapObject;
import TileMap.TileMap;

public class Mouse extends MapObject {

	public boolean DISPLAY_MOUSE = false;
	
	public Mouse(TileMap tm){
		super(tm);
		width = 5;
		height = 5;
		cwidth = 5;
		cheight = 5;
	}
	
	public void init(){
		
	
		
	}
	
	public void update(){
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
	}
	
	public void mouseClicked(MouseEvent e){
	
		
	}
	
	public void mouseReleased(MouseEvent e){
		
	}
	
	public void draw(Graphics2D g){
		setMapPosition();
		
		if(DISPLAY_MOUSE){
			Rectangle r = new Rectangle(width, height);
			r.setLocation((int)x + (int)xmap, (int)y + (int)ymap);
			g.draw(r);
		}
	}
	
	
}
