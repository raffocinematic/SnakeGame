import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	// unit size per decidere quanto vogliamo vedere grande un oggetto sulla griglia
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;
	// dichiaro due array per contenere le coordinate dello snake
	// (lo snake non può mai essere più grande dello schermo stesso)
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	// partiamo con lo snake lungo 6
	int bodyParts = 6;
	int applesEaten;
	// coordinate delle mele che una volta sparite faremo apparire random
	int appleX;
	int appleY;
	// lo snake inizerà andando a destra (right)
	char direction = 'R';
	// lo snake inizierà da fermo
	boolean running = false;
	Timer timer;
	Random random;

//costruttore per il game panel
	GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}

//metodi di varia utilità per il gioco

	// metodo per far partire il gioco
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
		
		if (running) {
			// faccio un for loop per disegnare una griglia(opzionale)
			/*
			for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
			}
			*/
			// diamo colore e forma alla mela
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

			// disegniamo testa e corpo dello snake
			for (int i = 0; i < bodyParts; i++) {
				if (i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					g.setColor(new Color(45, 180, 0));
					//la prossima linea serve graficamente ad avere uno snake multicolore
					//g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}

			}
			//per vedere in alto il punteggio della partita
			g.setColor(Color.red);
			g.setFont( new Font ("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
		}
		else {
			gameOver(g);
		}

	}

	// metodo per generare le mele
	public void newApple() {
		appleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
		appleY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;

	}

	// metodo per muovere lo snake
	public void move() {
		// for loop per iterare attraverso tutte le parti del corpo dello snake
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		// switch per cambiare la direzione dello snake
		switch (direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}

	}

	// metodo per mangiare la mela e crescere di 1 unità
	public void checkApple() {
		if ((x[0] == appleX && (y[0] == appleY))) {
			bodyParts++;
			applesEaten++;
			newApple();
		}

	}

	// metodo per controllare se la testa dello snake si scontra col suo corpo
	// iteriamo attraverso le body parts dello snake
	public void checkCollisions() {
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		// controlla se la testa tocca il bordo a sinistra
		if (x[0] < 0) {
			running = false;
		}
		// controlla se la testa tocca il bordo a destra
		if (x[0] > SCREEN_WIDTH) {
			running = false;
		}
		// controlla se la testa tocca il bordo sopra
		if (y[0] < 0) {
			running = false;
		}
		// controlla se la testa tocca il bordo sotto
		if (y[0] > SCREEN_HEIGHT) {
			running = false;
		}

		if (!running) {
			timer.stop();
		}
	}

	public void gameOver(Graphics g) {
		//per vedere il punteggio in alto con la schermata del Game Over
		g.setColor(Color.red);
		g.setFont( new Font ("Ink Free", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
		
		//testo del Game Over
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		//per centrarlo
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (running) {
			move();
			checkApple();
			checkCollisions();

		}
		repaint();

	}

	// inner class
	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			// per gestire la pressione dei tasti su giù sinistra e destra per muovere lo
			// snake
			// se vai a destra, non puoi andare a sinistra e viceversa
			// se vai sopra, non puoi andare subito sotto e viceversa
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if (direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					direction = 'D';
				}
				break;
			}
		}

	}
}
