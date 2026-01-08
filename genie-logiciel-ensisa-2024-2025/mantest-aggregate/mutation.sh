MUTATION_FILE=$(find mantest-aggregate -name mutations.csv)

if [[ -f "$MUTATION_FILE" ]]; then
  echo "Processing: $MUTATION_FILE"

  total_mutations=$(wc -l < "$MUTATION_FILE")
  killed_mutations=$(grep -c ",KILLED," "$MUTATION_FILE")

  if [[ $total_mutations -gt 0 ]]; then
    percentage_killed=$(awk -v killed="$killed_mutations" -v total="$total_mutations" 'BEGIN { printf "%d%%\n", int((killed / total) * 100) }')
    echo "Pitest Coverage: $percentage_killed"
  else
    echo "No mutation data available"
  fi
else
  echo "No Pitest mutations file found"
fi