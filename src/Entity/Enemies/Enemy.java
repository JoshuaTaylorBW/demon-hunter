package Entity.Enemies;

import Entity.MapObject;
import Entity.Luna;
import TileMap.TileMap;
//IMPORTANT
//WARNING: DO NOT SET THE POSITION OF ANY ENEMIES TO BE TOUCHING THE BORDERS OF THE MAP
//ON THE SOUTH OR EAST SIDE.
//THEY MUST BE AT LEAST ONE TILE AWAY FROM THESE SIDES

public class Enemy extends MapObject{

	protected int health;
	protected int maxHealth;
	protected int deathScore;
	protected boolean dead;
	protected int damage;
	
	public String name;
	
	protected boolean invincible;
	
	protected boolean flinching;
	protected long flinchTimer;
	
	public Enemy(TileMap tm) {
		super(tm);
	}
	//is this enemy dead?
	public boolean isDead(){return dead;}
	//how much damage does the enemy inflict?
	public int getDamage(){return damage;}
	//how many points does the enemy return when he is killed?
	public int getScore(){return deathScore;}
	
	public void setMaxHealth(int health){
		maxHealth = health;
	}
	
	public void addScore(int score){
		//Level1State.score += deathScore;
	}
	
	//immediately kill this enemy
	public void kill(){dead = true;}
	
	public String getName(){
		return name;
	}
	
	public void move(){}
	
	public double distanceToPlayer(Luna luna){
		
		//exes = enemy x - player x
		//whys = enemy y - player y
		double exes = (getx() - luna.getx());
		double whys = (gety() - luna.gety());
		
		//distance formula is d = square root(a^2 + b^2)
		//this line is (a^2 + b^2)
		double inside = (exes * exes) + (whys * whys);
		//this line is the square root
		double distance = Math.sqrt(inside);
		
		return distance;
	}
	
	public void hit(int damage){
		if(!invincible){
		if(dead || flinching) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) dead = true;
		flinching = true;
		flinchTimer = System.nanoTime();
		}
	}
	
	public void update(){
		
	}
	
}
