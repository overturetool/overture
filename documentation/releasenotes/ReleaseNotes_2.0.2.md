# Overture 2.0.2 Release Notes — 31 January 2014 

## What's New in Overture 2.0.2?

This release is primarily bugfix release --- our main goal was to incorporate fixes from VDMJ that had been made since we prepared v2.0.0.  A list of the bugs fixed is at the end of this release note.

New in this release is the preliminary inclusion of a constraint solver feature that uses the [ProB] model checker to broaden the executable subset of VDM that we cover.

[ProB]: http://www.stups.uni-duesseldorf.de/ProB/index.php5/The_ProB_Animator_and_Model_Checker "ProB"


## System Requirements

Overture is based on the Eclipse platform.  To run it, you must have a Java runtime system installed, version 6 or later.


## Download & Installation

Documentation, including tutorials and a language manual, are bundled in the download package, and are also available from the [Overture website](http://www.overturetool.org/).

Overture can be downloaded from the SourceForge site, at https://sourceforge.net/projects/overture/files/Overture_IDE/2.0.2/.

The download file is a ZIP archive.  To install the tool, unzip the file in a convenient location.  The main executable is in the top level directory, called `Overture`.

We will be making an exe-based installer available for Windows user at a future point.

### Upgrading:

* ATTENTION: If you are using the ZIP archive to update and if your workspace was located inside the previous installation directory, DO NOT FORGET TO BACKUP the workspace or it will be lost when the old version is deleted.
* If you are upgrading with the ZIP archive: do not unzip the latest version on top of the oldest one.  You will need to delete the previous version before continuing the installation.


### Uninstalling

To uninstall Overture, remove the contents of the directory where you installed it.  There are no other files stored on the system, apart from any workspace files you may have created.

For Windows users, if the Overture installer was used previously, it is possible to uninstall via the uninstall shortcut created by the installer in the start programs or via the Uninstall menu in the Control Panel.


## Reporting Problems and Troubleshooting

Please report bugs, problems, and other issues with the tool at https://github.com/overturetool/overture/issues.

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
* [VDM Tutorials](http://overturetool.org/?q=Documentation)
* [VDM Examples](http://overturetool.org/?q=node/11)
* [VDM Portal](http://www.vdmportal.org)
* [VDM Books](http://www.vdmbook.com)
* [Wikipedia on VDM](http://en.wikipedia.org/wiki/Vienna_Development_Method)
* [Overture Developers Wiki on GitHub](https://github.com/overturetool/overture/wiki/)
* [The Old Overture Wiki for developers](http://wiki.overturetool.org)


## Bug fixes

Please note that the interactive list is at https://github.com/overturetool/overture/issues?milestone=13&state=closed

[#260 Type check of operations](https://github.com/overturetool/overture/issues/260)
[#259 Suppress warnings from tycho about default env](https://github.com/overturetool/overture/issues/259)
[#257 Missing type error for value definitions calling operations](https://github.com/overturetool/overture/issues/257)
[#256 Polymorphic measure functions are not called](https://github.com/overturetool/overture/issues/256)
[#252 VDM-SL traces fail when referencing state data](https://github.com/overturetool/overture/issues/252)
[#248 Move module template readmes into place](https://github.com/overturetool/overture/issues/248)
[#242 Outline of large structures (modules/classes) collapses and unfolds if source is changed and saved](https://github.com/overturetool/overture/issues/242)
[#241 Combinatorial Testing with Japanese characters in module/class and traces names does not work](https://github.com/overturetool/overture/issues/241)
[#239 Correction to atomic evaluation with class invariants](https://github.com/overturetool/overture/issues/239)
[#238 Correction to satisfiability POs](https://github.com/overturetool/overture/issues/238)
[#237 Removed the "nil" type from the parser](https://github.com/overturetool/overture/issues/237)
[#236 Allow breakpoints on class invariant expressions](https://github.com/overturetool/overture/issues/236)
[#235 Correct type checking of static calls to non static operations](https://github.com/overturetool/overture/issues/235)
[#234 Allow static operation postconditions to use "old" static data](https://github.com/overturetool/overture/issues/234)
[#233 Corrected scheduling policy unfairness that affects stop ordering](https://github.com/overturetool/overture/issues/233)
[#232 Only allow stop to work on the same CPU as the caller](https://github.com/overturetool/overture/issues/232)
[#231 Fix breakpoints with "for" statement without "by" clause](https://github.com/overturetool/overture/issues/231)
[#230 Correction to type checking of nil returns](https://github.com/overturetool/overture/issues/230)
[#229 Type checker correction for "while true" loops](https://github.com/overturetool/overture/issues/229)
[#228 Corrections to type check of static operation guards](https://github.com/overturetool/overture/issues/228)
[#227 Correction for sync guards on static operations](https://github.com/overturetool/overture/issues/227)
[#226 Correction for two successive stop calls](https://github.com/overturetool/overture/issues/226)
[#225 Correction for deadlocked CT test cases](https://github.com/overturetool/overture/issues/225)
[#224 Correction to stopped thread handling](https://github.com/overturetool/overture/issues/224)
[#223 First cut of RM#18, sporadic threads](https://github.com/overturetool/overture/issues/223)
[#222 First cut of RM#20, thread stop statements](https://github.com/overturetool/overture/issues/222)
[#220 Suppress stderr output in CT tests](https://github.com/overturetool/overture/issues/220)
[#219 Correction to traces running in VDM-RT](https://github.com/overturetool/overture/issues/219)
[#218 Clean up threads properly with exit statements](https://github.com/overturetool/overture/issues/218)
[#216 Some class invariants cause thread scheduling problems](https://github.com/overturetool/overture/issues/216)
[#215 Incorrect satisfiability POs](https://github.com/overturetool/overture/issues/215)
[#214 Remove the `nil` type from the parser](https://github.com/overturetool/overture/issues/214)
[#213 Correct type checking of static calls to non static operations](https://github.com/overturetool/overture/issues/213)
[#212 Allow static operation postconditions to use "old" static data](https://github.com/overturetool/overture/issues/212)
[#211 Corrected scheduling policy unfairness that affects stop ordering](https://github.com/overturetool/overture/issues/211)
[#210 Type checker correction for "while true" loops](https://github.com/overturetool/overture/issues/210)
[#209 Problem with sync guards on static operations](https://github.com/overturetool/overture/issues/209)
[#173 Operations/functions in a flat SL spec are marked as unused.](https://github.com/overturetool/overture/issues/173)
[#106 Breakpoints on initializers don't work](https://github.com/overturetool/overture/issues/106)
[#71 Self update site name is missing](https://github.com/overturetool/overture/issues/71)
[#13 VDMJ function variable scoping error](https://github.com/overturetool/overture/issues/13)
