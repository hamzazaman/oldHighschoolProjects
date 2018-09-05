import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Entities.*;

public class MainGame extends JPanel { // main class for the game

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Define constants
	static final int CANVAS_WIDTH = 800; // width and height of the game screen
	static final int CANVAS_HEIGHT = 800;
	static int UPDATES_PER_SEC = 110; // number game update per second
	static long UPDATE_PERIOD_NSEC = 1000000000L / UPDATES_PER_SEC; // nanoseconds
	static double b_per_sec = 1; // barricades spawned per second
	static long b_period_nsec = (long) (1000000000L / b_per_sec); // nanoseconds
	static final String TITLE = "Dodge";

	// Enumeration for the states of the game.
	static enum GameState {
		HELP, PLAYING, PAUSED, GAMEOVER, DESTROYED
	}

	static GameState state; // current state of the game

	// Define instance variables for the game objects
	Player p;
	Entities[] en;
	int kills = 0;
	int timeLasted = 0;
	int position = 0;
	int lastP = 0;
	long timeBmade;
	long timeBElapse;
	Thread gameThread;
	Thread refresh;
	Image img;
	boolean refreshed = false;
	int killHelper = 0;
	long time1, time2;

	boolean lastpowerup;

	// Handle for the custom drawing panel
	private GameCanvas canvas;

	// Constructor to initialize the UI components and game objects
	public MainGame() {
		// Initialize the game objects
		gameInit();

		// UI components
		URL url = getClass().getResource("/resources/grid.jpg");
		if (url == null) {
			System.out.println("Backround image failed to load");
		} else {
			// path is OK
			img = Toolkit.getDefaultToolkit().getImage(url);
		}
		canvas = new GameCanvas();
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

		setBackground(Color.DARK_GRAY);
		setLayout(new BorderLayout());
		add(canvas);
		refresh = new Thread() {
			@Override
			public void run() {
				long t1;
				while (true) {
					t1 = System.nanoTime();
					canvas.repaint();
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					System.out.println(t1-System.nanoTime());
				}
			}

		};
		// refresh.start();

		gameStart();
		// gameStart();

	}

	public void gameReset() {
		state = GameState.GAMEOVER;
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		UPDATES_PER_SEC = 110; // number game update per second
		UPDATE_PERIOD_NSEC = 1000000000L / UPDATES_PER_SEC;
		b_per_sec = 1;
		b_period_nsec = (long) (1000000000L / b_per_sec);
		gameInit();
		gameStart();
		// gameStart();
	}

	public void gameInit() {
		en = new Entities[40];
		p = new Player(CANVAS_WIDTH, CANVAS_HEIGHT);
		en[0] = p;
		kills = 0;
		timeLasted = 0;
		lastpowerup = false;
		timeBmade = System.nanoTime();
		state = GameState.HELP;
	}

	// Shutdown the game, clean up code that runs only once.
	public void gameShutdown() {
		// ......
	}

	// To start and re-start the game.
	public void gameStart() {
		// Create a new thread
		gameThread = new Thread() {
			// Override run() to provide the running behavior of this thread.
			@Override
			public void run() {
				gameLoop();
				
			}
		};
		// Start the thread. start() calls run(), which in turn calls
		// gameLoop().
		gameThread.start();
	}

	private void gameLoop() {

		state = GameState.PLAYING;
		// Game loop
		long beginTime, timeTaken, timeLeft; // in msec
		
		time1 = (long) Math.floor(System.currentTimeMillis()/1000.0);
		time2 = time1;
		while (state != GameState.GAMEOVER) {
			beginTime = System.nanoTime();
			if (state == GameState.PLAYING) { 
				
				gameUpdate();
				time2 = time1;
				time1 = (long) Math.floor(System.currentTimeMillis()/1000.0);
				if(time1 != time2){
					timeLasted++;
					
					if (timeLasted % 2 == 0) {
						//System.out.println(timeLasted);
						if (UPDATES_PER_SEC < 300) {
							UPDATES_PER_SEC += 1;
							UPDATE_PERIOD_NSEC = 1000000000L / UPDATES_PER_SEC;
						}

					}
					if (timeLasted % 5 == 0) {
						System.out.println(timeLasted);
						b_per_sec+=.25;
						b_period_nsec = (long) (1000000000L / b_per_sec);
						

					}
					p.setScore(p.getScore() + 10);
				}
				
				if(p.getScore() < 0) p.setScore(0);
			}

			// Refresh the display

			if (refreshed) {
				refreshed = false;
			} else {
				repaint();
				refreshed = true;
			}
			// Delay timer to provide the necessary delay to meet the target
			// rate
			timeTaken = System.nanoTime() - beginTime;
			timeLeft = (UPDATE_PERIOD_NSEC - timeTaken) / 1000000L; // in
																	// milliseconds

			if (timeLeft < 0)
				timeLeft = 0; // set a minimum
			try {
				// Provides the necessary delay and also yields control so that
				// other thread can do work.
				Thread.sleep(timeLeft);
			} catch (InterruptedException ex) {
			}
		}
		repaint();
	}

	// Update the state and position of all the game objects,
	// detect collisions and provide responses.
	public synchronized void gameUpdate() {
		for (int z = 0; z < en.length; z++) {
			if (en[z] != null) {
				for (int s = 0; s < en.length; s++) {
					if (en[s] != null && s != z) {
						if (en[z].collide(en[s])) {
							//
						}
					}
				}

			}
		}
		if (!p.exist()) {
			state = GameState.GAMEOVER;
			return;
		}
		for (int z = 0; z < en.length; z++) {
			if (en[z] != null) {
				if (!en[z].exist()) {
					if ((en[z].getClass().equals(Barricade.class) || en[z]
							.getClass().equals(Box.class)) && p.exist()) {
						kills++;
						p.setScore(p.getScore() + 50);
					}
					en[z] = null;

				}
			}
		}
		for (int z = 0; z < en.length; z++) {
			if (en[z] != null) {
				if (en[z].getY() < -30 || en[z].getY() > CANVAS_HEIGHT + 30
						|| en[z].getX() < -30
						|| en[z].getX() > CANVAS_WIDTH + 30) {

					en[z] = null;
				} else {
					en[z].move();
				}
			}
		}

		if (p.isShoot() && p.exist()) {
			for (int z = 0; z < en.length; z++) {
				if (en[z] == null) {
					en[z] = new Bullet();
					en[z].setX(p.getX());
					en[z].setY(p.getY());
					en[z].setAngle(p.getAngle());

					break;
				}
			}
		}
		Random r = new Random();

		timeBElapse = System.nanoTime() - timeBmade;
		if (timeBElapse > b_period_nsec) {
			lastP = position;
			position = r.nextInt(4);
			if (position == lastP) {
				position = r.nextInt(4);
				if (position == lastP) {
					position = r.nextInt(4);
				}
			}
			for (int z = en.length - 1; z >= 0; z--) {
				if (en[z] == null) {
					timeBmade = System.nanoTime();
					en[z] = new Barricade();
					if (position == 0) {
						en[z].setX(-10);
						en[z].setY(r.nextInt(CANVAS_HEIGHT));
						en[z].setAngle(Math.PI / 2 + Math.PI);
					} else if (position == 1) {
						en[z].setX(r.nextInt(CANVAS_WIDTH));
						en[z].setY(-10);
					} else if (position == 2) {
						en[z].setX(CANVAS_WIDTH + 10);
						en[z].setY(r.nextInt(CANVAS_HEIGHT));
						en[z].setAngle(Math.PI / 2);
					} else {
						en[z].setX(r.nextInt(CANVAS_WIDTH));
						en[z].setY(CANVAS_HEIGHT + 10);
						en[z].setAngle(Math.PI);
					}
					break;
				}
			}

		} else {
			int power = r.nextInt(75);
			if (power == 5 && !lastpowerup) {
				lastpowerup = true;
				position = r.nextInt(4);
				if (position == lastP) {
					position = r.nextInt(3);
					if (position == lastP) {
						position = r.nextInt(3);
					}
				}
				for (int z = en.length - 1; z >= 0; z--) {
					if (en[z] == null) {
						int type = r.nextInt(16);
						switch (type) {
						case 0:
						case 9:
							en[z] = new PowerUp();
							// System.out.println("p");
							break;
						case 1:
						case 2:
						case 10:
						case 13:
							en[z] = new AmmoRefill();
							// System.out.println("a");
							break;
						case 3:
						case 4:
						case 11:
							en[z] = new ExtraLife();
							//System.out.println("e");
							break;
						case 5:
						case 6:
						case 7:
						case 8:
						case 12:
						case 14:
						case 15:
							en[z] = new Box();
							// System.out.println("b");
							break;
						}

						if (position == 0) {
							en[z].setX(-25);
							en[z].setY(r.nextInt(CANVAS_HEIGHT));
						} else if (position == 1) {
							en[z].setX(r.nextInt(CANVAS_WIDTH));
							en[z].setY(-25);
						} else if (position == 2) {
							en[z].setX(CANVAS_WIDTH + 25);
							en[z].setY(r.nextInt(CANVAS_HEIGHT));
						} else {
							en[z].setX(r.nextInt(CANVAS_WIDTH));
							en[z].setY(CANVAS_HEIGHT + 25);
						}
						en[z].setAngle(r.nextInt(6) + r.nextDouble());
						break;
					}
				}
			} else {
				lastpowerup = false;
			}
		}
	}

	// Refresh the display. Called back via repaint(), which invoke the
	// paintComponent().
	private void gameDraw(Graphics2D g2d) {
		switch (state) {
		case HELP:
			
			g2d.setColor(Color.CYAN);
			g2d.setFont(new Font(g2d.getFont().getFontName(), Font.PLAIN, 25));
			g2d.drawString("" + 1120, 520, 526);
			
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font(g2d.getFont().getFontName(), Font.BOLD, 35));
			
			g2d.drawString("Controls:",30, 50);
			
			g2d.drawString("Objects:",390, 50);
			
			g2d.drawString("Scoring:",30, 400);
			
			g2d.drawString("HUD:",390, 400);
			
			drawCenteredString("Press H to go back", CANVAS_WIDTH, CANVAS_HEIGHT * 2 - 100, g2d);
			
			g2d.setFont(new Font(g2d.getFont().getFontName(), Font.PLAIN, 20));
			g2d.drawString("WASD or Arrow keys to move", 30, 92);
			g2d.drawString("Mouse to Aim", 30, 134);
			g2d.drawString("Space to Shoot", 30, 176);
			g2d.drawString("P or ESC to Pause", 30, 218);
			g2d.drawString("R to Reset", 30, 260);
			g2d.drawString("H for Help", 30, 302);
			
			g2d.drawString("Player:", 390, 92);
			g2d.drawString("Enemies:", 390, 134);
			g2d.drawString("Shield:", 390, 176);
			g2d.drawString("Ammo:", 390, 218);
			g2d.drawString("Lives:", 390, 260);
			
			
			
			Player modelP = new Player(CANVAS_WIDTH, CANVAS_HEIGHT);
			modelP.setX(590);
			modelP.setY(86);
			modelP.render(g2d);
			
			Barricade modelB = new Barricade();
			modelB.setX(590);
			modelB.setY(126);
			modelB.render(g2d);
			
			PowerUp ms = new PowerUp();
			ms.setX(590);
			ms.setY(167);
			ms.render(g2d);
			
			AmmoRefill ma = new AmmoRefill();
			ma.setX(590);
			ma.setY(209);
			ma.render(g2d);
			
			ExtraLife ml = new ExtraLife();
			ml.setX(590);
			ml.setY(251);
			ml.render(g2d);
			
			g2d.setColor(Color.WHITE);
			g2d.drawString("10 points for every second alive", 30, 442);
			g2d.drawString("50 points for every kill", 30, 484);
			g2d.drawString("0 points lost for every death", 30, 526);
			
			g2d.drawString("Ammo Bar:", 390, 442);
			g2d.drawString("Lives:", 390, 484);
			g2d.drawString("Score:", 390, 526);
			
			g2d.setColor(Color.RED);
			g2d.drawRect(520, 425, 240, 25);
			g2d.setColor(Color.ORANGE);
			g2d.drawRect(560, 425, 200, 25);
			
			
			
			
			for (int z = 0; z < 3; z++) {
				g2d.setColor(Color.GRAY);
				g2d.drawRect(580 - (30 * z), 465,
						Player.WIDTH, Player.HEIGHT + 3);
				g2d.setColor(Color.GRAY);
				g2d.drawRect(580 - (30 * z), 468,
						Player.WIDTH, Player.HEIGHT);
			}
			
			for (int z = 0; z < 2; z++) {
				g2d.setColor(Color.RED);
				g2d.drawRect(580 - (30 * z), 465,
						Player.WIDTH, Player.HEIGHT + 3);
				g2d.setColor(Color.GREEN);
				g2d.drawRect(580 - (30 * z), 468,
						Player.WIDTH, Player.HEIGHT);
			}
			
			break;
		case PLAYING:
			for (Entities e : en) {
				if (e != null) {
					e.render(g2d);
				}
			}
			p.render(g2d);
			
			g2d.setFont(new Font(g2d.getFont().getFontName(), Font.PLAIN, 25));
			g2d.setColor(Color.CYAN);
			g2d.drawString("" + p.getScore(), 20, 30);
			
			for (int z = 0; z < 3; z++) {
				g2d.setColor(Color.GRAY);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * z), 45,
						Player.WIDTH, Player.HEIGHT + 3);
				g2d.setColor(Color.GRAY);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * z), 48,
						Player.WIDTH, Player.HEIGHT);
			}
			int s = 0;
			for (s = 0; s < p.getLives(); s++) {
				g2d.setColor(Color.RED);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * s), 45,
						Player.WIDTH, Player.HEIGHT + 3);
				g2d.setColor(Color.GREEN);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * s), 48,
						Player.WIDTH, Player.HEIGHT);
				
			}
			if(p.isShield()){
				g2d.setColor(Color.RED);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * s), 45,
						Player.WIDTH, Player.HEIGHT + 3);
				g2d.setColor(Color.MAGENTA);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * s), 48,
						Player.WIDTH, Player.HEIGHT);
			}
			
			g2d.setColor(Color.RED);
			g2d.drawRect(CANVAS_WIDTH - 250, 10, 240, 25);
			g2d.setColor(Color.ORANGE);
			g2d.drawRect(CANVAS_WIDTH - 250 + (60 - p.getShots()) * 4, 10,
					p.getShots() * 4, 25);
			break;
		case PAUSED:
			for (Entities e : en) {
				if (e != null) {
					e.render(g2d);
				}
			}
			p.render(g2d);
			
			g2d.setFont(new Font(g2d.getFont().getFontName(), Font.PLAIN, 25));
			g2d.setColor(Color.CYAN);
			g2d.drawString("" + p.getScore(), 20, 30);
			
			for (int z = 0; z < 3; z++) {
				g2d.setColor(Color.GRAY);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * z), 45,
						Player.WIDTH, Player.HEIGHT + 3);
				g2d.setColor(Color.GRAY);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * z), 48,
						Player.WIDTH, Player.HEIGHT);
			}

			for (s = 0; s < p.getLives(); s++) {
				g2d.setColor(Color.RED);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * s), 45,
						Player.WIDTH, Player.HEIGHT + 3);
				g2d.setColor(Color.GREEN);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * s), 48,
						Player.WIDTH, Player.HEIGHT);
				
			}
			if(p.isShield()){
				g2d.setColor(Color.RED);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * s), 45,
						Player.WIDTH, Player.HEIGHT + 3);
				g2d.setColor(Color.MAGENTA);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * s), 48,
						Player.WIDTH, Player.HEIGHT);
			}
			g2d.setColor(Color.RED);
			g2d.drawRect(CANVAS_WIDTH - 250, 10, 240, 25);
			g2d.setColor(Color.ORANGE);
			g2d.drawRect(CANVAS_WIDTH - 250 + (60 - p.getShots()) * 4, 10,
					p.getShots() * 4, 25);
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font(g2d.getFont().getFontName(), Font.BOLD, 30));
			drawCenteredString("PAUSED", CANVAS_WIDTH, CANVAS_HEIGHT - 60, g2d);
			drawCenteredString("P to Unpause", CANVAS_WIDTH,
					CANVAS_HEIGHT + 60, g2d);
			break;
		case GAMEOVER:
			for (Entities e : en) {
				if (e != null) {
					e.render(g2d);
				}
			}
			p.render(g2d);
			for (int z = 0; z < 3; z++) {
				g2d.setColor(Color.GRAY);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * z), 45,
						Player.WIDTH, Player.HEIGHT + 3);
				g2d.setColor(Color.GRAY);
				g2d.drawRect(CANVAS_WIDTH - 30 - (30 * z), 48,
						Player.WIDTH, Player.HEIGHT);
			}
			g2d.setColor(Color.RED);
			g2d.drawRect(CANVAS_WIDTH - 250, 10, 240, 25);
			g2d.setColor(Color.ORANGE);
			g2d.drawRect(CANVAS_WIDTH - 250 + (60 - p.getShots()) * 4, 10,
					p.getShots() * 4, 25);
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font(g2d.getFont().getFontName(), Font.BOLD, 30));
			drawCenteredString("GAME OVER", CANVAS_WIDTH, CANVAS_HEIGHT - 90,
					g2d);
			g2d.setFont(new Font(g2d.getFont().getFontName(), Font.BOLD, 20));
			drawCenteredString("SCORE: " + p.getScore(), CANVAS_WIDTH, CANVAS_HEIGHT,
					g2d);
			drawCenteredString("R to Reset", CANVAS_WIDTH, CANVAS_HEIGHT + 90,
					g2d);
			g2d.setColor(Color.GREEN);
			drawCenteredString("Press H for Help", CANVAS_WIDTH, CANVAS_HEIGHT * 2 - 100, g2d);
			break;
		case DESTROYED:
			break;
		default:
			break;
		}
	}

	public void drawCenteredString(String s, int w, int h, Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		int x = (w - fm.stringWidth(s)) / 2;
		int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
		g.drawString(s, x, y);
	}

	// Custom drawing panel, written as an inner class.
	class GameCanvas extends JPanel implements KeyListener, MouseMotionListener, MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		boolean wasGameOver = false;

		// Constructor
		public GameCanvas() {
			setFocusable(true); // so that can receive key-events
			requestFocus();
			addKeyListener(this);
			addMouseMotionListener(this);
			addMouseListener(this);
			setBackground(Color.BLACK);
		}

		// Override paintComponent to do custom drawing.
		// Called back by repaint().
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setClip(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
			g2d.drawImage(img, 0, 0, null);
			g2d.setStroke(new BasicStroke(2));

			// Draw the game objects

			gameDraw(g2d);
		}

		// KeyEvent handlers
		@Override
		public void keyPressed(KeyEvent e) {
			// gameKeyPressed(e.getKeyCode());
			if (e.getKeyCode() == KeyEvent.VK_R) {
				gameReset();
			} else if (e.getKeyCode() == KeyEvent.VK_P
					|| e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (state == GameState.PAUSED) {
					time1 = (long) Math.floor(System.currentTimeMillis()/1000.0);
					time2 = time1;
					state = GameState.PLAYING;
				} else if (state == GameState.PLAYING) {
					state = GameState.PAUSED;
				}
			} else if(e.getKeyCode() == KeyEvent.VK_H){
				if (state == GameState.HELP) {
					time1 = (long) Math.floor(System.currentTimeMillis()/1000.0);
					time2 = time1;
					state = GameState.PLAYING;
					if(wasGameOver) gameReset();
					wasGameOver = false;
				} else if (state == GameState.PLAYING) {
					wasGameOver = false;
					state = GameState.HELP;
				} else if(state == GameState.GAMEOVER){
					wasGameOver = true;
					state = GameState.HELP;
					repaint();
				}
			}
			p.press(e.getKeyCode());
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// gameKeyReleased(e.getKeyCode());
			p.released(e.getKeyCode());
		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// System.out.println("1");
			p.mouseMoved(e.getX(), e.getY());
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// System.out.println("2");
			p.mouseMoved(e.getX(), e.getY());
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getButton()== MouseEvent.BUTTON1){
				p.press(KeyEvent.VK_SPACE);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1){
				p.released(KeyEvent.VK_SPACE);
			}
			
		}

	}

	// main
	public static void main(String[] args) {
		// Use the event dispatch thread to build the UI for thread-safety.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame(TITLE);
				// Set the content-pane of the JFrame to an instance of main
				// JPanel
				MainGame g = new MainGame();
				frame.setContentPane(g); // main JPanel as content
											// pane
				// frame.setJMenuBar(menuBar); // menu-bar (if defined)
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				// frame.setResizable(false);
				frame.setLocationRelativeTo(null); // center the application
													// window
				// frame.setResizable(false);
				URL url = getClass().getResource("/resources/gameicon.jpg");

				if (url == null) {
					System.out.println("Icon image failed to load");
				} else {
					// path is OK
					Image img2 = Toolkit.getDefaultToolkit().getImage(url);
					frame.setIconImage(img2);
				}

				frame.setVisible(true); // show it
			}
		});
	}

}