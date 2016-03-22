package GameStates;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import State.GameState;
import State.GameStateManager;
import TileMap.Background;

public class LoadingState extends GameState{

	private long drawTime;
	private Background bg;	
	
	public LoadingState(GameStateManager gsm){
		this.gsm = gsm;
		init();
	}
	
	public void init(){
		bg = new Background("/Background/LoadingScreen.png", 0);
		bg.setPosition(0, 0);

		
		drawTime = System.nanoTime();
	}
	
	public void update(){
		bg.update();
		long elapsed = (System.nanoTime() - drawTime) / 1000000;
		if(elapsed > 1){
			gsm.setState(1);
		}
	}
	
	public void draw(Graphics2D g){
		g.setColor(Color.YELLOW);
		g.fillRect(100, 10, 100, 100);
		bg.draw(g);
	}

	
	public void handleInput() {
		
	}

	
	public void mouseClicked(MouseEvent e) {
			
	}
	
}
