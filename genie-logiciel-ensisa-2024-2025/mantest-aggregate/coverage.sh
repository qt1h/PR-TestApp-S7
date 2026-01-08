JACOCO_FILE=$(find mantest-aggregate -name jacoco.csv | head -n 1)

if [[ -f "$JACOCO_FILE" ]]; then
  echo "Processing: $JACOCO_FILE"
  tail -n +2 "$JACOCO_FILE" | awk -F, '{
     missed += $4; covered += $5;
  } END {
    if (covered + missed > 0)
      printf "JaCoCo Coverage: %d%%\n", int(covered / (covered + missed) * 100);
    else
      print "No coverage data available";
  }'
else
  echo "No JaCoCo coverage file found"
fi