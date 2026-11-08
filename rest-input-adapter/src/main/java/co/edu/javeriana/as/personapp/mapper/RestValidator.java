package co.edu.javeriana.as.personapp.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Utilidades de conversion de los campos String que llegan en los request REST.
 * Lanzan IllegalArgumentException, que el GlobalExceptionHandler traduce a 400.
 */
public final class RestValidator {

	private RestValidator() {
	}

	public static String requiredText(String value, String field) {
		if (value == null || value.isBlank())
			throw new IllegalArgumentException(field + " is required");
		return value.trim();
	}

	public static Integer requiredInteger(String value, String field) {
		try {
			return Integer.valueOf(requiredText(value, field));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(field + " must be an integer number");
		}
	}

	public static Integer optionalInteger(String value, String field) {
		if (value == null || value.isBlank() || value.equalsIgnoreCase("null"))
			return null;
		return requiredInteger(value, field);
	}

	public static LocalDate optionalDate(String value, String field) {
		if (value == null || value.isBlank())
			return null;
		try {
			return LocalDate.parse(value.trim());
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(field + " must have the format yyyy-MM-dd");
		}
	}
}
