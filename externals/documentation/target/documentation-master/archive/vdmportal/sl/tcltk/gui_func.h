/*-------------------------------------------------------------------------*/
/*  gui_func.h                                                             */
/*                                                                         */
/*  Headerfile zu gui_func.cc                                              */
/*  Functions of the Grafical User Interface                               */
/*                                                                         */
/*   makefile: Makefile                      created:       1996.02.09     */
/*    bf                                     last modified: 1996.02.20     */
/*-------------------------------------------------------------------------*/
/* glocal variables */

typedef int keytype;
/* typedef char datatype[21]; */
typedef int datatype;

struct item {
  keytype key;
  datatype data;
};

extern int KeyInput();
extern item ItemInput();
extern void ItemOutput(item tp);
extern void MsgOutput(char * err_func, char *err_msg);
extern int Menu();
extern void DefinedOutput(int key, int found);
extern void ShowInfo();
