package fractal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GraphicsPanel extends JPanel {

	private static final long serialVersionUID = 2002199253782033728L;
	private BufferedImage image;
	private Navigator navigator;

	public GraphicsPanel(Navigator navigator) {
		this.navigator = navigator;
		setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
		setFocusable(true);
		navigator.setGraphicsPanel(this);
		initialize();

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				handleKey(e.getKeyCode());
			}
		});
	}

	private void initialize() {
		handleKey(KeyEvent.VK_R);
	}

	public void handleKey(int keyCode) {
		navigator.setDimension(new Dimension(this.getWidth(), this.getHeight()));
		
		switch (keyCode) {
		case KeyEvent.VK_RIGHT:
			navigator.incrementXCoordinate(0.1);
			break;
		case KeyEvent.VK_UP:
			navigator.incrementYCoordinate(-0.1);
			break;
		case KeyEvent.VK_LEFT:
			navigator.incrementXCoordinate(-0.1);
			break;
		case KeyEvent.VK_DOWN:
			navigator.incrementYCoordinate(0.1);
			break;
		case KeyEvent.VK_S:
			navigator.incrementIterations(16);
			break;
		case KeyEvent.VK_X:
			navigator.incrementIterations(-16);
			break;
		case KeyEvent.VK_A:
			navigator.incrementZoom(2.0);
			break;
		case KeyEvent.VK_Z:
			navigator.incrementZoom(0.5);
			break;
		case KeyEvent.VK_R:
			navigator.requestRenderToPanel();
			break;
		default:
			return;
		}
	}

	public synchronized void drawImage(BufferedImage image) {
		this.image = image;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
		g.setColor(Color.WHITE);
		g.drawString(navigator.getParamString(), 20, 20);
	}
}
