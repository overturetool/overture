echo off
TITLE LATEX build

SET LATEX_MAIN_FILE="testauto"


echo RAW===================================================
echo RAW=== /b/c2/cMAKEINDEX/c0/c/b =====================================
echo RAW===================================================
makeindex < %LATEX_MAIN_FILE%.idx > %LATEX_MAIN_FILE%.ind

pdflatex %LATEX_MAIN_FILE%
pdflatex %LATEX_MAIN_FILE%