#!/bin/sh
export LATEX_MAIN_FILE="testauto"

makeindex < $LATEX_MAIN_FILE.idx > $LATEX_MAIN_FILE.ind

pdflatex $LATEX_MAIN_FILE
pdflatex $LATEX_MAIN_FILE
