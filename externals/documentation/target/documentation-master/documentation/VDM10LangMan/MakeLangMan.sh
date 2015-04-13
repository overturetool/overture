# Execute the commands necessary to rebuild the Language Reference Manual.
# (Works with MikTeX 2.9 on Windows!)

echo First pass...
pdflatex -quiet VDM10_lang_man

echo Bibtex pass...
bibtex -quiet VDM10_lang_man

echo Makeindex pass...
makeindex -quiet VDM10_lang_man

echo Second pass...
pdflatex -quiet VDM10_lang_man

echo Final pass...
pdflatex -quiet VDM10_lang_man

echo Done.
