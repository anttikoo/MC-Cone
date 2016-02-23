package gui.saving.ImageSet;

import information.ID;
import information.SharedVariables;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import gui.Color_schema;

/**
 * The Class ResolutionIntFilter. Filters the input text by checking is it numerical and the values of JTextFiels will not exceed 5000.
 */
public class ResolutionIntFilter extends DocumentFilter {
		
		/** The Constant LOGGER. */
		private final static Logger LOGGER = Logger.getLogger("MCCLogger");
		
		/** The arrow mouse listener. */
		private ArrowMouseListener arrowMouseListener;
		
		/** The text field id. */
		private int textFieldID=ID.TEXTFIELD_WIDTH;
		
		/** The another filter. */
		private ResolutionIntFilter anotherFilter=null;
		
		/** The another field. */
		private JTextField anotherField=null;
		
		/** The update another field. */
		private boolean updateAnotherField=true;
		
		/** The maximum image size. */
		private int maximumImageSize=SharedVariables.IMAGESET_EXPORT_MAX_RESOLUTION;
		
		
	
	/**
	 * Instantiates a new ResolutionIntFilter.
	 *
	 * @param aml the ArrowMouseListener
	 * @param id the ID of JTextField, width or height
	 * @param tf the JTextField which is modified.
	 */
	public ResolutionIntFilter(ArrowMouseListener aml, int id, JTextField tf) {
		try {
			this.arrowMouseListener=aml;
			this.textFieldID=id;
			this.anotherField=tf;
		} catch (Exception e) {
			LOGGER.severe("Error in initializing ResolutionIntFilter!");
			e.printStackTrace();
		}
		
	}
	
	   /**
   	 * Filters and update JTextField.
   	 *
   	 * @param numberString the number string
   	 * @return true, if successful
   	 * @throws Exception the exception
   	 */
   	private boolean filterAndUpdateTextFields(String numberString) throws Exception{
		 boolean ok_resolution=true;
		   // if this filter is allowed to update another field 
		   
		 if(isUpdateAnotherField()){
			 this.anotherFilter.setUpdateAnotherField(false);
			 if(arrowMouseListener != null){
	        	 if(textFieldID==ID.TEXTFIELD_WIDTH){
	        		 ok_resolution=updateField(numberString, this.anotherField, false);
	        		     
	        	 }
	        	 else{
	        		ok_resolution= updateField(numberString, this.anotherField, true);
	        	 }
	         }
			 this.anotherFilter.setUpdateAnotherField(true);
		   
		 }
		 return ok_resolution;
	   }

	/**
	 * Returns the another field.
	 *
	 * @return the another field
	 * @throws Exception the exception
	 */
	public JTextField getAnotherField() throws Exception{
		return anotherField;
	}

	/**
	 * Returns the another filter.
	 *
	 * @return the another filter
	 * @throws Exception the exception
	 */
	public ResolutionIntFilter getAnotherFilter() throws Exception{
		return anotherFilter;
	}

   /* (non-Javadoc)
    * @see javax.swing.text.DocumentFilter#insertString(javax.swing.text.DocumentFilter.FilterBypass, int, java.lang.String, javax.swing.text.AttributeSet)
    */
   @Override
   public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {

      try {
		Document doc = fb.getDocument();
		  StringBuilder sb = new StringBuilder();
		  sb.append(doc.getText(0, doc.getLength()));
		  sb.insert(offset, string);

		  if (test(sb.toString())) {
			  if(filterAndUpdateTextFields(sb.toString()))
		     super.insertString(fb, offset, string, attr);
		     
		  } else {
			// do nothing if not allowed character or number too big
		  }
	} catch (Exception e) {
		LOGGER.severe("Error in inserting String in ResolutionIntFilter!");
		e.printStackTrace();
	}
   }
	   
   /**
    * Checks if should update another JTextField.
    *
    * @return true, if is update another field
    */
   public boolean isUpdateAnotherField() {
		return updateAnotherField;
	}

	

	/* (non-Javadoc)
	 * @see javax.swing.text.DocumentFilter#remove(javax.swing.text.DocumentFilter.FilterBypass, int, int)
	 */
	@Override
	   public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
	      try {
			Document doc = fb.getDocument();
			  StringBuilder sb = new StringBuilder();
			  sb.append(doc.getText(0, doc.getLength()));
			  sb.delete(offset, offset + length);

			  if (test(sb.toString())) {
				
					if(filterAndUpdateTextFields(sb.toString()))
					 super.remove(fb, offset, length);
				
			   
			  } else {
				// do nothing if not allowed character or number too big
			  }
		} catch (Exception e) {
			LOGGER.severe("Error in removing String in TextField!");
			e.printStackTrace();
		}

	   }

	/* (non-Javadoc)
	 * @see javax.swing.text.DocumentFilter#replace(javax.swing.text.DocumentFilter.FilterBypass, int, int, java.lang.String, javax.swing.text.AttributeSet)
	 */
	@Override
	   public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
	      try {
			Document doc = fb.getDocument();
			  StringBuilder sb = new StringBuilder();
			  sb.append(doc.getText(0, doc.getLength()));
			  sb.replace(offset, offset + length, text);

			  if (test(sb.toString())) {
				  if(filterAndUpdateTextFields(sb.toString()))
			     super.replace(fb, offset, length, text, attrs);
			   
			  } else {
			     // do nothing if not allowed character or number too big
			  }
		} catch (Exception e) {
			LOGGER.severe("Error in replacing string!");
			e.printStackTrace();
		}

	   }

	/**
	 * Sets the another JTextField.
	 *
	 * @param anotherField the new another field
	 * @throws Exception the exception
	 */
	public void setAnotherField(JTextField anotherField) throws Exception{
		this.anotherField = anotherField;
	}

	/**
	 * Sets the another ResolutionFilter, which is used to filter the another JTextField.
	 *
	 * @param anotherFilter the new another filter
	 * @throws Exception the exception
	 */
	public void setAnotherFilter(ResolutionIntFilter anotherFilter) throws Exception {
		this.anotherFilter = anotherFilter;
	}
	
	
	/**
	 * Sets the scaling factor used to count image with from height and conversely.
	 *
	 * @param factor the new scaling factor
	 * @throws Exception the exception
	 */
	public void setScalingFactor(double factor) throws Exception{
		ArrowMouseListener.scalingFactor=factor;
	}
	
	/**
	 * Sets the update another field.
	 *
	 * @param updateAnotherField the new update another field
	 * @throws Exception the exception
	 */
	public void setUpdateAnotherField(boolean updateAnotherField)  throws Exception{
		this.updateAnotherField = updateAnotherField;
	}

	/**
	 * Tests is the text numerical and smaller than the max resolution size.
	 *
	 * @param text the text
	 * @return true, if successful
	 */
	private boolean test(String text) {
	      try {
	    	  if(text != null){
	    		  if(text.length() ==0)
	    			  return true; // no text yet
	        int value= Integer.parseInt(text);
	        if(value <= SharedVariables.IMAGESET_EXPORT_MAX_RESOLUTION )
	         return true;
	        else
	        	return false;
	    	  }
	    	  else
	    		  return false;
	      } catch (NumberFormatException e) {
	    	  LOGGER.severe("Error in testing the given text!");
	         return false;
	      }
	      catch(Exception ex){
	    	  LOGGER.severe("Error in testing the given text!");
	    	  return false;
	      }
	   }

	/**
	 * Updates JTextField if given text is acceptable: numerical and smaller or equal than 5000.
	 *
	 * @param field_changed_string the String of JTextField that is modified.
	 * @param field_calculated the JTextField which value will be calculated.
	 * @param divide the boolean is counting made by dividing or multiplying.
	 * @return true, if successful
	 */
	private boolean updateField(String field_changed_string, JTextField field_calculated, boolean divide){
		try {
			if(field_changed_string != null && field_changed_string.length()>0){	
				int presentValue=Integer.parseInt(field_changed_string.trim());			
				if(presentValue<=maximumImageSize && presentValue > 0){

					if(ArrowMouseListener.scalingFactor !=0){
						int secondValue=0;
						if(divide){
							secondValue= (int)(((double)presentValue)/ArrowMouseListener.scalingFactor);
						}
						else{

							secondValue= (int)(((double)presentValue)*ArrowMouseListener.scalingFactor);
						}
						if(secondValue <=maximumImageSize && secondValue >= 0){
							field_calculated.setText(""+secondValue);
							if(secondValue>0)
								field_calculated.setForeground(Color_schema.white_230);
							else
								field_calculated.setForeground(Color_schema.orange_dark);
							return true;
						}
						else{
							return false;
						}
					}
					else{
						return false;
					}		
				}
				else{
					return false;
				}		
			}
			else{
				field_calculated.setText("0");
				field_calculated.setForeground(Color_schema.orange_dark);
				return true;
			}
			
			} catch (NumberFormatException e) {
				
				LOGGER.warning("Resolution has to be numerical value");
				e.printStackTrace();
				return false;
			}
			catch(Exception ex){
				LOGGER.severe("Error in updating TextField");
				ex.printStackTrace();
				return false;
			}
	}
}
