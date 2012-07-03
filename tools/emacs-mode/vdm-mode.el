;;; vdm-mode.el --- major mode for editing VDM descriptions

;; Copyright (C) 2012 ESBEN Andreasen <esbena@cs.au.dk>

;; Authors: Esben Andreasen <esbena@cs.au.dk>

;; Keywords: VDM 

;; This file is not an official part of Emacs.

;; This program is free software; you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation; either version 2, or (at your option)
;; any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program; if not, you can either send email to this
;; program's maintainer or write to: The Free Software Foundation,
;; Inc.; 59 Temple Place, Suite 330; Boston, MA 02111-1307, USA.

;; INSTALLATION

;; place this file somewhere, edit the line below
;; accordingly, and insert both of the lines below into your
;; ~/.emacs -file. Files ending in .vdm should now be using
;; the vdm-mode.

;;(autoload 'vdm-mode "DIRECTORY-OF-THE-FILE/vdm-mode.el")
;;(add-to-list 'auto-mode-alist '("\\.vdm$" . vdm-mode))

;; FEATURES
;; * indentation rules
;; * syntax highlightning
;; * error highlightning
;; * run command

(defvar vdm-jar-location nil "location of the jar for compiling and running files")
(defvar vdm-project-root nil "root directory of the vdm files to use")
(defvar vdm-command-line-arguments nil "extra arguments to the compiler")
(defvar vdm-run-command nil "the expression vdm should evaluate")

;;;; example configuration

;; (setq vdm-jar-location "~/binaries/OvertureIde-1.2.1/plugins/org.overture.ide.core_2.1.1/vdmj-2.1.1.jar")

;; (setq vdm-command-line-arguments " -vdmpp ")

;; (setq vdm-project-root "~/overture_workspace/AbstractLanguageAnalysis/")

;; (setq vdm-run-command "new MainTest().Run()")

;; inner workings:

(defvar vdm-indent-offset 4
  "Indentation offset for `vdm-mode'.")
(defvar vdm-mode-map nil "Keymap for vdm-mode")
(defvar vdm-indentation-increasers "[[({]")
(defvar vdm-indentation-decreasers "[])}]")
(defvar vdm-mode-hook nil)

(defun vdm-generic-indent-line ()
  "Indent current line for any balanced-paren-mode'."
  (interactive)
  (let ((indent-col 0)
        )
    (save-excursion
      (beginning-of-line)
      (condition-case nil
          (while t
            (backward-up-list 1)
            (when (looking-at vdm-indentation-increasers)
              (setq indent-col (+ indent-col vdm-indent-offset))))
        (error nil)))
    (save-excursion
      (back-to-indentation)
      (when (and (looking-at vdm-indentation-decreasers) (>= indent-col vdm-indent-offset))
        (setq indent-col (- indent-col vdm-indent-offset))))
    (indent-line-to indent-col)))


(define-generic-mode 'vdm-mode
  '(("/*" . "*/"))
  '("public" "private" "protected" "async" "static" "while" "let" "do"
    "in" "if" "then" "return" "for" "all" "else" "end" "opertions"
    "functions" "thread" "instance" "variables" "sync" "class" "abs"
    "allsuper" "always" "and\\answer" "assumption" "atomic" "be" "bool"
    "by" "card" "and" "cases" "char" "comp" "compose" "conc" "cycles"
    "dcl" "def" "del" "dinter" "div" "dom" "dunion" "duration" "effect"
    "elems\\error" "errs" "exists" "exists1" "exit" "ext" "floor" "\\from"
    "general" "hd" "in" "inds" "init\\inmap" "input" "int" "inter" "inv"
    "inverse" "iota" "is" "isofbaseclass" "isofclas" "inv" "inverse"
    "lambda" "map" "mu" "mutex" "mod" "nat" "nat1" "new" "merge" "munion"
    "not" "of" "or" "or" "others" "per" "periodic" "post" "pre" "pref"
    "qsync" "rd" "responsibility" "reverse" "samebaseclass" "sameclass"
    "psubset" "rem" "rng" "sel" "self" "seq" "seq1" "set" "skip"
    "specified" "st" "start" "startlist" "subclass" "subset" "subtrace"
    "synonym" "threadid" "time" "tixe" "tl" "to" "token" "trap"
    "undefined" "union" "using" "values" "with" "wr" "yet" "RESULT" "types" "operations"
    "forall" "vdm"
    "false" "true" "nil" "periodic" "pref" "rat" "real")
  '(("[0-9]+" . 'font-lock-variable-name-face)
    ("\([A-Za-z]+\)(.*)" . 'font-lock-function-name-face)
    ("--{" . 'org-document-info-keyword)
    ("--}" . 'org-document-info-keyword)
    )
  '("\\.vdm\\(pp\\)?\\'")
  '( ;; env setup
    (lambda () 
      (make-local-variable 'vdm-indent-offset)
      (setq indent-line-function 'vdm-generic-indent-line)
      (when (not vdm-mode-map) 
        (setq vdm-mode-map (make-sparse-keymap))
        (define-key vdm-mode-map  (kbd "RET") 'newline-and-indent)
        (define-key vdm-mode-map  "\C-c\C-c"
          (lambda () (interactive) 
            (let* (
                   (jar-loc vdm-jar-location)
                   (cmd (concat "find " vdm-project-root " -name \\*\\.vdmpp | grep -v '\.\#' | grep -v flymake"))
                   (files (mapconcat 'identity 
                                     (split-string 
                                      (shell-command-to-string cmd)
                                      "\n") " "))
                   )
;;              (message (concat "executed command: " cmd))
              (message (concat "Executing with files in path: " files))
              (shell-command (concat "java -jar " jar-loc " " vdm-command-line-arguments " " files " -e '" vdm-run-command "'") (get-buffer-create "*vdm output*"))
              (display-buffer (get-buffer-create "*vdm output*"))
              )
            )
          )
        )
      (use-local-map vdm-mode-map)
      (run-hooks 'vdm-mode-hook)
      ))
  "Major mode for VDM-SL/VDM++ highlighting.")
(provide 'vdm-mode)
