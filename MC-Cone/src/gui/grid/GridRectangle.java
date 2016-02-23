package gui.grid;

import gui.Color_schema;
import information.Fonts;
import information.SharedVariables;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;

// TODO: Auto-generated Javadoc
/**
 * The Class GridRectangle. Contains data of single Grid cell: position and is selected.
 */
public class GridRectangle extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8475739093023436354L;

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
 
	/** The grid row and column number. */
	private Dimension grid_row_column;
 
	/** The row. */
	private int row;
 
	/** The column. */
	private int column;
 
	/** The is selected. */
	private boolean isSelected=true;

	/** The label. */
	private JLabel label;

	/** The grid properties panel. */
	private GridPropertiesPanel gridPropertiesPanel;

 /**
  * Instantiates a new grid rectangle.
  *
  * @param r the row position
  * @param c the column position
  * @param selected boolean is grid cell selected
  * @param gpp the gpp
  */
 public GridRectangle(int r, int c, boolean selected, GridPropertiesPanel gpp){
	 try {
		this.isSelected=selected;
		 this.gridPropertiesPanel=gpp;
		 this.setBounds(0,0,100, 100);
		 this.setRow(r);
		 this.setColumn(c);
		 this.setBackground(Color_schema.orange_medium);
		 this.setLayout(new GridBagLayout());

		 label = new JLabel("+");
		 label.setFont(Fonts.b14);
		 label.setForeground(Color_schema.dark_30);
		 this.add(label);
		 this.setToolTipText("This grid cell is used.");

		 setUpMouseListener();
		 updatePanel();
	} catch (Exception e) {
		LOGGER.severe("Error in initializing GridRectangle!");
		e.printStackTrace();
	}
 }


 /**
  * Checks if is grid selected.
  *
  * @return true, if is selected
  * @throws Exception the exception
  */
 public boolean isSelected() throws Exception{
	return isSelected;
}

/**
 * Sets the shown.
 *
 * @param selected boolean is grid cell selected
 * @throws Exception the exception
 */
public void setSelected(boolean selected) throws Exception {
	this.isSelected = selected;
}


/**
 * Gets the grid_row_column.
 *
 * @return the Dimension grid_row_column
 * @throws Exception the exception
 */
public Dimension getGrid_row_column() throws Exception{
	return grid_row_column;
}


/**
 * Sets the grid_row_column.
 *
 * @param grid_row_column the new grid_row_column
 * @throws Exception the exception
 */
public void setGrid_row_column(Dimension grid_row_column) throws Exception {
	this.grid_row_column = grid_row_column;
}


/**
 * Sets up the mouseListener.
 *
 * @throws Exception the exception
 */
private void setUpMouseListener() throws Exception{

	 this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					if(SharedVariables.canUserSelectGridRectangles){
						if(isSelected() ){
							int counter=0;
							// check that at least one selected cell will be remain				
							JPanel p = ((JPanel)((GridRectangle)e.getSource()).getParent()); // get owner of GridRectangles
							if(p != null){
								Component[] c=p.getComponents();
								if(c != null && c.length>0){
									System.out.println(c.length);
									
									for (int i = 0; i < c.length; i++) { // go through GridRectangles
										Component sc = c[i];
										if(sc != null){
											if(sc instanceof GridRectangle){
											GridRectangle gr = (GridRectangle)sc;
											if (gr != null){
												if(gr.isSelected)
													counter++;
											}
											
											}
										}
									}
									
								}	
							}
							if(counter>1) // still remains other selected cells
								setSelected(false);
						}
						else
							setSelected(true);

						updatePanel();
						gridPropertiesPanel.countRandomPercentBySelectedGridRectangles();
					}
				} catch (Exception e1) {
					LOGGER.severe("Error in releasing mouse on Grid Rectangle!");
					e1.printStackTrace();
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {


			}

			@Override
			public void mouseExited(MouseEvent e) {
				

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				

			}

			@Override
			public void mouseClicked(MouseEvent e) {


			}


		});
}



/**
 * Updates the color and tooltiptext of JPanel of this grid cell.
 *
 * @throws Exception the exception
 */
public void updatePanel() throws Exception{
	if(isSelected){
		this.setBackground(Color_schema.orange_medium);
		 label.setForeground(Color_schema.dark_30);
		 label.setText("+");
		 this.setToolTipText("This grid cell is used.");
	}
	else{
		this.setBackground(Color_schema.dark_30);
		label.setForeground(Color_schema.orange_medium);
		 label.setText("-");
		 this.setToolTipText("This grid cell is NOT used.");
	}
	this.repaint();
}


/**
 * Returns the column.
 *
 * @return the column
 * @throws Exception the exception
 */
public int getColumn() throws Exception {
	return column;
}


/**
 * Sets the column.
 *
 * @param column the new column
 * @throws Exception the exception
 */
public void setColumn(int column) throws Exception {
	this.column = column;
}


/**
 * returns the row in grid.
 *
 * @return the row
 * @throws Exception the exception
 */
public int getRow()  throws Exception{
	return row;
}


/**
 * Sets the row in grid.
 *
 * @param row the new row
 * @throws Exception the exception
 */
public void setRow(int row)  throws Exception{
	this.row = row;
}


}
