package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fields.Field;
import fields.GameOfLifeField;

public class MainWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4913125635804177620L;
	private final Dimension INIT_SIZE = new Dimension(600, 300);
	private ControlPanel controls;

	private FieldPanel fieldPanel;
	private JDialog pickFrame;
	private JButton gOlButton;
	private JButton antButton;
	Field field;

	public MainWindow(Field field) {
		this.field = field;
		initialWindowSetUp();
		// initControls(field);
		// initField(field);
		showPickFrame();

	}

	private void showPickFrame() {

		gOlButton = new JButton("Game of Life");
		antButton = new JButton("Ant");

		pickFrame = new JDialog(this);
		pickFrame.setUndecorated(true);
		pickFrame.getContentPane().setBackground(Color.RED);
		pickFrame.pack();

		centerPickWindow();
		pickFrame.setLayout(new GridLayout(1, 2));
		pickFrame.getContentPane().add(gOlButton);
		pickFrame.getContentPane().add(antButton);

		gOlButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.out.println("" + gOlButton.getText() + " clicked");
				field = new GameOfLifeField();
				initControls(field);
				initField(field);
				pickFrame.setVisible(false);
				pickFrame.dispose();
				pack();
				centerOnScreen();
			}
		});
		antButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.out.println("" + antButton.getText() + " clicked");

			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {

				centerPickWindow();
			}

			@Override
			public void componentResized(ComponentEvent e) {

				centerPickWindow();
			}
		});

		pickFrame.setVisible(true);
	}

	private void centerPickWindow() {
		Rectangle r = getBounds();
		pickFrame.setBounds(new Rectangle((int) (r.x + r.width * 0.25f / 2), (int) (r.y + r.height / 7 * 3f),
				(int) (r.width * 0.75f), r.height / 7));
	}

	private void initControls(Field field) {

		controls = new ControlPanel(field);

		add(controls, BorderLayout.NORTH);
		JPanel test = new JPanel(new GridLayout(3, 1));
		JButton testButton = new JButton(new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7292278876758633299L;
			int i;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(((JButton) e.getSource()).getText());
				((JButton) e.getSource()).setText(((JButton) e.getSource()).getText() + " " + (i +=1));
				
			}

		});
		testButton.setText("testButton");
		testButton.setPreferredSize(new Dimension(50,30));
	
		test.add(testButton);
		add(testButton, BorderLayout.EAST);

	}

	private void centerOnScreen() {
		Dimension screen = getToolkit().getScreenSize();
		Dimension newSize = getSize();
		setBounds(new Rectangle((screen.width - newSize.width) / 2, (screen.height - newSize.height) / 2, newSize.width,
				newSize.height));
	}

	private void initialWindowSetUp() {
		Dimension screen = getToolkit().getScreenSize();

		setBounds(new Rectangle((screen.width - INIT_SIZE.width) / 2, (screen.height - INIT_SIZE.height) / 2,
				INIT_SIZE.width, INIT_SIZE.height));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		setVisible(true);

	}

	private void initField(Field field) {
		this.fieldPanel = new FieldPanel(field);

		add(this.fieldPanel, BorderLayout.CENTER);
	}

}
