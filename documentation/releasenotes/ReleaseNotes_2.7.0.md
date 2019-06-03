
# [Overture 2.7.0 - Release Notes - 03 June 2019](https://github.com/overturetool/overture/milestone/41)

## What's New?

This release of Overture includes

- Support for VDM annotations (currently limited to the Overture
  command-line interface)
- Improved type checking of struct import/export (VDM-SL only)
- Improved Java code-generation
    - Limited support for polymorphic types
    - Support for renamed constructs (VDM-SL only)
    - Several bug fixes.

Model annotations are covered in chapter 17 in the [Overture User Manual](https://github.com/overturetool/documentation/blob/master/documentation/UserGuideOvertureIDE/OvertureIDEUserGuide.pdf).

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

Please note that the interactive list is at <https://github.com/overturetool/overture/milestone/41>
* [#698 closed - Attempt to call non-existing CPU constructor crashes the typechecker](https://github.com/overturetool/overture/issues/698)
* [#697 closed - Update Java code-generation runtime to support VDMUtil`seq_of_char2val](https://github.com/overturetool/overture/issues/697)
* [#694 closed - JODTool update site is not working](https://github.com/overturetool/overture/issues/694)
* [#693 closed - Warning for unused state variable](https://github.com/overturetool/overture/issues/693)
* [#691 closed - Support Java code-generation of polymorphic types (simple cases)](https://github.com/overturetool/overture/issues/691)
* [#690 closed - Support Java code-generation of 'renamed' constructs](https://github.com/overturetool/overture/issues/690)
