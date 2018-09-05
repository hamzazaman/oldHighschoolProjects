package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class Barricade implements Entities {
	double x = 10;
	double y = 10;
	double angle = 0;

	static final int WIDTH = 160;
	static final int HEIGHT = 30;
	private Color health = new Color(0,191,255);
	int collided = 0;
	boolean exists = true;
	int hits = 0;
	Shape r = new Rectangle();
	Rectangle p = new Rectangle();

	@Override
	public void render(Graphics2D g) {
		g.rotate(angle, x, y);
		g.setColor(health);
		g.drawRoundRect((int)Math.round(x - WIDTH / 2),(int)Math.round( y - HEIGHT / 2), WIDTH, HEIGHT,0,0);
		g.setTransform(new AffineTransform());
		/*g.setColor(Color.WHITE);
		g.draw(r);
		g.draw(p);*/
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
		setX(getX() - (Math.sin(angle) * 5));
		setY(getY() + (Math.cos(angle) * 5));
		if (collided == 0){
			health = new Color(	0,191,255);
		} else {
		collided--;
		}

	}

	@Override
	public boolean collide(Entities ent) {
		if(ent.getClass() != Player.class && ent.getClass() != Bullet.class ){
			return false;
		}
		AffineTransform transform = new AffineTransform();
		r = new Rectangle((int)Math.round(x - WIDTH / 2), (int)Math.round(y - HEIGHT / 2), WIDTH,
				HEIGHT);
		transform.rotate(getAngle(), x, y);
		r = transform.createTransformedShape(r);
		p = new Rectangle((int)Math.round( ent.getX() - ent.getWidth() / 2), (int)Math.round(ent.getY()
				- ent.getHeight() / 2), ent.getWidth(), ent.getHeight());
		AffineTransform t3 = new AffineTransform();
		t3.rotate(-ent.getAngle(), ent.getX(), ent.getY());
		r = t3.createTransformedShape(r);
		if (r.intersects(p)) {
			health = Color.RED;
			collided = 4;
			hits++;
			if (hits >= 3 ) {
				exists = false;
				
			}
			if(ent.getClass() == Player.class){
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
