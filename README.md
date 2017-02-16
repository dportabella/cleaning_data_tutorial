# Cleaning data

Simple examples about cleaning text data

See source code, output and comments on the scala files:

## [TextCleanExample.scala](https://github.com/dportabella/cleaning_data_tutorial/blob/master/src/main/scala/application/TextCleanExample.scala)
- Word freq
- Special chars, identify and clean
- Word stemmer

## [StanfordNLPExample.scala](https://github.com/dportabella/cleaning_data_tutorial/blob/master/src/main/scala/application/StanfordNLPExample.scala)
- Natural Language Processing

## notebook/regexs.ipynb, [regexs.pdf](https://github.com/dportabella/cleaning_data_tutorial/blob/master/notebook/regexs.pdf)
- Regexs

## [RemoveStopWordsExample.scala](https://github.com/dportabella/cleaning_data_tutorial/blob/master/src/main/scala/application/RemoveStopWordsExample.scala)
- Stop words

## [PatentNumbersPatterns.scala](https://github.com/dportabella/cleaning_data_tutorial/blob/master/src/main/scala/application/PatentNumbersPatterns.scala)
- Find patterns in tokens

## [EPFLPatentsProject.scala](https://github.com/dportabella/cleaning_data_tutorial/blob/master/src/main/scala/application/EPFLPatentsProject.scala)
- querying Patstat outside the SQL relational model


# Next time
- comparing text, text distance, alignment, disambiguation, google refine…
- regex vs CFG, web scraping, table stats, validation, data curation workflow


# Requirements
- [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/)
- [SBT >=0.13.12](http://www.scala-sbt.org/) (build tool for Scala)
- [IntelliJ](https://www.jetbrains.com/idea/) with the Scala plugin (IDE, optional)

# How to run a scala example
```
$ sbt "runMain application.TextCleanExample"
$ sbt "runMain application.StanfordNLPExample"
$ export dbUrl="jdbc:mysql://example.com/patstat_2015a?user=__USER__&password=__PASSWORD__&useSSL=false"
$ sbt "runMain application.RemoveStopWordsExample $dbUrl"
$ sbt "runMain application.PatentNumbersPatterns $dbUrl"
$ sbt "runMain application.EPFLPatentsProject $dbUrl"
```

# How to run jupyther with the regexs example
```
docker run -it --rm -p 8888:8888 -v ./notebook:/home/jovyan/work jupyter/all-spark-notebook start-notebook.sh
```


# Do you have other use cases or questions?
Contact me at <david.portabella@gmail.com>
