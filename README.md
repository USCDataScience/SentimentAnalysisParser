# Sentiment Analysis Parser
A parser performing sentiment analysis that uses the [Apache OpenNLP](https://opennlp.apache.org/) and [Apache Tika](https://tika.apache.org/) libraries to perform text analysis on the the [Large Movie Review Dataset](http://ai.stanford.edu/~amaas/data/sentiment/). Negative and positive reviews were combined together in a file "result", and each review has a "positive" or a "negative" label before it.


# Use
### How to build Sentiment Analysis Parser

```shell
$ cd $HOME/src
$ git clone https://github.com/USCDataScience/SentimentAnalysisParser
$ cd SentimentAnalysisParser
$ mvn install assembly:assembly
```
### How to train a model

```shell
$ cd target/sentiment
$ mkdir -p model/org/apache/tika/parser/sentiment/topic/
$ bin/sentiment SentimentTrainer -model model/org/apache/tika/parser/sentiment/topic/en-sentiment.bin -lang en -data ./../../examples/categorical_dataset -encoding UTF-8
```
The model is written to en-sentiment.bin

### How to run the parser

Make sure you are in target/sentiment

```shell
$ bin/sentiment Tika -model model/org/apache/tika/parser/sentiment/topic/en-sentiment.bin -o ../../examples/gun-output1 -j ../../examples/gun-ads
```


# Contributors
* Chris A. Mattmann, JPL
* Anastasija Mensikova, Trinity College, CT


# Credits
This project began as the [Google Summer of Code 2016](https://summerofcode.withgoogle.com/projects/#6472482521350144) project of [Anastasija Mensikova](https://github.com/amensiko) for [Apache Software Foundation](http://www.apache.org/) under the supervision of [Chris Mattmann](https://github.com/chrismattmann)


# License 
[Apache License, version 2](http://www.apache.org/licenses/LICENSE-2.0)