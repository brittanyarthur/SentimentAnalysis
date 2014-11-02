import java.util.*;
import java.lang.*;

class Pair{

     public static class bestMatch
     {
     private String student_set;
     private int score;
     public bestMatch(String student_set, int score)
  	 	  {
  	 	     this.student_set = student_set;
  	 	     this.score = score;
  	 	  }
     }

	public static void main(String[] args){
		//create table of all combinations of students 
		//next step: write an algorithm to:
		//			A. Compute all pairs
		//			B. Find the score to represent every pair's maximum predicted benefit from a learning exchange
		//			C. Insert these two pieces of information into the hashtable 
		Hashtable<String, Integer> pair_table = new Hashtable<String, Integer>();
		pair_table.put("AB",1);
		pair_table.put("AC",1);
		pair_table.put("AD",2);
		pair_table.put("AE",1);
		pair_table.put("AF",1);

		pair_table.put("BC",1);
		pair_table.put("BD",1);
		pair_table.put("BE",1);
		pair_table.put("BF",15);

		pair_table.put("CD",2);
		pair_table.put("CE",1);
		pair_table.put("CF",1);

		pair_table.put("DE",1);
		pair_table.put("DF",1);

		pair_table.put("EF",2);

		bestMatch best = Score("ABCDEF", pair_table);

		System.out.printf("\nMax score is: %d\n", best.score);
		System.out.printf("Student set is: %s\n\n",best.student_set);
	}

	public static bestMatch Score(String remainder, Hashtable<String, Integer> table){
		bestMatch best = new bestMatch("",0);
		if(remainder.length() == 0){
			return best;
		}
		int max = 0;
		String best_pair = "";
		int current_pair_score = 0;
		for(int index=1; index < remainder.length(); index++){

			if(table.get(remainder.charAt(0)+remainder.charAt(index)) == null){
				//insert into the hashtable - in this case I know all values are in hash.
				//to do: call a function to compute the max benefit this pair can have working together -> insert into table
			}
			//get pair score of current pair
			String lookup_pair = remainder.charAt(0)+""+remainder.charAt(index)+"";
			current_pair_score = table.get(remainder.charAt(0)+""+remainder.charAt(index)+"");

			//create the substring to find most optimal subsets
			StringBuilder remainder_copy = new StringBuilder(remainder);
			remainder_copy.deleteCharAt(0); 
			remainder_copy.deleteCharAt(index-1);
			best = Score(remainder_copy.toString(), table);

			//gets max so far at this level of the recursion
			if(max < (current_pair_score + best.score)){
			   max = current_pair_score + best.score;
			   best_pair = lookup_pair + " " + best.student_set;
			}
		}
		best.score = max;
		best.student_set = best_pair;
		return best;
	}
}














