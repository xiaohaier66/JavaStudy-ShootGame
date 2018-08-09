package xiaohaier66.shoot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;

public class ShootGame extends JPanel {
	public static final int WIDTH = 400;
	public static final int HEIGHT = 654;
	
	public static BufferedImage background;
	public static BufferedImage start;
	public static BufferedImage airplane;
	public static BufferedImage bee;
	public static BufferedImage bullet;
	public static BufferedImage hero0;
	public static BufferedImage hero1;
	public static BufferedImage pause;
	public static BufferedImage gameover;
	
	private FlyingObject[] flyings = {};
	private Bullet[] bullets = {};
	private Hero hero = new Hero();
	
	private int score = 0;
	
	private int state;
	public static final int START = 0;
	public static final int RUNNING = 1;
	public static final int PAUSE = 2;
	public static final int GAME_OVER = 3;

	static {
		try {
			background = ImageIO.read(ShootGame.class.getResource("background.png"));
			airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ShootGame() {
//		flyings = new FlyingObject[2];
//		flyings[0] = new Airplane();
//		flyings[1] = new Bee();
//		bullets = new Bullet[1];
//		bullets[0] = new Bullet(200,350);
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, null);
		paintHero(g);
		paintBullets(g);
		paintFlyingObjects(g);
		paintState(g);
		
		paintScore(g);
	}
	
	public static FlyingObject nextOne() {
		Random random = new Random();
		int type = random.nextInt(20);
		if (type == 0) {
			return new Bee();
		}else {
			return new Airplane();
		}
	}
	
	int flyEnteredIndex = 0;
	public void enterAction() {
		flyEnteredIndex++;
		if (flyEnteredIndex % 40 ==0) {
			FlyingObject obj = nextOne();
			flyings = Arrays.copyOf(flyings, flyings.length+1);
			flyings[flyings.length - 1] = obj;
		}
	}
	
	public void stepAction() {
		for(int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			f.step();
		}
		
		for(int i =0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			b.step();
		}
		hero.step();
	}
	
	public void paintHero(Graphics g) {
		g.drawImage(hero.getImage(), hero.getX(), hero.getY(), null);
	}
	
	public void paintBullets(Graphics g) {
		for(int i = 0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			g.drawImage(b.getImage(), b.getX(),b.getY(),null);
			
		}
	}
	
	public void paintFlyingObjects(Graphics g) {
		for(int i =0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			g.drawImage(f.getImage(), f.getX(), f.getY(), null);
		}
	}
	
	private Timer timer;
	private int intervel = 1000/100;
	public void action() {
		
		//鼠标监听事件
		MouseAdapter l = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if(state == RUNNING) {
					int x = e.getX();
					int y = e.getY();
					hero.moveTo(x, y);
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(state == PAUSE) {
					state = RUNNING;
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				if(state != GAME_OVER) {
					state = PAUSE;
				}
			}
			
			
			@Override
			public void mouseClicked(MouseEvent e) {
				switch(state) {
				case START:
					state = RUNNING;
					break;
				case GAME_OVER:
					flyings = new FlyingObject[0];
					bullets = new Bullet[0];
					hero = new Hero();
					score = 0;
					state = START;
					break;
				}
			}
		};
		this.addMouseMotionListener(l);
		this.addMouseListener(l);
		
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(state == RUNNING) {
					enterAction();
					stepAction();
					
					shootAction();
					
					bangAction();
					
					outOfBoundsAction();
					
					checkGameOverAction();
				}
				
				
				repaint();
			}
		}, (long)intervel,(long)intervel);
		

	}
	
	public void bangAction() {
		for(int i = 0;i<bullets.length;i++) {
			Bullet b = bullets[i];
			bang(b);
		}
	}
	
	public void bang(Bullet bullet) {
		int index = -1;
		for(int i =0 ; i< flyings.length;i++) {
			FlyingObject obj = flyings[i];
			if(obj.shootBy(bullet)) {
				index = i;
				break;
			}
		}
		if(index != -1) {
			FlyingObject one = flyings[index];
			FlyingObject temp = flyings[index];
			
			flyings[index] = flyings[flyings.length -1 ];
			flyings[flyings.length - 1] = temp;
			
			flyings = Arrays.copyOf(flyings, flyings.length - 1);
			
			if( one instanceof Enemy) {
				Enemy e = (Enemy) one;
				score += e.getScore();
			}
			
			if(one instanceof Award) {
				Award a = (Award) one;
				int type = a.getType();
				switch(type) {
				case Award.DOUBLE_FIRE:
					hero.addDoubleeFire();
					break;
				case Award.LIFE:
					hero.addLife();
					break;
				}
			}
		}
	}
	
	public void paintScore(Graphics g) {
		int x = 10;
		int y = 25;
		Font font = new Font(Font.SANS_SERIF,Font.BOLD,14);
		g.setColor(new Color(0x3A3B3B));
		g.setFont(font);
		g.drawString("SCORE:"+score, x, y);
		y+= 20;
		g.drawString("LIFE:"+hero.getLife(), x, y);
	}
	
	public void paintState(Graphics g) {
		switch(state) {
		case START:
			g.drawImage(start, 0, 0, null);
			break;
		case PAUSE:
			g.drawImage(pause,0,0,null);
			break;
		case GAME_OVER:
			g.drawImage(gameover,0,0,null);
			break;
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Fly");
		ShootGame game = new ShootGame();
		
		frame.add(game);
		frame.setSize(WIDTH, HEIGHT);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.action();
	}
	
	int shootIndex = 0;
	public void shootAction() {
		shootIndex++;
		if(shootIndex % 30 == 0) {
			Bullet[] bs = hero.shoot();
			bullets = Arrays.copyOf(bullets, bullets.length+bs.length);
			
			System.arraycopy(bs, 0, bullets, bullets.length - bs.length,bs.length);
		}
	}
	
	public void outOfBoundsAction() {
		int index = 0;
		FlyingObject[] flyingLives = new FlyingObject[flyings.length];
		for(int i = 0; i< flyings.length; i++) {
			FlyingObject f = flyings[i];
			if(!f.outOfBounds()) {
				flyingLives[index++] = f;
			}
		}
		flyings = Arrays.copyOf(flyingLives, index);
		
		index = 0;
		Bullet[] bulletLives = new Bullet[bullets.length];
		for(int i =0; i < bullets.length;i++) {
			Bullet b = bullets[i];
			if(!b.outOfBounds()) {
				bulletLives[index++] = b;
			}
		}
		bullets = Arrays.copyOf(bulletLives, index);
	}
	
	public boolean isGameOver() {
		for(int i = 0; i < flyings.length; i++) {
			int index = -1;
			FlyingObject obj = flyings[i];
			if(hero.hit(obj)) {
				hero.subtractLife();
				hero.setDoubleFire(0);
				index = i;
			}
			if(index != -1) {
				FlyingObject t = flyings[index];
				flyings[index] = flyings[flyings.length -1];
				flyings[flyings.length - 1]= t;
				flyings = Arrays.copyOf(flyings, flyings.length -1 );
				
			}
		}
		return hero.getLife() <= 0;
	}
	
	public void checkGameOverAction() {
		if(isGameOver()) {
			state = GAME_OVER;
		}
	}
	
	
}
