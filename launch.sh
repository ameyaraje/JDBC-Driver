 #! /bin/bash
javac -cp './mysql-connector-java-5.0.8-bin.jar:.' MovieDB.java

echo 'Compile Successful'

java -cp './mysql-connector-java-5.0.8-bin.jar:.' MovieDB
