package tree_fp;

import java.util.*;

/**
 * @author Poojitha, Snehal, Sandeep
 * AIM: Applying FP Tree and FP Growth algorithm to generate Association rules.
 */
public class FP_Growth {
	
	/**A Global ArrayList of integer arrays to store the preprocessed data. */
	public static ArrayList<int []> data = new ArrayList<int[]>(); 
	
	/**A global array list of FP_Tree to store the ending node of all items in the main tree. */
	public static ArrayList<FP_Tree> startNode = new ArrayList<FP_Tree>();
	
	/** A global Hash map of treeset of integers and integer to store the frequent item sets with their supports. */
	public static HashMap<TreeSet<Integer>, Integer> itemWithSupport = new HashMap<TreeSet<Integer>, Integer>();
	
	/** A global Hash map of array list of tree set of integers and double to store the LHS and RHS of a rule with its confidence.*/
	public static HashMap<ArrayList<TreeSet<Integer>>, Double> confidentRules = new HashMap<ArrayList<TreeSet<Integer>>, Double>(); 
	
	public static void main(String args[]){
		PreProcessing pre = new PreProcessing("rule.data"); /** An Object of Preprocessing Class */
		int[] row;
		for(int i=0;i<pre.expandedData.size();i++){
			row = new int[9];
			for(int j=0;j<pre.expandedData.get(i).length;j++){
				row[j] = pre.expandedData.get(i)[j];
			}
			data.add(i,row);
		}
		
		long startTime = System.currentTimeMillis();
		
		FP_Tree fp = new FP_Tree(-1,null,0); /** Creates a FP tree with just root */
		
		for(int i=0;i<pre.support.length;i++){
			startNode.add(null);
		}
		
		/*int test1[] = {1,2};
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
  		
		int support[] = {0,8,7,6,5,3};*/
 		
		for(int i=0;i<data.size();i++){
			fp.addTransaction(data.get(i), startNode);
		}
		
		int minsup = 153;
		double minConfidence = 0.9;
		
		FP_Growth fpg = new FP_Growth(); /** An Object of FP_Tree class.*/
		TreeSet<Integer> set = new TreeSet<Integer>();
		for(int i=1;i<pre.support.length;i++){
			if(pre.support[i]>minsup){
				fpg.generateItemsets(fp, i, minsup, pre.support,set,startNode);
			}
		}
		System.out.println("Total No. of Frequent Itemsets: "+itemWithSupport.size());
		
		/*for(Map.Entry<TreeSet<Integer>, Integer> e: itemWithSupport.entrySet()){
			System.out.println(e.getKey()+" "+e.getValue());
		}*/
		
		long supportStopTime = System.currentTimeMillis();
		System.out.println("\nThe Time elapsed to find all frequent Item Subsets: " + (supportStopTime-startTime)+" milliseconds\n");
		
		System.out.println("Rules Generated: ");
		
		confidentRuleGen(minConfidence);
				
		System.out.println(" ");
		System.out.println("No of Rules: "+confidentRules.size());
		System.out.println(" ");
		
		long finalStopTime = System.currentTimeMillis();
		System.out.println("The Time elapsed for confidence pruning:  " + (finalStopTime-supportStopTime)+" milliseconds");
		System.out.println("The Total Time for generating all rules: " + (finalStopTime-startTime)+" milliseconds");
		
	}
	
	/**
	 * @param tree The main tree whose subtree(ending with a specified item) has to be generated
	 * @param endWith The item with which every path in the subtree should end
	 * @param minsup Minimum Support
	 * @param support The integer array containing the support of all the elements
	 * @param subSet Candidate frequent itemset which is appended to create a frequent itemset
	 * @param startNode An array containing the ending node of all items.
	 * This function recursively finds all the frequent itemsets ending with a particular item by creating a subtree,
	 * then pruning it to create a conditional FP Tree.
	 * It then adds the frequent itemset to the Hashmap itemWithSupport.
	 */
	public void generateItemsets(FP_Tree tree, int endWith, int minsup, int support[],TreeSet<Integer> subSet, ArrayList<FP_Tree> startNode){
		TreeSet<Integer> set = new TreeSet<Integer>(subSet);
		ArrayList<Integer> candidates = new ArrayList<Integer>();
		ArrayList<FP_Tree> subStartNode = new ArrayList<FP_Tree>();
		ArrayList<FP_Tree> condSubStartNode = new ArrayList<FP_Tree>();
		set.add(endWith);
		itemWithSupport.put(set,support[endWith]);
		support = new int[support.length];
		FP_Tree sub = new FP_Tree((-1)*endWith,null,0);
		for(int j=0;j<support.length;j++){
			subStartNode.add(null);
		}
		sub = tree.subTree(endWith, startNode.get(endWith), subStartNode);
		FP_Tree curr = tree;
		for(int i=1;i<subStartNode.size();i++){
			curr = subStartNode.get(i);
			while(curr!=null){
				support[i] += curr.count;
				curr = curr.ref;
			}
		}
		FP_Tree condSub = new FP_Tree(-1*endWith,null,0);
		for(int j=0;j<support.length;j++){
			condSubStartNode.add(null);
		}
		condSub = sub.conditionalSubTree(subStartNode, minsup, condSubStartNode,support);
		candidates = condSub.getNextEnd();
		for(int i = 0;i<candidates.size();i++){
			generateItemsets(condSub,candidates.get(i),minsup,support,set,condSubStartNode);
		}
	}
	
	/**
	 * @param minConf The Confidence threshold below which any rule would be pruned out for not having sufficient confidence
	 * This function sends every possible candidate rule of all frequent itemsets to the function isConfRule()
	 */
	public static void confidentRuleGen(double minConf){
		
			for(Map.Entry<TreeSet<Integer>, Integer> f: itemWithSupport.entrySet()){
				
				HashMap<TreeSet<Integer>,TreeSet<Integer>> can = candidatesForConfidence(f.getKey());
				for(Map.Entry<TreeSet<Integer>, TreeSet<Integer>> e : can.entrySet()){
					isConfRule(e,minConf);
				}
			}
		
	}

	/**
	 * @param e The Rule in the set format whose confidence is to be tested
	 * @param minConf The Confidence threshold below which any rule would be pruned out for not having sufficient confidence
	 * This function checks the confidence of each rule and adds only those rules to the global variable : <b> confidentRules </b> who have confidence more than the minimum threshold
	 * This function also prints all these rules in sentence format by referencing the DataRef Class
	 */
	public static void isConfRule(Map.Entry<TreeSet<Integer>, TreeSet<Integer>> e, double minConf){
		TreeSet<Integer> lhs = new TreeSet<Integer>(e.getKey());
		TreeSet<Integer> rhs = new TreeSet<Integer>(e.getValue());
 		TreeSet<Integer> union = new TreeSet<Integer>();
 		union.addAll(lhs);
 		union.addAll(rhs);
		double conf = (double)itemWithSupport.get(union)/(double)itemWithSupport.get(lhs); 
		
		DataRef dref = new DataRef();
		
		ArrayList<TreeSet<Integer>> temp = new ArrayList<TreeSet<Integer>>();
		temp.add(lhs);
		temp.add(rhs);
		
		if(conf >= minConf){
			confidentRules.put(temp, conf);
			//System.out.println("No of Rules: "+confidentRules.size());
			for(Integer i:temp.get(0)){
				if(i<=6){
					System.out.print(dref.classes[0]+":"+DataRef.subclasses.get(1)[i-1]+" ");
				}
				else if(i<=11){
					System.out.print(dref.classes[1]+":"+DataRef.subclasses.get(2)[i-7]+" ");
				}
				else if(i<=15){
					System.out.print(dref.classes[2]+":"+DataRef.subclasses.get(3)[i-12]+" ");
				}
				else if(i<=20){
					System.out.print(dref.classes[3]+":"+DataRef.subclasses.get(4)[i-16]+" ");
				}
				else if(i<=25){
					System.out.print(dref.classes[4]+":"+DataRef.subclasses.get(5)[i-21]+" ");
				}
				else if(i<=28){
					System.out.print(dref.classes[5]+":"+DataRef.subclasses.get(6)[i-26]+" ");
				}
				else if(i<=33){
					System.out.print(dref.classes[6]+":"+DataRef.subclasses.get(7)[i-29]+" ");
				}
				else if(i<=38){
					System.out.print(dref.classes[7]+":"+DataRef.subclasses.get(8)[i-34]+" ");
				}
				else if(i<=40){
					System.out.print(dref.classes[8]+":"+DataRef.subclasses.get(9)[i-39]+" ");
				}
		    }
			System.out.print(" ---> ");
			for(Integer i:temp.get(1)){
				if(i<=6){
					System.out.print(dref.classes[0]+":"+DataRef.subclasses.get(1)[i-1]+" ");
				}
				else if(i<=11){
					System.out.print(dref.classes[1]+":"+DataRef.subclasses.get(2)[i-7]+" ");
				}
				else if(i<=15){
					System.out.print(dref.classes[2]+":"+DataRef.subclasses.get(3)[i-12]+" ");
				}
				else if(i<=20){
					System.out.print(dref.classes[3]+":"+DataRef.subclasses.get(4)[i-16]+" ");
				}
				else if(i<=25){
					System.out.print(dref.classes[4]+":"+DataRef.subclasses.get(5)[i-21]+" ");
				}
				else if(i<=28){
					System.out.print(dref.classes[5]+":"+DataRef.subclasses.get(6)[i-26]+" ");
				}
				else if(i<=33){
					System.out.print(dref.classes[6]+":"+DataRef.subclasses.get(7)[i-29]+" ");
				}
				else if(i<=38){
					System.out.print(dref.classes[7]+":"+DataRef.subclasses.get(8)[i-34]+" ");
				}
				else if(i<=40){
					System.out.print(dref.classes[8]+":"+DataRef.subclasses.get(9)[i-39]+" ");
				}
		    }
			System.out.println(" "+conf);
			System.out.println(" ");
		}
	}
	
	/**
	 * @param originalSet The Set of which we have to find all the possible Sub sets
	 * @return All the possible sub sets of the given Set including null set and the set itself
	 */
	public static Set<Set<Integer>> powerSet(Set<Integer> originalSet) {
        Set<Set<Integer>> sets = new HashSet<Set<Integer>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<Integer>());
            return sets;
        }
        List<Integer> list = new ArrayList<Integer>(originalSet);
        Integer head = list.get(0);
        Set<Integer> rest = new HashSet<Integer>(list.subList(1, list.size()));
        
        for (Set<Integer> set : powerSet(rest)) {
            Set<Integer> newSet = new HashSet<Integer>();
            newSet.add(head);
            newSet.addAll(set);
    		sets.add(set);
    		sets.add(newSet);
        }

        return sets;
    }
	
	/**
	 * @param rule The set currently under consideration
	 * @return Returns the possible candidates for confidence pruning. Eliminates Null gives all and vice versa rules.
	 */
	public static HashMap<TreeSet<Integer>,TreeSet<Integer>> candidatesForConfidence(TreeSet<Integer> rule){
		HashMap<TreeSet<Integer>,TreeSet<Integer>> candidates = new HashMap<TreeSet<Integer>,TreeSet<Integer>>();
		Set<Set<Integer>> temp = powerSet(rule);

		for (Set<Integer> s : temp) {
		    if(!(s.size()==0 || s.size()==rule.size()))
		    {
		    	Set<Integer> tempSet = new HashSet<Integer>(rule);
		    	tempSet.removeAll(s);
		    	candidates.put(new TreeSet<Integer>(s),new TreeSet<Integer>(tempSet));
		    }
		}
		return candidates;
	}
}
