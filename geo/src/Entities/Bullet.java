package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class Bullet implements Entities{
	double x = 10;
	double y = 10;
	double angle = 0;
	public static final int WIDTH = 9;
	public static final int HEIGHT = 18;
	boolean exists = true;
	
	@Override
	public void render(Graphics2D g) {
		g.rotate(angle, x, y);
		g.setColor(Color.ORANGE);
		//g.fillOval((int)Math.round(x - WIDTH/2), (int)Math.round(y - HEIGHT), WIDTH, WIDTH*2);
		//g.setColor(Color.BLACK);
		g.drawOval((int)Math.round(x - WIDTH/2), (int)Math.round(y - HEIGHT), WIDTH, WIDTH*2);
		g.setColor(Color.BLACK);
		g.fillRect((int)Math.round(x - WIDTH/2), (int)Math.round(y - HEIGHT/2), WIDTH, HEIGHT);
		g.setColor(Color.RED);
		//g.fillRect((int)Math.round(x - WIDTH/2), (int)Math.round(y - HEIGHT/2), WIDTH, HEIGHT);
		//g.setColor(Color.BLACK);
		g.drawRect((int)Math.round(x - WIDTH/2), (int)Math.round(y - HEIGHT/2), WIDTH, HEIGHT);
		//g.rotate(-angle, x, y);
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

	@Override
	public void move() {
		setX(getX() + (Math.sin(angle) * 12));
		setY(getY() - (Math.cos(angle) * 12));
	}

	@Override
	public boolean collide(Entities ent) {
		if(ent.getClass() == Player.class)
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

	@Override
	public double getAngle() {
		return angle;
	}
	
	@Override
	public void setAngle(double newAngle){
		angle = newAngle;
	}

}
