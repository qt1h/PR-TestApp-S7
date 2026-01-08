MUTATION_AGGREGATE_FILE=mantest-aggregate/target/pit-reports/mutations.csv

rm $MUTATION_AGGREGATE_FILE
echo "Removing: $MUTATION_AGGREGATE_FILE"

MUTATION_APP_FILE=$(find mantest-app -name mutations.csv)
echo "Processing: $MUTATION_APP_FILE"

MUTATION_DAOMEM_FILE=$(find mantest-dao-mem -name mutations.csv)
echo "Processing: $MUTATION_DAOMEM_FILE"

MUTATION_MODEL_FILE=$(find mantest-model -name mutations.csv)
echo "Processing: $MUTATION_MODEL_FILE"

echo "Consolidating files for mutation coverage"

echo "Creating: $MUTATION_AGGREGATE_FILE"

cat "$MUTATION_APP_FILE" "$MUTATION_DAOMEM_FILE" "$MUTATION_MODEL_FILE" > $MUTATION_AGGREGATE_FILE