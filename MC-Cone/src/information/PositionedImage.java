package information;

import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * The Class PositionedImage. Contains image and its position at window.
 */
public class PositionedImage {
	
	/** The image. */
	private BufferedImage image = null;
	
	/** The position. */
	private Point position =null;
	
	/**
	 * Instantiates a new positioned image.
	 *
	 * @param im the BufferedImage
	 */
	public PositionedImage(BufferedImage im){
		this.setImage(im);
		this.setPosition(new Point(0,0));
	}
	
	/**
	 * Instantiates a new positioned image.
	 *
	 * @param im the BufferedImage
	 * @param p the Point
	 */
	public PositionedImage(BufferedImage im, Point p){
		this.setImage(im);
		this.setPosition(p);
	}
	
	/**
	 * Returns the image.
	 *
	 * @return the BufferedImage
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * Returns the position.
	 *
	 * @return the position Point
	 */
	public Point getPosition() {
		return position;
	}
	
	/**
	 * Sets the image.
	 *
	 * @param image the new Bufferedimage
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	/**
	 * Sets the position.
	 *
	 * @param position the new position Point
	 */
	public void setPosition(Point position) {
		this.position = position;
	}
}
