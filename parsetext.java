import java.lang.*;
import java.io.*; 
import java.util.*;
import java.text.BreakIterator;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

class parsetext{

    public static String Tag(String sentence) {
        try{
        // Initialize the tagger
        MaxentTagger tagger = new MaxentTagger(
                "models/left3words-wsj-0-18.tagger");
 
        // The tagged string is returned 
        return tagger.tagString(sentence);
    }catch(ClassNotFoundException cl){

    }catch(IOException io){

    }
      return sentence;
    }
}