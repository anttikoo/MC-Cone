package gui.saving.ImageSet;

import gui.Color_schema;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import managers.TaskManager;

/**
 * The Class SingleDrawImagePanel contains information of ImageLayers and MarkingLayers of them. 
 * User can select which ImageLayers and MarkingLayers are drawn to exported images.
 */
public class SingleDrawImagePanel extends JPanel{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 489319581557270431L;

	/** The single image. */
	private SingleImage singleImage;
	
	/** The grid position. */
	private int[] gridPosition=null; // row, column
	
	/** The title. */
	private String title;
	
	/** The font. */
	private Font font;
	
	/** The title panel. */
	private JPanel titlePanel;
	
	/** The BufferedImage. */
	private BufferedImage image;
	
	/** The task manager. */
	private TaskManager taskManager;
	
	/** The title field. The title of image is written above image and is adjustable. */
	private JTextField titleField;
	
	/** The panel dimension. */
	private Dimension panelDimension;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");


	/**
	 * Instantiates a new SingleDrawImagePanel.
	 *
	 * @param bi the bi
	 * @param title the title
	 * @param taskManager the task manager
	 * @param font the font
	 */
	public SingleDrawImagePanel(BufferedImage bi, String title,TaskManager taskManager, Font font ){
		try {
			this.setOpaque(true);
			this.taskManager=taskManager;
			this.singleImage=new SingleImage(bi, this.taskManager);
			this.title=title;
			this.setImage(bi);
			this.font=font;
			initPanel();
			this.revalidate();
			this.repaint();
		} catch (Exception e) {
			LOGGER.severe("Error in initializing SingleDrawImagePanel !");
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see java.awt.Component#getFont()
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Returns the grid position.
	 *
	 * @return the grid position
	 * @throws Exception the exception
	 */
	public int[] getGridPosition() throws Exception{
		return this.gridPosition;
	}

	/**
	 * Returns the image.
	 *
	 * @return the image
	 * @throws Exception the exception
	 */
	public BufferedImage getImage() throws Exception{
		return image;
	}

	/**
	 * Returns the panel size.
	 *
	 * @return the panel size
	 * @throws Exception the exception
	 */
	public Dimension getPanelSize() throws Exception{
		return this.panelDimension;
	}

	/**
	 * Returns the scaled image.
	 *
	 * @param destination the destination
	 * @return the scaled image
	 * @throws Exception the exception
	 */
	public BufferedImage getScaledImage(Dimension destination) throws Exception{
		return this.singleImage.getScaledImage(destination);
	}

	/**
	 * Returns the scaled image dimension.
	 *
	 * @return the scaled image dimension
	 * @throws Exception the exception
	 */
	public Dimension getScaledImageDimension() throws Exception{
		return this.singleImage.getScaledImageDimension();
	}

	/**
	 * Returns the title.
	 *
	 * @return the title
	 * @throws Exception the exception
	 */
	public String getTitle() throws Exception {
		return this.titleField.getText();
	}

	/**
	 * Returns the title panel height.
	 *
	 * @return the title panel height
	 * @throws Exception the exception
	 */
	public int getTitlePanelHeight() throws Exception{
		return this.titlePanel.getPreferredSize().height;
	}

	/**
	 * Returns the title width.
	 *
	 * @param f the Font
	 * @return the title width
	 * @throws Exception the exception
	 */
	private int getTitleWidth(Font f) throws Exception{
		return this.titleField.getFontMetrics(f).stringWidth(this.title);
	}

	/**
	 * Initializes the panel.
	 *
	 * @throws Exception the exception
	 */
	private void initPanel() throws Exception{

		this.setLayout(new BorderLayout());
		this.setBackground(Color_schema.white_230);
		titlePanel = new JPanel();
		this.titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
		this.titlePanel.setBackground(Color_schema.white_230);
		this.titleField=new JTextField(this.title);
		this.titleField.setFont(font);
		this.titleField.setHorizontalAlignment(JLabel.LEFT);
		this.titleField.setBackground(Color_schema.white_230);
		this.titleField.setForeground(Color_schema.dark_20);
		this.titleField.setMinimumSize(new Dimension(300,font.getSize()+6));
		this.titleField.setBorder(null);
		this.titlePanel.add(this.titleField);
		this.add(titlePanel, BorderLayout.PAGE_START);
		this.add(this.singleImage, BorderLayout.CENTER);
	}

	/**
	 * Sets the dimension for the single ImagePanel drawn on ImageSet window.
	 *
	 * @param dim the dim
	 * @param titlePanelHeight the title panel height
	 * @return the dimension
	 * @throws Exception the exception
	 */
	public Dimension setDimension(Dimension dim, int titlePanelHeight) throws Exception{
		Dimension imageDimension=this.singleImage.setImageDimension(new Dimension(dim.width,dim.height-titlePanelHeight));
		panelDimension = new Dimension(imageDimension.width, imageDimension.height+titlePanelHeight);

		this.setMaximumSize(panelDimension);
		this.setPreferredSize(panelDimension);
		this.setMinimumSize(panelDimension);
		this.titlePanel.setMaximumSize(new Dimension(panelDimension.width,titlePanelHeight));
		this.titlePanel.setMinimumSize(new Dimension(panelDimension.width,titlePanelHeight));
		this.titlePanel.setPreferredSize(new Dimension(panelDimension.width,titlePanelHeight));
		this.titleField.setMaximumSize(new Dimension(panelDimension.width,titlePanelHeight));
		this.titleField.setMinimumSize(new Dimension(panelDimension.width,titlePanelHeight));
		this.titleField.setPreferredSize(new Dimension(panelDimension.width,titlePanelHeight));
	//	updateFont(this.font); // if the title don't fit to field -> decrease font size
		return panelDimension;

	}

	/**
	 * Sets the grid position.
	 *
	 * @param r the r
	 * @param c the c
	 * @throws Exception the exception
	 */
	public void setGridPosition(int r, int c) throws Exception{
		this.gridPosition=new int[]{r,c};
	}

	/**
	 * Sets the selected grid position.
	 *
	 * @param rc the new grid position
	 * @throws Exception the exception
	 */
	public void setGridPosition(int[] rc) throws Exception{
		this.gridPosition=rc;
	}

	/**
	 * Sets the grid position null.
	 *
	 * @throws Exception the exception
	 */
	public void setGridPositionNull() throws Exception{
		this.gridPosition=null;
	}

	/**
	 * Sets the image.
	 *
	 * @param image the new image
	 * @throws Exception the exception
	 */
	public void setImage(BufferedImage image) throws Exception {
		this.image = image;
	}

	/**
	 * Update font of titles.
	 *
	 * @param f the Font
	 * @throws Exception the exception
	 */
	public void updateFont(Font f) throws Exception{
		if(this.titlePanel.getPreferredSize().width >0)
		while(this.titlePanel.getPreferredSize().width < getTitleWidth(f)){
			f=new Font(f.getFontName(),f.getStyle(),f.getSize()-1);
		}
		this.font = f;
		this.titleField.setFont(this.font);
		this.titleField.repaint();

	}

}
