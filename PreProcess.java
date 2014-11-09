import java.util.Scanner;
import java.io.*;
import java.lang.*;
import java.util.*;

class PreProcess{
	public static String Begin(String response){
		response = RemoveLike(response);
		return response;
	}
	//runtime: O(m*n), m: # words in response, n: # words in pronoun list
	//this can be improved by using a hashtable. 
	public static String RemoveLike(String response){
		try {
			Scanner pro_data = new Scanner(new File("./like/pronoun"));
            String all = pro_data.nextLine(); //the file is large but none of these are in it: \n
            String[] pdList = all.split(" ");
            String[] response_words = response.split(" ");
            for(int k = 0; k < response_words.length; k++){
            	for (int i = 0; i < pdList.length; i++) {
            		if (pdList[i].contains("*")) {
            			String positiveStart = pdList[i].replace("*", "");
            			if (response_words[k].startsWith(positiveStart) || response_words[k].equals(positiveStart)) {
            				return "like";
            			}
            		} 
            	}
            }
            return "";
        } catch (IOException error) {
        	System.out.println("error occured reading in file.");
        }
        return "";
    }
} 
