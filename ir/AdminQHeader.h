/* ---------------- HEADER API DEFINITION FILE PARSER SECTION ---------------- */

/* Available Error Codes */
class Errors {
	char* fromByte(uint16_t val); /* A String representation of the corresponding error code - or ERR_UNKNOWN if not defined */
};

/* Available AdminQ Commands */
class Opcodes {
	AdminQCommand* fromByte(uint16_t opcode); /* Returns the command definition corresponding to that opcode */
};

/* A Command Definition Derived From the API Header File*/
class AdminQCommand {
	char* name; /* The name of the command (corresponds to its key within the OpCode enum) */
	char* flagFromByte(uint16_t flags); /* A string representation of the flag value for this command - can be NULL */
	Map<char*, Field> fieldsFromByte(uint16_t buflen, uint8_t* buf); /*  A mapping of field name -> field value from the provided (direct or indirect) data buffer */
};

/* A field within a command definition */
class Field {
	char* value; /* A string representation of the value: could be raw bytes, or a corresponding #define if available */
	int startpos; /* The starting position within the byte buffer corresponding to this value (might be useful for highlighting) */
	int endpos; /* The ending position within the byte buffer corresponding to this value */
};
