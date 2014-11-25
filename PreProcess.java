import java.util.Scanner;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.BreakIterator;

class PreProcess{

  public static class Connectors{
      public static final ArrayList<String> parts = new ArrayList<String>();;
  }
	public static String Begin(String response){
        setUp();
        response = response.replaceAll("[,'’]", "");
        response = response.replaceAll("dont", "do not");
        response = response.replaceAll("didnt", "did not");
        response = RemoveLike(response);
        response = restoreCapitals(response);
        //response = response.replaceAll("don't", "not");
        //response = response.replaceAll("not like", "notlike");
        response = notWhat(response);
        response = response.replaceAll("not like", "notlike");
        /*capitals need to be restored - in the next phase of processing, 
          BreakIterator uses capitals to infer where a sentence starts and ends*/
        response = restoreCapitals(response);
        SettingOptions opts = SettingOptions.getOptions();
        print("f", opts.get_cmds(), "FULLY EDITED RESPONSE: " + response);
		return response;
	}

  public static void setUp(){
      Connectors.parts.add("IN");
      Connectors.parts.add("VB");
      Connectors.parts.add("VBD");
      Connectors.parts.add("VBG");
      Connectors.parts.add("VBN");
      Connectors.parts.add("VBP");
      Connectors.parts.add("VBZ");
      Connectors.parts.add("DT");
      Connectors.parts.add("RB");
      Connectors.parts.add("RBR");
      Connectors.parts.add("RBS");
      Connectors.parts.add("TO");
  }

    protected static String restoreCapitals(String response){
       String[] words = response.split("\\.");
       String newresponse = "";
       for(int i = 0; i < words.length; i++){
        words[i] = words[i].trim();
           if(words[i] == null || words[i].isEmpty() || words[i] == ""){ continue;}
           words[i] = words[i].trim();
           newresponse += " " + Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1, words[i].length()) + ".";
       }
       return newresponse;
    }

    //next goal today is to make a design for how I will analyze the meaning
    protected static String notWhat(String response)
    {
      //search for keywords: if a keyword is found, then look back 5 words to find positive words
      BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
      SettingOptions opts = SettingOptions.getOptions();
      iterator.setText(response);
      int start = iterator.first();
      String newresponse = "";
      for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String curr_sentence = response.substring(start,end).toLowerCase();
            curr_sentence = curr_sentence.replaceAll("[.,']", "");
            if(curr_sentence.contains("not ")){
                  curr_sentence = curr_sentence.trim();
                  String tagged_sentence = parsetext.Tag(curr_sentence);
                  String[] tagged_words = tagged_sentence.split(" ");
                  String[] untagged_words = curr_sentence.split(" ");
                  print("a", opts.get_cmds(), "UNTAGGED: %s\n", curr_sentence);
                  print("a", opts.get_cmds(), "TAGGED:   %s\n", tagged_sentence);
                  for(int i = 0; i < untagged_words.length && i < tagged_words.length; i++){
                       if(tagged_words[i].length()>3){
                           if(tagged_words[i].substring(0,3).equals("not")){
                                //now searching for connectors to reach the object
                                //do not include not: we will prepend it: newresponse += untagged_words[i];
                               newresponse += untagged_words[i] + " ";
                               for(int k = i+1; k < tagged_words.length && k < untagged_words.length; k++){
                                   i = k;
                                   String[] word_tag = tagged_words[k].split("/");
                                  if(Connectors.parts.contains(word_tag[1].trim())){
                                       newresponse += untagged_words[i] + " ";
                                   }else{
                                       //prepend not to this word, it is the predicted subject
                                       newresponse += "NOT"+ untagged_words[i] + " ";
                                       break;
                                   }
                               }
                           }else{
                               newresponse += untagged_words[i] + " ";
                           }
                       }else{
                         newresponse += untagged_words[i] + " ";
                       }
                   }
            }else{ 
              newresponse += curr_sentence;
            }
       newresponse.trim();
       newresponse += ". ";     
     } 
      newresponse.trim();
      return newresponse;
  }

	//runtime: O(m*n), m: # words in response, n: # words in pronoun list
	//this can be improved by using a hashtable. 
	public static String RemoveLike(String response){
    String newresponse = "";
    SettingOptions opts = SettingOptions.getOptions();
		try {
			Scanner pro_data = new Scanner(new File("./like/pronoun"));
            String all = pro_data.nextLine(); //the file is large but none of these are in it: \n
            String[] pdList = all.split(" ");
            response = response.toLowerCase();
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
                            if (response_words[k-1].startsWith(positiveStart)) {
                            newresponse += response_words[k] + " ";
                            break;
                            }
            			}
                  //if what came before it is the positiveword, then we should include it
            			if (response_words[k-1].equals(positiveStart)) {
                    newresponse += response_words[k] + " ";
            				break;
            			}
            		}
            	}else{
                     newresponse += response_words[k] + " ";
            	}
            }
            print("l", opts.get_cmds(), "\n[LIKE CASE] REPONSE WAS: " + response);
            print("l", opts.get_cmds(), "[LIKE CASE] NEW RESPONSE IS: " + newresponse);
            return newresponse.trim();
        } catch (IOException error) {
        	System.out.println("error occured reading in file.");
        }
        return response;
    }

   public static void printCmdLineFormat(String input){
      SettingOptions opts = SettingOptions.getOptions();
      int size = 50; //size of line
      int start = 0;
      int end = size;
      int difference = 0;
      String newstr = "";
      input = input.replaceAll("\n","");
      while(end < input.length()){
           difference = getEndIndex(input, start, end);
           newstr += input.substring(start,end-difference) + "\n";
           print("", opts.get_cmds(), "%25s|%-50s\n", "", input.substring(start,end-difference));
           start += size - difference;
           end += size - difference;
      }
      print("", opts.get_cmds(), "%25s|%-50s\n","",input.substring(start, input.length()));
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

  //process options
  public static void print(String option, String cmd_options, String message){
     SettingOptions opts = SettingOptions.getOptions();
     if(opts.get_file_destination() != "" && cmd_options.contains(option)){
         try{
             PrintWriter writer = new PrintWriter(new FileOutputStream(new File(opts.get_file_destination()), true));
             writer.println(message);
             writer.close();
         }catch (IOException error) {
             System.out.println("error occured reading in file.");
        }
     }else if(cmd_options.contains(option)){
        System.out.println(message);
     }
  }

  public static void print(String option, String cmd_options, String format, String item1){
    SettingOptions opts = SettingOptions.getOptions();
     if(opts.get_file_destination() != "" && cmd_options.contains(option)){
         try{
             PrintWriter writer = new PrintWriter(new FileOutputStream(new File(opts.get_file_destination()), true));
             writer.printf(format, item1);
             writer.close();
         }catch (IOException error) {
             System.out.println("error occured reading in file.");
        }
     }else if(cmd_options.contains(option)){
        System.out.printf(format, item1);
     }
  }

  public static void print(String option, String cmd_options, String format, String item1, String item2){
     SettingOptions opts = SettingOptions.getOptions();
     if(opts.get_file_destination() != "" && cmd_options.contains(option)){
         try{
             PrintWriter writer = new PrintWriter(new FileOutputStream(new File(opts.get_file_destination()), true));
             writer.printf(format, item1, item2);
             writer.close();
         }catch (IOException error) {
             System.out.println("error occured reading in file.");
        }
     }else if(cmd_options.contains(option)){
        System.out.printf(format, item1, item2);
     }
  }

} 

/*
java findsentiment - results/example/standard test
java findsentiment -l results/example/option_like test
java findsentiment -a results/example/option_analysis test
java findsentiment -f results/example/option_full-edit test
*/

/*
java findsentiment - results/Q8_500/standard transcripts_q8/Q8_500
java findsentiment - results/Q8_501/standard transcripts_q8/Q8_501
java findsentiment - results/Q8_503/standard transcripts_q8/Q8_503
java findsentiment - results/Q8_504/standard transcripts_q8/Q8_504
java findsentiment - results/Q8_505/standard transcripts_q8/Q8_505
java findsentiment - results/Q8_506/standard transcripts_q8/Q8_506
java findsentiment - results/Q8_507/standard transcripts_q8/Q8_507
java findsentiment - results/Q8_508/standard transcripts_q8/Q8_508
java findsentiment - results/Q8_509/standard transcripts_q8/Q8_509
java findsentiment - results/Q8_800/standard transcripts_q8/Q8_800
java findsentiment - results/Q8_801/standard transcripts_q8/Q8_801
java findsentiment - results/Q8_802/standard transcripts_q8/Q8_802
java findsentiment - results/Q8_803/standard transcripts_q8/Q8_803
java findsentiment - results/Q8_804/standard transcripts_q8/Q8_804
*/

/*
java findsentiment -l results/Q8_500/option_like transcripts_q8/Q8_500
java findsentiment -l results/Q8_501/option_like transcripts_q8/Q8_501
java findsentiment -l results/Q8_503/option_like transcripts_q8/Q8_503
java findsentiment -l results/Q8_504/option_like transcripts_q8/Q8_504
java findsentiment -l results/Q8_505/option_like transcripts_q8/Q8_505
java findsentiment -l results/Q8_506/option_like transcripts_q8/Q8_506
java findsentiment -l results/Q8_507/option_like transcripts_q8/Q8_507
java findsentiment -l results/Q8_508/option_like transcripts_q8/Q8_508
java findsentiment -l results/Q8_509/option_like transcripts_q8/Q8_509
java findsentiment -l results/Q8_800/option_like transcripts_q8/Q8_800
java findsentiment -l results/Q8_801/option_like transcripts_q8/Q8_801
java findsentiment -l results/Q8_802/option_like transcripts_q8/Q8_802
java findsentiment -l results/Q8_803/option_like transcripts_q8/Q8_803
java findsentiment -l results/Q8_804/option_like transcripts_q8/Q8_804
*/

/*
java findsentiment -a results/Q8_500/option_analysis transcripts_q8/Q8_500
java findsentiment -a results/Q8_501/option_analysis transcripts_q8/Q8_501
java findsentiment -a results/Q8_503/option_analysis transcripts_q8/Q8_503
java findsentiment -a results/Q8_504/option_analysis transcripts_q8/Q8_504
java findsentiment -a results/Q8_505/option_analysis transcripts_q8/Q8_505
java findsentiment -a results/Q8_506/option_analysis transcripts_q8/Q8_506
java findsentiment -a results/Q8_507/option_analysis transcripts_q8/Q8_507
java findsentiment -a results/Q8_508/option_analysis transcripts_q8/Q8_508
java findsentiment -a results/Q8_509/option_analysis transcripts_q8/Q8_509
java findsentiment -a results/Q8_800/option_analysis transcripts_q8/Q8_800
java findsentiment -a results/Q8_801/option_analysis transcripts_q8/Q8_801
java findsentiment -a results/Q8_802/option_analysis transcripts_q8/Q8_802
java findsentiment -a results/Q8_803/option_analysis transcripts_q8/Q8_803
java findsentiment -a results/Q8_804/option_analysis transcripts_q8/Q8_804
*/

/*
java findsentiment -f results/Q8_500/option_full-edit transcripts_q8/Q8_500
java findsentiment -f results/Q8_501/option_full-edit transcripts_q8/Q8_501
java findsentiment -f results/Q8_503/option_full-edit transcripts_q8/Q8_503
java findsentiment -f results/Q8_504/option_full-edit transcripts_q8/Q8_504
java findsentiment -f results/Q8_505/option_full-edit transcripts_q8/Q8_505
java findsentiment -f results/Q8_506/option_full-edit transcripts_q8/Q8_506
java findsentiment -f results/Q8_507/option_full-edit transcripts_q8/Q8_507
java findsentiment -f results/Q8_508/option_full-edit transcripts_q8/Q8_508
java findsentiment -f results/Q8_509/option_full-edit transcripts_q8/Q8_509
java findsentiment -f results/Q8_800/option_full-edit transcripts_q8/Q8_800
java findsentiment -f results/Q8_801/option_full-edit transcripts_q8/Q8_801
java findsentiment -f results/Q8_802/option_full-edit transcripts_q8/Q8_802
java findsentiment -f results/Q8_803/option_full-edit transcripts_q8/Q8_803
java findsentiment -f results/Q8_804/option_full-edit transcripts_q8/Q8_804
*/

