package operators;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JProgressBar;
import gui.Color_schema;

/**
 * The Class ShinyProgressBar. A progressbar with oranger bar.
 */
public class ShinyProgressBar extends JProgressBar {
  
    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6426655716699510555L;
	
	/** The Constant DISABLED_PERCENT_STRING. */
	private static final String DISABLED_PERCENT_STRING = " --- ";
    
    /** The Constant gradientEndingColor. */
    private static final Color gradientEndingColor = Color_schema.dark_50;
    
    /** The Constant borderColor. */
    private static final Color borderColor = Color_schema.dark_50;
    
    /** The Constant disabledBorderColor. */
    private static final Color disabledBorderColor = Color_schema.dark_40;
    
    /** The gradient. */
    private static GradientPaint gradient;
    
    /** The Constant TOP_INSET. */
    private static final int TOP_INSET = 0;
    
    /** The Constant LEFT_INSET. */
    private static final int LEFT_INSET = 1;

    /** The Constant BOTTOM_INSET. */
    private static final int BOTTOM_INSET = 2;
    
    /** The Constant RIGHT_INSET. */
    private static final int RIGHT_INSET = 3;

    /** The Constant PREFERRED_PERCENT_STRING_MARGIN_WIDTH. */
    private static final int PREFERRED_PERCENT_STRING_MARGIN_WIDTH = 3;
    
    // public static final Color PREFERRED_PROGRESS_COLOR = new Color(0x1869A6);
    public static final Color PREFERRED_PROGRESS_COLOR = Color_schema.orange_medium;
    
    /** The old width. */
    private int oldWidth;
    
    /** The old height. */
    private int oldHeight;
    
    /** The display width. */
    private int displayWidth;

    /** The display height. */
    private int displayHeight;

   /** The insets. */
    private int insets[] = new int[4];

    /** The percent string visible. */
    private boolean percentStringVisible = true;

    /** The progress color. */
    private Color progressColor;

    /** The max percent string. */
    private String maxPercentString;

    /**
     * Instantiates a new shiny progress bar.
     */
    public ShinyProgressBar() {
        progressColor = PREFERRED_PROGRESS_COLOR;
    }

    /**
     * Returns the progress color.
     *
     * @return the progress color
     * @throws Exception the exception
     */
    public Color getProgressColor() throws Exception {
        return progressColor;
    }

    @Override
    protected void paintBorder(Graphics g) {
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        int w = displayWidth != 0 ? displayWidth - 1 : getWidth() - 1;
        int h = displayHeight != 0 ? displayHeight - 1 : getHeight() - 1;

        int x = insets[LEFT_INSET];
        int y = insets[TOP_INSET];
        w -= (insets[RIGHT_INSET] << 1);
        h -= (insets[BOTTOM_INSET] << 1);

        if (gradient == null) {
            gradient = new GradientPaint(0.0f, 0.0f, Color.BLACK, 0.0f, h, gradientEndingColor);
        }
        Graphics2D g2d = (Graphics2D) g;
        
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        
        RenderingHints rh2 = new RenderingHints(
        		 RenderingHints.KEY_INTERPOLATION,
                 RenderingHints.VALUE_INTERPOLATION_BICUBIC);
     
        
       g2d.setRenderingHints(rh);
       g2d.setRenderingHints(rh2);

        if (isOpaque()) {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.translate(x, y);

        if (percentStringVisible) {
            FontMetrics fm = g.getFontMetrics();
            int stringW = 0;
            int stringH = 0;

            g2d.setColor(getForeground());

            if (isEnabled()) { 
                int p = getValue();
                String percent = Integer.toString(p, 10) + " %";
                if (p < 10) {
                    percent = "0" + percent;
                }

                if (maxPercentString == null) {
                    maxPercentString = Integer.toString(getMaximum(), 10) + " %";
                }
                stringW = fm.stringWidth(maxPercentString);
                stringH = ((h - fm.getHeight()) / 2) + fm.getAscent();

                g2d.drawString(percent, w - stringW, stringH);
            } else {
                stringW = fm.stringWidth(DISABLED_PERCENT_STRING);
                stringH = ((h - fm.getHeight()) / 2) + fm.getAscent();

                g2d.drawString(DISABLED_PERCENT_STRING, w - stringW, stringH);
            }
            w -= (stringW + PREFERRED_PERCENT_STRING_MARGIN_WIDTH);            
        }

        // Control Border
        g2d.setColor(isEnabled() ? borderColor : disabledBorderColor);
        g2d.drawLine(1, 0, w - 1, 0);
        g2d.drawLine(1, h, w - 1, h);
        g2d.drawLine(0, 1, 0, h - 1);
        g2d.drawLine(w, 1, w, h - 1);

        // Fill in the progress
        int min = getMinimum();
        int max = getMaximum();
        int total = max - min;
        float dx = (float) (w - 2) / (float) total;
        int value = getValue();
        int progress = 0; 
        if (value == max) {
            progress = w - 1;
        } else {
            progress = (int) (dx * getValue());            
        }

        g2d.setColor(progressColor);
        g2d.fillRoundRect(1, 1, progress, h-2, 10, 10);
        
       GradientPaint gp = new GradientPaint(0.0f, 0.0f, Color_schema.orange_bright, 0.0f, h/2, Color_schema.orange_medium);

        g2d.setPaint(gp);
        
        g2d.fillRoundRect(1, 1, progress, h/2, 10, 10);
        
        gp = new GradientPaint(0.0f, 0.0f, Color_schema.orange_dark, 0.0f, h/2-2, Color_schema.orange_medium);

        g2d.setPaint(gp);
        g2d.fillRoundRect(1, h/2+2, progress, h/2-2, 10, 10);
        
    }

    /**
     * Sets the display size.
     *
     * @param width the width
     * @param height the height
     * @throws Exception the exception
     */
    public void setDisplaySize(int width, int height) throws Exception {
        displayWidth = width;
        displayHeight = height;
    }

    public void setInsets(int top, int left, int bottom, int right) {
        insets[TOP_INSET] = top;
        insets[LEFT_INSET] = left;
        insets[BOTTOM_INSET] = bottom;
        insets[RIGHT_INSET] = right;
    }

    @Override
    public void setMaximum(int n) {
        super.setMaximum(n);
        maxPercentString = Integer.toString(n, 10) + " %";
    }

    /**
     * Sets the percent string visible.
     *
     * @param percentStringVisible the new percent string visible
     * @throws Exception the exception
     */
    public void setPercentStringVisible(boolean percentStringVisible) throws Exception{
        this.percentStringVisible = percentStringVisible;
    }

    /**
     * Sets the progress color.
     *
     * @param progressColor the new progress color
     * @throws Exception the exception
     */
    public void setProgressColor(Color progressColor) throws Exception {
        this.progressColor = progressColor;
    }

    /**
     * Update graphics.
     */
    public void updateGraphics() {
        update(getGraphics());
    }

    @Override
    public void validate() {
        int w = getWidth();
        int h = getHeight();

        super.validate();
        if (oldWidth != w || oldHeight != h) {
            oldWidth = w;
            oldHeight = h;
            gradient = null;
        }
    }
}
