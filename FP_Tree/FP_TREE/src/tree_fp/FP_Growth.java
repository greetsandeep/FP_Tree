package tree_fp;

import java.util.*;

public class FP_Growth {
	public static ArrayList<int []> data = new ArrayList<int[]>();
	public static void main(String args[]){
		PreProcessing pre = new PreProcessing("rule.data");
		int[] row = new int[9];
		for(int i=0;i<pre.expandedData.size();i++){
			for(int j=0;j<pre.expandedData.get(i).length;j++){
				row[j] = pre.expandedData.get(i)[j];
			}
			data.add(i,row);
		}
		FP_Tree fp = new FP_Tree(-1);
		for(int i=0;i<data.size();i++){
			for(int j=0;j<data.get(i).length;j++){
				if()
			}
		}
	}
}
