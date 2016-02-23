package gui.saving.ImageSet;

import java.awt.image.BufferedImage;

/**
 * The Class BufferedImageWithName. Contains BufferedImage and name for image.
 */
public class BufferedImageWithName {

/** The image. */
private BufferedImage image;

/** The image name. */
private String imageName;

/**
 * Instantiates a new buffered image with name.
 *
 * @param bi the bi
 * @param name the name
 */
public BufferedImageWithName(BufferedImage bi, String name){
	this.setImage(bi);
	this.setImageName(name);
}

/**
 * Returns the image.
 *
 * @return the image
 */
public BufferedImage getImage() {
	return image;
}

/**
 * Returns the image name.
 *
 * @return the image name
 */
public String getImageName() {
	return imageName;
}

/**
 * Sets the image.
 *
 * @param image the new image
 */
public void setImage(BufferedImage image) {
	this.image = image;
}

/**
 * Sets the image name.
 *
 * @param imageName the new image name
 */
public void setImageName(String imageName) {
	this.imageName = imageName;
}
}
