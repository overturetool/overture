
# [Overture 2.4.0 - Release Notes - 30 August 2016](https://github.com/overturetool/overture/milestones/v2.4.0)



## What's New?

Overture has been updated to include two new language features. The first language feature, which is based on Request for Modification (RM) 35, adds a set1 type constructor to the language. Second, VDM-10 now supports sequence bindings (RM 36), which allows iterating over sequences in a more concise way. For example, one can now write [ f(x) | x in seq s & P(x) ] rather than [ f(s(i)) | i in set inds s & P(s(i)) ].

In the previous release, Overture was upgraded to support self-update using Eclipse p2. It should therefore be possible to update Overture 2.3.8 to version 2.4.0 (this version) using p2. Make sure that the Eclipse release site is available: http://download.eclipse.org/releases/neon

Updating Overture to a newer version is similar to updating any other Eclipse application. From the menu choose

Help -> Install New Software...

Next, from the list of repositories select 'Overture Development', make sure you have 'Overture' selected, and execute the update.

Finally, Overture has been updated to build against the newest version of Eclipse (Neon).

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


## Bugfixes

Please note that the interactive list is at <https://github.com/overturetool/overture/milestones/v2.4.0>
* [#588 closed - Overture won't open after update](https://github.com/overturetool/overture/issues/588)
* [#587 closed - VDM-RT log viewer check box is not reflecting the actual settings](https://github.com/overturetool/overture/issues/587)
* [#585 closed - VDM-RT Diagram Export crops figures](https://github.com/overturetool/overture/issues/585)
* [#584 closed - Type checker crashes when trying to call invalid CPU constructor](https://github.com/overturetool/overture/issues/584)
* [#583 closed - Upgrade Overture to build against Eclipse Neon](https://github.com/overturetool/overture/issues/583)
* [#582 closed - VDMUtil`seq_of_char2val should handle nonsense conversions gracefully](https://github.com/overturetool/overture/issues/582)
* [#579 closed - print some VDM value are strange](https://github.com/overturetool/overture/issues/579)
* [#578 closed - Output informations of "CT Test Case result" View is not enough](https://github.com/overturetool/overture/issues/578)
* [#577 closed - Repository split - disabled artifacts](https://github.com/overturetool/overture/issues/577)
* [#574 closed - Hexadecimal Values](https://github.com/overturetool/overture/issues/574)
* [#570 closed - Unexpected failure of run configuration](https://github.com/overturetool/overture/issues/570)
* [#521 closed - Inconsistent import behaviour](https://github.com/overturetool/overture/issues/521)
