
# [Overture 2.6.2 - Release Notes - 18 May 2018](https://github.com/overturetool/overture/milestone/39)

## What's New?

This release contains fixes for the type-checker, interpreter and Java/JML code-generator. In addition, the Overture command-line interface (CLI) now supports the "-version" argument (which is helpful if the CLI is invoked from a build-job). A more elaborate list of issues addressed since the last release can be found below.

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

Please note that the interactive list is at <https://github.com/overturetool/overture/milestone/38>
* [#665 closed - The Class/function selection in launch configurations is not sorted making it difficult to find a class](https://github.com/overturetool/overture/issues/665)
* [#681 closed - Type of record pattern not resolved](https://github.com/overturetool/overture/issues/681)
* [#676 closed - The Java bridge can't always resolve imported VDM-SL types](https://github.com/overturetool/overture/issues/676)
* [#672 closed - Support Java code-generation of setUp and tearDown](https://github.com/overturetool/overture/issues/672)
* [#679 closed - Support Java code-generation of VDMUtil\`set2seq](https://github.com/overturetool/overture/issues/679)
* [#678 closed - Function values with polymorphic type parameters are not correctly instantiated](https://github.com/overturetool/overture/issues/678)
* [#673 closed - Add "-version" argument to the Overture command-line interface](https://github.com/overturetool/overture/issues/673)
* [#669 closed - let expression interprets tail of a seq1 as a seq1](https://github.com/overturetool/overture/issues/669)
* [#666 closed - Is not yet specified does not work with overloads](https://github.com/overturetool/overture/issues/666)
