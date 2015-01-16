# Overture 2.1.4 Release Notes — 20 November 2014 

## What's New?

This release contains small bugfixes. It also includes experimental support for code generation of concurrent models (must be turned on in preferences) and live proof obligation generation.


## System Requirements

Overture is based on the Eclipse platform.  To run it, you must have a Java runtime system installed, version 6 or later.


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

Please note that the interactive list is at <https://github.com/overturetool/overture/issues?milestone=18&state=closed>

* [#396 Code generation of the concurrency mechanisms of VDM++](https://github.com/overturetool/overture/issues/396)
* [#394 Problem converting composed function values](https://github.com/overturetool/overture/issues/394)
* [#393 Type check: of the || operator in traces is missing](https://github.com/overturetool/overture/issues/393)
* [#392 Second half of implication needlessly evaluated in class invariant](https://github.com/overturetool/overture/issues/392)
* [#391 IO`freadval uses the default character encoding](https://github.com/overturetool/overture/issues/391)
* [#389 Problem with || operator in combinatorial tests](https://github.com/overturetool/overture/issues/389)
* [#388 Problem with object reference compares in postconditions](https://github.com/overturetool/overture/issues/388)
* [#385 Code generator hangs with no error](https://github.com/overturetool/overture/issues/385)
* [#382 Choosing what classes should be code generated to Java](https://github.com/overturetool/overture/issues/382)
* [#381 Can't generate Java code.](https://github.com/overturetool/overture/issues/381)
* [#380 Type constraint error with unary minus](https://github.com/overturetool/overture/issues/380)
* [#379 Interpreter crashes on evaluation of the 'is'-expression when the checked type is recursively defined](https://github.com/overturetool/overture/issues/379)
* [#378 Java 7 dependency in the ctruntime tests](https://github.com/overturetool/overture/issues/378)
* [#377 Removing the vdmj_compatibility_tests projects from the code base](https://github.com/overturetool/overture/issues/377)
* [#375 Inherited definition not visible in the sync section](https://github.com/overturetool/overture/issues/375)
* [#373 VDMTools fails to open](https://github.com/overturetool/overture/issues/373)
* [#372 Bug of comp operator?](https://github.com/overturetool/overture/issues/372)
* [#371 Error Object Pattern?](https://github.com/overturetool/overture/issues/371)
* [#369 Outline Icon for functions](https://github.com/overturetool/overture/issues/369)
* [#365 Possibly a type checking issue with bracket types](https://github.com/overturetool/overture/issues/365)
* [#361 Changing the default language version of a VDM project to VDM10](https://github.com/overturetool/overture/issues/361)
* [#330 Tag Assistants with Interface](https://github.com/overturetool/overture/issues/330)
* [#301 The CyberRail Example is broken](https://github.com/overturetool/overture/issues/301)
* [#276 Ordering of files/folders in a VDM Explorer view](https://github.com/overturetool/overture/issues/276)
* [#163 Proof Obliagtion Explorer doesn't refresh with spec changes](https://github.com/overturetool/overture/issues/163)

