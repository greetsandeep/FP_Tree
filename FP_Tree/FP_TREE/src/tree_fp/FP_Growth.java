package tree_fp;

import java.util.*;

public class FP_Growth {
	public static ArrayList<int []> data = new ArrayList<int[]>();
	public static void main(String args[]){
		PreProcessing pre = new PreProcessing("rule.data");
		int[] row;
		for(int i=0;i<pre.expandedData.size();i++){
			row = new int[9];
			for(int j=0;j<pre.expandedData.get(i).length;j++){
				row[j] = pre.expandedData.get(i)[j];
			}
			data.add(i,row);
		}
		
		FP_Tree fp = new FP_Tree(-1,null,0);
		/*int test1[] = {1,3,4};
		int test2[] = {1,2,5};
		int test3[] = {1,2,6};
		fp.addTransaction(test1);
		fp.addTransaction(test2);
		fp.addTransaction(test3);
		fp.treeTraversal();*/
		
		for(int i=0;i<data.size();i++){
			/*for(int j=0;j<data.get(i).length;j++){
				System.out.print(data.get(i)[j]+" ");
			}
			System.out.println();*/
			fp.addTransaction(data.get(i));
		}
		fp.treeTraversal();
	}
}
