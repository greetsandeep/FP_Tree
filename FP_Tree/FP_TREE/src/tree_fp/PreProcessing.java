package tree_fp;

import java.io.*;
import java.util.*;


public class PreProcessing {
	public ArrayList<int []> data = new ArrayList<int[]>();
	public PreProcessing(String filename) {
		try{
			inputHandle(filename,data);
		}catch(Exception e){
			System.out.println("\nError In file Reading " + e);
		}
	}

	/**
	 * @param filename : the filename to be opened
	 * @param data : The ArrayList of data objects which is to be populated on the basis of data
	 * @throws IOException : To handle File not found error.
	 * @see IOException
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
		ArrayList<Boolean> diabetes = new ArrayList<Boolean>();

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
				diabetes.add(Integer.parseInt(st.nextToken())==1?true:false);
			}
			System.out.println();
		} 


		discPregnancy(pregnancy);
		discPlasma(plasma);
		discBP(blood_pressure);
		discSkinFold(skinFold);
		discSerum(serum);
		discBMI(bmi);
		discPedigree(pedigree);
		discAge(age);

		br.close();
	}
	public void discPregnancy(ArrayList<Integer> pregnancy){

	}
	public void discPlasma(ArrayList<Integer> plasma){

	}
	public void discBP(ArrayList<Integer> blood_pressure){

	}
	public void discSkinFold(ArrayList<Integer> skinFold){

	}
	public void discSerum(ArrayList<Integer> serum){

	}
	public void discBMI(ArrayList<Double> bmi){

	}
	public void discPedigree(ArrayList<Double> pedigree){

	}
	public void discAge(ArrayList<Integer> age){

	}
}
