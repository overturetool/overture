This folder contains different scripts to automate the development.

*.bat is batch files for windows
*.sh  is shell scrips for linux
      The *.sh file is developed on windows which leads to the problem of
      undecired end chars <CR>. To remore them look in dos2unixConvert

_________________________________________________________________
Scrips:

dos2unixConvert: 
  Remove windows *$+.. chars

UpdatePpLatexDocCoverageScript.bat: 
  Update latex documentation with testcoverage and PP of files

UpdateTestSuiteEvalExpand: 
  Update spec test of expantion from files

UpdateTestSuiteFilteringVDMJ: 
  Update spec test of filtering from files

UpdateVDMJmodel: 
  Update cmd to execute VDMJ on model from VDM Tools project file

UpdateVDMToolsCmd: 
  Update cmd to execute VDMTools on cmd line from 
  VDM Tools project file
