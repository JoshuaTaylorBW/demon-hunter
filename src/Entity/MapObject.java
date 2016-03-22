package Entity;

import java.awt.Rectangle;

import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;


public abstract class MapObject {
	
	// tile stuff
	protected TileMap tileMap;
	public boolean isTopLeft() {
		return topLeft;
	}

	protected int tileSize;
	protected double xmap;
	protected double ymap;
	
	// position and vector
	protected double x;
	protected double y;
	protected double dx;
	protected double dy;
	
	// dimensions
	protected int width;
	protected int height;
	
	// collision box
	protected int cwidth;
	protected int cheight;
	
	// collision
	protected int currRow;
	protected int currCol;
	protected double xdest;
	protected double ydest;
	protected double xtemp;
	protected double ytemp;
	protected boolean topLeft;
	protected boolean topRight;
	protected boolean bottomLeft;
	protected boolean bottomRight;
	protected boolean north;
	protected boolean east;
	protected boolean south;
	protected boolean west;
	protected boolean here;
	
	// animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;
	protected boolean facingLeft;
	protected boolean facingUp;
	protected boolean facingDown;
	
	// movement
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;
	
	// movement attributes
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpStart;
	protected double stopJumpSpeed;
	
	// constructor
	public MapObject(TileMap tm) {
		tileMap = tm;
		tileSize = tm.getTileSize();
		animation = new Animation();
	}
	
	public boolean intersects(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();
		return r1.intersects(r2);
	}
	
	public boolean intersects(Rectangle r) {
		return getRectangle().intersects(r);
	}
	
	public boolean contains(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();
		return r1.contains(r2);
	}
	
	public boolean contains(Rectangle r) {
		return getRectangle().contains(r);
	}
	
	public Rectangle getRectangle() {
		if(!facingRight && !facingLeft){
			return new Rectangle(
					(int)x - cwidth,
					(int)y - cheight,
					cwidth,
					cheight
			);
		}
		return new Rectangle(
				(int)x - cwidth,
				(int)y - cheight,
				cwidth,
				cheight
		);
	}
	
	public void calculateCorners(double x, double y) {
		int leftTile = (int)(x - cwidth / 2) / tileSize;
		int rightTile = (int)(x + cwidth / 2 - 1) / tileSize;
		int topTile = (int)(y - cheight / 2) / tileSize;
		int bottomTile = (int)(y + cheight / 2 - 1) / tileSize;
		if(topTile < 0 || bottomTile >= tileMap.getNumRows() ||
			leftTile < 0 || rightTile >= tileMap.getNumCols()) {
			topLeft = topRight = bottomLeft = bottomRight = false;
			return;
		}
		int tl = tileMap.getType(topTile, leftTile);
		int tr = tileMap.getType(topTile, rightTile);
		int bl = tileMap.getType(bottomTile, leftTile);
		int br = tileMap.getType(bottomTile, rightTile);
		topLeft = tl == Tile.BLOCKED; //topLeft = true if(tl == tile.blocked);
		topRight = tr == Tile.BLOCKED;
		bottomLeft = bl == Tile.BLOCKED;
		bottomRight = br == Tile.BLOCKED;
	}
	
	public void calculateCardinals(double x, double y){
		int leftTile = (int)(x - cwidth / 2) / tileSize;
		int rightTile = (int)(x + cwidth / 2 - 1) / tileSize;
		int topTile = (int)(y - cheight / 2) / tileSize;
		int bottomTile = (int)(y + cheight / 2 - 1) / tileSize;
		int nowTileX = (int)(x + cwidth / 2) / tileSize;
		int nowTileY = (int)(y + cheight / 2) / tileSize;
		
		int tops = tileMap.getType(topTile, nowTileX);
		int right = tileMap.getType(nowTileY, rightTile);
		int down = tileMap.getType(bottomTile, rightTile);
		int left = tileMap.getType(nowTileY, leftTile);
		int now = tileMap.getType(currRow, currCol);
		north = tops == Tile.BLOCKED;
		east = right == Tile.BLOCKED;
		south = down == Tile.BLOCKED;
		west = left == Tile.BLOCKED;
		here = now == Tile.BLOCKED;
		
	}
	
	public void checkTileMapCollision() {
		
		currCol = (int)x / tileSize;
		currRow = (int)y / tileSize;
		
		xdest = x + dx;
		ydest = y + dy;
		
		xtemp = x;
		ytemp = y;
		
		calculateCorners(x, ydest);
		calculateCardinals(x, y);
		if(dy < 0){
			if((topLeft || (topRight && getScreenY() > 50)) && 
					(getScreenX() < 608 && getScreenX() > 32)){
				dy = 0;
				ytemp = currRow * tileSize + cheight;
				up = false;
			} else {
				ytemp += dy;
			}
		}
		
		if(dy > 0){
			if((bottomLeft || bottomRight && getScreenY() < 447) &&
					getScreenX() < 608 && getScreenX() > 32){
				dy = 0;
				ytemp = (currRow + 2) * tileSize - cheight;
				down = false;
			}else{
				ytemp += dy;
			}
		}	
		calculateCorners(xdest, y);
		if(dx < 0){
			if(topLeft && dy == 0){
				dx = 0;
				xtemp = currCol * tileSize + cwidth;
				left = false;
			} else {
				xtemp += dx;
			}
		}
		if(dx > 0){
			if(topRight && dy == 0){
				dx = 0;
				xtemp = (currCol + 1) * tileSize - cwidth; 
				right = false;
			}else{
				xtemp += dx;
			}
		}
	}

	
	
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void setMapPosition() {
		xmap = tileMap.getx();
		ymap = tileMap.gety();
	}
	
	public void setLeft(boolean b) { left = b; }
	public void setRight(boolean b) { right = b; }
	public void setUp(boolean b) { up = b; }
	public void setDown(boolean b) { down = b; }
	public void setJumping(boolean b) { jumping = b; }
	
	public boolean notOnScreen() {
		return x + xmap + width < 0 ||
			x + xmap - width > GamePanel.WIDTH ||
			y + ymap + height < 0 ||
			y + ymap - height > GamePanel.HEIGHT;
	}
	
	public void draw(java.awt.Graphics2D g) {
		setMapPosition();
			g.drawImage(
				animation.getImage(),
				(int)(x + xmap - width),
				(int)(y + ymap - height),
				null
			);
		// draw collision box
		
//		Rectangle r = getRectangle();
//		r.x += xmap;
//		r.y += ymap;
//		g.draw(r);
	}
	
	public void setTopLeft(boolean topLeft) {
		this.topLeft = topLeft;
	}
	public boolean isTopRight() {
		return topRight;
	}
	public void setTopRight(boolean topRight) {
		this.topRight = topRight;
	}
	public boolean isBottomLeft() {
		return bottomLeft;
	}
	public void setBottomLeft(boolean bottomLeft) {
		this.bottomLeft = bottomLeft;
	}
	public boolean isBottomRight() {
		return bottomRight;
	}
	public void setBottomRight(boolean bottomRight) {
		this.bottomRight = bottomRight;
	}
	public boolean isNorth() {
		return north;
	}
	public void setNorth(boolean north) {
		this.north = north;
	}
	public boolean isEast() {
		return east;
	}
	public void setEast(boolean east) {
		this.east = east;
	}
	public boolean isSouth() {
		return south;
	}
	public void setSouth(boolean south) {
		this.south = south;
	}
	public boolean isWest() {
		return west;
	}
	public void setWest(boolean west) {
		this.west = west;
	}
	public int getx() { return (int)x; }
	public void setx(int newX) {x = newX;}
	public int gety() { return (int)y; }
	public void sety(int newY) {y = newY;}
	public double getScreenX(){return x + xmap;}
	public double getScreenY(){return ymap + y;}
	public int getCurrCol(){return currCol;}
	public int getCurrRow(){return currRow;}
	public double getDX() {return dx;}
	public double getDY(){return dy;}
	public void setDy(double y){dy = y;}
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getCWidth() { return cwidth; }
	public int getCHeight() { return cheight; }
	public boolean isFacingUp() { return facingUp; }
	public boolean isFacingRight() { return facingRight; }
	public boolean isFacingDown() { return facingDown; }
	public boolean isFacingLeft() { return facingLeft; }
	public boolean isHere() {
		return here;
	}
	public void setHere(boolean here) {
		this.here = here;
	}

}