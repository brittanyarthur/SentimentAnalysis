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

    public static class Max {
    	public static int score;
    	public static String students;
    	public static Stack<String> st;
	}

	public static void main(String[] args){
		//set table
		Hashtable<String, Integer> pair_table = new Hashtable<String, Integer>();
		pair_table.put("AB",100);
		pair_table.put("AC",1);
		pair_table.put("AD",1);
		pair_table.put("AE",1);
		pair_table.put("AF",1);

		pair_table.put("BC",1);
		pair_table.put("BD",1);
		pair_table.put("BE",1);
		pair_table.put("BF",1);

		pair_table.put("CD",2);
		pair_table.put("CE",1);
		pair_table.put("CF",1);

		pair_table.put("DE",1);
		pair_table.put("DF",1);

		pair_table.put("EF",2);

		Max.st = new Stack<String>();
		Max.students = "";
		int scoretotal = Score("ABCDEF", pair_table);

		System.out.println("Max score is: " + scoretotal + "\n");
		//System.out.println("\n\nStudent set is: "+match.student_set);
	}

	public static int Score(String remainder, Hashtable<String, Integer> table){
		if(remainder.length() == 0){
			return 0;
		}
		int score = 0;
		int max = 0;
		int current_pair_score = 0;
		int candidate_score = 0;
		for(int index=1; index < remainder.length(); index++){

			if(table.get(remainder.charAt(0)+remainder.charAt(index)) == null){
				//insert into the hashtable
			}
			//get pair score
			String lookup_pair = remainder.charAt(0)+""+remainder.charAt(index)+"";
			current_pair_score = table.get(remainder.charAt(0)+""+remainder.charAt(index)+"");

			//create the substring to pass in to look for smaller substrings
			StringBuilder remainder_copy = new StringBuilder(remainder);
			remainder_copy.deleteCharAt(0); 
			remainder_copy.deleteCharAt(index-1);
			candidate_score = Score(remainder_copy.toString(), table);

			//gets max at this level of the recursion
			if(max < (current_pair_score+candidate_score)){
			   max = current_pair_score+candidate_score;
			   System.out.println(lookup_pair + " with max = " + max );
			}
		}
		return max;
	}
}














