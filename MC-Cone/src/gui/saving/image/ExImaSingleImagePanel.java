package gui.saving.image;

import java.awt.Component;
import java.util.ArrayList;
import java.util.logging.Logger;

import gui.saving.SaverDialog;
import gui.saving.SingleImagePanel;
import gui.saving.SingleMarkingPanel;
import information.ImageLayer;
import information.MarkingLayer;

/**
 * The Class ExImaSingleImagePanel. Shows information of single ImageLayer and its MarkingLayers when exporting ImageLayers.
 */
public class ExImaSingleImagePanel extends SingleImagePanel{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2085833168256742639L;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * Instantiates a new Panel.
	 *
	 * @param imageLayer the image layer
	 * @param saverDialog the saver dialog
	 */
	public ExImaSingleImagePanel(ImageLayer imageLayer, SaverDialog saverDialog) {
		super(imageLayer, saverDialog);
		
	}

	/* (non-Javadoc)
	 * @see gui.saving.SingleImagePanel#createSingleMarkingPanel(information.MarkingLayer)
	 */
	protected ExImaSingleMarkingPanel createSingleMarkingPanel(MarkingLayer markingLayer) throws Exception{
		return new ExImaSingleMarkingPanel(markingLayer);
	}

	/* (non-Javadoc)
	 * @see gui.saving.SingleImagePanel#getAllSelectedMarkingLayerIDs()
	 */
	public ArrayList<Integer> getAllSelectedMarkingLayerIDs() throws Exception{
		ArrayList<Integer> selectedMarkingLayerIDs=new ArrayList<Integer>();
		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				SingleMarkingPanel smp= (SingleMarkingPanel)sMarkingList[i];
				if(smp.isSelected()){
					selectedMarkingLayerIDs.add(smp.getMarkingLayer().getLayerID());
				}
			}
		}
		return selectedMarkingLayerIDs;
	}

	/* (non-Javadoc)
	 * @see gui.saving.SingleImagePanel#getAllSelectedMarkingLayers()
	 */
	public ArrayList<MarkingLayer> getAllSelectedMarkingLayers() throws Exception{
		ArrayList<MarkingLayer> selectedMarkingLayers=new ArrayList<MarkingLayer>();
		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				ExImaSingleMarkingPanel smp= (ExImaSingleMarkingPanel)sMarkingList[i];
				if(smp.isSelected()){
					selectedMarkingLayers.add(smp.getMarkingLayer());
				}
			}
		}
		return selectedMarkingLayers;

	}

	/* (non-Javadoc)
	 * @see gui.saving.SingleImagePanel#initProperFilePathForSaving(information.ImageLayer)
	 */
	protected String initProperFilePathForSaving(ImageLayer iLayer){
		try {
			if(iLayer != null){
					if(iLayer.getExportImagePath() != null && iLayer.getExportImagePath().length()>0)
						return iLayer.getExportImagePath();

					else if(iLayer.getImageFilePath() != null && iLayer.getImageFilePath().length()>0){
						return  iLayer.getFolderOfImage(); // just give folder

					}
			}
				return System.getProperty("user.home");
		} catch (Exception e) {
			LOGGER.severe("Error in !");
			e.printStackTrace();
			return System.getProperty("user.home");
		}

	}
	
	/**
	 * Updates MarkingLayer grid drawing property to true if user has selected the Grid to be drawn on export image.
	 *
	 * @throws Exception the exception
	 */
	public void updateGridDrawing() throws Exception{

		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				ExImaSingleMarkingPanel esmp= (ExImaSingleMarkingPanel)sMarkingList[i];
				if(esmp.isSelected()){
					if(esmp.isDrawCheckBoxSelected()){ // user has selected the Grid to be drawn on image.
						MarkingLayer ml = this.imageLayer.getMarkingLayer(esmp.getMarkingLayerID());
						if(ml != null){
							ml.setDrawGridToImage(true);
						}
					}
				}
			}

		}

	}

}
