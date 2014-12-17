#!/usr/local/bin/python

import sys
from json import loads

if len(sys.argv) < 2:
    print """Usage: %s <github-issues-dump.json>

  Makes a Markdown formatted list of closed bugs for the release notes.

  To get the github-issues-dump.json, do something like:

   * Close the relevant milestone in github, and make sure all the issues in that milestone are closed (transfer all open ones out to the next version milestone)

   * Get the github-internal milestone number
     $ curl "https://api.github.com/repos/overturetool/overture/milestones?state=closed" | jq -r '.[] | "\(.number) --- \(.title)"'
     or
     $ curl "https://api.github.com/repos/overturetool/overture/milestones?state=closed" | jq '.[] | select(.title == "$MILESTONE_TITLE") | .number'

   * Get the issues for the milestone
     $ curl "https://api.github.com/repos/overturetool/overture/issues?state=closed&milestone=$MILESTONE_NUMBER" > issues.json

   * And output the markdown-formatted list directly
     $ jq -r '.[] | "* [#\(.number) \(.title)](\(.html_url))"' issues.json


"""
    exit(1)

with open(sys.argv[1]) as f:
    issues_json = f.read()

issues = loads(issues_json)

issues.sort(None, lambda issue:issue['number'], True)
for issue in issues:
    print "* [#%(number)d %(title)s](%(url)s)" % issue



