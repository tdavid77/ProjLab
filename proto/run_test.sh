#!/bin/sh
# Prototipus teszt futtatas Unix-szeru rendszereken.
# Hasznalat: ./run_test.sh <testszam> [bemenet.txt] [kimenet.txt]

if [ "$1" = "" ]; then
  echo "Hasznalat: ./run_test.sh <testszam> [bemenet.txt] [kimenet.txt]"
  echo "Pelda: ./run_test.sh 4 input.txt output.txt"
  exit 1
fi

TEST="$1"
INPUT="$2"
OUTPUT="$3"

CMD="java skeletonprogram.SzkeletonProgram --test=$TEST"
if [ -n "$INPUT" ]; then
  CMD="$CMD --input=$INPUT"
fi
if [ -n "$OUTPUT" ]; then
  CMD="$CMD --output=$OUTPUT"
fi

echo "Futtatas: $CMD"
sh -c "$CMD"
