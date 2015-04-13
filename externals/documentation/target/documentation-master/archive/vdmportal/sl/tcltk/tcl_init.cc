/*-------------------------------------------------------------------------*/
/*  tcl_init.cc                                                            */
/*                                                                         */
/*  Generierung und Initialisierung eines Tcl-Interpreters                 */
/*  It creates an tcl interpreter and opens a main Window                  */
/*                                                                         */
/*   makefile: Makefile                                                    */
/*    bf                                                   1996.02.09      */
/*-------------------------------------------------------------------------*/

#include <stdio.h>
#include <string.h>
#include <tcl.h>
#include <tk.h>

Tcl_Interp * interp;   /* Tcl interpreter */
Tk_Window mainWindow;  /* Main Window ==  */

int Init_Tcl();
int MainWindow (char * mainwdname, char * windowclass);

int Init_Tcl()
  {
    /* Interpreter creation */
   
     interp = Tcl_CreateInterp ();
     
     if (interp == NULL) {
        fprintf(stderr, "Could not create a tcl interpreter!\n");
        exit(1);
      }
   }

int MainWindow (char * mainwdname, char * windowclass)
{
     static char *display = NULL;  /* default display */
    
     mainWindow = Tk_CreateMainWindow(interp, display, mainwdname, 
           windowclass);

	if (mainWindow == NULL) {
		fprintf(stderr, "%s\n", interp->result);
		exit(1);
	}
  
	if (Tcl_Init(interp) == TCL_ERROR) {
		fprintf(stderr, "Tcl_Init failed: %s\n", interp->result);
                return 1;
	}
   
	if (Tk_Init(interp) == TCL_ERROR) {
		fprintf(stderr, "Tk_Init failed: %s\n", interp->result);
                return 1;
	}
        return 0;  /*  Everything Okay */
   }

