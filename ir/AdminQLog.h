/* ---------------- LOG PARSER SECTION ---------------- */

/* Represents a single parsed entry within the log */
class Entry {
    /* Main Command Fields (bytes 0-15) */
	uint16_t flags;
	uint16_t opcode;
	uint16_t retval;
	uint32_t cookie_high;
	uint32_t cookie_low;

	/* Buffer Related Fields : can either be direct or indirect buffer */
	uint16_t buflen;
	uint8_t* buf;
};
