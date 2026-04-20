#!/bin/bash

# Run script for Hospital Management System
# Usage: ./run.sh

echo "======================================================"
echo "RBH Hospital Management System"
echo "======================================================"
echo ""

# Check if bin directory exists
if [ ! -d "bin" ]; then
    echo "ERROR: bin directory not found."
    echo "Please compile the project first using: ./compile.sh"
    exit 1
fi

# Check for MySQL JDBC driver
if ! ls lib/mysql-connector-*.jar > /dev/null 2>&1; then
    echo "ERROR: MySQL JDBC driver not found in lib/ directory"
    echo "Please download MySQL Connector and place it in lib/"
    exit 1
fi

echo "Starting Hospital Management System..."
echo ""

# Run the application
java -cp "bin:lib/*" Main
