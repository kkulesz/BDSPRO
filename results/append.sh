#!/bin/bash

# Specify the input directory and output file name
input_directory="./run1/results"
output_file="benchmark_result"

# Navigate to the input directory
cd "$input_directory"

# Check if the output file already exists and delete it if it does
if [ -f "$output_file" ]; then
    rm "$output_file"
fi

# Loop through each file in the input directory and append its content to the output file
for file in *; do
    if [ -f "$file" ]; then
        cat "$file" >> "$output_file"
    fi
done