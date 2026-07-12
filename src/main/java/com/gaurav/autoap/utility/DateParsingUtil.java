package com.gaurav.autoap.utility;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateParsingUtil {

    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d MMMM yyyy"),   // e.g. "10 July 2026"
            DateTimeFormatter.ofPattern("MMMM d, yyyy"),  // e.g. "July 10, 2026"
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

    /**
     * Attempts to parse a date string using multiple known formats.
     * Returns null if parsing fails with all formats, instead of throwing.
     */
    public static LocalDate parseFlexible(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }

        String trimmed = rawDate.trim();

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
                // try next formatter
            }
        }

        // All formats failed — log it and return null rather than crashing
        System.out.println("WARNING: Could not parse date '" + rawDate + "' with any known format.");
        return null;
    }
}
