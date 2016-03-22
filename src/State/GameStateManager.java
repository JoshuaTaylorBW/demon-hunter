package State;
import java.awt.event.MouseEvent;

import GameStates.DungeonState;
import GameStates.LoadingState;

public class GameStateManager {

	public static boolean printing = false;
	
	private GameState[] gameStates;
	private int currentState;
	
	public int score = 0;
	
	public int currentLevel = 5;
	
	
	public static final int NUMGAMESTATES = 20;
	public static final int LOADINGSTATE = 0;
	public static final int LEVEL1STATE = 1;
	
	public GameStateManager(){
		
		gameStates = new GameState[NUMGAMESTATES];
		
		currentState = LOADINGSTATE;
		loadState(currentState);
		
	}
	
	private void loadState(int state){
		if(state == LEVEL1STATE){
			gameStates[state] = new DungeonState(this);
		}else if(state == LOADINGSTATE){
			gameStates[state] = new LoadingState(this);
		}
	}
	
	private void unloadState(int state){
		gameStates[state] = null;
	}
	
	public void nextLevel(){
		currentLevel++;
		unloadState(currentState);
		currentState = LOADINGSTATE;
		loadState(LOADINGSTATE);
	}
	
	public int getCurrentLevel(){return currentLevel;}
	
	public void setState(int state){
		unloadState(currentState);
		currentState = state;
		loadState(currentState);
	}
	
	public void mouseClicked(MouseEvent e){
		try{
			gameStates[currentState].mouseClicked(e);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void update(){
		try{
		gameStates[currentState].update();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void draw(java.awt.Graphics2D g){
		try{
		gameStates[currentState].draw(g);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//adds score to the player. amount is the amount of points we add to the score
	public void addScore(int amount){
		score += amount;
	}
	public int getScore(){
		return score;
	}
}
