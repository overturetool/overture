#!/usr/local/bin/python

import sys
from json import loads

if len(sys.argv) < 2:
    print """Usage: %s <github-issues-dump.json>

  Makes a Markdown formatted list of closed bugs for the release notes.

  To get the github-issues-dump.json, Use something like
    curl -u "<userid>:<github-token>" https://api.github.com/repos/overturetool/overture/issues?milestone=14&state=closed > issues.json
  where the milestone number corresponds to the github milestone for the release.
"""
    exit(1)

with open(sys.argv[1]) as f:
    issues_json = f.read()

issues = loads(issues_json)

issues.sort(None, lambda issue:issue['number'], True)
for issue in issues:
    print "* [#%(number)d %(title)s](%(url)s)" % issue



