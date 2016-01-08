#!/bin/bash

find . -type f -name *.vm -exec ./removeLastNewline.sh {} \;
