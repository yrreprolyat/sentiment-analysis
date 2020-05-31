# Movie Review Analysis

**Goal: The goal of this project is to demonstrate knowledge of the following Java programming concepts:**

1.	Input/Output to and from the terminal. 
2.	Storing data in a file and reading data from a file.
3.	Creating object-oriented classes and methods to handle data.
4.	Using data structures to store data in main memory (e.g. HashSet, ArrayList).
5.	Working with character strings.
6.	Using Javadoc comments and generating and html documentation of the program.
7.	Using Java Exceptions to improve the error handling capabilities of the program.
8.  Using JUnit to test Java code

# Description:
A program to classify a set of movie reviews as positive or negative based on their sentiment. This process is known as Sentiment Analysis, and there are multiple approaches to analyze the data and estimate the sentiment. More information about sentiment analysis can be found on Wikipedia and other sources.
https://en.wikipedia.org/wiki/Sentiment_analysis 

In this project, a Java program will classify a review as positive or negative by counting the number of positive and negative words that appear in that review.  

# Program flow

## Start the program

The program has two inputs as command line arguments, which are the paths to two text files:  the list of positive words (positive-words.txt) and the list of negative words (negative-words.txt). The program loads the positive words and negative words and stores them in two separate lookup tables. The HashSet data structure is used as a lookup table in Java as it provides a fast way to look if a word exists in it or not.

Run example:

```
javac -d classes ./sentiment_analysis/src/*.java
java -cp classes sentiment_analysis.MovieReviewApp ./sentiment_analysis/data/positive-words.txt ./sentiment_analysis/data/negative-words.txt
javadoc -d docs ./sentiment_analysis/src/*.java
```
## Load existing database of movie reviews

Every time the program loads, it first checks if there exists a database file (database.txt) in its working directory. If such a file exists, it will load its contents (movie reviews) into the main memory.


## Present the user with an interaction menu

0. Exit program.
1. Load new movie review collection (given a folder or a file path).
2. Delete movie review from database (given its id).
3. Search movie reviews in database by id or by matching a substring.

## When the user selects “0”, the database file saves and the program exits.

# Notes: 

* The above interaction menu is coded in a loop, so that the user can choose among the different options multiple times, until they choose option “0”, in which case the program terminates.
* Every time the program loads, it first checks if there exists a database file in its working directory. If such a file exists, it loads its contents (movie reviews) into the main memory. If the database file does not exist, an empty HashMap will be created. When the program exits (user selects action “0”), it saves the new database contents back to the database file, replacing the old one.
* When the user selects option “1”:
  * The program also asks  the user to provide the real class of the review collection (if known). The user can choose from the options: 0. Negative, 1. Positive, and 2. Unknown. 
  * Upon loading each review, the program assigns a unique ID to each review, which does not conflict with existing ones, and it also assigns the value of the real class (as provided by the user). 
  * Then the program automatically classifies each review, as positive or negative by counting the number of positive and negative words that appear in that review and assign a value to the “predictedClass” field of each review. The overall classification accuracy is also be reported, if the real class is known.
  * Finally, the newly loaded reviews are added to the permanent database.
  * When the user selects option “3”, the results are printed in a formatted manner. The printed information is a table with each row showing: review ID, first 50 characters of review text, predicted class, real class.



