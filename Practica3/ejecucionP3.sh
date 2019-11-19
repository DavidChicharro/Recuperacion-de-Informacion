#!/bin/bash


if [ -d "./jar_files" ];
then
   unzip -f jar_files.zip -d ./jar_files
else
   unzip jar_files.zip -d ./jar_files
fi

javac -cp jar_files/commons-beanutils-1.9.3.jar:jar_files/commons-lang3-3.6.jar:jar_files/commons-text-1.1.jar:jar_files/commons-collections-3.2.2.jar:jar_files/commons-logging-1.2.jar:jar_files/opencsv-4.1.jar informacionpeliculas/*.java

java -cp jar_files/commons-beanutils-1.9.3.jar:.:jar_files/commons-lang3-3.6.jar:.:jar_files/commons-text-1.1.jar:jar_files/commons-collections-3.2.2.jar:.:jar_files/commons-logging-1.2.jar:.:jar_files/opencsv-4.1.jar:. informacionpeliculas.InformacionPeliculas

rm informacionpeliculas/*.class
