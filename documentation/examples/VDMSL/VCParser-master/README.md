VCParser
========

A little combinatory parser in VDM-SL

Basics
-------
Here I introduce how to use VCParser by writing a binary number parser.

* To create a parser that accepts only '0' (a character),
~~~~~
zero = takeChar('0')
~~~~~
will take a string (seq of char, in VDM-SL) and returns a PARSED value, for example,
~~~~~
zero("011")
~~~~~
will return
~~~~~
mk_PARSED(mk_TREE(nil, "0"), "11")
~~~~~
which says it parsed the input into a tree of "0" and "110" is remained.
If failed,
~~~~~
zero("101")
~~~~~
will result in
~~~~~
mk_PARSED(mk_ERROR("Expected '0'"), "101")
~~~~~
which says it couldn't parse the input and "1001" is remained.

* To create a parser that accepts only "1" (a string),
~~~~~
one = takeString("1")
~~~~~
For example,
~~~~~
one("101")
~~~~~
will return
~~~~~
mk_PARSED(mk_TREE(nil, "1"), "01")
~~~~~

* To create a parser that accepts either 0 or 1 (aka "/"),
~~~~~
bin = either([zero, one])
~~~~~
For example,
~~~~~
mk_(
  bin("101"),
  bin("011")
)
~~~~~
will return
~~~~~
mk_(
  mk_PARSED(mk_TREE(nil, "1"), "01"),
  mk_PARSED(mk_TREE(nil, "0"), "11")
)
~~~~~

* To create a parser that accepts a repeated series of binary digits (aka "*"),
~~~~~
binStar = star(bin)
~~~~~
For example,
~~~~~
mk_(
  binStar("101"),
  binStar("")
)
~~~~~
will return
~~~~~
mk_(
  mk_PARSED(mk_TREE(nil, [mk_TREE(nil, "1"), mk_TREE(nil, "0"), mk_TREE(nil, "1")]), []),
  mk_PARSED(mk_TREE(nil, []), [])
)
~~~~~
Please note that the second value in the result pair is NOT an error.

* To create a parser that accepts a binary digit and then the binStar (aka ","),
~~~~~
binPlus = series([bin, binStar])
~~~~~
For example,
~~~~~
mk_(
  binPlus("1001"),
  binPlus("")
)
~~~~~
will return
~~~~~
mk_(
  mk_PARSED(mk_TREE(nil, [mk_TREE(nil, "1"), mk_TREE(nil, [mk_TREE(nil, "0"), mk_TREE(nil, "1")])]), []), 
  mk_PARSED(mk_ERROR("Unexpected EOF"), [])
)
~~~~~

* To create a parser that optionally accepts "0b" prefix before a series of binary digits (aka "[]"),
~~~~~
binary = series([option(takeString("0b")), binPlus])
~~~~~
For example,
~~~~~
mk_(
  binary("0b10"),
  binary("10"),
  binary("0x10")
)
~~~~~
will return
~~~~~
mk_(
  mk_PARSED(mk_TREE(nil, [mk_TREE(nil, "0b"), mk_TREE(nil, [mk_TREE(nil, "1"), mk_TREE(nil, [mk_TREE(nil, "0")])])]), []),
  mk_PARSED(mk_TREE(nil, [mk_TREE(nil, []), mk_TREE(nil, [mk_TREE(nil, "1"), mk_TREE(nil, [mk_TREE(nil, "0")])])]), []),
  mk_PARSED(mk_TREE(nil, [mk_TREE(nil, []), mk_TREE(nil, [mk_TREE(nil, "0"), mk_TREE(nil, [])])]), "x10")
)
~~~~~

* To create a parser that accepts a binary number trimming blanks around it,
~~~~~
binaryTerm = trimBlanks(binary)
~~~~~
For example,
~~~~~
binaryTerm(" 0b10 1")
~~~~~
will return
~~~~~
mk_PARSED(mk_TREE(nil, [mk_TREE(nil, "0b"), mk_TREE(nil, [mk_TREE(nil, "1"), mk_TREE(nil, [mk_TREE(nil, "0")])])]), "1")
~~~~~

* To obtain a flatten tree from compound parser
~~~~~
flatBinStar = concat(binStar)
~~~~~
For example,
~~~~~
mk_(
  binStar("101"),
  flatBinStar("101")
)
~~~~~
will return
~~~~~
mk_(
  mk_PARSED(mk_TREE(nil, [mk_TREE(nil, "1"), mk_TREE(nil, "0"), mk_TREE(nil, "1")]), []),
  mk_PARSED(mk_TREE(nil, "101"), [])
)
~~~~~

* To change an error message,
~~~~~
bin2 = iferror("Accepts only 0 or 1", bin)
~~~~~
For example,
~~~~~
mk_(
	bin2("101"),
  bin2("210")
)
~~~~~
will return
~~~~~
mk_(
  mk_PARSED(mk_TREE(nil, "1"), "01"), 
  mk_PARSED(mk_ERROR("Accepts only 0 or 1"), "210")
)
~~~~~

* To modify a parsed tree,
~~~~~
translate = transtree(lambda tree:TREE & cases tree:
  mk_TREE(-, "0") -> mk_TREE(nil, "zero"),
  mk_TREE(-, "1") -> mk_TREE(nil, "one") end, bin)
~~~~~
For example,
~~~~~
mk_(
	translate("0"),
  translate("1")
)
~~~~~
will return
~~~~~
mk_(
  mk_PARSED(mk_TREE(nil, "zero"), []),
  mk_PARSED(mk_TREE(nil, "one"), [])
)
~~~~~

There are a bunch of other useful combinators and minimal parsers, such as any, digit, alphabet and so on.
Please read the VCParser.vdmsl for details.
