package tree_fp;

import java.io.*;
import java.util.*;

/**
 * @author Sandeep, Poojitha, Snehal
 * Class that converts the continuous data to discrete data
 * Handles the missing values and 
 * Sorts the trasaction based on their support count.
 */
public class PreProcessing {
	
	/** An array list of integer arrays that stores the given data */
	public ArrayList<int []> data = new ArrayList<int[]>();
	
	/** An array list of integer arrays that stores the discrete data after pre processing */
	public ArrayList<int []> expandedData = new ArrayList<int[]>();
	
	/** An integer array that stores the Support of all items */
	public int support[] = new int[41];
	
	public PreProcessing(String filename) {
		try{
			inputHandle(filename,data);
		}catch(Exception e){
			System.out.println("\nError In file Reading " + e);
		}

		expand(data,expandedData);
		support = calcSupport(expandedData);
				
	}

	/**
	 * @param filename The file to be read
	 * @param data The data structure which stores the data in the file
	 * @throws IOException
	 * This function reads the input file and stores them in a data structure
	 * Converts continuous data tob discrete data
	 * Handles missing values 
	 */
	public void inputHandle(String filename,ArrayList<int[]> data)throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line=null;


		ArrayList<Integer> pregnancy = new ArrayList<Integer>();
		ArrayList<Integer> plasma = new ArrayList<Integer>();
		ArrayList<Integer> blood_pressure = new ArrayList<Integer>();
		ArrayList<Integer> skinFold = new ArrayList<Integer>();
		ArrayList<Integer> serum = new ArrayList<Integer>();
		ArrayList<Double> bmi = new ArrayList<Double>();
		ArrayList<Double> pedigree = new ArrayList<Double>();
		ArrayList<Integer> age = new ArrayList<Integer>();
		ArrayList<Integer> diabetes = new ArrayList<Integer>();

		while((line=br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line,",");

			while(st.hasMoreTokens()){
				pregnancy.add(Integer.parseInt(st.nextToken()));
				plasma.add(Integer.parseInt(st.nextToken()));
				blood_pressure.add(Integer.parseInt(st.nextToken()));
				skinFold.add(Integer.parseInt(st.nextToken()));
				serum.add(Integer.parseInt(st.nextToken()));
				bmi.add(Double.parseDouble(st.nextToken()));
				pedigree.add(Double.parseDouble(st.nextToken()));
				age.add(Integer.parseInt(st.nextToken()));
				diabetes.add(Integer.parseInt(st.nextToken()));
			}
		} 


		discPregnancy(pregnancy);
		discPlasma(plasma);
		discBP(blood_pressure);
		discSkinFold(skinFold);
		discSerum(serum);
		discBMI(bmi);
		discPedigree(pedigree);
		discAge(age);
		discDiabetes(diabetes);

		handleMissing(age);
		handleMissing(serum);
		handleMissing(skinFold);
		handleMissing(blood_pressure);
		handleMissing(plasma);
		handleMissing(pregnancy);

		handleMissingDouble(pedigree);
		handleMissingDouble(bmi);


		for(int i=0;i<pregnancy.size();i++)
		{
			int row[] = new int[9];
			row[0] = pregnancy.get(i);
			row[1] = plasma.get(i);
			row[2] = blood_pressure.get(i);
			row[3] = skinFold.get(i);
			row[4] = serum.get(i);
			row[5] = bmi.get(i).intValue();
			row[6] = pedigree.get(i).intValue();
			row[7] = age.get(i);
			row[8] = diabetes.get(i);
			data.add(i,row);
		}
		br.close();
	}

	/**
	 * @param pregnancy Takes in an ArrayList of Pregnancies and classifies into sub categories as mentioned.
	 * The Classification Principle is Equal Frequency Classes
	 */
	public void discPregnancy(ArrayList<Integer> pregnancy){
		for(int i=0;i<pregnancy.size();i++)
		{
			if(pregnancy.get(i)==0)
				pregnancy.set(i,1);
			else if(pregnancy.get(i)==1)
				pregnancy.set(i,2);
			else if(pregnancy.get(i)==2)
				pregnancy.set(i,3);
			else if(pregnancy.get(i)==3 ||pregnancy.get(i)==4)
				pregnancy.set(i,4);
			else if(pregnancy.get(i)>=5 && pregnancy.get(i)<=7)
				pregnancy.set(i,5);
			else
				pregnancy.set(i,6);
		}
		String categories[] = {"0","1","2",">=3 and <=4",">=5 and <=7",">7"};
		DataRef.subclasses.put(1,categories);
	}

	/**
	 * @param plasma Takes in an ArrayList of Plasma Glucose Conc. and classifies into sub categories as mentioned.
	 * The Classification Principle is Equal Frequency Classes
	 * The function feeds -1 in case of missing values which in this data set have been represented by 0
	 */
	public void discPlasma(ArrayList<Integer> plasma){
		for(int i=0;i<plasma.size();i++)
		{
			if(plasma.get(i)==0)
				plasma.set(i,-1);
			else if(plasma.get(i)>=44 &&plasma.get(i)<99)
				plasma.set(i,1);
			else if(plasma.get(i)>=99 && plasma.get(i)<111)
				plasma.set(i,2);
			else if(plasma.get(i)>=111 && plasma.get(i)<127)
				plasma.set(i,3);
			else if(plasma.get(i)>=127 && plasma.get(i)<=150)
				plasma.set(i,4);
			else
				plasma.set(i,5);
		}
		String categories[] = {"44-98","99-110","111-126","127-150","150+"};
		DataRef.subclasses.put(2,categories);
	}

	/**
	 * @param blood_pressure Takes in an ArrayList of Blood Pressures and classifies into sub categories as mentioned. 
	 * The classification into classes is based on the domain knowledge.
	 * The function feeds -1 in case of missing values which in this data set have been represented by 0
	 */
	public void discBP(ArrayList<Integer> blood_pressure){
		for(int i=0;i<blood_pressure.size();i++)
		{
			if(blood_pressure.get(i)==0)
				blood_pressure.set(i,-1);
			else if(blood_pressure.get(i)<=60)
				blood_pressure.set(i,1);
			else if(blood_pressure.get(i)<=80)
				blood_pressure.set(i,2);
			else if(blood_pressure.get(i)<=90)
				blood_pressure.set(i,3);
			else
				blood_pressure.set(i,4);
		}
		String categories[] = {"<=60",">60 and <=80",">80 and <=90",">90"};
		DataRef.subclasses.put(3,categories);
	}

	/**
	 * @param skinFold Takes in an ArrayList of Triceps skin fold thickness and classifies into sub categories as mentioned. 
	 * The Classification Principle is Equal Frequency Classes
	 * The function feeds -1 in case of missing values which in this data set have been represented by 0
	 */
	public void discSkinFold(ArrayList<Integer> skinFold){

		for(int i=0;i<skinFold.size();i++)
		{
			if(skinFold.get(i)==0)
				skinFold.set(i,-1);
			else if(skinFold.get(i)<=19)
				skinFold.set(i,1);
			else if(skinFold.get(i)<=26)
				skinFold.set(i,2);
			else if(skinFold.get(i)<=31)
				skinFold.set(i,3);
			else if(skinFold.get(i)<=38)
				skinFold.set(i,4);
			else
				skinFold.set(i,5);
		}
		String categories[] = {"<=19",">19 and <=26",">26 and <=31",">31 and <=38",">38"};
		DataRef.subclasses.put(4,categories);
	}
	/**
	 * @param serum Takes in an ArrayList of 2-Hour serum insulin (mu U/ml) and classifies into sub categories as mentioned. 
	 * The Classification Principle is Equal Frequency Classes
	 * The function feeds -1 in case of missing values which in this data set have been represented by 0
	 */
	public void discSerum(ArrayList<Integer> serum){

		for(int i=0;i<serum.size();i++)
		{
			if(serum.get(i)==0)
				serum.set(i,-1);
			else if(serum.get(i)<=70)
				serum.set(i,1);
			else if(serum.get(i)<=106)
				serum.set(i,2);
			else if(serum.get(i)<=150)
				serum.set(i,3);
			else if(serum.get(i)<=210)
				serum.set(i,4);
			else
				serum.set(i,5);
		}
		String categories[] = {"<=70",">70 and <=106",">106 and <=150",">150 and <=210",">210"};
		DataRef.subclasses.put(5,categories);
	}


	/**
	 * @param bmi Takes in an ArrayList of BMIs and classifies into sub categories as mentioned.
	 * The classification into classes is based on the domain knowledge.
	 * The function feeds -1 in case of missing values which in this data set have been represented by 0
	 */
	public void discBMI(ArrayList<Double> bmi){
		for(int i=0;i<bmi.size();i++)
		{
			if(bmi.get(i)==0)
				bmi.set(i,-1.0);
			else if(bmi.get(i)<=18.5)
				bmi.set(i,1.0);
			else if(bmi.get(i)<=25)
				bmi.set(i,2.0);
			else
				bmi.set(i,3.0);
		}
		String categories[] = {"<=18.5",">18.5 and <=25",">25"};
		DataRef.subclasses.put(6,categories);
	}


	/**
	 * @param pedigree Takes in an ArrayList of Diabetes pedigree function and classifies into sub categories as mentioned.
	 * This discretisation function uses the equal width strategy to classify data
	 */
	public void discPedigree(ArrayList<Double> pedigree){
		double min = 100,max = 0;
		for(int i=0;i<pedigree.size();i++){
			if(pedigree.get(i)>max)
				max = pedigree.get(i);
			if(pedigree.get(i)<min)
				min = pedigree.get(i);
		}
		double width = (max-min)/5;
		for(int i=0;i<pedigree.size();i++)
		{
			if(pedigree.get(i)==0)
				pedigree.set(i,-1.0);
			else
			{
				for(int j=1;j<6;j++)
				{
					if(pedigree.get(i)<= min+(j*width))
					{
						pedigree.set(i,(double)j);
						break;
					}
				}
			}
		}
		String temp[] = new String[5];
		for(int i=1;i<6;i++)
		{
			String temp1 =" >= "+ Double.toString(min+(i-1)*width)+ " and <= " + Double.toString(min+i*width);
			temp[i-1] = temp1;
		}
		DataRef.subclasses.put(7,temp);
	}

	/**
	 * @param age Takes in an ArrayList of Ages and classifies into sub categories as mentioned.
	 * This discretization function uses the equal width strategy to classify data
	 * Missing data is represented as -1
	 */
	public void discAge(ArrayList<Integer> age){
		int min = 100,max = 0;
		for(int i=0;i<age.size();i++){
			if(age.get(i)>max)
				max = age.get(i);
			if(age.get(i)<min)
				min = age.get(i);
		}
		int width = (max-min)/5;
		for(int i=0;i<age.size();i++)
		{
			if(age.get(i)==0)
				age.set(i,-1);
			else
			{
				for(int j=1;j<6;j++)
				{
					if(age.get(i)<= min+(j*width))
					{
						age.set(i,j);
						break;
					}
				}
			}
		}
		String temp[] = new String[5];
		for(int i=1;i<6;i++)
		{
			String temp1 =" >= "+ Integer.toString(min+(i-1)*width)+ " and <= " + Integer.toString(min+i*width);
			temp[i-1] = temp1;
		}
		DataRef.subclasses.put(8,temp);
	}
	
	public void discDiabetes(ArrayList<Integer> diabetes){
		for(int i=0;i<diabetes.size();i++)
		{
			if(diabetes.get(i)==0)
				diabetes.set(i,1);
			
			else
				diabetes.set(i,2);
		}
		String categories[] = {"No","Yes"};
		DataRef.subclasses.put(9,categories);
	}

	/**
	 * @param list The list which contains Missing Values
	 * Fills the missing values with the class which occurs the most.
	 */
	public void handleMissing(ArrayList<Integer> list){
		TreeSet<Integer> unique = new TreeSet<Integer>();
		for(int i=0;i<list.size();i++)
			unique.add(list.get(i));

		HashMap<Integer, Integer> freqTable = new HashMap<Integer,Integer>();

		for(Integer temp : unique)
			freqTable.put(temp,0);


		for(Integer temp : list)
			freqTable.put(temp, freqTable.get(temp)+1);

		int max = 0;
		for(Integer temp : unique)
			if(freqTable.get(temp)>max)
				max = temp;

		for(int i=0;i<list.size();i++)
			if(list.get(i)==-1)
				list.set(i,max);
	}
	/**
	 * @param list The list which contains Missing Values
	 * Fills the missing values with the class which occurs the most.
	 */
	public void handleMissingDouble(ArrayList<Double> list){
		TreeSet<Double> unique = new TreeSet<Double>();
		for(int i=0;i<list.size();i++)
			unique.add(list.get(i));

		HashMap<Double, Integer> freqTable = new HashMap<Double,Integer>();

		for(Double temp : unique)
			freqTable.put(temp,0);

		for(Double temp : unique)
			freqTable.put(temp, freqTable.get(temp)+1);

		double max = 0;
		for(Double temp : unique)
			if(freqTable.get(temp)>max)
				max = temp;

		for(int i=0;i<list.size();i++)
			if(list.get(i)==-1.0)
				list.set(i,max);
	}


	/**
	 * @param row the row which we want to print
	 * An utility function that prints the passed array
	 */
	public static void printArray(int []row){
		for(int i=0;i<row.length;i++)
			System.out.print(row[i]+" ");
		System.out.println();
	}
	public static void printArray(double []row){
		for(int i=0;i<row.length;i++)
			System.out.print(row[i]+" ");
		System.out.println();
	}

	/**
	 * @param data The data to be handled
	 * @param expandedData The representation of data considering each category to be a product
	 */
	public static void expand(ArrayList<int []> data, ArrayList<int []> expandedData){
		for(int i=0;i<data.size();i++)
		{
			int row[] = new int[9];
			row[0] = data.get(i)[0];
			row[1] = data.get(i)[1]+6;
			row[2] = data.get(i)[2]+11;
			row[3] = data.get(i)[3]+15;
			row[4] = data.get(i)[4]+20;
			row[5] = data.get(i)[5]+25;
			row[6] = data.get(i)[6]+30;
			row[7] = data.get(i)[7]+33;
			row[8] = data.get(i)[8]+38;
			expandedData.add(i,row);
		}
	}

	/**
	 * @param list The list of all transactions
	 * @return The Support of each product
	 */
	public int[] calcSupport(ArrayList<int []> list){
		int row[] =  new int[41];
		for(int i=0;i<list.size();i++)
			for(int j=0;j<list.get(i).length;j++)
				row[list.get(i)[j]]++;

		return row;
	}

	/**
	 * @param toSort Tha transaction to be sorted
	 * @param support Array containing support counts of all items
	 * This function sorts a transaction based on their support counts.
	 */
	public void sort(ArrayList<int []> toSort, int []support)
	{
		
		
		for(int c =0; c<toSort.size();c++){
			
			for(int i=0;i<(toSort.get(c).length - 1);i++)
			{
				for(int j=0;j<(toSort.get(c).length - i - 1);j++){
					if(support[toSort.get(c)[j]] < support[toSort.get(c)[j+1]]){
						int temp;
						temp = toSort.get(c)[j];
						toSort.get(c)[j] = toSort.get(c)[j+1];
						toSort.get(c)[j+1] = temp;
					}
				}
				
			}
		}
	}

	
}
