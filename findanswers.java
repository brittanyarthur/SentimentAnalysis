import java.lang.*;
import java.io.*;
import java.util.*;
import java.text.BreakIterator;

class ParentStorage {
       private static QuestionSet[] op_info;
       private static ParentStorage storage;
       private ParentStorage(int size){
          op_info = new QuestionSet[size];
       }
       //this will only be called once
       public static ParentStorage setStorage(int size){
          if(storage == null){
              storage = new ParentStorage(size);
          }
          return storage;
       }
       //get an existing ParentStorage object
       public static ParentStorage getStorage(){
          return storage;
       }
       public static void initIndex(int index, int size){
          op_info[index] = new QuestionSet(size);
       }
       public static void setID(int option_index, int syn_index, String optionIDValue){
          op_info[option_index].synonym_set[syn_index].optionID = optionIDValue;
       } 
       public static void setIncrementFreq(int index_qnum, int index_option, int freq){
          op_info[index_qnum].synonym_set[index_option].freq_count += freq;
       }
       public static void setIncrementPosCount(int index_qnum, int index_option, int posCount){
          op_info[index_qnum].synonym_set[index_option].pos_count += posCount;
       }
       public static int getFrequency(int index_qnum, int index_syn){
          return ParentStorage.op_info[index_qnum].synonym_set[index_syn].freq_count;
       }
       public static int getPositiveCount(int index_qnum, int index_syn){
          return ParentStorage.op_info[index_qnum].synonym_set[index_syn].pos_count;
       }
       public static String getOption(int index_qnum, int index_syn){
          return ParentStorage.op_info[index_qnum].synonym_set[index_syn].optionID;
       }
       public static int getLengthSynSet(int question_num){
          return (op_info[question_num].synonym_set).length;
       }
       
       protected static class QuestionSet{
          protected QInfoSet[] synonym_set;
          protected QuestionSet(int count){ 
             QInfoSet[] op = new QInfoSet[count];
             for (int i = 0; i < count; ++i) {
                op[i] = new QInfoSet();
             }
          this.synonym_set = op;
          }
      }
      
       protected static class QInfoSet{
          protected String optionID;
          protected int freq_count;
          protected int pos_count;
          protected QInfoSet(int freq_count, int pos_count, String optionID){
             this.optionID = optionID;
             this.freq_count = freq_count;
             this.pos_count = pos_count;
          }
          protected QInfoSet(){
             this.optionID =  ""; 
             this.freq_count = 0; 
             this.pos_count =  0; 
          }
      }

}

class findanswers{ 

    /**
    * to do: Refactor this with get/set
    * Contains all options and synonyms for the associated question (association by index)
    */
  	protected static class Keywords{
		   public static String[] keywordmap;
	  }

    protected static class Globals{
       public static final int number_options = 6;
       public static String cmd_options = "";
    }

    /**
    * The predicted keyword of the answer is listed for each question.
    */
    protected static void FillMap(){
       Keywords.keywordmap = new String[Globals.number_options]; 
       //Is competition an important part of why you like to play video games?
       Keywords.keywordmap[0] = "competition";

       //What is it about competition that is motivating?
       Keywords.keywordmap[1] = " "; //there are no options presented

       //Do you prefer games where you are alone against other people or with others (on a team) against other people?
       Keywords.keywordmap[2] = "alone team teams both either depends"; 

       //When playing alone or as part  of a team, do you prefer to play against people who are better, the same, or worse than you?
       Keywords.keywordmap[3] = "better equal both same depends worse";

       //Do you prefer direct competition - where you can influence the other person, strategize (like in chess), or indirect competition
       //where you cannot influence them, it's primarily about luck (like in bingo, Yahtzee)?
       Keywords.keywordmap[4] = "strategize strategies strategy skill direct influence indirect luck equal both depends";

       //Are there any circumstances related to video gaming under which competition against others is motivating to you?
       Keywords.keywordmap[5] = " "; //options are not given
  }
    /**
    * Keywords are grouped. Two words are in the same grouping if they both suggest the same answer.
    */
    protected static void InitializeOptionSets(){
       ParentStorage store = ParentStorage.getStorage();
       store.setID(0,0,"competition");
       
       store.setID(1,0,"");

       store.setID(2,0,"alone");
       store.setID(2,1,"team teams");
       store.setID(2,2,"both either depends");

       store.setID(3,0,"better");
       store.setID(3,1,"equal both same depends");
       store.setID(3,2,"worse");

       store.setID(4,0,"strategize strategies strategy skill direct influence");
       store.setID(4,1,"indirect");
       store.setID(4,2,"luck");
       store.setID(4,3,"equal both depends");

       store.setID(5,0,"");
   }

    /**
    * Creates a place where findings about frequency and positive counts can be stored.
    * @param cmd_options The options are set.
    * note: store.initIndex takes 2 arguments. 
    *       > Arg 1: question number. 
    *       > Arg 2: number of answer sets. example: "equal both same" is one answer set.
    */
  public static void SetUp(String cmd_options){
      FillMap();
      Globals.cmd_options = cmd_options;

      ParentStorage store = ParentStorage.setStorage(Globals.number_options);
      store.initIndex(0,1);
      store.initIndex(1,1);
      store.initIndex(2,3);
      store.initIndex(3,3);
      store.initIndex(4,4);
      store.initIndex(5,1);
      InitializeOptionSets();
  }

  /**
    * Fill trie with positive words that allow for any extension.
    * @throws IOException
    */
  protected static TrieTree.Trie FillTrie(){
     TrieTree.Trie dict = new TrieTree.Trie(); 
     try{
        Scanner posListdata = new Scanner(new File("./utilities/positiveWordListLIWC")); 
        while(posListdata.hasNextLine()){
           String line = posListdata.nextLine();
           String[] poswords = line.split(" ");

           for(int word_index = 0; word_index < poswords.length; word_index++){
              if(poswords[word_index].contains("*")){
                 poswords[word_index] = poswords[word_index].replaceAll("\\*", "");
                 TrieTree.fill(dict, poswords[word_index]);
              }
           }
        }
      } catch (IOException error) {
         System.out.println("error occured emotion>readFilePos");
     }
     return dict;
  }

  /**
    * Put all the positive words in a hash table. 
    * @throws IOException
    */
  protected static Hashtable<String, String> FillHash(){
     Hashtable<String, String> pos_table = new Hashtable<String, String>();
     try{
        Scanner posListdata = new Scanner(new File("./utilities/positiveWordListLIWC")); 
        while(posListdata.hasNextLine()){
           String line = posListdata.nextLine();
           String[] poswords = line.split(" ");
           for(int i = 0; i < poswords.length; i++){
              if(!poswords[i].contains("*")){
                    poswords[i] = poswords[i].replaceAll("\\*", "");
                    pos_table.put(poswords[i], poswords[i]);
              }
           }
        }
      } catch (IOException error) {
           System.out.println("error occured emotion>readFilePos");
      }
      return pos_table;
  }

   /**
   * Finds the frequency for each keyword in response along with the number of positive words nearby it
   * @Output The answer selected for that question
   * @param q_num: question number
   * @param response: a response to analyze 
   */
  public static String OptionSelected(int q_num, String response){ 
     TrieTree.Trie dict = FillTrie();
     Hashtable<String, String> pos_table = FillHash();
     ParentStorage storage = ParentStorage.getStorage();
     String answer = "";
     String keyword_options = Keywords.keywordmap[q_num];   
     if(q_num==0){
        answer += "See special case response in addition.";
     }
     if(keyword_options==""){
        answer = "This question does not present options -> will not be analyzed this way";
     }

     //get the frequency of each keyword in the response
     answer += GetFrequency(keyword_options, response, q_num);
      
     //find number of positive words per option
     GetPositiveCount(response, dict, pos_table, q_num);

     //DEBUG OPTION 'a' : Print out results
     int max = 0;
     String max_opt = "";
     for(int k = 0; k < storage.getLengthSynSet(q_num); k++){
        PreProcess.print("a",Globals.cmd_options, "========================================");
        PreProcess.print("a",Globals.cmd_options, "Analysis for Question #"+q_num);
        PreProcess.print("a",Globals.cmd_options, "Options are: " + storage.getOption(q_num, k));
        PreProcess.print("a",Globals.cmd_options, "Frequency: " + storage.getFrequency(q_num, k));
        PreProcess.print("a",Globals.cmd_options, "Positive Words: " + storage.getPositiveCount(q_num, k));
       
        int score = storage.getFrequency(q_num, k)+(2*storage.getPositiveCount(q_num, k));
        if(score>max){
           max = score; 
           max_opt = storage.getOption(q_num, k);
        }
     }

     String score_info = "";
     if(Globals.cmd_options.contains("a")){
        score_info = " SCORE: "+max;
     }
     PreProcess.print("",Globals.cmd_options,"\n\n====== SELECTED OPTION: " + max_opt + score_info +" ======\n\n\n\n");
     return answer;
  }

              
   /**
   * Finds the number of positive words 5 words away from option-words.
   * note: The non-numeric is already stripped out from the PreProcess step. 
   * @Output The answer selected for that question
   * @param dict: a trie tree filled with positive words ending in * (any extension)
   * @param pos_table: a hash table filled with positive words without any flexible extension 
   * @param response: a response to analyze 
   * @param q_num: the question number under analysis
   * more info:
   *   maintains a queue of size 5 of positive words 
   *           everytime a new word is read in, we do three things:
   *           1. we remove 1 item from the bottom of the queue
   *           2. we check to see if it is a positive word
   *           if it is, then we add it to the stack.
   *           3. we check to see if it is an option.
   *           if it is, then we check to see the size of the queue and add that
   *           number to the number of words that are in a 5 word left-distance from the option
   */
   protected static void GetPositiveCount(String response, TrieTree.Trie dict, Hashtable<String, String> pos_table, int q_num){
      BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
      SettingOptions opts = SettingOptions.getOptions();
      ParentStorage store = ParentStorage.getStorage();
      iterator.setText(response);
      int start = iterator.first();
      double count = 0;

      //process each sentence > look through the words in each sentence
      for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
         Queue<String> posFiveWordSpanQueue = new LinkedList<String>();
         String sentence = (response.substring(start,end)).toLowerCase();
         sentence = sentence.replaceAll("[?!.,']", "");
         String[] processWords = sentence.split(" ");
         int positive_word_count = 0;
         boolean not_complete = true;
         for(int wordIndex = 0; (wordIndex < processWords.length || not_complete); wordIndex++){
            boolean tempBool = false;
            String test = response.substring(start,end);
            if(!posFiveWordSpanQueue.isEmpty() && (posFiveWordSpanQueue.size() > 5||wordIndex >= processWords.length)){
               //checking for "look ahead" positive words as well
               String pop = posFiveWordSpanQueue.remove();
               String pop_pos_match_trie = dict.getMatchingPrefix(pop);
               String pop_pos_match_hash = pos_table.get(pop);
               //if we have poped off a positive word, then we will decrement the positive word count 
               if((pop_pos_match_trie!=null && !pop_pos_match_trie.isEmpty()) || (pop_pos_match_hash!=null && !pop_pos_match_hash.isEmpty())){
                  --positive_word_count;
               }
               UpdatePositiveScore(pop, positive_word_count, q_num);
               if(posFiveWordSpanQueue.isEmpty()){
                  not_complete = false;
                  continue;
               }
               if(wordIndex >= processWords.length){
                  continue;
               }
            }
            tempBool = false;

            //DEBUG OPTION 'a' 
            if(processWords[wordIndex]!=null && !processWords[wordIndex].isEmpty()){
              for(String s : posFiveWordSpanQueue) { 
                    PreProcess.print("a", opts.get_cmds(), "element: " + s.toString()); 
               }
               PreProcess.print("a", opts.get_cmds(), "positive words in queue = "+positive_word_count+"\nfor word (new element): "+processWords[wordIndex]+"\n\n");
            }

            UpdatePositiveScore(processWords[wordIndex], positive_word_count, q_num);
   
            //check to see if the word is a positive word
            String result_trie = dict.getMatchingPrefix(processWords[wordIndex]);
            String result_hash = pos_table.get(processWords[wordIndex]);
            if((result_trie != null && !result_trie.isEmpty())||(result_hash != null && !result_hash.isEmpty())){
               ++positive_word_count;
            }
            //insert into queue
            posFiveWordSpanQueue.add(processWords[wordIndex]);
         }
      }
   }

   /**
   * Updates positive word count for a given keyword
   * @param q_num: question number
   * @param candidate_word: a word to search for
   * @param positive_word_count: number of positive words in the stack
   * note:  It is expensive finding which index a set of keywords is located in for every word - to mitigate this 
   *        cost, first check to see if the word is even a keyword at all. If it is, then search through all the
   *        synonym sets in the array to find which set the word belongs to. 
   */
  protected static void UpdatePositiveScore(String candidate_word, int positive_word_count, int q_num){
     ParentStorage storage = ParentStorage.getStorage();
     String[] options = (Keywords.keywordmap[q_num]).split(" ");
        for(int i = 0; i < options.length; i++){
        //if the word that is being examined is one of the keywords, find the set it belongs to (its index)
           if(candidate_word.equals(options[i])){
              int option_index = GetOptionIndex(options[i], storage, q_num);
              //add to the positive score count for that number
              storage.setIncrementPosCount(q_num, option_index, positive_word_count);
           }
        }
   }

   /**
   * Gets the grouping # that the keyword is a part of. Synonyms to a single answer are in groups. 
   * The index (grouping #) of that set is needed to increment the positive word count for that option.
   * @param option: option that was detected to match response
   * @param storage: ParentStorage object to get information about the groupings
   * @param q_num: question number under analysis
   */
   protected static int GetOptionIndex(String option, ParentStorage storage, int q_num){
      for(int i = 0; i < storage.getLengthSynSet(q_num); i++){
         String optionID = storage.getOption(q_num, i);
         String[] optionIDList = optionID.split(" ");
         for(int k = 0; k < optionIDList.length; k++){
            if(option.equals(optionIDList[k])){
               return i;
            }
         }
      }
      return -1;
   }

   /**
   * The frequency per option is found for a specific question.
   * @param keyword_options: listing of options for a question
   * @param response: response from interviewee
   * @param q_num: question number under analysis
   */
   protected static String GetFrequency(String keyword_options, String response, int q_num){
      //the question number given maps to a listing of predicted subjects of the sentence for the response
      ParentStorage store = ParentStorage.getStorage();
      String answer = "";
      String[] options = keyword_options.split(" ");
      //remove all non numeric
      response = response.toLowerCase();
      response = response.replaceAll("[^a-zA-Z0-9\\s]", "");
      String[] response_words = response.split(" ");

      //put all the options in a hash table
      Hashtable<String, Integer> word_frequency_table = new Hashtable<String, Integer>();
      for(int i = 0; i < options.length; i++){
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
         int option_index = GetOptionIndex(options[i], store, q_num);
         if(option_index == -1){
            System.out.println("ERROR GIVEN WITH INPUT: " + options[i]);
         }else{
            store.setIncrementFreq(q_num, option_index, freq);
         }
         answer += "\n<<" + options[i] + ">> frequency is: " + freq;
      }
      return answer;
   }

}



