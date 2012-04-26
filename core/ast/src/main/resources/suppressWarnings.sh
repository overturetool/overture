grep -lr -e '\/\/ \*\*\*\*\* VDMTOOLS END Name=imports' * | xargs sed -i 's/\/\/ \*\*\*\*\* VDMTOOLS END Name=imports/@SuppressWarnings(all) \n\/\/ \*\*\*\*\* VDMTOOLS END Name=imports/g'
