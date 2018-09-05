package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class ExtraLife implements Entities{
	double x = 0;
	double y = 0;
	double angle = 0;
	int speed = 4;
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	boolean exists = true;
	
	public ExtraLife(){
		Random r = new Random();
		speed = r.nextInt(5) + 1;
		if(speed == 1 || speed == 2){
			speed = r.nextInt(5) + 1;
			if(speed == 1){
				speed = r.nextInt(5) + 1;
			}
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.drawOval((int)Math.round(x - WIDTH / 2), (int)Math.round(y - HEIGHT / 2), WIDTH, HEIGHT);
		
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

	@Override
	public int getWidth() {
		
		return WIDTH;
	}

	@Override
	public int getHeight() {
		
		return HEIGHT;
	}

	@Override
	public void move() {
		setX(getX() - (Math.sin(angle) * speed));
		setY(getY() + (Math.cos(angle) * speed));
		
	}

	@Override
	public boolean collide(Entities ent) {
		
		if(ent.getClass() != Player.class  && ent.getClass() != Bullet.class)
			return false;
		AffineTransform transform = new AffineTransform();
		Shape r = new Rectangle((int)Math.round(x - WIDTH / 2), (int)Math.round(y - HEIGHT / 2), WIDTH,
				HEIGHT);
		transform.rotate(getAngle(), x, y);
		r = transform.createTransformedShape(r);
		Rectangle p = new Rectangle((int)Math.round( ent.getX() - ent.getWidth() / 2), (int)Math.round(ent.getY()
				- ent.getHeight() / 2), ent.getWidth(), ent.getHeight());
		AffineTransform t3 = new AffineTransform();
		t3.rotate(-ent.getAngle(), ent.getX(), ent.getY());
		r = t3.createTransformedShape(r);
		if (r.intersects(p.getBounds())) {
			exists = false;
			if(ent.getClass() == Player.class){
				Player player = (Player) ent;
				player.powerUp(2);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean exist() {
		
		return exists;
	}

	@Override
	public double getAngle() {
		// TODO Auto-generated method stub
		return angle;
	}

	@Override
	public void setAngle(double newAngle) {
		
		angle = newAngle;
	}
	
}
