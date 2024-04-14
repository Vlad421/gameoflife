package panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPanel;

import fields.Field;
import interfaces.DrawOnCall;

public class FieldPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2237591384451333797L;
	private final int FIELD_X_SIZE = 600;
	private final int FIELD_Y_SIZE = 600;
	Field field;
	int[] mousePosition = new int[2];
	FieldMouseListener mouseListener;

	private boolean isCallBackSet;

	public FieldPanel(Field field) {

		this.field = field;
		mouseListener = new FieldMouseListener();
		setPreferredSize(new Dimension(FIELD_X_SIZE, FIELD_Y_SIZE));
		field.setImage(getPreferredSize());

		setDoubleBuffered(true);

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {

				if (isCallBackSet) {
					field.requestMeshRebuild();
				}
			}

		});

		addMouseWheelListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addMouseListener(mouseListener);

		field.start();

	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);

		field.drawMesh(g, getWidth(), getHeight()/* , image */, new DrawOnCall() {

			@Override
			public void draw() {

				updateUI();

			}
		});
		if (!isCallBackSet) isCallBackSet = true;

	}

	class FieldMouseListener extends MouseAdapter {
		private boolean isShiftStared;
		private int dragXstart;
		private int dragYstart;

		@Override
		public void mouseReleased(MouseEvent e) {
			isShiftStared = false;

			e.consume();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() > 0) {
				field.zoomOut(e.getX(), e.getY());
			} else {
				field.zoomIn(e.getX(), e.getY());
			}
			e.consume();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!isShiftStared) {
				dragXstart = e.getX();
				dragYstart = e.getY();
				field.shift(e.getX(), e.getY(), dragXstart, dragYstart);

				isShiftStared = true;
			} else {
				field.shift(e.getX(), e.getY());

			}

			e.consume();
		}

	}

}