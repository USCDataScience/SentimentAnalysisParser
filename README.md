Sentiment Analysis Parser
=========================
A parser performing sentiment analysis that uses the [Apache OpenNLP](https://opennlp.apache.org/) and [Apache Tika](https://tika.apache.org/) libraries to perform text analysis on the the [Large Movie Review Dataset](http://ai.stanford.edu/~amaas/data/sentiment/). We have combined both negative and positive reviews together in a file result, and each review has a "positive" or a "negative" label before it.


Use
===
1. How to build Sentiment Analysis Parser
```shell
$ cd $HOME/src
$ git clone https://github.com/USCDataScience/SentimentAnalysisParser
$ cd SentimentAnalysisParser
$ mvn assembly:assembly
```

2. How to train a model
```shell
$ cd target/sentiment/bin
$ ./sentiment SentimentTrainer -model en-sentiment.bin -lang en -data ./../../../result -encoding UTF-8
```

The model is written to en-sentiment.bin


Contributors
============
* Chris A. Mattmann, JPL
* Anastasija Mensikova, Trinity College, CT


Credits
=======
This project began as the [Google Summer of Code 2016](https://summerofcode.withgoogle.com/projects/#6472482521350144) project of [Anastasija Mensikova](https://github.com/amensiko) for [Apache Software Foundation](http://www.apache.org/) under the supervision of [Chris Mattmann](https://github.com/chrismattmann)


License 
======= 
[Apache License, version 2](http://www.apache.org/licenses/LICENSE-2.0)