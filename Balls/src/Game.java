import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
public class Game extends JPanel implements Runnable, MouseListener,
		MouseMotionListener, ComponentListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Random rand = new Random();// for finding random numbers
	private Thread t = new Thread(this);
	private int ncx; // these are for storing information from my
	private int ncy; // mouse pressed events to pass to mouse released
	private int nc;
	private int[] line = new int[5]; // to store info about a line drawn when mouse is
								// clicked and dragged
	private JFrame j = new JFrame("Bounce");
	int ballWidth = 30;
	private JSlider js = new JSlider(0, 200, 16);
	Physics ph = new Physics();

	public Game() {
		super();
		setPreferredSize(new Dimension(500 + ballWidth, 500 + ballWidth));
		j.setLayout(new BorderLayout());
		j.add(this, BorderLayout.CENTER);
		JButton bu = new JButton("Clear");
		bu.setBackground(Color.DARK_GRAY);
		bu.setForeground(Color.WHITE);
		add(bu);
		bu.addActionListener(ph);
		js.addChangeListener(this);
		js.setMajorTickSpacing(100);
		js.setMinorTickSpacing(10);
		js.setPaintTicks(true);
		js.setPaintLabels(true);
		j.add(js, BorderLayout.SOUTH);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		j.pack();
		setBackground(Color.LIGHT_GRAY);
		// j.setResizable(false);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setVisible(true);
		addComponentListener(this);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D ga = (Graphics2D) g;
		ga.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		ga.setStroke(new BasicStroke(2));// set stroke size to 2 pixels
		for (int z = 0; z < ph.cr.length; z++) { // for every entity in cr
			if (ph.cr[z] != null) { // make sure the entity isn't null, avoid
									// null
									// pointer exception
				ga.setColor(ph.cr[z].c);
				ga.fillOval(ph.cr[z].getX(), ph.cr[z].getY(), ballWidth, ballWidth);
				ga.setColor(Color.BLACK);
				ga.drawOval(ph.cr[z].getX(), ph.cr[z].getY(), ballWidth, ballWidth);
			}

		}
		if (line[4] != 0) { // the last spot in line[] is there to tell whether
							// line should be drawn
			ga.drawLine(line[0], line[1], line[2], line[3]);
		}

	}

	public static int randInt(int min, int max) {// to make my life easier
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static void main(String[] args) {
		Game g = new Game();
		g.ph.cr[0] = new Cords(randInt(0, 500), randInt(0, 500), Color.RED,
				randInt(-10, 10), randInt(-10, 10), 0);
		g.ph.cr[1] = new Cords(randInt(0, 500), randInt(0, 500), Color.BLUE,
				randInt(-10, 10), randInt(-10, 10), 1);
		g.ph.cr[2] = new Cords(randInt(0, 500), randInt(0, 500), Color.GREEN,
				randInt(-10, 10), randInt(-10, 10), 2);
		g.ph.cr[3] = new Cords(randInt(0, 500), randInt(0, 500), Color.YELLOW,
				randInt(-10, 10), randInt(-10, 10), 3);
		g.ph.cr[4] = new Cords(randInt(0, 500), randInt(0, 500), Color.MAGENTA,
				randInt(-10, 10), randInt(-10, 10), 4);
		g.t.start();
		g.ph.start();
	}

	@Override
	public synchronized void run() {
		while (true) {
			repaint();
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	
	@Override
	public void mousePressed(MouseEvent e) {
		if (MouseEvent.BUTTON1 == e.getButton()) {
			boolean collision = false;
			boolean created = false;
			for (int z = ph.cr.length - 1; z >= 0; z--) {
				if (ph.cr[z] != null && e.getX() > ph.cr[z].x
						&& e.getX() < ph.cr[z].x + ballWidth
						&& ph.cr[z].y < e.getY()
						&& e.getY() < ph.cr[z].y + ballWidth) {
					ph.cr[z].freeze = true;
					ph.cr[z].rise = 0;
					ph.cr[z].run = 0;
					collision = true;
					nc = z;
					ncx = ph.cr[z].getX() + (ballWidth / 2);
					ncy = ph.cr[z].getY() + (ballWidth / 2);
					break;
				}
				line[4] = 1;
			}
			if (!collision) {
				for (int z = 0; z < ph.cr.length; z++) {
					if (ph.cr[z] == null) {
						ph.cr[z] = new Cords(e.getX() - ballWidth / 2, e.getY()
								- ballWidth / 2, new Color(randInt(0, 255),
								randInt(0, 255), randInt(0, 255)), 0, 0, z);
						Cords.setMaxX(getWidth() - ballWidth);
						Cords.setMaxY(getHeight() - ballWidth);
						ph.cr[z].freeze = true;
						ph.ta[z] = new Thread(ph.cr[z]);
						ph.ta[z].start();
						nc = z;
						ncx = e.getX();
						ncy = e.getY();
						created = true;
						break;
					}
				}
				if (!created) {
					line[4] = 0;
					nc = -1;
				} else {
					line[4] = 1;
				}
			}
			line[0] = ncx;
			line[1] = ncy;
			line[2] = e.getX();
			line[3] = e.getY();
		} else if (line[4] != 1 && MouseEvent.BUTTON3 == e.getButton()) {
			for (int z = ph.cr.length - 1; z >= 0; z--) {
				if (ph.cr[z] != null && e.getX() > ph.cr[z].x
						&& e.getX() < ph.cr[z].x + ballWidth
						&& ph.cr[z].y < e.getY()
						&& e.getY() < ph.cr[z].y + ballWidth) {
					ph.cr[z] = null;
					break;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (nc >= 0 && line[4] == 1 && MouseEvent.BUTTON1 == e.getButton()) {
			ph.cr[nc].rise = (int) Math.ceil((e.getY() - ncy) / 6);
			ph.cr[nc].run = (int) Math.ceil((e.getX() - ncx) / 6);
			ph.cr[nc].freeze = false;
			line[4] = 0;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		line[2] = e.getX();
		line[3] = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentResized(ComponentEvent e) {
		Cords.setMaxX(getWidth() - ballWidth);
		Cords.setMaxY(getHeight() - ballWidth);
		//ballWidth = getWidth() / 30;
	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (source.getValueIsAdjusting()) {
			ph.rate = (long) source.getValue() + 1;
		}
	}
}

