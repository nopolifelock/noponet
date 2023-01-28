package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

public class Frame extends JFrame{

	private JPanel contentPane;
	
	private Console console;
	private JLabel label;
	private DefaultListModel demoList;
	private Popper popper = new Popper(25);
	private JList<String> list;
	private JButton pauseButton;
	private JTextField textField;
	private boolean paused = false;
	private JButton refreshButton;
	public Frame(Console console) {
		this.console = console;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 278, 537);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setResizable(false);
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{131, 0, 0, 0, 0, 1, 0};
		gbl_contentPane.rowHeights = new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		 demoList = new DefaultListModel();
		
		 
		list = new JList<String>(demoList);
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.gridwidth = 6;
		gbc_list.gridheight = 14;
		gbc_list.insets = new Insets(0, 0, 5, 0);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 0;
		contentPane.add(list, gbc_list);
		
		textField = new JTextField();
		textField.setBackground(new Color(192, 192, 192));
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 6;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 14;
		contentPane.add(textField, gbc_textField);
		textField.setColumns(10);
		
		label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 6;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 15;
		contentPane.add(label, gbc_lblNewLabel);
		
		pauseButton = new JButton("| |");
		pauseButton.addActionListener(new ConsoleButtonListener(console, this) {
			public void actionPerformed(ActionEvent e) {
				togglePause();
			}
		});
		GridBagConstraints gbc_pauseButton = new GridBagConstraints();
		gbc_pauseButton.insets = new Insets(0, 0, 0, 5);
		gbc_pauseButton.gridx = 2;
		gbc_pauseButton.gridy = 16;
		contentPane.add(pauseButton, gbc_pauseButton);
		
		refreshButton = new JButton("refresh");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		JButton pushBtn = new JButton("push");
		GridBagConstraints gbc_pushBtn = new GridBagConstraints();
		gbc_pushBtn.insets = new Insets(0, 0, 0, 5);
		gbc_pushBtn.gridx = 3;
		gbc_pushBtn.gridy = 16;
		contentPane.add(pushBtn, gbc_pushBtn);
		pushBtn.addActionListener(new ConsoleButtonListener(console, this) {
			public void actionPerformed(ActionEvent e) {
				if(frame.getList().getSelectedValuesList().size()>0) {
					console.addToGit(frame.getList().getSelectedValuesList(), "whitelist.txt");
				}else {
					
					
				}
			}
		});
		
		refreshButton.addActionListener(new ConsoleButtonListener(console, this) {
			public void actionPerformed(ActionEvent e) {
				console.send("refresh\n");
			}
		});
		GridBagConstraints gbc_refreshButton = new GridBagConstraints();
		gbc_refreshButton.insets = new Insets(0, 0, 0, 5);
		gbc_refreshButton.gridx = 4;
		gbc_refreshButton.gridy = 16;
		contentPane.add(refreshButton, gbc_refreshButton);
		
		demoList.setSize(25);
		textField.addKeyListener(new ConsoleKeyListener(console, this) {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {
					console.addToGit(textField.getText(), "keywords.txt");
					frame.textField.setText("");
				}
				super.keyPressed(e);
			}
		});
		 
	}

	public void updateLabel(String update) {
		this.label.setText(update);
	}
	public void addToList(String toAdd) {
		if(!paused) {
		popper.pop(toAdd);
		for(int i =0; i<popper.elements.length; i++)
			this.demoList.setElementAt(popper.elements[i], i);
		}
	}
	
	public JList<String> getList(){
		return list;
	}
	public void togglePause() {
		paused = !paused;
	}
}class ConsoleButtonListener implements ActionListener{
	protected Console console;
	protected Frame frame;
	public ConsoleButtonListener(Console c, Frame frame) {
		this.console = c;
		this.frame = frame;
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		
	}

}
class ConsoleKeyListener implements KeyListener{
	protected Console console;
	protected Frame frame;
	
	public ConsoleKeyListener(Console c, Frame frame) {
		this.console = c;
		this.frame = frame;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}