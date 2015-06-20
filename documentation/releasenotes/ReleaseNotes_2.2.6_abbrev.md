# Overture 2.2.6 Release Notes — 19 June 2015

## What's New?

This release contains the following:

* Various bugfixes
* Better performance of the CT GUI on MacOS
* Java Code-Generation support for VDM-SL models
* Support for JML when code-generating VDM-SL models


## Reporting Problems and Troubleshooting

Please report bugs, problems, and other issues with the tool at <https://github.com/overturetool/overture/issues>.

If you encounter a problem with the Overture IDE itself, please contact the Overture project and we will try to help.  You can contact us at info@overturetool.org, or use [StackOverflow](http://stackoverflow.com/questions/tagged/vdm%2b%2b) — we monitor for questions using the `vdm`, `vdm++`, or `vdmrt` tags.

If you encounter a problem with a VDM specification, please try to make a small example that illustrates the problem before you contact us.  If you are sure the bug is not already known in the GitHub issues list, you can create a new bug report.


## Other Resources and Links

* [Overture Community site](http://www.overturetool.org)
* [VDM Tutorials](http://overturetool.org/documentation/tutorials.html)
* [VDM Examples](http://overturetool.org/download/examples/)
* [Wikipedia on VDM](http://en.wikipedia.org/wiki/Vienna_Development_Method)
* [Overture Developers Wiki on GitHub](https://github.com/overturetool/overture/wiki/)
* [The Old Overture Wiki for developers](http://wiki.overturetool.org)


## Bug fixes

Please note that the interactive list is at <https://github.com/overturetool/overture/issues?q=milestone%3Av2.2.6>

* [#450 Update of Eclipse version: The kepler Eclipse version is not longer properly mirrored](https://github.com/overturetool/overture/issues/450)
* [#449 static constructor that is new’ed](https://github.com/overturetool/overture/issues/449)
* [#448 Assignment to local constants not rejected by the type checker](https://github.com/overturetool/overture/issues/448)
* [#447 Launcher remembers entry point, even when using Console](https://github.com/overturetool/overture/issues/447)
* [#446 The interpreter fails to report a type invariant violation](https://github.com/overturetool/overture/issues/446)
* [#445 POG crash on postcondition quoting in classes with invariants](https://github.com/overturetool/overture/issues/445)
* [#443 RESULT is not really reserved](https://github.com/overturetool/overture/issues/443)
* [#442 "Could not create the view..." ](https://github.com/overturetool/overture/issues/442)
* [#441 Disable Trivial PO Status](https://github.com/overturetool/overture/issues/441)
* [#440 Record with unnamed fields fails to code generate to Java](https://github.com/overturetool/overture/issues/440)
* [#439 Potential issue with addition of reals](https://github.com/overturetool/overture/issues/439)
* [#436 Re-remove vdmjc](https://github.com/overturetool/overture/issues/436)
* [#430 Overture closes with error when expanding and collapsing traces on a Mac OSX](https://github.com/overturetool/overture/issues/430)
* [#426 New function for current system time](https://github.com/overturetool/overture/issues/426)
* [#422 Standardize UI tests](https://github.com/overturetool/overture/issues/422)
* [#397 Poor message for stack overflow errors](https://github.com/overturetool/overture/issues/397)
