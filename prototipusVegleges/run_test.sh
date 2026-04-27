#!/bin/bash

echo -e "=== KOD FORDITASA (JAVAC) ==="
# A mappak tartalmanak kozvetlen forditasa UTF-8 kodolassal
javac -encoding UTF-8 skeletonprogram/*.java jarmuvek/*.java jatekosok/*.java takaritofejek/*.java terkep/*.java
echo -e "Forditas kesz!\n"

echo -e "=== AUTOMATIKUS TESZTFUTTATO ==="
mkdir -p tesztek/out

# Vegigmegyunk az osszes bemeneti fajlon
for in_file in tesztek/*_in.txt; do
    # Fajlnev es alap nev kinyerese (pl. 01_fejcsere_sikeres)
    filename=$(basename -- "$in_file")
    base_name="${filename%_in.txt}"
    
    exp_file="tesztek/${base_name}_exp.txt"
    out_file="tesztek/out/${base_name}_out.txt"
    
    # Java program futtatasa es kimenet atiranyitasa
    java skeletonprogram.SzkeletonProgram --input="$in_file" > "$out_file"
    
    # Osszehasonlitas a diff parnccsal, whitespace-ek ignoralasaval (-w)
    if diff -q -w "$exp_file" "$out_file" > /dev/null; then
        echo -e "[\033[32mPASS\033[0m] $base_name"
    else
        echo -e "[\033[31mFAIL\033[0m] $base_name - Elteres talalhato!"
        # Ha hiba van, reszletesen is kiirjuk az elterest
        diff -w "$exp_file" "$out_file"
    fi
done
echo -e "================================"