package information;

import java.awt.Rectangle;

/**
 * The Class PositionedRectangle. Contains data of GRID rectangle: row, column, position and size.
 */
public class PositionedRectangle extends Rectangle{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1677210619511129932L;

	/** The row position. */
	private int row;
	
	/** The column position. */
	private int column;
	
	/** The selected. */
	private boolean selected;

	
	/**
	 * Instantiates a new PositionedRectangle.
	 * 
	 *
	 * @param x the position x
	 * @param y the postion y
	 * @param width the width
	 * @param height the height
	 * @param r the row
	 * @param c the column
	 * @param selected the is GRID selected
	 */
	public PositionedRectangle(int x, int y, int width, int height, int r, int c, boolean selected){
		super(x,y,width,height);
		this.row=r;
		this.column=c;
		this.selected=selected;
	}



	/**
	 * Returns the column.
	 *
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns the row.
	 *
	 * @return the row
	 */
	public int getRow() {
		return row;
	}



	/**
	 * Checks is the position as given row and column parameters.
	 *
	 * @param r the r
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean hasPosition(int r, int c){
		if(r==this.row && c==this.column)
			return true;
		return false;
	}



	/**
	 * Checks if is selected.
	 *
	 * @return true, if is selected
	 */
	public boolean isSelected() {
		return selected;
	}



	/**
	 * Sets the column.
	 *
	 * @param column the new column
	 */
	public void setColumn(int column) {
		this.column = column;
	}



	/**
	 * Sets the row.
	 *
	 * @param row the new row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Sets the rectangle as selected.
	 *
	 * @param selected the new selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
