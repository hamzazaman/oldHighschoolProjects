import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Physics implements ActionListener {
	Cords[] cr;
	boolean[][] collided;
	Thread[] ta;
	long rate = 15;
	private boolean running = true;
	
	public Physics(){
		cr = new Cords[50];
		collided = new boolean[50][50];
		ta = new Thread[cr.length];
	}
	
	public void collide(Cords cor) {
		for (int z = 0; z < cr.length; z++) { // for every ent\ty in cr
			if (cr[z] != null && cor.id != z) { // make sure the entity isn't
												// null, avoid
				float xd = cor.getX() - cr[z].getX();
				float yd = cor.getY() - cr[z].getY();

				float sumRadius = 30;
				float sqrRadius = sumRadius * sumRadius;
				float distSqr = (xd * xd) + (yd * yd);

				if (distSqr <= sqrRadius) {
					if(!collided[cor.id][z] || !collided[z][cor.id]){
						double vtheta = Math.atan(cor.rise/cor.run);
						double v = Math.sqrt(cor.rise*cor.rise + cor.run*cor.run);
						double comptheta = Math.atan(yd/xd); 
						double comp = v*Math.cos(comptheta-vtheta);
						
						System.out.println("rise:" + cor.rise);
						System.out.println("V : " + v);
						System.out.println("Vtheta : " + vtheta);
						System.out.println("comp : " + comp);
						System.out.println("ctheta : " + comptheta);
						System.out.println();

						cor.run += comp*Math.cos(comptheta); 
						cor.rise += comp*Math.sin(comptheta);
						cr[z].run -= comp*Math.cos(comptheta); 
						cr[z].rise -= comp*Math.sin(comptheta);
						
						vtheta = Math.atan(cr[z].rise/cr[z].run);
						v = Math.sqrt(cr[z].rise*cr[z].rise + cr[z].run*cr[z].run);
						comp = v*Math.cos(comptheta-vtheta);
						
						cor.run -= comp*Math.cos(comptheta); 
						cor.rise -= comp*Math.sin(comptheta);
						cr[z].run += comp*Math.cos(comptheta); 
						cr[z].rise += comp*Math.sin(comptheta);
						
						cor.c = Color.BLACK;
					}
					collided[cor.id][z] = true;
					collided[z][cor.id] = true;
				}else{
					cor.c = cor.oc;
					collided[cor.id][z] = false;
					collided[z][cor.id] = false;
				}
			}
		}
	}

	synchronized void start() {
		while (running) {
			for (int z = 0; z < cr.length; z++) { // for every entity in cr
				if (cr[z] != null) { // make sure the entity isn't null, avoid
					ta[z] = new Thread(cr[z]);
					ta[z].start();
				}
			}
			for (int z = 0; z < cr.length; z++) { // for every entity in cr
				if (cr[z] != null) { // make sure the entity isn't null, avoid
					collide(cr[z]);
				}
			}
			try {
				Thread.sleep(rate);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int z = 0; z < cr.length; z++) { // for every entity in cr
			ta[z] = null;
			cr[z] = null;
		}
	}
}
