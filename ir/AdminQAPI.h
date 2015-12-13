#include "AdminQLog.h"
#include "AdminQHeader.h"
#include "stdio.h"

/* ---------------- GLOBAL SHARED INTERFACE ---------------- */

/* Standard Accessor Interface for Internal Functions */

/* The version of the i40e AdminQ API definition being used*/
uint32_t API_VERSION;

/* Error Code Definition Access */
Errors* ERRORS;

/* Command Definition Access */
Commands* COMMANDS;

/* Retrieves a in-order representation of AdminQ entries within this log file */
Entry** parse(FILE* log);

