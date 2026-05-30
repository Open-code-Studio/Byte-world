#!/bin/bash
export JAVA_HOME=/Users/cangcang/Documents/jdk21
export PATH=$JAVA_HOME/bin:$PATH

cd "$(dirname "$0")"

echo "=== Compiling ==="
$JAVA_HOME/bin/javac -d build src/com/sandbox/*.java

echo "=== Running Sandbox Game ==="
$JAVA_HOME/bin/java -cp build com.sandbox.Game