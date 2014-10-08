# VDM Pretty Printer
- **Primary Contacts:**
  Luis Diogo Couto
- **Status:**
  (Sporadic) Development

## Description:
This module provides pretty printing of VDM models by transforming ASTs into
Strings.  It consists of two implementations:

The original pretty printer (prettyprinter package) was quickly implemented with AST toString methods and 
provides support for translation of UML models to VDM.

The new pretty printer (npp package) is a visitor-based implementation. Its purpose is to
provide support for extensibility so that VDM language extensions can be
pretty-printed correctly.


## Known Issues:
* The two different implementations of pretty-printing co-exist safely but are
not compatible and do not interact. The original one should be deleted but a
lot of its functionality is not yet present in the new one.
* The original pretty printer's reliance on  toString methods makes it
not-particularly-extensible.
* The new pretty printer only supports expressions fully. 

## Contributors:

