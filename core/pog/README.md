# Proof Obligation Generator (AST-based)
- **Primary Contacts:**
  Nick Battle, Luis Diogo Couto
- **Status:**
  Development

## Description:
The Proof Obligation Generator, as the name implies, is responsible for generating POs in Overture. This module
takes a VDM model AST as input and produces a list of POs as output. The PO expressions are themselves represented
as ASTs which allows them to be translated easily into other formalisms (such as Isabelle) so they can be discharged.

## Known Issues:
This module has very poor testability. Related to this, some of the POs generated have never been properly verified.


## Contributors:
Luis and Nick
