package information;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.logging.Logger;

public class Fonts {
	public static Font inconsolata16b;
	public static Font b14;
	public static Font b15;
	public static Font b16;
	public static Font b17;
	public static Font b18;
	public static Font b19;
	public static Font b20;
	public static Font b21;
	public static Font b22;
	public static Font p14;
	public static Font p15;
	public static Font p16;
	public static Font p17;
	public static Font p18;
	public static Font p19;
	public static Font p20;
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/**
	 * Returns the SourceSansPro regular font.
	 *
	 * @param size the size of font
	 * @return the font SourceSansPro-Regualar
	 * @throws FontFormatException the font format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	private static Font getSourceSansPro(float size) throws FontFormatException, IOException, Exception{
		Font font = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/information/fonts/SourceSansPro-Regular.otf"));
		return font.deriveFont(size);
	}
	
	/**
	 * Returns the SourceSansPro bold font.
	 *
	 * @param size the size of font
	 * @return the font SourceSansPro-Bold
	 * @throws FontFormatException the font format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
private static Font getSourceSansProBold(float size) throws FontFormatException, IOException, Exception{
		
		Font font = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/information/fonts/SourceSansPro-Bold.otf"));
		return font.deriveFont(size);
	}

	/**
	 * Initializes the fonts.
	 */
	public static void initFonts(){
		try {
			b14=getSourceSansProBold(14.0f);
			b15=getSourceSansProBold(15.0f);
			b16=getSourceSansProBold(16.0f);
			b17=getSourceSansProBold(17.0f);
			b18=getSourceSansProBold(18.0f);
			b19=getSourceSansProBold(19.0f);
			b20=getSourceSansProBold(20.0f);
			b21=getSourceSansProBold(21.0f);
			b22=getSourceSansProBold(22.0f);
			p14=getSourceSansPro(14.0f);
			p15=getSourceSansPro(15.0f);
			p16=getSourceSansPro(16.0f);
			p17=getSourceSansPro(17.0f);
			p18=getSourceSansPro(18.0f);
			p19=getSourceSansPro(19.0f);
			p20=getSourceSansPro(20.0f);

		} catch (Exception e) {
			LOGGER.severe("Error in importing fonts");
			e.printStackTrace();
		}

	}
	
}
