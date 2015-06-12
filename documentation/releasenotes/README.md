# Overture Release Notes

To make the release notes with the process below, you will need `curl` and a utility called `jq` (which may be obtained on its [home page](http://stedolan.github.io/jq/) or, on a mac with HomeBrew, via `brew install jq`).

## Preparation

Once the version that will be released is set and the `development` branch points to the appropriate commit, they release notes can be made.  The process of generating the list of fixed issues involves using issues tracker for the Overture repository, tidying up the list of open issues for the release.  Several things need to be done:

* Create a new milestone for the next release.
* Sort through the list of issues. Fixed bugs should be closed and assigned to the current release milestone. Unfixed bugs are moved to the next milestone.
* Close the milestone for the current release.

When the issues have been sorted, all of the issues in the current release milestone should be closed.

Typically, two files are created for the release notes: one with the a name fitting the format `ReleaseNotes_<version>.md`, and one with the format `ReleaseNotes_<version>_abbrev.md`.  The former is the full release notes, the latter is abbreviated and used on the GitHub Releases page for the release. Copy the notes from a previous release and update them -- Title, What's New? and Bug fixes.


## Generation

Get the list of milestones with their id numbers from the GitHub API:

```
curl -s "https://api.github.com/repos/overturetool/overture/milestones?state=all" | jq -r '.[] | "milestone: \(.title); id: \(.number)"'
```

Note the id of the release to be made; we will use `${MILESTONE_ID}` to represent this id.  Then we get the list of closed issues associated with that milestone.

```
curl -s "https://api.github.com/repos/overturetool/overture/issues?state=closed&milestone=${MILESTONE_ID}" > issues.json
```

Then use `jq` to process the list of issues into a Markdown-formatted bulletted list that links to the actual issue page.

```
jq -r '.[] | "* [#\(.number) \(.title)](\(.html_url))"' issues.json
```

The output from that may be pasted directly into the release notes file.  Remember to delete the `issues.json` file once you have finished.
