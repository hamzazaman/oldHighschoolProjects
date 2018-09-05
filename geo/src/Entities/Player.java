package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

public class Player implements Entities {
	public static final long SHOTS_PER_SEC = 10;
	public static final long SHOTS_PERIOD_NSEC = 1000000000L / SHOTS_PER_SEC;
	public long timeShot = System.nanoTime();
	public long timeElapse;
	// public static final long RELOAD_PERIOD_NSEC = 100000000L;
	public long timeReload = System.nanoTime();
	public static final int WIDTH = 20;
	public static final int HEIGHT = 20;
	double x = 400;
	double y = 300;
	int lastMouseX = (int) Math.round(x);
	int lastMouseY = (int) Math.round(y - 100);
	double angle = 0;
	public int maxX;
	public int maxY;
	// To Store input
	boolean up = false;
	boolean down = false;
	boolean right = false;
	boolean left = false;
	boolean space = false;
	boolean cheat = false;
	private boolean shoot = false;
	boolean exists = true;
	private int lives = 3;
	private int shots = 60;
	private boolean shield = false;
	int score = 0;
	int hitanimation = 0;
	Color hitcolor = Color.GREEN;
	

	Shape r = new Rectangle();
	Rectangle p = new Rectangle();

	public Player(int mx, int my) {
		maxX = mx;
		maxY = my;
		x = mx / 2;
		y = my / 2;
		lastMouseX = (int) Math.round(x);
		lastMouseY = (int) Math.round(y);
	}

	@Override
	public void render(Graphics2D g) {
		if(hitanimation > 0 && hitcolor == Color.RED){
			g.setColor(new Color(225, 0, 0, 40));
			g.fillRect(-10, -10, maxX + 20, maxY + 20);
			//g.fillOval((int) Math.round(x - WIDTH / 2) - 40, (int) Math.round(y - HEIGHT / 2) - 40, WIDTH + 80, HEIGHT + 80);
		}
		g.rotate(angle, x, y);
		g.setColor(Color.RED);
		g.drawRect((int) Math.round(x - WIDTH / 2),
				(int) Math.round((y - HEIGHT / 2) - 3), WIDTH, HEIGHT + 3);

		//g.setColor(Color.BLACK);
		//g.fillRoundRect((int) Math.round(x - WIDTH / 2),
			//	(int) Math.round(y - HEIGHT / 2), WIDTH, HEIGHT, 0, 0);
		g.setColor(Color.GREEN);
		g.drawRect((int) Math.round(x - WIDTH / 2),
				(int) Math.round(y - HEIGHT / 2), WIDTH, HEIGHT);
		
		if(hitanimation > 0){
			if(hitanimation%15 == 0){
				if(hitcolor == Color.RED){
					hitcolor = Color.GREEN;
				}else {
					hitcolor = Color.RED;
					
				}
			}
			g.setColor(hitcolor);
			g.drawRect((int) Math.round(x - WIDTH / 2),
					(int) Math.round(y - HEIGHT / 2), WIDTH, HEIGHT);
			
			hitanimation--;
		}
		
		if (isShield() == true) {
			g.setColor(Color.MAGENTA);
			g.drawRect((int) Math.round(x - WIDTH / 2),
					(int) Math.round(y - HEIGHT / 2), WIDTH, HEIGHT);

		}
		g.setTransform(new AffineTransform());
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setX(double nx) {
		x = nx;

	}

	@Override
	public void setY(double ny) {
		y = ny;

	}

	public void up(int length) {
		if (y - HEIGHT / 2 > 0)
			setY(getY() - length);
	}

	public void down(int length) {
		if (y + HEIGHT / 2 < maxY)
			setY(getY() + length);
	}

	public void left(int length) {
		if (x - WIDTH / 2 > 0)
			setX(getX() - length);
	}

	public void right(int length) {
		if (x + WIDTH / 2 < maxX)
			setX(getX() + length);
	}

	@Override
	public void move() {
		if (up)
			up(7);
		if (down)
			down(7);
		if (left)
			left(7);
		if (right)
			right(7);
		if (space) {
			if (getShots() > 0
					&& System.nanoTime() - timeShot > SHOTS_PERIOD_NSEC) {
				timeShot = System.nanoTime();
				timeReload = System.nanoTime();
				setShoot(true);
				setShots(getShots() - 1);
			} else {
				setShoot(false);
			}
		} else {
			setShoot(false);
		}
		/*
		 * if (System.nanoTime() - timeReload > RELOAD_PERIOD_NSEC && shots < 3)
		 * { timeReload = System.nanoTime(); shots++; }
		 */
		findAngle(lastMouseX, lastMouseY);
	}

	public void press(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_W:
			up = true;
			break;
		case KeyEvent.VK_S:
			down = true;
			break;
		case KeyEvent.VK_A:
			left = true;
			break;
		case KeyEvent.VK_D:
			right = true;
			break;
		case KeyEvent.VK_SPACE:
			space = true;
			break;
		case KeyEvent.VK_LEFT:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		case KeyEvent.VK_UP:
			up = true;
			break;
		case KeyEvent.VK_DOWN:
			down = true;
			break;
		case KeyEvent.VK_BACK_SPACE:
			cheat = true;
			break;
		}

	}

	public void released(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_W:
			up = false;
			break;
		case KeyEvent.VK_S:
			down = false;
			break;
		case KeyEvent.VK_A:
			left = false;
			break;
		case KeyEvent.VK_D:
			right = false;
			break;
		case KeyEvent.VK_SPACE:
			space = false;
			break;
		case KeyEvent.VK_LEFT:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		case KeyEvent.VK_UP:
			up = false;
			break;
		case KeyEvent.VK_DOWN:
			down = false;
			break;
		case KeyEvent.VK_BACK_SPACE:
			cheat = false;
			break;
		}
	}

	public void mouseMoved(int mouseX, int mouseY) {
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		// findAngle(mouseX, mouseY);
	}

	public void findAngle(int mouseX, int mouseY) {
		angle = Math.atan2(y - mouseY, x - mouseX) - Math.PI / 2;
		// System.out.println(angle);
	}

	@Override
	public boolean collide(Entities ent) {
		// if (ent.getClass() == ExtraLife.class || ent.getClass() ==
		// PowerUp.class || ent.getClass() == Bullet.class || ent.getClass() ==
		// AmmoRefill.class)
		// return false;
		if (ent.getClass() != Barricade.class && ent.getClass() != Box.class) {
			return false;
		}
		if (cheat == true)
			return false;
		AffineTransform transform = new AffineTransform();
		r = new Rectangle((int) Math.round(x - WIDTH / 2), (int) Math.round(y
				- HEIGHT / 2), WIDTH, HEIGHT);
		transform.rotate(getAngle(), x, y);
		r = transform.createTransformedShape(r);
		p = new Rectangle((int) Math.round(ent.getX() - ent.getWidth() / 2),
				(int) Math.round(ent.getY() - ent.getHeight() / 2),
				ent.getWidth(), ent.getHeight());
		AffineTransform t3 = new AffineTransform();
		t3.rotate(-ent.getAngle(), ent.getX(), ent.getY());
		r = t3.createTransformedShape(r);
		if (r.intersects(p)) {
			if (isShield() == true) {
				setShield(false);
				return true;
			}
			if (getLives() > 0) {
				hitanimation = 60;
				setLives(getLives() - 1);
			} else {
				exists = false;
			}

			return true;
		}
		return false;
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public boolean exist() {
		return exists;
	}

	public void powerUp(int power) {
		switch (power) {
		case 0:
			setShield(true);
			break;
		case 1:
			setShots(getShots() + 30);
			if (getShots() > 60)
				setShots(60);
			break;
		case 2:
			setLives(getLives() + 1);
			if (getLives() > 3)
				setLives(3);
			break;
		default:
			System.out.println("error");
		}
	}

	@Override
	public double getAngle() {

		return angle;
	}

	@Override
	public void setAngle(double newAngle) {
		angle = newAngle;

	}

	public int getLives() {
		return lives;
	}

	void setLives(int lives) {
		this.lives = lives;
	}

	public boolean isShield() {
		return shield;
	}

	void setShield(boolean shield) {
		this.shield = shield;
	}

	public int getShots() {
		return shots;
	}

	void setShots(int shots) {
		this.shots = shots;
	}

	public boolean isShoot() {
		return shoot;
	}

	void setShoot(boolean shoot) {
		this.shoot = shoot;
	}
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
