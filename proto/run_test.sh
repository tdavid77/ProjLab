#!/bin/sh
# Prototipus futtatas Unix-szeru rendszereken.
# Hasznalat:
#   ./run_test.sh [script.txt]

SCRIPT="$1"
if [ -z "$SCRIPT" ]; then
  SCRIPT="input.txt"
fi

if [ ! -f "$SCRIPT" ]; then
  echo "Hiba: script fajl nem talalhato: $SCRIPT"
  exit 1
fi

CMD="java skeletonprogram.SzkeletonProgram --input=$SCRIPT"
echo "Futtatas: $CMD"
exec $CMD
