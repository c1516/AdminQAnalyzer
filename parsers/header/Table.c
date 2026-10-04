#include <stdlib.h>
#include "../../ir/AdminQHeader.h"

void Table::init(char *field, Field *val) {
    fieldName = field;
    fieldVal = val;
    next = NULL;
    last = this;
}

void Table::insert(char *field, Field *val) {
    /* The assumption is made that all operations will be done on the root node */
    last->next = malloc(sizeof(Table));
    last = last->next;
    last->init(field, val);
}

