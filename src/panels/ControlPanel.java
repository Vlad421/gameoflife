package panels;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import fields.Field;

public class ControlPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -795742004000354331L;
	private JButton startButton;
	private JButton stopButton;
	private JButton resetButton;
	private JTextField width;
	private JTextField heigth;
	private JLabel labelBy;
	private JButton okButton;

	public ControlPanel(Field field) {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		startButton = new JButton("Start");
		stopButton = new JButton("Stop");
		resetButton = new JButton("Reset");
		okButton = new JButton("OK");

		startButton.setPreferredSize(new Dimension(135, 25));
		stopButton.setPreferredSize(new Dimension(135, 25));
		resetButton.setPreferredSize(new Dimension(135, 25));
		okButton.setPreferredSize(new Dimension(135, 25));

		add(startButton);
		add(stopButton);
		add(resetButton);

		DocumentFilter numbersOnly = new DocumentFilter() {
			Pattern regex = Pattern.compile("^[0-9]*$");
			private int maxLength = 3;

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				Matcher matcher = regex.matcher(text);
				if (!matcher.matches() || !(fb.getDocument().getLength() + text.length() <= maxLength)) {
					Toolkit.getDefaultToolkit().beep();
					return;
				}

				super.replace(fb, offset, length, text, attrs);
			}

		};
		width = new JTextField();
		heigth = new JTextField();
		width.setHorizontalAlignment(JTextField.RIGHT);
		heigth.setHorizontalAlignment(JTextField.RIGHT);
		ActionListener resize = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (width.getText().length() > 0 && heigth.getText().length() > 0) {
					int w = Integer.parseInt(width.getText());
					int h = Integer.parseInt(heigth.getText());
					field.stopSim();
					
					field.resizeField(w, h);
				} else
					field.resetSim();

			}
		};
		((AbstractDocument) width.getDocument()).setDocumentFilter(numbersOnly);
		((AbstractDocument) heigth.getDocument()).setDocumentFilter(numbersOnly);
		width.addActionListener(e -> width.transferFocus());
		heigth.addActionListener(resize);
		labelBy = new JLabel("X");
		width.setPreferredSize(new Dimension(30, 25));
		heigth.setPreferredSize(new Dimension(30, 25));

		add(width);
		add(labelBy);
		add(heigth);
		add(okButton);

		startButton.addActionListener(e -> {
			startButton.setText("Start");
			field.startSim();
		});
		stopButton.addActionListener(e -> {
			startButton.setText("Resume");
			field.stopSim();
		});
		resetButton.addActionListener(e -> {
			startButton.setText("Start");
			field.resetSim();
		});
		okButton.addActionListener(resize);

	}

}
