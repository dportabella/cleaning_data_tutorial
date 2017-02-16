# Cleaning data

Simple examples about cleaning text data

## TextCleanExample.scala
- Word freq
- Special chars, identify and clean
- Word stemmer

## StanfordNLPExample.scala
- Natural Language Processing

## notebook/regexs.ipynb, regexs.pdf
- Regexs

## RemoveStopWordsExample.scala
- Stop words

## PatentNumbersPatterns.scala
- Find patterns in tokens

## EPFLPatentsProject.scala
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
$ export dbUrl="jdbc:mysql://cdm6-143.epfl.ch/patstat_2015a?user=mysqluser&password=__PASSWORD__&useSSL=false"
$ sbt "runMain application.TextCleanExample $dbUrl"
```

# How to run jupyther with the regexs example
```
docker run -it --rm -p 8888:8888 -v ./notebook:/home/jovyan/work jupyter/all-spark-notebook start-notebook.sh
```


# Do you have other use cases or questions?
Contact me at <david.portabella@gmail.com>
