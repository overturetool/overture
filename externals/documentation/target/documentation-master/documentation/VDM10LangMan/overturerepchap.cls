% overture.cls for latex2e
% For Overture Tech Reports		Last Update: 2/10/08
%
% This LaTeX2e class is designed for producing Overture Technical Reports.
% Usage:
%	\documentclass{overture}
% NOTE: This class will use 12pt so don't bother specifying any pointsizes.
%
% Written by Peter Gorm Larsen (pgl@iha.dk)
% (inspired by Dave Steiner (steiner@dimacs.rutgers.edu).
%
% (C) Copyright 2009 by Peter Gorm Larsen
% Permission to copy all or part of this work is granted, provided
% that the copies are not made or distributed for resale, and that
% the copyright notice and this notice are retained.
% 
% THIS WORK IS PROVIDED ON AN "AS IS" BASIS.  THE AUTHOR PROVIDES NO
% WARRANTY WHATSOEVER, EITHER EXPRESS OR IMPLIED, REGARDING THE WORK,
% INCLUDING WARRANTIES WITH RESPECT TO ITS MERCHANTABILITY OR FITNESS
% FOR ANY PARTICULAR PURPOSE.
%
% If you make any improvements, I'd like to hear about them.

\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{overture}
              [2009/02/09 v1.0
               Overture TechReport class]
\RequirePackage{graphics}

% use 12pt style:
\DeclareOption{10pt}{\OptionNotUsed}
\DeclareOption{11pt}{\OptionNotUsed}
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{article}}
\ProcessOptions
\LoadClass[12pt]{book}

% use more of the page (from fullpage.sty):
\setlength\marginparwidth {0\p@}
\setlength\oddsidemargin  {0\p@}
\setlength\evensidemargin {0\p@}
\setlength\marginparsep   {0\p@}
\setlength\topmargin      {0\p@}
\setlength\textwidth      {6.5in}
\setlength\textheight     {8.5in}

\def\@reportno{YY--NN}		% default report no.
\def\reportno#1{\gdef\@reportno{#1}}

\def\maketitle{
  \begin{titlepage}
    \let\@footnotetext\@tpfootnotetext
    \parindent=0pt \parskip.7\baselineskip
%    \def\@makefnmark{\hbox to 0pt{$^{\@thefnmark}$\hss}} 
    \let\footnotesize\small \let\footnoterule\relax
% This next statement is done by \begin{titlepage}
%    \setcounter{page}{0}
    \begin{center}
      {\large\bf Overture Technical Report Series\\ No. \@reportno \\[5mm]}
      {\large\bf \@date \par}
    \end{center}
    \vfill
    \begin{center}
      {\LARGE \@title \par} \vskip 2em
      {\large \lineskip .75em
       \begin{center}by\end{center}\par
       \begin{tabular}[t]{c}
         \let\\=\@atabularcr
         \@author 
       \end{tabular}\par}
    \end{center} \par
% This next statement is done by \begin{titlepage}
%    \thispagestyle{empty}
    \vfill
    \null
%    \@thanks
    \ifvoid\@tpfootins\else\vskip\skip\@tpfootins\unvbox\@tpfootins\fi
    \vbox{
      \vskip.7\baselineskip
      \titlepagerule
\begin{minipage}{.8\textwidth}
{\large{\bf  Overture -- Open-source Tools for Formal Modelling}}
\end{minipage}
\hfill
\resizebox{0.05\textwidth}{!}{\includegraphics{overture}}
    }
  \end{titlepage}
  \setcounter{footnote}{0} \let\thanks\relax \let\maketitle\relax
  \gdef\@thanks{}\gdef\@author{}\gdef\@title{}\let\maketitle\relax} 

\def\abstract{
  \titlepage
  \begin{center}
    {\bf ABSTRACT}
  \end{center}}

% This "\thispagestyle{empty}" is for the first intro page, not the
% abstract.  Note that \titlepage...\endtitlepage does this automatically.
\def\endabstract{\par\vfil\null\endtitlepage\thispagestyle{empty}}

\newinsert\@tpfootins

% copy of \@footnotetext from latex.tex
\long\def\@tpfootnotetext#1{\global\setbox\@tpfootins
    \vbox{\unvbox\@tpfootins
    \footnotesize
    \hsize\columnwidth \@parboxrestore
   \edef\@currentlabel{\csname p@footnote\endcsname\@thefnmark}\@makefntext
     {\rule{\z@}{\footnotesep}\ignorespaces #1\strut}}}

\long\def\@makefntext#1{\parindent 0em\noindent 
 \hbox to 0pt{\hss$^{\@thefnmark}$}#1}

\def\thanks#1{%
   \@commap\footnotemark%
   \gdef\@commap{${}^,$}%
   \footnotetext[\the\c@footnote]{#1}}

\def\and{%%                             % \begin{tabular} 
  \gdef\@commap{}
  \end{tabular}\hskip .5\parskip
%  \begin{center}and\end{center}\par
%  \hskip 2em plus .17fil
  \begin{tabular}[t]{c}%%                 \end{tabular}
}

\def\@atabularcr{{\ifnum0=`}\fi\gdef\@commap{}
                \@ifstar{\@xtabularcr}{\@xtabularcr}}

\def\@commap{}

\def\titlepagerule{\kern-3\p@
  \hrule width \textwidth \kern 2.6\p@} % the \hrule is .4pt high

% just the month and year
\def\today{\ifcase\month\or
 January\or February\or March\or April\or May\or June\or
 July\or August\or September\or October\or November\or December\fi
 \space\number\year}

% redefine page number to be centered at top of page
\pagestyle{empty}
\def\@oddhead{\hfil{\rm--\ \thepage\ --}\hfil}

\endinput
