#ifndef ADMINQ_IR_HEADER_H
#define ADMINQ_IR_HEADER_H

#include <stdio.h>

/* ---------------- HEADER API DEFINITION FILE PARSER SECTION ---------------- */

/* Available Error Codes */
typedef struct {
    char *fromByte(uint16_t val); /* A String representation of the corresponding error code - or ERR_UNKNOWN if not defined */
} Errors;

/* Available AdminQ Commands */
typedef struct {
    AdminQCommand *fromByte(uint16_t opcode); /* Returns the command definition corresponding to that opcode */
} Commands;

/* A field within a command definition */
typedef struct Field Field;

/* Table Node */
typedef struct Table Table;

/* A Command Definition Derived From the API Header File*/
typedef struct {
    char *name; /* The name of the command (corresponds to its key within the OpCode enum) */
    char *flagFromByte(uint16_t flags); /* A string representation of the flag value for this command - can be NULL */
    Table *fieldsFromByte(uint16_t buflen,uint8_t *buf); /*  A mapping of field name -> field value from the provided (direct or indirect) data buffer */
} AdminQCommand;

/* A field within a command definition */
struct Field {
    char *value; /* A string representation of the value: could be raw bytes, or a corresponding #define if available */
    int startpos; /* The starting position within the byte buffer corresponding to this value (might be useful for highlighting) */
    int endpos; /* The ending position within the byte buffer corresponding to this value */
};

/* A linear field->value table node */
struct Table {
    Table *next;
    char *fieldName;
    Field *fieldVal;
    Table *last;

    void init(char *field, Field *val);
    void insert(char *field, Field *val);
};

#endif /* ADMINQ_IR_HEADER_H */

