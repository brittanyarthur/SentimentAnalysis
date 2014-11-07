import java.lang.*;
import java.io.*;
import java.util.*;
import java.text.BreakIterator;

/*
Program will be taking in a text file and partitioning it into distinct regions
based on which question they are a part of.
These regions will be stored in a string array.

Applying machine learning to sentiment analysis with a small data set

result is data curation

every data point updates a predictive model
predictive system will adapt, builds up what the optimal prediction method is, given new information
feed whether it made the right decision back into itself

bag of words can be used as a feature to be trained as a classifier 
: amount of negative words, amount of positive words
length of the answer can be a feature 

magnifier, strength words: really or 

bag of words for the how positive
> I wonder if counting the number of positive words vs. their proportion is a better predictor. 
>> I should carry out that analysis by hand and see what the results are
>> there is of course the subjectivity in what I regard as a very positive, etc. 
>> Another feautre that might be interesting to consider is how long the response is
>> Another feautre: # of positive words, # negative words (if I am going to use the Google API for predicting strength of reaction, perhaps LIWC method is suitable)

aylien text analysis api

call this a reinforcement learning 
*/
class findsentiment{

  public static class Globals{
    public static int numberofquestions = 6;
  }

  public static class keyvalue
  {
     private String keywords;
     private String fragments;
     private String notcontains;
     public keyvalue(String keywords, String fragments, String notcontains)
     {
        this.keywords = keywords;
        this.fragments = fragments;
        this.notcontains = notcontains;
     }
  }

  public static class searchstatus
  {
    private int current_question;
    private int question_matched;
    private boolean success_status;
    private boolean finished;
    public searchstatus(int current_question, int question_matched, boolean success_status, boolean finished)
    {
        this.current_question = current_question;
        this.question_matched = question_matched;
        this.success_status = success_status;
        this.finished = finished;
    }
  }

  public static void main (String[] args)
  {
     keyvalue[] keyword_question = setquestions();
     findanswers.SetUp();
     //Globals.numberofquestions = 6;
     ProcessFile(args[0], keyword_question);
  }

  protected static void ProcessFile(String file, keyvalue[] keyword_question){
    try{
        BufferedReader buffer = new BufferedReader(new FileReader(file));
        String line;
        //for testing purposes, I will for now give as an argument a file which is the one to process.
        int starting_index = 0;
        searchstatus status = new searchstatus(0,0,false,false);
        String[] questions = fillNullArray(Globals.numberofquestions);
        String[] responses = fillNullArray(Globals.numberofquestions);
        while ((line = buffer.readLine()) != null) {       
           rank(line, starting_index, keyword_question, status);
           //format
           if(status.success_status == true)
           {  
              line = line.replaceAll("\t","");
              questions[status.question_matched] += ("\n" + line + "\n\n");
              starting_index = status.current_question;
           }else{
              if(line.contains("Speaker 2")){
                line = line.replaceAll("Speaker 2:","");
                line = line.replaceAll("\t","");
                responses[status.question_matched] += (line + " ");
              }
           }
        }
        printResponses(responses, questions, keyword_question);
        buffer.close(); 
     }catch(IOException e){
           System.err.println("Caught IOException: " + e.getMessage());
     }
  }

  //All of the analysis of answers starts here
  protected static void printResponses(String[] responses, String[] questions, keyvalue[] keyword_question)
  {
     for(int question_num = 0; question_num < responses.length; question_num++)
     {
       if(responses[question_num]=="\n"){
          continue;
       }
       //Index 2 is for question 2 and contains responses to q2 and so on...
       System.out.println("------------------------------------------------------------------------------------------------------------");
       System.out.println("-----------------------------------------Question #"+question_num+"-----------------------------------------");
       System.out.println("--------------------------------------------------------------------------------------------\n");
        System.out.printf("%10s %20s \n\n", "*** QUESTION:", questions[question_num]);
        System.out.printf("%10s %20s \n", "*** RESPONSE: ",responses[question_num]);
       findmeaning(responses, question_num, keyword_question);
       String answer = findanswers.OptionSelected(question_num,responses[question_num]);
      //System.out.printf("*** ANSWER: %20s \n\n", answer);
     }
  }

  //starting very basics
  //next goal today is to make a design for how I will analyze the meaning
  protected static void findmeaning(String[] response, int question_num, keyvalue[] keyword_question)
  {
      //search for keywords
      //if a keyword is found, then look back 5 words to find positive words
      BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
      iterator.setText(response[question_num]);
      int start = iterator.first();
      String[] keywords = ((keyword_question[question_num]).keywords).split(" ");
      boolean printedyet_special_case = false;
      for (int end = iterator.next();
         end != BreakIterator.DONE;
         start = end, end = iterator.next()) {
         boolean printedyet = false;
         for(int match = 0; match < keywords.length; match++)
         {
            String curr_sentence = response[question_num].substring(start,end);
            curr_sentence = curr_sentence.toLowerCase();
            if(curr_sentence.contains(keywords[match]) && !printedyet)
            {
               //System.out.println("***************************************For Analysis***************************************\n"+response[question_num].substring(start,end));
               printedyet = true;
            }
            if(question_num==0 && !printedyet_special_case && (curr_sentence.contains("yes")||curr_sentence.contains("yeah")||curr_sentence.contains("no")))
            {
               System.out.println("==================___________Yes No Case: Special Case___________==================\n"+response[question_num].substring(start,end));
            }
            //only the first sentence is examined.
            printedyet_special_case = true;
         }
      } 
  }

  protected static String[] fillNullArray(int size){
    String[] array = new String[size];
    for(int fill_index = 0; fill_index < array.length; fill_index++){
      array[fill_index] = "";
    }
    return array;
  }

  //the problem right now is that recovering is bad from a misplaced question. 
  //perhaps it is best to always start from 0 not staring_index??
  protected static void rank(String line, int starting_index, keyvalue[] keyword_question, searchstatus status)
  {
      if(starting_index>keyword_question.length || line.contains("Speaker 2")){
        status.success_status = false;
        return;
      } 
      int score = 0;
      //check the line to see if it is any of the questions left to check
      for(int itor_questionnum = starting_index; itor_questionnum < keyword_question.length; itor_questionnum++){
        //reset score for the next iteration
        score = 0;

           String[] words = ((keyword_question[itor_questionnum]).keywords).split(" ");
           for(int match_index = 0; match_index < words.length; match_index++){
               if(line.contains(words[match_index])){
                  score += 1;
               }
           }

           //tally sentence fragments
           String[] sentencefragments = (keyword_question[itor_questionnum].fragments).split("%");
           for(int match_index2 = 0; match_index2 < sentencefragments.length; match_index2++){
               if(line.contains(sentencefragments[match_index2])){
                  score += 3;
               }
           }
           
           String[] not_contains_words = ((keyword_question[itor_questionnum]).notcontains).split("%");
           for(int match_index3 = 0; match_index3 < not_contains_words.length; match_index3++){
               if(line.contains(not_contains_words[match_index3]) && not_contains_words[match_index3].length() > 3){
                  score -= 100;
               }
           }
           /*  if the score was high enough:
            1. break out of this loop
            2. return the new starting question to search for
            3. return the success status & if success: which question was matched to caputure response
           */
            if(score>3){
                //System.out.println("\n\nMatchFound!\nSCORE: " + score + "\nQuestion: "+itor+"\nQuestion is: \n" + line);
                status.current_question = itor_questionnum+1;
                status.question_matched = itor_questionnum;
                status.success_status = true;
                return;
            }
      }
      //if the loop exits without breaking, then all questions have been considered and none matched
      status.success_status = false;
  }
  
  //todo: look at 1 question to see what # of each category is matched. do this 6 times to make a point system.
  //I am making the assumption that questions are being asked sequentially 
  protected static keyvalue[] setquestions()
  {
     keyvalue[] keyword_question = new keyvalue[6];
     
     //Is competition an important part of why you like to play video games?
     keyvalue general_keywords0 = new keyvalue("competition important part play", "is competition% competition an important% important part% why you like to play","");
     keyword_question[0] = general_keywords0;
     
     //What is it about competition that is motivating?
     keyvalue general_keywords1 = new keyvalue("What competition motivating motivation reward won win", "what is it% about competition% is motivating", "any circumstances");//any circumstances%
     keyword_question[1] = general_keywords1;

     //Do you prefer games where you are alone against other people or with others (on a team) against other people?
     keyvalue general_keywords2 = new keyvalue("prefer alone against others other team", "prefer games% alone against% against other people% with others against","worse");
     keyword_question[2] = general_keywords2;

     
     //When playing alone or as part  of a team, do you prefer to play against people who are better, the same, or worse than you?
     keyvalue general_keywords3 = new keyvalue("prefer better same worse equal", "playing alone or% play against% do you prefer to% better, the same% better, same% same, or worse% same, worse% than you","");
     keyword_question[3] = general_keywords3;
     
     //Do you prefer direct competition - where you can influence the other person, strategize (like in chess), or indirect competition
     //where you cannot influence them, it's primarily about luck (like in bingo, Yahtzee)?
     keyvalue general_keywords4 = new keyvalue("direct influence strategize indirect luck", "direct competition% influence the other% indirect competition","");
     keyword_question[4] = general_keywords4;
     
     //Are there any circumstances related to video gaming under which competition against others is motivating to you?
     keyvalue general_keywords5 = new keyvalue("any circumstances competition motivating scholarship", "any circumstances% circumstances related to% competition against others% is motivating","");
     keyword_question[5] = general_keywords5;
     
     return keyword_question;
  }
  
}







