package fractal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GraphicsPanel extends JPanel {

	private static final long serialVersionUID = 2002199253782033728L;
	private DistributedRenderer renderer;
	private BufferedImage image;

	private Dimension dimension;
	private Location location;
	private AlgorithmParams algParams;

	private int iterations;
	private double x;
	private double y;
	private double zoom;

	public GraphicsPanel(DistributedRenderer renderer) {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				handleKey(e.getKeyCode());
			}
		});
		this.renderer = renderer;
		setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
		setFocusable(true);
		initialize();
	}

	private void initialize() {
		iterations = DefaultParameters.iterations();
		location = DefaultParameters.mandelbrotLocation();
		x = location.coordinate().x();
		y = location.coordinate().y();
		zoom = location.zoom();
		algParams = DefaultParameters.mandelbrotParameters();
		handleKey(KeyEvent.VK_R);
	}

	public void handleKey(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_RIGHT:
			x += 0.1 / zoom;
			break;
		case KeyEvent.VK_UP:
			y -= 0.1 / zoom;
			break;
		case KeyEvent.VK_LEFT:
			x -= 0.1 / zoom;
			break;
		case KeyEvent.VK_DOWN:
			y += 0.1 / zoom;
			break;
		case KeyEvent.VK_S:
			iterations += 16;
			break;
		case KeyEvent.VK_X:
			iterations = Math.max(iterations - 16, 0);
			break;
		case KeyEvent.VK_A:
			zoom *= 2.0;
			break;
		case KeyEvent.VK_Z:
			zoom *= 0.5;
			break;
		case KeyEvent.VK_R:
			break;
		default:
			return;
		}
		AlgorithmParams newParams = null;
		if (algParams instanceof MandelbrotParams) {
			newParams = new MandelbrotParams(iterations);
		} else if (algParams instanceof JuliaParams) {
			newParams = new JuliaParams(iterations,
					((JuliaParams) algParams).coefficient());
		}
		Dimension newDimension = new Dimension(getWidth(), getHeight());
		Location newLocation = new Location("default", new Coordinate(x, y),
				zoom);
		renderer.render(Task.apply(new RenderParams(newDimension, newLocation,
				newParams)), this);
	}

	public synchronized void drawImage(ImageSegment image) {
		this.image = image.getBufferedImage();
		repaint();
	}

	private String currentParameters() {
		return "x=" + x + " y=" + y + " zoom=" + zoom + " iterations="
				+ iterations;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
		g.setColor(Color.WHITE);
		g.drawString(currentParameters(), 20, 20);
	}
}
