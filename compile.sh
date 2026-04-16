#!/bin/bash

# Compile script for Hospital Management System
# Usage: ./compile.sh

echo "======================================================"
echo "RBH Hospital Management System - Compilation Script"
echo "======================================================"

# Check if bin directory exists
if [ ! -d "bin" ]; then
    echo "Creating bin directory..."
    mkdir -p bin
fi

# Find Java compiler
JAVAC=$(which javac)
if [ -z "$JAVAC" ]; then
    echo "ERROR: javac not found. Please install Java 11+"
    exit 1
fi

echo "Using Java compiler: $JAVAC"

# Set classpath
CLASSPATH="lib/mysql-connector-java-8*.jar:."
export CLASSPATH

# Compile phase
echo ""
echo "Compiling Java files..."
echo "--------------------------------------------------"

# Compile db package
echo "Compiling db package..."
$JAVAC -cp "bin:lib/*" -d bin src/db/*.java 2>&1
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile db package"
    exit 1
fi

# Compile model package
echo "Compiling model package..."
$JAVAC -d bin src/model/*.java 2>&1
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile model package"
    exit 1
fi

# Compile dao package
echo "Compiling dao package..."
$JAVAC -cp "bin:lib/*" -d bin src/dao/*.java 2>&1
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile dao package"
    exit 1
fi

# Compile datastructure package
echo "Compiling datastructure package..."
$JAVAC -cp "bin:lib/*" -d bin src/datastructure/*.java 2>&1
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile datastructure package"
    exit 1
fi

# Compile ui package
echo "Compiling ui package..."
$JAVAC -cp "bin:lib/*" -d bin src/ui/*.java 2>&1
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile ui package"
    exit 1
fi

# Compile Main
echo "Compiling Main.java..."
$JAVAC -cp "bin:lib/*" -d bin src/Main.java 2>&1
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile Main.java"
    exit 1
fi

echo "--------------------------------------------------"
echo ""
echo "✓ Compilation successful!"
echo ""
echo "To run the application, use:"
echo "java -cp \"bin:lib/mysql-connector-java-8*.jar\" Main"
echo ""
echo "======================================================"
