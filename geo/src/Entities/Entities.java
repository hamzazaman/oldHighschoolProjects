package Entities;

import java.awt.Graphics2D;

public interface Entities {
	public void render (Graphics2D g);
	public double getX();
	public double getY();
	public double getAngle();
	public void setAngle(double newAngle);
	public void setX(double nx);
	public void setY(double ny);
	public int getWidth();
	public int getHeight();
	public void move();
	public boolean collide(Entities ent);
	public boolean exist();
}
