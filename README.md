Sentiment Analysis Tool for Interviews
======================================

This tool can be applied to sentiment analysis problems that arise during interviews.
Given a set of options by the interviewer, and the response from the respondent, the tool 
will predict which option the respondent chose. 

* Tools: LIWC, Stanford Tagger 
* Techniques: reinforcement learning, bag of words

Example (using fake data)
-------------------------
|    | Result
|-------------|--------
|*** QUESTION |
|                          Speaker 1: Do you prefer direct competition -     
|                          where you can influence the other person,        
|                          strategize (like in chess), OR indirect          
|                          competition - where you cannot influence them,   
|                          it's primarily about luck (like in bingo,        
|                          Yahtzee)?                                        
|*** RESPONSE |
|                          All kinds of tile based games are fun, they are  
|                          challenging and make me think. But, hm, I'm not  
|                          sure.. let me think.. I also like the game Sorry 
|                          sometimes, but like, it's about luck and it's    
|                          very fun but that is the only one I can think    
|                          of. Overall, I don't like very much luck though. 
|                          It can get kindof like, boring, with just like   
|                          luck..you know? But I've enjoyed all the         
|                          strategy games I've played, so yeah direct.      
|
|
|PREDICTED SELECTED OPTION | strategize strategies strategy skill direct influence 

Options
-------

|Option | Description
|------|----------
| `-a` | Analysis. Gives output of Stanford tagger. Gives output of the queue. Gives frequency and nearby positive-word count for each option. The Stanford part of speech tagger is used to handle the word “not” - we would like to know what not is being applied to: not what? The queue is used to find the number of positive words ahead of each word and behind each word. The output only shows when an element is being added to the queue > it tells you how many positive words were in the queue at the time when the new element was being pushed. The frequency is used in predicting which option the respondent selected. Positive words are determined by LIWC. 
| `-l` | Like Handling. This gives output of what the response looked like directly before removing filler likes and the output of the response after removing filler likes. Filler likes are removed using LIWC and the Stanford part of speech tagger. All words tagged as pronouns were gathered by LIWC, then the Stanford parser went through all of them and only kept the ones that were possessive pronouns. If a possessive pronouns precedes “like”, this implies that “like” in that context is a positive word. If a possessive pronoun does not precede like, it is regarded as a filler and removed. 
| `-f` | Print out of fully preprocessed response is shown.

Methods
=======

Further Preprocessing: 
* 'don’t' is replaced with 'do not'
* 'didn’t' is replaced with 'did not'
* 'not like' is replaced with 'notlike'

Handling the word “not”
-----------------------
When the word “not” is encountered, we want to know what the respondent meant to apply it to. The Stanford Tagger labels each part of the sentence preceding “not”. It will look through words that fall into “linker” categories, as I call them, until it finds a word that is /not/ a “connector”. A word is a “connector” if its part of speech is used to express a relationship between two other words. 

if the word “not” is seen I need to know - what is not applying to? not what? This is what the stanford parser helps with. 
Not will be applied to words coming after it - nouns and adjectives. 
'<not> <connectors> <adjective or noun>'

Connectors parts of speech: IN, VB, VBD, VBG, VBN, VBP, VBZ, DT, RB, RBR, RBS, TO
See part of speech labels here: http://stackoverflow.com/questions/1833252/java-stanford-nlp-part-of-speech-labels

|Examples using the test file:|

Input: '“Not any of the teams.”'
I want to know: not /what/? One of the functions predicts what this /not/ is by using some functionality
from the Stanford parser and from LIWC.
Output: '"Not any of the NOTteams."'

* See question 2 for good example analysis of the “like” cases and “not” cases.


Handling the word “like”
------------------------

See question 3 for analysis of the “like”. 
Prior to any preprocessing:
*** RESPONSE 
                         | It's always more fun to play against people who  
                         | are the same as me. I get bored playing if they  
                         | are worse or better than me. I think the same is 
                         | pretty great though, I play against equal        
                         | competition frequently, equal and same. It like. 
                         | You know like it. Because like i am. I like it.  
                         | So like yesterday I went to beach and like, it   
                         | was really cold. I don't know like how people    
                         | can go surfing, but I guess they like it. I like 
                         | the beach, really I do like to go, but like gosh 
                         | why does it have to be so windy? I thought like, 
                         | if I brought a sweater it could be alright. I    
                         | didn't like the wind. 

After some preprocessing:
[LIKE CASE] REPONSE WAS:  its always more fun to play against people who are the same as me. i get bored playing if they are worse or better than me. i think the same is pretty great though i play against equal competition frequently equal and same. it like. you know like it. because like i am. i like it. so like yesterday i went to beach and like it was really cold. i do not know like how people can go surfing but i guess they like it. i like the beach really i do like to go but like gosh why does it have to be so windy? i thought like if i brought a sweater it could be alright. i did not like the wind. 

After removing filler preprocessing:
[LIKE CASE] NEW RESPONSE IS:  its always more fun to play against people who are the same as me. i get bored playing if they are worse or better than me. i think the same is pretty great though i play against equal competition frequently equal and same. it like. you know it. because i am. i like it. so yesterday i went to beach and it was really cold. i do not know how people can go surfing but i guess they like it. i like the beach really i do like to go but gosh why does it have to be so windy? i thought if i brought a sweater it could be alright. i did not like the wind.

In addition, here is another case of the use of not being applied to its subject. Sentence was: “I did not like the wind”:  
element: i
element: did
element: notlike
element: the
positive words in queue = 0
for word (new element): notwind


How to run:

First set classpath:
export CLASSPATH=${CLASSPATH}:stanford-postagger-2011-04-20.jar:stanford-postagger.jar

If no options are specified, the default printout will be given
If no output file is given, output will print to stdout
Options can be given in any combination.
Available options are: a l f 
template command: 
java findsentiment <optional options here> <optional output file here> <inputfile here>

example commands:
java findsentiment inputfile
java findsentiment -l results/Q8_500/option_like transcripts_q8/Q8_500


