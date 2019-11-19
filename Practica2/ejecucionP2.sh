#!/bin/bash

javac -cp lucene-core-8.0.0.jar:lucene-analyzers-common-8.0.0.jar:tika-app-1.22.jar extractorinformacion/*.java
java -cp lucene-core-8.0.0.jar:.:lucene-analyzers-common-8.0.0.jar:.:tika-app-1.22.jar:. extractorinformacion.ExtractorInformacion $*

rm extractorinformacion/*.class
