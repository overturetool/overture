#!/bin/bash
awk '{q=p;p=$0}NR>1{print q}END{ORS = ""; print p}' $1 > tmp.txt
mv tmp.txt $1
