/*//indices start at 1
hashtable table <- stores scores for all pairs. ex. pair a and b would have the key "ab" and a value reflecting their score together
Score(remainder) <- remainder starts off as a listing of all students such as ABCDEFGH
{  
optimal solution =
    for(j=2 to remainder.length)
       if(table does not contain key [remainder[1]remainder[j])]) then call ComputePairScore(remainder[1]remainder[j])
       remainderCopy = remainder.Copy()
       remainderCopy.removeIndex(1)
       remainderCopy.removeIndex(j)
          max(table.getscore(remainder[1]remainder[j]) + Score(remainder))
}

ComputePairScore(student a, student b)
{
//look up in database to analyze best possible swap of skills student a and b can make
//put score in hash table with key a+b
}
*/
import java.util.*;

class Students{

	public static void main(String[] args){
		//set table
		Hashtable pair_table = new Hashtable();
		pair_table.put("AB",1);
		pair_table.put("AC",1);
		pair_table.put("AD",100);
		pair_table.put("AE",1);
		pair_table.put("AF",1);
		pair_table.put("BC",2);
		pair_table.put("BD",3);
		pair_table.put("BE",4);
		pair_table.put("BF",100);
		pair_table.put("CD",5);
		pair_table.put("CE",100);
		pair_table.put("CF",7);
		pair_table.put("DE",5);
		pair_table.put("DF",8);
	}
	public static void Score(int remainder){
		//
	}
}





