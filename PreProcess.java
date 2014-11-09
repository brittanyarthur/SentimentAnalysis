import java.util.Scanner;
import java.io.*;
import java.lang.*;
import java.util.*;

class PreProcess{
	public static String Begin(String response){
        response = response.toLowerCase();
        response = response.replaceAll("[^a-zA-Z0-9\\s]", "");
        response = RemoveLike(response);
		return response;
	}
	//runtime: O(m*n), m: # words in response, n: # words in pronoun list
	//this can be improved by using a hashtable. 
	public static String RemoveLike(String response){
         String newresponse = "";
		try {
			Scanner pro_data = new Scanner(new File("./like/pronoun"));
            String all = pro_data.nextLine(); //the file is large but none of these are in it: \n
            String[] pdList = all.split(" ");
            String[] response_words = response.split(" ");
            for(int k = 0; k < response_words.length; k++){
            	if(response_words[k].equals("like") && k > 0){
                    //now there is a "like" under consideration
                    //iterating through the list of what is valid to come before
            		for (int i = 0; i < pdList.length; i++) {
                        //take the current value
            			String positiveStart = pdList[i];
                        //strip it of the * if there is one
            			if (pdList[i].contains("*")) {
            				positiveStart = pdList[i].replace("*", "");
            			}
                        //if what came before it is the positiveword, then we should include it
            			if (response_words[k-1].startsWith(positiveStart) || response_words[k-1].equals(positiveStart)) {
            				newresponse += response_words[k] + " ";
            				break;
            			}
            		}
            	}else{
                     newresponse += response_words[k] + " ";
            	}
            }
            System.out.println("REPONSE WAS: "); 
            printCmdLineFormat(response);
            System.out.println("NEW RESPONSE IS: "); 
            printCmdLineFormat(newresponse);
            return newresponse.trim();
        } catch (IOException error) {
        	System.out.println("error occured reading in file.");
        }
        return response;
    }

   public static void printCmdLineFormat(String input){
      int size = 50; //size of line
      int start = 0;
      int end = size;
      int difference = 0;
      String newstr = "";
      while(end < input.length()){
           difference = getEndIndex(input, start, end);
           newstr += input.substring(start,end-difference) + "\n";
           System.out.printf("%25s|%-50s\n","",input.substring(start,end-difference));
           start += size - difference;
           end += size - difference;
      }
      System.out.printf("%25s|%-50s\n","",input.substring(start, input.length()));
  }
  
 protected static int getEndIndex(String input, int start, int end){
       char whitespace = 'X';
       int difference = 0;
       int new_end = input.substring(start,end).length()-1;
       
       while(whitespace != ' ' && new_end > 0){
           whitespace = (input.substring(start,end)).charAt(new_end);
           ++difference;
           --new_end;
       }
       return difference;//(curr_end==0)?0:
  }

} 








