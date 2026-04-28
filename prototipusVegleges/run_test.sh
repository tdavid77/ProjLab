#!/bin/bash

cd "$(dirname "$0")"

echo -e "=== KOD TISZTITASA ==="
rm -rf bin
mkdir -p bin

echo -e "=== KOD FORDITASA (JAVAC) ==="
find src -name "*.java" > sources_tmp.txt
javac -d bin -encoding UTF-8 @sources_tmp.txt
rm sources_tmp.txt
echo -e "Forditas kesz!\n"

echo -e "=== AUTOMATIKUS TESZTFUTTATO ==="
mkdir -p tesztek/out

for in_file in tesztek/*_in.txt; do
    filename=$(basename -- "$in_file")
    base_name="${filename%_in.txt}"
    
    exp_file="tesztek/${base_name}_exp.txt"
    out_file="tesztek/out/${base_name}_out.txt"
    
    # === FONTOS ===
    # Ha a SzkeletonProgram.java-ban van package deklaracio, 
    # akkor ide is kell: java -cp bin csomagnev.SzkeletonProgram
    java -cp bin motor.SzkeletonProgram --input="!in_file!" > "!out_file!"
    
    if diff -q -w "$exp_file" "$out_file" > /dev/null; then
        echo -e "[\033[32mPASS\033[0m] $base_name"
    else
        echo -e "[\033[31mFAIL\033[0m] $base_name - Elteres talalhato!"
        diff -w "$exp_file" "$out_file"
    fi
done
echo -e "================================"