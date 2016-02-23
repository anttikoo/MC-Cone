package gui.grid;

import java.util.logging.Logger;

/**
 * The Class SingleGridSize. Contains data for Grid sizes and amount of rows and columns in grid.
 */
public class SingleGridSize {
	
/** The Constant LOGGER. */
private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
/** The grid cell size. */
private int gridCellSize; // size in pixels in preview panel of GridPropertiesPanel

/** The height align value. */
private int heightAlign;

/** The width align value. */
private int widthAlign;

/** The rows. */
private int rows;

/** The columns. */
private int columns;

/**
 * Instantiates a new single grid size.
 *
 * @param size the size in pixels
 * @param hAlign the height align
 * @param wAlign the width align
 * @param r the rows
 * @param c the columns
 */
public SingleGridSize(int size, int hAlign, int wAlign, int r, int c){
	try {
		this.setGridCellSize(size);
		this.setHeightAlign(hAlign);
		this.setWidthAlign(wAlign);
		this.setRows(r);
		this.setColumns(c);
	} catch (Exception e) {
		LOGGER.severe("Error in initializing single Grid Size!");
		e.printStackTrace();
	}
}

/**
 * Gets the columns.
 *
 * @return the columns
 * @throws Exception the exception
 */
public int getColumns()  throws Exception{
	return columns;
}

/**
 * Gets the grid cell size.
 *
 * @return the grid cell size in pixels
 * @throws Exception the exception
 */
public int getGridCellSize()  throws Exception{
	return gridCellSize;
}

/**
 * Gets the height align.
 *
 * @return the height align
 * @throws Exception the exception
 */
public int getHeightAlign()  throws Exception{
	return heightAlign;
}

/**
 * Gets the rows.
 *
 * @return the rows
 * @throws Exception the exception
 */
public int getRows()  throws Exception {
	return rows;
}

/**
 * Gets the width align.
 *
 * @return the width align
 * @throws Exception the exception
 */
public int getWidthAlign()  throws Exception{
	return widthAlign;
}

/**
 * Sets the columns.
 *
 * @param columns the new columns
 * @throws Exception the exception
 */
public void setColumns(int columns)  throws Exception{
	this.columns = columns;
}

/**
 * Sets the grid cell size.
 *
 * @param gridCellSize the new grid cell size
 * @throws Exception the exception
 */
public void setGridCellSize(int gridCellSize) throws Exception {
	this.gridCellSize = gridCellSize;
}

/**
 * Sets the height align.
 *
 * @param heightAlign the new height align
 * @throws Exception the exception
 */
public void setHeightAlign(int heightAlign)  throws Exception{
	this.heightAlign = heightAlign;
}

/**
 * Sets the rows.
 *
 * @param rows the new rows
 * @throws Exception the exception
 */
public void setRows(int rows)  throws Exception{
	this.rows = rows;
}

/**
 * Sets the width align.
 *
 * @param widthAlign the new width align
 * @throws Exception the exception
 */
public void setWidthAlign(int widthAlign)  throws Exception{
	this.widthAlign = widthAlign;
}
}
