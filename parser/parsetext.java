import java.lang.*;
import java.io.*;
import java.util.*;
import java.text.BreakIterator;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

class parsetext{


     public static void main(String[] args) throws IOException,
            ClassNotFoundException {
 
        // Initialize the tagger
        MaxentTagger tagger = new MaxentTagger(
                "models/left3words-wsj-0-18.tagger");
 
        // The sample string
        String sample = "But maybe not.  I’m not sure. from 504. you are not going to learn from people. from 801. NOTpeople was selected.";
 
        // The tagged string
        String tagged = tagger.tagString(sample);
 
        // Output the result
        System.out.println(tagged);
    }

}
