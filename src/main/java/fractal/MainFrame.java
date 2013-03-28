package fractal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MainFrame {

  private JFrame frame;
  private DistributedRenderer distRenderer;
  private Navigator navigator;
  private JPanel graphicsPanel;

  public static int WIDTH = DefaultParameters.dimension().x();
  public static int HEIGHT = DefaultParameters.dimension().y();

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MainFrame window = new MainFrame();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public MainFrame() {
    initialize();

  }

  private void initialize() {
    frame = new JFrame();
    distRenderer = new DistributedRenderer();
    navigator = new Navigator(distRenderer);
    graphicsPanel = new GraphicsPanel(navigator);

    frame.setBounds(100, 100, WIDTH, HEIGHT);
    frame.setLayout(new BorderLayout());
    frame.add(graphicsPanel, BorderLayout.CENTER);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        shutdown();
      }
    });
  }

  private void shutdown() {
    frame.dispose();
    distRenderer.shutdown();
    System.exit(0);
  }
}
