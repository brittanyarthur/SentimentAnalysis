import java.lang.*;
import java.io.*;
import java.util.*;
import java.text.BreakIterator;

class findanswers{
	public static class Keywords{
    //contains options for the sentence
		public static String[] keywordmap;
	}

  public class Globals{
    public static final int number_options = 6;
  }

  public static class OpInfoArray{
    private OpInfoSet[] opInfo;
    public OpInfoArray(int count){ //OpInfoSet set){
  OpInfoSet[] op = new OpInfoSet[count];
  for (int i = 0; i < count; ++i) {
    op[i] = new OpInfoSet();
  }
      this.opInfo = op;//set;
    }
  }

  public static class OpInfoSet{
    private String optionID;
    private int freq_count;
    private int pos_count;
    public OpInfoSet(int freq_count, int pos_count, String optionID){
      this.optionID = optionID;
      this.freq_count = freq_count;
      this.pos_count = pos_count;
    }
    public OpInfoSet(){
      this.optionID =  ""; //optionID;
      this.freq_count = 0; //freq_count;
      this.pos_count =  0; //pos_count;
    }
  }

 //The predicted subject of the sentence is known for each question.
  public static void FillMap(){
		Keywords.keywordmap = new String[Globals.number_options]; //number of questions == 6
		   //Is competition an important part of why you like to play video games?
   Keywords.keywordmap[0] = "competition";

     	 //What is it about competition that is motivating?
    	 Keywords.keywordmap[1] = " "; //there are no options presented

    	//Do you prefer games where you are alone against other people or with others (on a team) against other people?
      Keywords.keywordmap[2] = "alone others other team both";

   		//When playing alone or as part  of a team, do you prefer to play against people who are better, the same, or worse than you?
      Keywords.keywordmap[3] = "better same worse equal both";

   		//Do you prefer direct competition - where you can influence the other person, strategize (like in chess), or indirect competition
   		//where you cannot influence them, it's primarily about luck (like in bingo, Yahtzee)?
      Keywords.keywordmap[4] = "direct indirect luck both";

   		//Are there any circumstances related to video gaming under which competition against others is motivating to you?
   		Keywords.keywordmap[5] = " "; //options are not given
     }

     public static OpInfoArray[] InitializeOptionSets(OpInfoArray[] option_set){
      option_set[0].opInfo[0].optionID = "competition";
      
      option_set[1].opInfo[0].optionID = "";

      option_set[2].opInfo[0].optionID = "alone";
      option_set[2].opInfo[1].optionID = "others other team";
      option_set[2].opInfo[2].optionID = "both";

      option_set[3].opInfo[0].optionID = "better";
      option_set[3].opInfo[1].optionID = "equal both same";
      option_set[3].opInfo[2].optionID = "worse";

      option_set[4].opInfo[0].optionID = "direct";
      option_set[4].opInfo[1].optionID = "indirect";
      option_set[4].opInfo[2].optionID = "luck";
      option_set[4].opInfo[3].optionID = "equal both";

      option_set[5].opInfo[0].optionID = "";

      return option_set;
    }

    public static TrieTree.Trie FillTrie(){
      TrieTree.Trie dict = new TrieTree.Trie(); 
      try{
        Scanner posListdata = new Scanner(new File("./utilities/positiveWordListLIWC")); 
        while(posListdata.hasNextLine()){
          String line = posListdata.nextLine();
          line = line.replaceAll("\\*", "");
          String[] poswords = line.split(" ");

          for(int word_index = 0; word_index < poswords.length; word_index++){
            TrieTree.fill(dict, poswords[word_index]);
          }
        }
      } catch (IOException error) {
        System.out.println("error occured emotion>readFilePos");
      }
      return dict;
    }

     /* input: question number, a response to analyze
     output: the answer selected for that question
     logic: finds the frequency for that keyword along with the number of positive words nearby it
     */
  public static String OptionSelected(int q_num, String response){
      FillMap();
      TrieTree.Trie dict = FillTrie();
      
      String answer = "";
      String keyword_options = Keywords.keywordmap[q_num];
      
      if(q_num==0){
        answer += "See special case response in addition.";
      }
      if(keyword_options==""){
        answer = "This question does not present options -> will not be analyzed this way";
      }

      //Create a place where findings about frequency and positive counts can be stored
      OpInfoArray[] op_info = new OpInfoArray[Globals.number_options];
      op_info[0] = new OpInfoArray(1);
      op_info[1] = new OpInfoArray(1);
      op_info[2] = new OpInfoArray(3);
      op_info[3] = new OpInfoArray(3);
      op_info[4] = new OpInfoArray(4);
      op_info[5] = new OpInfoArray(1);
      op_info = InitializeOptionSets(op_info);

      //get the frequency of each keyword in the response
      answer += GetFrequency(keyword_options, response, q_num, op_info);
      
      //find number of positive words per option
      GetPositiveCount(response, dict, q_num, op_info);

      //Print out results
      for(int k = 0; k < (op_info[q_num].opInfo).length; k++){
        System.out.println("========================================");
        System.out.println("Analysis for Question #"+q_num);
        System.out.println("Options are: " + op_info[q_num].opInfo[k].optionID);
        System.out.println("Frequency: " + op_info[q_num].opInfo[k].freq_count);
        System.out.println("Positive Words: " + op_info[q_num].opInfo[k].pos_count);
      }

      return answer;
    }

  //to do: add into a directory positiveList
  //find the number of positive words 5 words away from option-words
    public static void GetPositiveCount(String response, TrieTree.Trie dict, int q_num, OpInfoArray[] op_info){
      //remove all non numeric
      //response = response.replaceAll("[^\\.\\!a-zA-Z0-9\\s]", "");
      //split on sentences
      //String[] processSentences = response.split(".");
      BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
      iterator.setText(response);
      int start = iterator.first();
      double count = 0;
        //process each sentence
      //for(int sentenceNum = 0; sentenceNum < processSentences.length; sentenceNum++){
      for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            //look through the words in each sentence
        Queue<String> posFiveWordSpanQueue = new LinkedList<String>();
        String[] processWords = (response.substring(start,end)).split(" ");
        for(int wordIndex = 0; wordIndex < processWords.length; wordIndex++){
              /*maintain a queue of size 5 of positive words 
              everytime a new word is read in, we do three things:
              1. we remove 1 item from the bottom of the queue
              2. we check to see if it is a positive word
              if it is, then we add it to the stack.
              3. we check to see if it is an option.
              if it is, then we check to see the size of the queue and add that
              number to the number of words that are in a 5 word left-distance from the option */
              String test = response.substring(start,end);
              if(!posFiveWordSpanQueue.isEmpty() && posFiveWordSpanQueue.size() >= 5){
                posFiveWordSpanQueue.remove();
              }
              String result = dict.getMatchingPrefix(processWords[wordIndex]);
              if(result != null && !result.isEmpty()){
                posFiveWordSpanQueue.add(result);
                //System.out.println("+++++++++++++EXISTS // START ++++++++");
                //System.out.println("result was: " + result + " from input word: " + processWords[wordIndex]);
                //System.out.println("+++++++++++++EXISTS // END++++++++");
              }

              //find out which option this is
              String[] options = (Keywords.keywordmap[q_num]).split(" ");
              for(int i = 0; i < options.length; i++){
                if(processWords[wordIndex].equals(options[i])){
                  int pos_count = posFiveWordSpanQueue.size();
                  int option_index = getOptionIndex(options[i], op_info[q_num]);
                  //add to the positive score count for that number
                  op_info[q_num].opInfo[option_index].pos_count = pos_count;
                }
              }

            }
          }
        }

        public static int getOptionIndex(String option, OpInfoArray op_info){
          for(int i = 0; i < (op_info.opInfo).length; i++){
           String optionID = (op_info.opInfo)[i].optionID;
             //System.out.println(optionID + "\n\n");
           String[] optionIDList = optionID.split(" ");
           for(int k = 0; k < optionIDList.length; k++){
            if(option.equals(optionIDList[k])){
              return i;
            }
          }
        }
        return -1;
      }

      public static String GetFrequency(String keyword_options, String response, int q_num, OpInfoArray[] op_info){
        //the question number given maps to a listing of predicted subjects of the sentence for the response
        String answer = "";
        String[] options = keyword_options.split(" ");
        //remove all non numeric
        response = response.replaceAll("[^a-zA-Z0-9\\s]", "");
        String[] response_words = response.split(" ");

        //put all the options in a hash table
        Hashtable<String, Integer> word_frequency_table = new Hashtable<String, Integer>();
        for(int i = 0; i < options.length; i++){
           //op_info[q_num].optionID = options[itor];
         if(word_frequency_table.get(options[i])==null){
           word_frequency_table.put(options[i], 0);
         }
       }

       //incremement the hash table for every word in the response that matches an option
       for(int i = 0; i < response_words.length; i++){
         if(word_frequency_table.get(response_words[i])==null){
           continue;
         }
         int current_score = word_frequency_table.get(response_words[i]);
         word_frequency_table.put(response_words[i], ++current_score);
       }

       //print out the results
       for(int i = 0; i < options.length; i++){
         int freq = word_frequency_table.get(options[i]);
         int option_index = getOptionIndex(options[i], op_info[q_num]);
         if(option_index == -1){
          System.out.println("ERROR GIVEN WITH INPUT: " + options[i]);
         }else{
          op_info[q_num].opInfo[option_index].freq_count = freq;
         }
         answer += "\n<<" + options[i] + ">> frequency is: " + freq;
       }
       return answer;
     }

   }

























