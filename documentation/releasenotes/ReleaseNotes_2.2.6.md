# Overture 2.2.6 Release Notes — 19 June 2015

## What's New?

This release contains the following:

* Various bugfixes
* Better performance of the CT GUI on MacOS
* Java Code-Generation support for VDM-SL models
* Support for JML when code-generating VDM-SL models


## System Requirements

Overture is based on the Eclipse platform.  To run it, you must have a Java runtime system installed, version 7 or later.


## Download & Installation

Documentation, including tutorials and a language manual, are bundled in the download package, and are also available from the [Overture website](http://www.overturetool.org/).

Overture can be downloaded from GitHub at <https://github.com/overturetool/overture/releases>.

The download file is a ZIP archive.  To install the tool, unzip the file in a convenient location.  The main executable is in the top level directory, called `Overture`.

We will be making an exe-based installer available for Windows user at a future point.


### Upgrading:

* ATTENTION: If you are using the ZIP archive to update and if your workspace was located inside the previous installation directory, DO NOT FORGET TO BACKUP the workspace or it will be lost when the old version is deleted.
* If you are upgrading with the ZIP archive: do not unzip the latest version on top of the oldest one.  You will need to delete the previous version before continuing the installation.


### Uninstalling

To uninstall Overture, remove the contents of the directory where you installed it.  There are no other files stored on the system, apart from any workspace files you may have created.

For Windows users, if the Overture installer was used previously, it is possible to uninstall via the uninstall shortcut created by the installer in the start programs or via the Uninstall menu in the Control Panel.


## Reporting Problems and Troubleshooting

Please report bugs, problems, and other issues with the tool at <https://github.com/overturetool/overture/issues>.

If you encounter a problem with the Overture IDE itself, please contact the Overture project and we will try to help.  You can contact us at info@overturetool.org, or use [StackOverflow](http://stackoverflow.com/questions/tagged/vdm%2b%2b) — we monitor for questions using the `vdm`, `vdm++`, or `vdmrt` tags.

If you encounter a problem with a VDM specification, please try to make a small example that illustrates the problem before you contact us.  If you are sure the bug is not already known in the GitHub issues list, you can create a new bug report.


## Frequently Asked Questions

* Who's behind Overture?
> Overture was written by the members of the Overture project, a group of industry and academic researchers interested in the use of VDM.

* How is Overture licensed?
> Overture is open source. It is released under a GPLv3 license.

* What can I do to help?
> If you are interested in helping you can drop a mail to info@overturetool.org.  You are also very welcome to fork our code on GitHub and send us pull requests.

* Where is Overture source code?
> Overture source code is hosted by GitHub, within the [overturetool](https://github.com/overturetool) organisation account.


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
