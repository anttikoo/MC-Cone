package gui;

import information.Fonts;
import information.ID;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;



/**
 * The Class ShadyMessageDialog. Shows message and buttons for selection.
 */
public class ShadyMessageDialog extends JDialog{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3759564265081943415L;

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/** The message. */
	private String message;
	
	/** The title. */
	private String title;
	
	/** The title label. */
	private JLabel titleLabel;
	
	/** The type of buttons. */
	private int typeOfButtons;
	
	/** The return value. */
	private int returnValue=-1;
	
	/** The parent component. */
	protected Component parentComponent;
	
	/** The dialog back panel. */
	protected JPanel dialogBackPanel;
	
	/** The message panel. */
	protected JPanel messagePanel;
	
	/** The button panel. */
	private JPanel buttonPanel;
	

	/**
	 * Instantiates a new shady message dialog.
	 *
	 * @param dialog the dialog
	 * @param title the title
	 * @param message the message
	 * @param typeOfButtons the type of buttons
	 * @param comp the parent Component
	 */
	public ShadyMessageDialog(JDialog dialog, String title, String message, int typeOfButtons, Component comp){
		super(dialog,true);
		super.setLocationRelativeTo(comp);
		
		try {
			this.setLocationRelativeTo(comp);
			this.parentComponent = comp;
			this.title=title;
			this.message=message;
			this.typeOfButtons=typeOfButtons;	
			initDialog();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing message dialog!");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Instantiates a new shady message dialog.
	 *
	 * @param frame the frame
	 * @param title the title
	 * @param message the message
	 * @param typeOfButtons the type of buttons
	 * @param comp the comp
	 */
	public ShadyMessageDialog(JFrame frame, String title, String message, int typeOfButtons, Component comp){
		super(frame,true);
		
		super.setLocationRelativeTo(comp);
		try {
			this.setLocationRelativeTo(comp);
			this.parentComponent = comp;
			this.title=title;
			this.message=message;
			this.typeOfButtons=typeOfButtons;	
			initDialog();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing message dialog!");
			e.printStackTrace();
		}
		
	}

	
	
	/**
	 * Creates the button by given ButtonID.
	 *
	 * @param buttonID the button id
	 * @return the j button
	 * @throws Exception the exception
	 */
	protected JButton createButton(final int buttonID) throws Exception{
		JButton button=new JButton(getButtonText(buttonID));
		int maxStringWidth = button.getFontMetrics(Fonts.b15).stringWidth(getButtonText(buttonID));
		button.setPreferredSize(new Dimension(maxStringWidth+20,30));
		button.setMinimumSize(new Dimension(maxStringWidth+20,30));
		button.setMaximumSize(new Dimension(maxStringWidth+20,30));
		button.setBackground(Color_schema.dark_20);
		button.setFont(Fonts.b15);

		if(buttonID == ID.NO || buttonID == ID.CANCEL){
			button.setForeground(Color_schema.orange_dark);
			button.setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 2));
		//	addMouseListenerToCancelButtons(button);
			MouseListenerCreator.addMouseListenerToCancelButtons(button);
			MouseListenerCreator.addKeyListenerToButton(button, ID.BUTTON_CANCEL);
		}
		else{
			MouseListenerCreator.addMouseListenerToNormalButtons(button);
		//	addMouseListenerToNormalButtons(button);
			if(buttonID==ID.OK)
			MouseListenerCreator.addKeyListenerToButton(button, ID.BUTTON_ENTER);
		}

		button.setFocusable(false);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					returnValue=buttonID;
					hideDialog();
				} catch (Exception e1) {
					LOGGER.severe("Error in closing message dialog!");
					e1.printStackTrace();
				}
			}
		});


		return button;
	}
	
	/**
	 * Gets the Text of JButton by given ID.
	 *
	 * @param id the ID
	 * @return the button text
	 * @throws Exception the exception
	 */
	protected String getButtonText(int id) throws Exception{
		switch (id) {

		case ID.CANCEL:
			return "CANCEL";

		case ID.YES:
			return "YES";

		case ID.NO:
			return "NO";

		case ID.OVERWRITE:
			return "OVERWRITE";

		case ID.APPEND:
			return "APPEND";

		default:
			return "OK";
		}
	}
	
	/**
	 * Gets the backPanel of dialog.
	 *
	 * @return the dialog back panel
	 * @throws Exception the exception
	 */
	public JPanel getDialogBackPanel() throws Exception{
		return this.dialogBackPanel;
	}

	/**
	 * Calculates and returns the width of downButton Panel. Width depends on how many buttons there are.
	 *
	 * @return the down button panel width
	 * @throws Exception the exception
	 */
	private int getDownButtonPanelWidth() throws Exception{
		int width=0;
		Component[] bComp=this.buttonPanel.getComponents();
		for (int i = 0; i < bComp.length; i++) {
			if(bComp[i] instanceof JButton){
			JButton bb=(JButton)bComp[i];
			width+=bb.getMaximumSize().width;
			}
			
		}
		return width;
	}



	/**
	 * Gets the first button.
	 *
	 * @return the first button
	 * @throws Exception the exception
	 */
	

	public JButton getFirstButton() throws Exception{
		Component[] buttons=this.buttonPanel.getComponents();
		if(buttons.length>0 && buttons[0] instanceof JButton)
			return (JButton)buttons[0];
		else
			return null;
	}

	/**
	 * Gets the JPanel messagePanel.
	 *
	 * @return JPanel MessagePanel
	 * @throws Exception the exception
	 */
	public JPanel getMessagePanel() throws Exception{
		return this.messagePanel;
	}

	/**
	 * Hides dialog.
	 *
	 * @throws Exception the exception
	 */
	private void hideDialog() throws Exception{
		
		this.setVisible(false);
		this.dispose();
	}
	
	/**
	 * Initializes the dialog.
	 */
	protected void initDialog(){

		try {
			this.setResizable(false);		
			this.setBounds(WindowLocator.getVisibleWindowBounds(parentComponent));
			this.setUndecorated(true); // no titlebar or buttons
			this.setBackground(new Color(0,0,0,0)); // transparent color
			this.setContentPane(new ContentPane());
			this.getContentPane().setLayout(new GridBagLayout());
			this.getContentPane().setBackground(Color_schema.dark_30);
		

			dialogBackPanel = new JPanel();
			dialogBackPanel.setBackground(Color_schema.dark_30);
			dialogBackPanel.setLayout(new BorderLayout());	
			dialogBackPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color_schema.white_230));
			dialogBackPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			dialogBackPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
			if(dialogBackPanel.getPreferredSize().getWidth()<500)
				dialogBackPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.95), (int)(this.getBounds().getHeight()*0.95)));

			JPanel titlePanel = new JPanel();
			titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			titlePanel.setBackground(Color_schema.dark_30);
			titleLabel = new JLabel(this.title);		
			titleLabel.setFont(Fonts.b20);
			
			int titleWidth = titleLabel.getFontMetrics(Fonts.b20).stringWidth(titleLabel.getText());
			titleLabel.setForeground(Color_schema.white_230);
			titlePanel.add(Box.createRigidArea(new Dimension(20,0)));
			titlePanel.add(titleLabel);

			int maxwidth = initMessagePanel();
					
			if(titleWidth > maxwidth)
				maxwidth = titleWidth;
			JScrollPane messageScrollPane = new JScrollPane(messagePanel);
			messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);


			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
			buttonPanel.setBackground(Color_schema.dark_30);
			buttonPanel.add(Box.createHorizontalGlue());
			JButton yesButton =null;
			JButton okButton =null;
			JButton cancelButton = null;
			
			// add buttons by the type of message box
			switch (typeOfButtons) {
				case ID.OK:
					okButton = createButton(ID.OK);
					MouseListenerCreator.addKeyListenerToButton(okButton, ID.BUTTON_ENTER);
					buttonPanel.add(okButton);
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					break;
				case ID.YES_NO:
					yesButton = createButton(ID.YES);
					MouseListenerCreator.addKeyListenerToButton(yesButton, ID.BUTTON_ENTER);
					
					buttonPanel.add(yesButton);
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					buttonPanel.add(createButton(ID.NO));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					break;					
				case ID.YES_NO_CANCEL:
					yesButton = createButton(ID.YES);
					MouseListenerCreator.addKeyListenerToButton(yesButton, ID.BUTTON_ENTER);
					cancelButton = createButton(ID.CANCEL);
					MouseListenerCreator.addKeyListenerToButton(cancelButton, ID.BUTTON_CANCEL);
					buttonPanel.add(yesButton);
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					buttonPanel.add(createButton(ID.NO));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					buttonPanel.add(cancelButton);
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					break;
				case ID.APPEND_OVERWRITE_CANCEL:
					cancelButton = createButton(ID.CANCEL);
					MouseListenerCreator.addKeyListenerToButton(cancelButton, ID.BUTTON_CANCEL);
					buttonPanel.add(createButton(ID.APPEND));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					buttonPanel.add(createButton(ID.OVERWRITE));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					buttonPanel.add(cancelButton);
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					break;
				case ID.CANCEL:
					cancelButton = createButton(ID.CANCEL);
					MouseListenerCreator.addKeyListenerToButton(cancelButton, ID.BUTTON_CANCEL);
					buttonPanel.add(cancelButton);
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					break;

				case ID.NO_BUTTONS:

					// do nothing
					break;
				}
			maxwidth=Math.max(maxwidth,getDownButtonPanelWidth());
			dialogBackPanel.setMaximumSize(new Dimension(maxwidth+200,150));
			dialogBackPanel.setPreferredSize(new Dimension(maxwidth+100,messagePanel.getPreferredSize().height+90));
			titlePanel.setMinimumSize(new Dimension(maxwidth+100,50));
			titlePanel.setMaximumSize(new Dimension((int)dialogBackPanel.getMaximumSize().getWidth(),50));
		//	messagePanel.setMaximumSize(new Dimension((int)dialogBackPanel.getMaximumSize().getWidth(),60));
			buttonPanel.setMaximumSize(new Dimension((int)dialogBackPanel.getMaximumSize().getWidth(),40));
			buttonPanel.setMinimumSize(new Dimension(maxwidth,40));
			buttonPanel.setPreferredSize(new Dimension(maxwidth,40));
		//	messageScrollPane.setPreferredSize(new Dimension((int)dialogBackPanel.getPreferredSize().getWidth(),60));

			dialogBackPanel.add(titlePanel, BorderLayout.PAGE_START);
			dialogBackPanel.add(messageScrollPane, BorderLayout.CENTER);
			dialogBackPanel.add(buttonPanel, BorderLayout.PAGE_END);
		
			this.getContentPane().add(dialogBackPanel);
		


		} catch (Exception e) {
			LOGGER.severe("Error in initializing Dialog: " +e.getClass().toString() + " :" +e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the message panel.
	 *
	 * @return the int
	 * @throws Exception the exception
	 */
	protected int initMessagePanel() throws Exception{
		messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.PAGE_AXIS));
		messagePanel.setBackground(Color_schema.dark_30);
		
		JPanel messageTextPanel = new JPanel();
		messageTextPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,5));
		messageTextPanel.setBackground(Color_schema.dark_30);
		JLabel messageLabel = new JLabel(this.message);
		
		int maxwidth =messageLabel.getFontMetrics(Fonts.p18).stringWidth(messageLabel.getText())+20;
		
		messagePanel.setPreferredSize(new Dimension(maxwidth,30));
		messageLabel.setFont(Fonts.p18);
		messageLabel.setForeground(Color_schema.white_230);
		messageTextPanel.add(messageLabel);
		messagePanel.add(messageTextPanel, BorderLayout.CENTER);
		
		return maxwidth;
	}
	
	/**
	 * Sets the NO-button enabled state.
	 *
	 * @param enabledState the new n obutton enabled state
	 * @throws Exception the exception
	 */
	protected void setNObuttonEnabledState(boolean enabledState) throws Exception{
		Component[] buttons = this.buttonPanel.getComponents();
		
		if(buttons != null && buttons.length>0){
			for(int i = 0;i<buttons.length;i++){
				Component c = buttons[i];
				
				if(c instanceof JButton){
					if(((JButton)c).getText().equals("NO")){
						((JButton)c).setEnabled(enabledState);
						return;
					}				
				}			
			}				
		}		
	}
	

	/**
	 * Sets the panel position with given Rectangle.
	 *
	 * @param bounds the new panel position
	 * @throws Exception the exception
	 */
	public void setPanelPosition(Rectangle bounds) throws Exception{
		if(bounds != null)
			this.setBounds(bounds);
	
	}

/**
 * Shows dialog.
 *
 * @return the int
 */
public int showDialog(){
	try {
		this.validate();
		this.repaint();

	SwingUtilities.invokeLater(new Runnable() {
		
		@Override
		public void run() {
			setVisible(true);
			
		}
	});
		//this.setVisible(true);

	//	this.repaint();
		return returnValue;
	} catch (Exception e) {
		LOGGER.severe("Error in showing message dialog!");
		e.printStackTrace();
		return -1;
	}
}



}
