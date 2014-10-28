import java.util.*;

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
		//set table
		Hashtable<String, Integer> pair_table = new Hashtable<String, Integer>();
		pair_table.put("AB",2);
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
		bestMatch match = new bestMatch("",0);
		Score("ABCDEF", pair_table, match);
		System.out.println("Max score is: " + match.score + "\nBest Group: " + match.student_set);
	}

	public static int Score(String remainder, Hashtable<String, Integer> table, bestMatch match){
		if(remainder.length() == 0){
			return 0;
		}
		int max = 0;
		int score = 0;
		int tempmax = 0;
		int current_pair_score = 0;
		int candidate_score = 0;
		for(int index=1; index < remainder.length(); index++){
			if(table.get(remainder.charAt(0)+remainder.charAt(index)) == null){
				//insert into the hashtable
			}
			StringBuilder remainder_copy = new StringBuilder(remainder);
			remainder_copy.deleteCharAt(index); 
			remainder_copy.deleteCharAt(0);
			String lookup_pair = remainder.charAt(0)+""+remainder.charAt(index)+"";
			System.out.println("pair is: " + lookup_pair);
			current_pair_score = table.get(remainder.charAt(0)+""+remainder.charAt(index));
		    candidate_score = current_pair_score + Score(remainder_copy.length()==0?"":remainder_copy.toString(), table, match);
		    System.out.println("Current max is: " + candidate_score);
			if(candidate_score > match.score){
				
				//System.out.println("pair is: " + lookup_pair +"\n");
				max = candidate_score;
				match.score = candidate_score;
				///match.student_set += lookup_pair + temp_match.student_set;
				//match.score = candidate_score;
				//max = candidate_score;
			}
		}
		return candidate_score;
	}
}














