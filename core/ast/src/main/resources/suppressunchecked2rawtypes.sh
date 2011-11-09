grep -lr -e '@SuppressWarnings(unchecked)' * | xargs sed -i 's/@SuppressWarnings(unchecked)/@SuppressWarnings(rawtypes)/g'
