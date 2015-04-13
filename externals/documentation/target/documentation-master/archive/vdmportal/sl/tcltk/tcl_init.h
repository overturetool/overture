/*-------------------------------------------------------------------------*/
/*  tcl_init.h                                                             */
/*                                                                         */
/*  Headerfile zu tcl_init.cc                                              */
/*  Generierung und Initialisierung eines Tcl-Interpreters                 */
/*                                                                         */
/*   makefile: Makefile                                                    */
/*    bf                                                   1996.02.09      */
/*-------------------------------------------------------------------------*/

extern Tcl_Interp * interp;
extern Tk_Window mainWindow;

extern int Init_Tcl();
int MainWindow (char * mainwdname, char * windowclass);
