package tree_fp;

import java.util.*;

public class FP_Growth {
	public static ArrayList<int []> data = new ArrayList<int[]>();
	public static ArrayList<FP_Tree> startNode = new ArrayList<FP_Tree>();
	
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
		
		for(int i=0;i<6;i++){
			startNode.add(null);
		}
		
		int test1[] = {1,2};
		int test2[] = {2,3,4};
		int test3[] = {1,3,4,5};
		int test4[] = {1,4,5};
		int test5[] = {1,2,3};
		int test6[] = {1,2,3,4};
		int test7[] = {1};
		int test8[] = {1,2,3};
		int test9[] = {1,2,4};
		int test10[] = {2,3,5};
		
		fp.addTransaction(test1, startNode);
		fp.addTransaction(test2, startNode);
		fp.addTransaction(test3, startNode);
		fp.addTransaction(test4, startNode);
		fp.addTransaction(test5, startNode);
		fp.addTransaction(test6, startNode);
		fp.addTransaction(test7, startNode);
		fp.addTransaction(test8, startNode);
		fp.addTransaction(test9, startNode);
		fp.addTransaction(test10, startNode);
  		
		/*for(int i=0;i<data.size();i++){
			fp.addTransaction(data.get(i), startNode);
		}*/
		
		fp.treeTraversal();
		for(int i=1;i<6;i++){
			System.out.println(startNode.get(i).pos);
		}
	}
}
