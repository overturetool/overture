
# [Overture 2.4.6 - Release Notes - 06 March 2017](https://github.com/overturetool/overture/milestone/32)



## What's New?

This release contains a number of bug fixes covering the parser, type checker, quick interpreter and combinatorial testing. See the `Bugfixes` section for additional details.

In addition, a change has been made to Overture (see issue 613) that may give a noticeable interpreter performance improvement in specifications that internally throw several exceptions. This occurs naturally in specs with complex union types or which perform a lot of complex pattern matching. In specifications with traces a 20% performance improvement has been seen.

## Reporting Problems and Troubleshooting

Please report bugs, problems, and other issues with the tool at <https://github.com/overturetool/overture/issues>.

If you encounter a problem with the Overture IDE itself, please contact the Overture project and we will try to help.  You can contact us at info@overturetool.org, or use [StackOverflow](http://stackoverflow.com/questions/tagged/vdm%2b%2b) — we monitor for questions using the `vdm`, `vdm++`, or `vdmrt` tags.

If you encounter a problem with a VDM specification, please try to make a small example that illustrates the problem before you contact us.  If you are sure the bug is not already known in the GitHub issues list, you can create a new bug report.


## Other Resources and Links

* [Overture Community site](http://www.overturetool.org)
* [VDM-10 Language Manual](http://raw.github.com/overturetool/documentation/master/documentation/VDM10LangMan/VDM10_lang_man.pdf)
* [VDM Tutorials](http://overturetool.org/documentation/tutorials.html)
* [VDM Examples](http://overturetool.org/download/examples/)
* [Wikipedia on VDM](http://en.wikipedia.org/wiki/Vienna_Development_Method)
* [Overture Developers Wiki on GitHub](https://github.com/overturetool/overture/wiki/)
* [The Old Overture Wiki for developers](http://wiki.overturetool.org)


## Bugfixes

Please note that the interactive list is at <https://github.com/overturetool/overture/milestone/32>
* [#622 closed - Single trace test is executed using default JVM options](https://github.com/overturetool/overture/issues/622)
* [#620 closed - AccountSys VDM Class](https://github.com/overturetool/overture/issues/620)
* [#617 closed - Quick Interpreter should check for rubbish after expressions](https://github.com/overturetool/overture/issues/617)
* [#616 closed - discrepancy between VDMJ and Overture outputs](https://github.com/overturetool/overture/issues/616)
* [#613 closed - Core modules use different versions of AstCreator](https://github.com/overturetool/overture/issues/613)
* [#610 closed - Parser allows multiple consecutive access modifiers for functional descriptions](https://github.com/overturetool/overture/issues/610)
* [#609 closed - Remove abbreviated release notes](https://github.com/overturetool/overture/issues/609)
* [#603 closed - The welcome page does not work after self-update](https://github.com/overturetool/overture/issues/603)
* [#594 closed - Type checking of dlmodules should be relaxed](https://github.com/overturetool/overture/issues/594)
