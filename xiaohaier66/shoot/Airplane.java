package xiaohaier66.shoot;

import java.util.Random;

import xiaohaier66.shoot.ShootGame;

public class Airplane extends FlyingObject implements Enemy {
	private int speed = 2;
	/*public int getScore() {
		return 0;
	}*/
	public Airplane() {
		this.image = ShootGame.airplane;
		width = image.getWidth();
		height = image.getHeight();
		y = -height;
		x= (int)(Math.random()*(ShootGame.WIDTH - width));
		//y = 100;
		//x = 100;
	
	}
	
	@Override
	public void step() {
		y += speed;
	}
	
	public int getScore() {
		return 5;
	}
	
	@Override
	public boolean outOfBounds() {
		return y > ShootGame.HEIGHT;
	}
}
