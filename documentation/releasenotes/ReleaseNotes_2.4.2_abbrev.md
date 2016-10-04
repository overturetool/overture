
# [Overture 2.4.2 - Release Notes - 09 October 2016](https://github.com/overturetool/overture/milestones/v2.4.2)



## What's New?

This release contains a prototype version of an auto-completion
feature for VDM. It has been developed by Peter Mathiesen and Magnus
Louvmand Pedersen in connection with an R&D project. The
auto-completion feature is able to list proposals for function and
operation invocations, constructors and built-in types.

There's a few things worth mentioning about the current version of the
auto-completion feature: First, this feature only works when the model
is type correct, so if you introduce parse/type errors and save the
model sources, no proposals will be listed. The parser/type checker is
currently not able to cope with partially correct ASTs, and the
auto-completion feature does not yet cache the most recent type
correct AST and use that to compute proposals. Second, auto-completion
in blank spaces tries to propose "everything" -- even built-in types
such as 'nat' and 'bool', and templates for functions and
operations. Third, proposals on the form `x.op()` are not yet
supported. It's only possible to list proposals for functions and
operations defined in the enclosing class.

Feedback on the auto-completion feature can be
reported [here](https://github.com/overturetool/overture/issues/423)

In addition, a new feature, called the "Overture Graphics Plugin" is
now available via one of the pre-installed update sites. This plugin
allows you to visualise a VDM model's instance variables over time
using, for example, 2D plots. More information about how to use this
plugin can be found in
the
[Overture tool's user manual](http://raw.github.com/overturetool/documentation/master/documentation/UserGuideOvertureIDE/OvertureIDEUserGuide.pdf).

Finally, a new VDM++ model of a traffic management system has been
added to the standard examples. This example is GUI-based and uses the
"Overture Graphics Plugin" to visualise the model's instance
variables. The model has been developed in
the
[TEMPO](http://eng.au.dk/en/research-in-engineering/research-projects/electrical-and-computer-engineering-research-projects/tms-experiment-with-mobility-in-the-physical-world-using-overture/) (TMS
Experiment with Mobility in the Physical World using Overture)
project, where technologies that can reduce traffic jams have been
researched.

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

Please note that the interactive list is at <https://github.com/overturetool/overture/milestones/v2.4.2>
* [#599 closed - Problem with function value conversion](https://github.com/overturetool/overture/issues/599)
* [#589 closed - Overture key bindings are in conflict](https://github.com/overturetool/overture/issues/589)
* [#423 closed - "Improve" auto-completion](https://github.com/overturetool/overture/issues/423)
