# New Overture Tests
- **Primary Contacts:**
  ldc
- **Status:**
  Early development. Not usable.

## Description:
This module is a proof of concept implementation of a new, much simpler test
framework for Overture. The old one is pre Junit 4 relies on on-the-fly
generation of test cases. It also uses XML for results for which we must
manually provide serialization functionality.

This new module will rely on Junit parameterized tests and use GSON
for test results. It aims to be much smaller (volume wise) and easier
to use than the old one.

The goal is to enable user to provide only a comparison function between
2 objects of result type. We hope to make result storage as automated as possible
but for very complex class structures, some help may be needed.


## Known Issues:
None

## Contributors:
ldc

