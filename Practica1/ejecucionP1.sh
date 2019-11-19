#!/bin/bash

javac -cp tika-app-1.22.jar extractorinformacion/*.java
java -cp tika-app-1.22.jar:. extractorinformacion.ExtractorInformacion $*

rm extractorinformacion/*.class
