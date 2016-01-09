# Overture 2.3.0 Release Notes — 7 October 2015

## What's New?

This release adds support for the new VDM pure operations feature.

### VDM Pure Operations

Excerpt from the [VDM language manual](https://github.com/overturetool/documentation/raw/editing/documentation/VDM10LangMan/VDM10_lang_man.pdf), page 94, defines pure operations as follows:

> If an operation is declared *pure* it means that it is executed atomically when
> it is called from a functional context (from functions, invariants, pre or
> post-conditions). Otherwise calling a pure operation is no different to calling
> a normal operation, except that a pure operation has various constraints...


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

Please note that the interactive list is at <https://github.com/overturetool/overture/issues?q=milestone%3Av2.3.0>

* [#459 Type invariant violation incorrectly reported for the state type](https://github.com/overturetool/overture/issues/459)
* [#455 POG crash for atomic statements in VDM-SL](https://github.com/overturetool/overture/issues/455)
* [#453 Wrong template construction for implicit operations and functions](https://github.com/overturetool/overture/issues/453)
* [#452 Can't launch GUI examples in OpenJDK](https://github.com/overturetool/overture/issues/452)
* [#451 Make new code generation functionality accessible via the Overture IDE](https://github.com/overturetool/overture/issues/451)
* [#437 Command line coverage only works with absolute filenames](https://github.com/overturetool/overture/issues/437)
* [#419 fixme: only used in 1 class. Move it over.](https://github.com/overturetool/overture/issues/419)
