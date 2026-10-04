#ifndef ADMINQ_IR_MAIN_H
#define ADMINQ_IR_MAIN_H

#include <stdio.h>
#include "AdminQLog.h"
#include "AdminQHeader.h"

/* ---------------- GLOBAL SHARED INTERFACE ---------------- */

/* Standard Accessor Interface for Internal Functions */

/* The version of the i40e AdminQ API definition being used*/
uint32_t API_VERSION;

/* Error Code Definition Access */
Errors *ERRORS;

/* Command Definition Access */
Commands *COMMANDS;

/* Retrieves a in-order representation of AdminQ entries within this log file */
Entry **parse(FILE *log);

#endif /* ADMINQ_IR_MAIN_H */


