package xiaohaier66.shoot;

public class Bullet extends FlyingObject {
	private int speed = 3;
	
	public Bullet(int x, int y) {
		this.x = x;
		this.y = y;
		this.image = ShootGame.bullet;
	}
	@Override
	public void step() {
		y -= speed;
	}
	@Override
	public boolean outOfBounds() {
		// TODO 自动生成的方法存根
		return y < -height;
	}


}

