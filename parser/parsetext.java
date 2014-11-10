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
        //String sample = "But maybe not.  I’m not sure. from 504. you are not going to learn from people. from 801. NOTpeople was selected.";
        String sample = "your like it. youre like it. you're like it. yours like it. youve like it. you've like it. ";

        // The tagged string
        String tagged = tagger.tagString(sample);
 
        // Output the result
        System.out.println(tagged);
    }

}

/*
PRPLIST   : 
he he her herself he him himself his it it it its it itself me my myself our ourselves she she she she their them themselves they they they they us we we we we we ya you you you your you you 

NONPRPLIST: 
anybod \* anyone \* anything everybod \* everyone \* everything \* hed 'd hers hes 's i id i 'd i 'll im i 'm itd 'd itll 'll 's ive i 've lets let 's mine nobod \* oneself other others ours 'd 'll shes 's somebod \* someone \* something \* somewhere stuff that thatd that 'd thatll that 'll thats that 's thee \* these theyd 'd theyll 'll theyve 've thine thing \* this those thou thoust thy 'd 'll 're weve 've what whatever whats what 's which whichever who whod who 'd wholl who 'll whom whose yall y' all ye youd 'd youll 'll youre 're yours youve 've besides could couldnt could n't couldve could 've desir \* expect \* hope hoped hopeful hopefully hopefulness hopes hoping ideal \* if impossib \* inadequa \* lack \* liabilit \* mistak \* must mustnt must ` nt must n't mustve must 've need needed needing neednt need ` nt need n't needs normal ought oughta oughtnt ought ` nt ought n't oughtve ought 've outstanding prefer \* problem \* rather regardless regret \* should shouldnt should ` nt should n't shoulds shouldve should 've undesire \* undo unneccess \* unneed \* unwant \* wanna want wanted wanting wants wish wished wishes wishing would wouldnt would n't wouldve would 've yearn \* do to don ? ??t dont not do n't did n't didnt 
        // The sample string
        //String sample = "But maybe not.  I’m not sure. from 504. you are not going to learn from people. from 801. NOTpeople was selected.";
        String sample = "anybod* anyone* anything everybod* everyone* everything* he hed he'd her hers herself hes he's him himself his i id i'd i'll im i'm it itd it'd itll it'll its it's itself ive i've lets let's me mine my myself nobod* oneself other others our ours ourselves she she'd she'll shes she's somebod* someone* something* somewhere stuff that thatd that'd thatll that'll thats that's thee their* them themselves these they theyd they'd theyll they'll theyve they've thine thing* this those thou thoust thy us we we'd we'll we're weve we've what whatever whats what's which whichever who whod who'd wholl who'll whom whose ya yall y'all ye you youd you'd youll you'll your youre you're yours youve you've besides could couldnt couldn't couldve could've desir* expect* hope hoped hopeful hopefully hopefulness hopes hoping ideal* if impossib* inadequa* lack* liabilit* mistak* must mustnt must'nt mustn't mustve must've need needed needing neednt need'nt needn't needs normal ought oughta oughtnt ought'nt oughtn't oughtve ought've outstanding prefer* problem* rather regardless regret* should shouldnt should'nt shouldn't shoulds shouldve should've undesire* undo unneccess* unneed* unwant* wanna want wanted wanting wants wish wished wishes wishing would wouldnt wouldn't wouldve would've yearn* do to don’t dont not don't didn't didnt";

        // The tagged string
        /String tagged = tagger.tagString(sample);
        String[] list = tagged.split(" ");
        String prp_list = "";
        String non_prp_list = "";
        for(int i = 0; i < list.length; i++){
            String[] parts = list[i].split("/");
            if(parts[1].contains("PRP")){
                parts[1].replace("\\*","");
                prp_list += parts[0] + " ";
            }else{
                parts[1].replace("\\*","");
                non_prp_list += parts[0] + " ";
            }
        }

        System.out.printf("PRPLIST   : \n%s\n",prp_list);
        System.out.printf("NONPRPLIST: \n%s\n",non_prp_list);
*/
