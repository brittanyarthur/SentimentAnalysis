Sentiment Analysis Tool for Interviews
======================================

This tool can be applied to sentiment analysis problems that arise during natural language interviews.
Given a set of options by the interviewer, and the response from the respondent, the tool 
will predict which option the respondent chose. 

It will also do the work of finding where each question was asked in the text file of the interview. It will aggregate all responses to each question and responses to follow up questions from each question. 

* Tools: LIWC (http://www.liwc.net), Stanford Tagger (http://nlp.stanford.edu/software/tagger.shtml)
* NLP Techniques: reinforcement learning, bag of words
* Data Structures used: trie, hash table, queue, nested objects, linked lists

Example (using fake data)
-------------------------
|    | Result
|-------------|--------
| QUESTION | Speaker 1: Do you prefer direct competition where you can influence the other person, strategize (like in chess), OR indirect competition - where you cannot influence them, it's primarily about luck (like in bingo, Yahtzee)?                                    
| RESPONSE | All kinds of tile based games are fun, they are challenging and make me think. But, hm, I'm not sure.. let me think.. I also like the game Sorry sometimes, but like, it's about luck and it's very fun but that is the only one I can think of. Overall, I don't like very much luck though. It can get kindof like, boring, with just like luck..you know? But I've enjoyed all the strategy games I've played, so yeah direct. 
|PREDICTED SELECTION | direct competition

Options
-------

|Option | Description
|------|----------
| `-a` | Analysis. Gives output of Stanford tagger. Gives output of the queue. Gives frequency and nearby positive-word count for each option. The Stanford part of speech tagger is used to handle the word “not” - we would like to know what not is being applied to: not what? The queue is used to find the number of positive words ahead of each word and behind each word. The output only shows when an element is being added to the queue > it tells you how many positive words were in the queue at the time when the new element was being pushed. The frequency is used in predicting which option the respondent selected. Positive words are determined by LIWC. 
| `-l` | Like Handling. This gives output of what the response looked like directly before removing filler likes and the output of the response after removing filler likes. Filler likes are removed using LIWC and the Stanford part of speech tagger. All words tagged as pronouns were gathered by LIWC, then the Stanford parser went through all of them and only kept the ones that were possessive pronouns. If a possessive pronouns precedes “like”, this implies that “like” in that context is a positive word. If a possessive pronoun does not precede like, it is regarded as a filler and removed. 
| `-f` | Print out of fully preprocessed response is shown.

Methods
=======

1 Handling the word “not”
-----------------------
The word 'not' is always a modifier to some other word. When 'not' encountered, we want to know what the respondent meant to apply it to. The Stanford Tagger is used to label words in the sentence after 'not' until it encounters the predicted subject of 'not'. It will look through words that fall into “linker” categories, as I call them, until it finds a word that is *not* a “connector”. A word is a “connector” if its part of speech is used to express a relationship between two other words. 

Not will be applied to words coming after it - nouns and adjectives. 

`not` - `connectors` - `adjective or noun`

"Connector" parts of speech include: IN, VB, VBD, VBG, VBN, VBP, VBZ, DT, RB, RBR, RBS, TO

See part of speech labels here: http://stackoverflow.com/questions/1833252/java-stanford-nlp-part-of-speech-labels

Example: 

| Input | Tagger | Output
|-------|--------|-------
| "Not any of the teams.” | Not/RB any/DT of/IN the/DT teams/NNS ./. | "Not any of the NOTteams."
| " I don't like to see any mess." | I/PRP do/VBP n't/RB like/VB to/TO see/VB any/DT mess/NN ./.  | "I do notlike to see any NOTmess."
| “I did not like the wind.” | I/PRP did/VBD not/RB like/IN the/DT wind/NN ./.  | "I did not like the NOTwind."


2 Handling the word “like”
------------------------

The word 'like' is an important word, as it can be used to express preference. However, it can also be used as a filler, for example "Then like, we ...". To handle this, LIWC is used in combination with the Stanford Tagger. LIWC provided a list of words that are pronouns, then the Tagger identified which were possessive pronouns. If a possessive pronoun precedes the word 'like', then 'like' is considered to be a positive word that expresses preference - such as "I like this". Or else, it is discarded. 

Example:

| Input | Output
|-------|-------
| So like yesterday I went to beach and like, it  was really cold. I don't know like how people can go surfing, but I guess they like it. I like the beach, really I do like to go, but like gosh why does it have to be so windy? I thought like, if I brought a sweater it could be alright. | so yesterday i went to beach and it was really cold. i do not know how people can go surfing but i guess they like it. i like the beach really i do like to go but gosh why does it have to be so windy? i thought if i brought a sweater it could be alright. 


3 Nearby Positive Words and Frequency 
-------------------------------------
There are predicted keywords that will be used by the respondent to express which option they have selected. For example, if I ask "Do you like cookies or icecream more?" the one keyword set will be "cookies, cookie" for the first option and "icecream popsicles" for the second option. There may be a third option to express both with keywords such as "both either depends". 

I use a queue to count how many positive words there within a distance of 5 words for each option set. For example, "I really enjoy cookies, they are great!" - the count for positive words surrounding the cookie option will increase by +2. 

At the end of the response, each option will have a score to represent how many positive words there are surrounding all keywords expressing that option. This count will be multiplied by 2. Each option will also have a frequency count representing the number of times a word for that option was mentioned in the response.

The total score for each option = frequency + positive_word_count*2

Whichever option has the highest score is the one that this tool will predict was selected. It is important that Method (1) and (2) mentioned above were used so that we do not inaccurately count "like" as positive or ignore the importance of the "not" modifier. 

Further Preprocessing: 
* 'don’t' is replaced with 'do not'
* 'didn’t' is replaced with 'did not'
* 'not like' is replaced with 'notlike'

How to run
==========

First add to classpath (Linux command):
---------------------------------------
export CLASSPATH=${CLASSPATH}:stanford-postagger-2011-04-20.jar:stanford-postagger.jar

Use Command Line
----------------
If no options are specified, the default printout will be given
If no output file is given, output will print to stdout
Options can be given in any combination.

Available options are: a l f 

template command: 

java findsentiment `(optional options here)` `(optional output file here)` `(inputfile here)`

example commands:
-----------------
java findsentiment inputfile

java findsentiment -l results/Q8_500/option_like transcripts_q8/Q8_500


