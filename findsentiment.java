import java.lang.*;
import java.io.*;
import java.util.*;
import java.text.BreakIterator;

// options for debug output are recorded here. file output desination is also recorded.
class SettingOptions{
    private static String cmd_options = "";
    private static String file_destination;
    private static SettingOptions opts;
    private SettingOptions(){
      this.cmd_options = "";
      this.file_destination = "";
    }
    public static SettingOptions getOptions(){
      if(opts == null){
        opts = new SettingOptions();
      }
      return opts;
    }
    public static String get_cmds(){
        return cmd_options;
    }
    public static void set_cmds(String cmds){
        cmd_options = cmds;
    }
    public static String get_file_destination(){
        return file_destination;
    }
    public static void set_file_destination(String filedestination){
        file_destination = filedestination;
    }
}

class findsentiment{
  public class Globals{
    public static final int numberofquestions = 6;
  }

  public static void main (String[] args)
  {
     SettingOptions opts = SettingOptions.getOptions();
     String filename = "";
     switch(args.length){
        case 0: System.out.println("ERROR: File required as an argument.");
                return;
        case 1: filename = args[0]; 
                break;
        case 2: if(args[0].contains("-")) {
                   opts.set_cmds(args[0]);
                   filename = args[1];
                } else {
                   opts.set_file_destination(args[0]);
                   filename = args[1];
                }
                break;
        case 3: if(args[0].contains("-")) {
                   opts.set_cmds(args[0]);
                }else{
                   System.out.println("Remember to include a dash (-) when specifying options.");
                }
                opts.set_file_destination(args[1]);
                filename = args[2];
                break;
        default: System.out.println("ERROR: Too many arguments given. see README.");
                return;
     }
     String[][] questions_responses = findquestions.ProcessFile(filename);
     if (questions_responses[0][0] == null){
        return;
     } 
     PrintResponses(questions_responses[0], questions_responses[1]);
  }

  // Print the question and response. Then get the predicted answer immediately and print it.
  // Index 2 is for question 2 and contains responses to q2 and so on...
  protected static void PrintResponses(String[] questions, String[] responses)
  {
       SettingOptions opts = SettingOptions.getOptions();
       findanswers.SetUp();
       for(int question_num = 0; question_num < responses.length; question_num++)
       {
           //ignore newlines
          if(responses[question_num]=="\n"){
             continue;
          }
          
          String heading = "---------------------------------------------------------------------------------------" 
                           + "-----------------------------------------Question #"+question_num+"-----------------------------------"
                           + "---------------------------------------------------------------------------------------\n";
          PreProcess.print("", opts.get_cmds(), heading);
          PreProcess.printFormattedBlock("*** QUESTION", questions[question_num]);
          PreProcess.printFormattedBlock("*** RESPONSE ", responses[question_num]);
          //preprocess the response to increase accuracy of prediction
          responses[question_num] = PreProcess.Begin(responses[question_num]);
          findanswers.OptionSelected(question_num,responses[question_num]);
     }
  }
  
}







