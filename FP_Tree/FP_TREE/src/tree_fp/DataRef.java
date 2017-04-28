package tree_fp;

import java.util.HashMap;

/**
 * @author Sandeep, Snehal, Poojitha
 * Class which stores all the attributes in a transaction.
 */
public class DataRef {
	
	/** A string array which stores the names of all attributes */
	public String[] classes = new String[9];
	
	/** A global Hash map of integer and string array which stores all the different subclasses after discretizattion of the data. */
	public static HashMap<Integer, String[]> subclasses = new HashMap<Integer,String[]>();
	
	DataRef() {
		classes[0] = "Number of times pregnant";
		classes[1] = "Plasma glucose concentration a 2 hours in an oral glucose tolerance test";
		classes[2] = "Diastolic blood pressure (mm Hg)";
		classes[3] = "Triceps skin fold thickness (mm)";
		classes[4] = "2-Hour serum insulin (mu U/ml)";
		classes[5] = "Body mass index (weight in kg/(height in m)^2)";
		classes[6] = "Diabetes pedigree function";
		classes[7] = "Age (years)";
		classes[8] = "Tested Positive for Diabetes?"; 	
	}
}
