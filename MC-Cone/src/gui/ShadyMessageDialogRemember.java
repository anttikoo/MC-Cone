package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import information.Fonts;
import operators.CheckBoxIcon;

public class ShadyMessageDialogRemember extends ShadyMessageDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8362907566894682233L;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/** The remember check box. */
	private JCheckBox rememberCheckBox;

	public ShadyMessageDialogRemember(JDialog dialog, String title, String message, int typeOfButtons, Component comp) {
		super(dialog, title, message, typeOfButtons, comp);
		
	}
	
	public ShadyMessageDialogRemember(JFrame frame, String title, String message, int typeOfButtons, Component comp) {
		super(frame, title, message, typeOfButtons, comp);
		
	}
	
	/**
	 * Returns has user checked the checkbox for remember the answer.
	 *
	 * @return true, if selected to remember answer
	 */
	public boolean getRememberAnswer(){
		return this.rememberCheckBox.isSelected();
	}
	
	/* (non-Javadoc)
	 * @see gui.ShadyMessageDialog#initMessagePanel()
	 */
	protected int initMessagePanel() throws Exception{
		int maxwidth = super.initMessagePanel();
		
		// add checkbox for rememeber this answer
		
		JPanel rememberPanel = new JPanel();
		rememberPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,2,2));
		rememberPanel.setBackground(Color_schema.dark_30);
		
		Icon checkBoxIcon=new CheckBoxIcon();
		rememberCheckBox = new JCheckBox(checkBoxIcon);		
		rememberCheckBox.setSelected(false);
		rememberCheckBox.setBackground(Color_schema.dark_30);
		rememberCheckBox.setMaximumSize(new Dimension(20,20));
		rememberCheckBox.setPreferredSize(new Dimension(20,20));
		rememberCheckBox.setMinimumSize(new Dimension(20,20));
		rememberCheckBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
					setNObuttonEnabledState(!((JCheckBox)e.getSource()).isSelected());
				}
				catch(Exception ex){
					LOGGER.severe("Error in changing NO-button enabled!");
					ex.printStackTrace();
				}
				
			}
		});

		JLabel messageLabel = new JLabel("Remember overwriting");
		
		
		int checkingPanelWidth = messageLabel.getFontMetrics(Fonts.p20).stringWidth(messageLabel.getText()) +20;
		if(checkingPanelWidth > maxwidth)
			maxwidth = checkingPanelWidth;
		
		messageLabel.setFont(Fonts.p15);
		messageLabel.setForeground(Color_schema.white_180);
		rememberPanel.add(rememberCheckBox);
		rememberPanel.add(Box.createRigidArea(new Dimension(2,0)));
		rememberPanel.add(messageLabel);
		
		this.messagePanel.add(rememberPanel);
	
		return maxwidth;
	}

}
