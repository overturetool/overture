# Overture 2.1.2 Release Notes — 9 October 2014 

## What's New?

This release is contains many bugfixes.  Notable among them are a number of improvements to the typechecker, and a significant refactoring of the way the combinatorial testing plugin expands traces.  The latter fix means that combinatorial testing supports bigger tests sets in less memory than before.


## Reporting Problems and Troubleshooting

Please report bugs, problems, and other issues with the tool at <https://github.com/overturetool/overture/issues>.

If you encounter a problem with the Overture IDE itself, please contact the Overture project and we will try to help.  You can contact us at info@overturetool.org, or use [StackOverflow](http://stackoverflow.com/questions/tagged/vdm%2b%2b) — we monitor for questions using the `vdm`, `vdm++`, or `vdmrt` tags.

If you encounter a problem with a VDM specification, please try to make a small example that illustrates the problem before you contact us.  If you are sure the bug is not already known in the GitHub issues list, you can create a new bug report.


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

Please note that the interactive list is at <https://github.com/overturetool/overture/issues?milestone=17&state=closed>

* [#277 Socket to IDE not valid in Console execution](https://github.com/overturetool/overture/issues/277)
* [#279 CT Overview contents are unsorted?](https://github.com/overturetool/overture/issues/279)
* [#290 VDM keywords are not highlighted in the Proof Obligation View ](https://github.com/overturetool/overture/issues/290)
* [#318 Combinatorial problems in 2.1.0 memory issues resulting in connection reset](https://github.com/overturetool/overture/issues/318)
* [#319 Flat SL models with multiple files don't stop at breakpoints correctly](https://github.com/overturetool/overture/issues/319)
* [#323 Breakpoint is being ignored in forall statement](https://github.com/overturetool/overture/issues/323)
* [#324 Getting the values from a record type does not work](https://github.com/overturetool/overture/issues/324)
* [#326 Abstract method can be declared private](https://github.com/overturetool/overture/issues/326)
* [#328 Type Comparator Used Statically](https://github.com/overturetool/overture/issues/328)
* [#331 "Send to Interpreter" does not work for SL traces](https://github.com/overturetool/overture/issues/331)
* [#332 Type checker missing some inequalities](https://github.com/overturetool/overture/issues/332)
* [#333 Create module to test tool functionality against all VDM examples](https://github.com/overturetool/overture/issues/333)
* [#334 NullPointerException reported in Problems view ](https://github.com/overturetool/overture/issues/334)
* [#335 Quick Interpreter fixes](https://github.com/overturetool/overture/issues/335)
* [#336 Fix Overture Examples](https://github.com/overturetool/overture/issues/336)
* [#337 New type checking too tough on higher order polymorphic functions](https://github.com/overturetool/overture/issues/337)
* [#338 undefined should be of any possible type in type checking](https://github.com/overturetool/overture/issues/338)
* [#339 Examples Crashing Combinatorial Testing](https://github.com/overturetool/overture/issues/339)
* [#340 Map patterns not working with munion](https://github.com/overturetool/overture/issues/340)
* [#341 Optional types masking type unions](https://github.com/overturetool/overture/issues/341)
* [#342 Account for is_ expressions in and clauses](https://github.com/overturetool/overture/issues/342)
* [#343 Unify VDM Library Sources](https://github.com/overturetool/overture/issues/343)
* [#345 CT Overview sorts test ranges lexically](https://github.com/overturetool/overture/issues/345)
* [#346 Type checking abstract classes](https://github.com/overturetool/overture/issues/346)
* [#347 Remove periodic/sporadic from VDM++ dialect, RM #26](https://github.com/overturetool/overture/issues/347)
* [#349 Fix Failing Interpreter Tests](https://github.com/overturetool/overture/issues/349)
* [#352 Parser: No warning when declaring end Module name in flat specs](https://github.com/overturetool/overture/issues/352)
* [#353 Type check: When checking frame conditions in COMPASS a wrong warning is reported due to location comparison](https://github.com/overturetool/overture/issues/353)
* [#356 POG error: functions with curried arguments](https://github.com/overturetool/overture/issues/356)
* [#367 Add Matching Brackets to the Editor](https://github.com/overturetool/overture/issues/367)
