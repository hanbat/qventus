#!/bin/bash

# export JAVA_HOME=$(echo $JAVA_HOME)

# echo $JAVA_HOME

echo --- Deleting
rm *.jar
rm *.class

# echo --- Compiling
# $JAVA_HOME/javac *.java
# if [ $? -ne 0 ]; then
#     exit
# fi

# echo --- Jarring
# $JAVA_HOME/bin/jar -cf Invest_tweet.jar *.class

echo --- Running
INPUT=tweet_input/tweets_large.txt
OUTPUT=tweet_output/ft1.txt
OUTPUT1=tweet_output/ft2.txt


rm -fr $OUTPUT
javac Invest_tweet.java
time java Invest_tweet $INPUT $OUTPUT $OUTPUT1