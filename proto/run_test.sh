#!/bin/bash
echo -e "=== \033[1mAUTOMATIKUS TESZTFUTTATO\033[0m ==="

mkdir -p tesztek/out

for in_file in tesztek/*_in.txt; do
    # Fájlnév kinyerése
    filename=$(basename -- "$in_file")
    base_name="${filename%_in.txt}"
    
    exp_file="tesztek/${base_name}_exp.txt"
    out_file="tesztek/out/${base_name}_out.txt"
    
    # Java program futtatása
    java skeletonprogram.SzkeletonProgram --input="$in_file" > "$out_file"
    
    # Összehasonlítás (diff)
    if diff -q -w "$exp_file" "$out_file" > /dev/null; then
        echo -e "[\033[32mPASS\033[0m] $base_name"
    else
        echo -e "[\033[31mFAIL\033[0m] $base_name - Eltérés található:"
        diff -w "$exp_file" "$out_file"
    fi
done
echo "================================"