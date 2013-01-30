PATH=$PATH:/cygdrive/d/Program\ Files/MiKTeX\ 2.9/miktex/bin

echo First pass...
pdflatex -quiet VDM10_lang_man

echo Bibtex pass...
bibtex -quiet VDM10_lang_man

echo Second pass...
pdflatex -quiet VDM10_lang_man

echo Final pass...
pdflatex -quiet VDM10_lang_man

echo Done.
