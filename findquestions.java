import java.lang.*;
import java.io.*;
/*
Program will be taking in a text file and partitioning it into distinct regions
based on which question they are a part of.
These regions will be stored in a string array.
*/
class findquestions{

  public static class keyvalue
  {
     private String keywords;
     private String fragments;
     public keyvalue(String keywords, String fragments)
     {
        this.keywords = keywords;
        this.fragments = fragments;
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
     int numberofquestions = 6;
     try{
        BufferedReader buffer = new BufferedReader(new FileReader(args[0]));
        String line;
        //for testing purposes, I will for now give as an argument a file which is the one to process.
        int starting_index = 0;
        searchstatus status = new searchstatus(0,0,false,false);
        String[] responses = fillNullArray(numberofquestions);//new String[numberofquestions];
        while ((line = buffer.readLine()) != null) {
           rank(line, starting_index, keyword_question, status);
           if(status.success_status == true)
           {  
              starting_index = status.current_question;
              responses[status.current_question] += line;
           }else{
              System.out.printf("***RESPONSE: %s\n",line);
           }
          //contain the response for that question
         // if(status.current_question<numberofquestions){
              responses[status.current_question] += (line + "\n");
              
       //   }else{
       //       break;
       //   }
        }
        buffer.close(); 
        /*for(int print_itor = 0; print_itor<responses.length; print_itor++){
            if(responses[print_itor]!="\n"){
              System.out.println(responses[print_itor] + "\n");
            }
        }*/
     }catch(IOException e){
           System.err.println("Caught IOException: " + e.getMessage());
     }
  }

  protected static String[] fillNullArray(int size){
    String[] array = new String[size];
    for(int fill_index = 0; fill_index < array.length; fill_index++){
      array[fill_index] = "";
    }
    return array;
  }

  //the problem right now is that recovering is bad from a misplaced question. perhaps it is best to always start from 0 not staring_index??
  protected static void rank(String line, int starting_index, keyvalue[] keyword_question, searchstatus status)
  {
      if(starting_index<keyword_question.length){
        return;
      }
      int score = 0;
      //check the line to see if it is any of the questions left to check
      for(int itor = starting_index; itor < keyword_question.length; itor++){
        //tally keywords
           String[] words = ((keyword_question[itor]).keywords).split(" ");
        for(int match_index = 0; match_index < words.length; match_index++){
               if(line.contains(words[match_index])){
                  score += 1;
               }
           }
           //tally sentence fragments
           String[] sentencefragments = (keyword_question[itor].fragments).split("%");
           for(int match_index2 = 0; match_index2 < sentencefragments.length; match_index2++){
               if(line.contains(sentencefragments[match_index2])){
                  score += 3;
               }
           }
           /*  if the score was high enough:
            1. break out of this loop
            2. return the new starting question to search for
            3. return the success status & if success: which question was matched to caputure response
           */
            if(score>4){
                System.out.println("\n\nMatchFound!\nSCORE: " + score + "\nQuestion: "+itor+"\nQuestion is: \n" + line);
                status.current_question = itor+1;
                status.question_matched = itor;
                status.success_status = true;
                return;
            }
      }
      //if the loop exits without breaking, then all questions have been considered and none matched
      status.success_status = false;
  }
  
  //todo: look at 1 question to see what # of each category is matched. do this 5 times to make a point system.
  //I am making the assumption that questions are being asked sequentially 
  protected static keyvalue[] setquestions()
  {
     keyvalue[] keyword_question = new keyvalue[6];
     
     //Is competition an important part of why you like to play video games?
     keyvalue general_keywords0 = new keyvalue("competition important part play", "is competition% competition an important% important part% why you like to play");
     keyword_question[0] = general_keywords0;
     
     //What is it about competition that is motivating?
     keyvalue general_keywords1 = new keyvalue("What competition motivating", "what is it about% about competition% is motivating");
     keyword_question[1] = general_keywords1;

     //Do you prefer games where you are alone against other people or with others (on a team) against other people?
     keyvalue general_keywords2 = new keyvalue("prefer alone against others", "prefer games% alone against% against other people% with others against");
     keyword_question[2] = general_keywords2;

     
     //When playing alone or as part  of a team, do you prefer to play against people who are better, the same, or worse than you?
     keyvalue general_keywords3 = new keyvalue("prefer better same worse", "playing alone or% play against% do you prefer to% better, the same% better, same% same, or worse% same, worse% than you");
     keyword_question[3] = general_keywords3;
     
     //Do you prefer direct competition - where you can influence the other person, strategize (like in chess), or indirect competition
     //where you cannot influence them, it's primarily about luck (like in bingo, Yahtzee)?
     keyvalue general_keywords4 = new keyvalue("direct influence strategize indirect luck", "direct competition% influence the other% indirect competition");
     keyword_question[4] = general_keywords4;
     
     //Are there any circumstances related to video gaming under which competition against others is motivating to you?
     keyvalue general_keywords5 = new keyvalue("circumstances under", "any circumstances% circumstances related to% competition against others% is motivating");
     keyword_question[5] = general_keywords5;
     
     return keyword_question;
  }
  
}







