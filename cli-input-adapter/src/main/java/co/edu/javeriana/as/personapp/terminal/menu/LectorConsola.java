package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Lectura de datos por consola linea a linea. El template original usaba
 * nextInt() y, ante una entrada no numerica, volvia a llamar a leerOpcion() sin
 * consumir el token invalido: el resultado era un ciclo infinito
 * (StackOverflowError). Leyendo la linea completa ese problema desaparece.
 */
public final class LectorConsola {

	private LectorConsola() {
	}

	public static int leerOpcion(Scanner keyboard) {
		while (true) {
			System.out.print("Ingrese una opción: ");
			String linea = keyboard.nextLine().trim();
			try {
				return Integer.parseInt(linea);
			} catch (NumberFormatException e) {
				System.out.println("Solo se permiten números.");
			}
		}
	}

	public static Integer leerEntero(Scanner keyboard, String etiqueta) {
		while (true) {
			System.out.print(etiqueta + ": ");
			String linea = keyboard.nextLine().trim();
			try {
				return Integer.valueOf(linea);
			} catch (NumberFormatException e) {
				System.out.println("Debe ingresar un número entero.");
			}
		}
	}

	public static Integer leerEnteroOpcional(Scanner keyboard, String etiqueta) {
		while (true) {
			System.out.print(etiqueta + " (Enter para dejar vacío): ");
			String linea = keyboard.nextLine().trim();
			if (linea.isEmpty())
				return null;
			try {
				return Integer.valueOf(linea);
			} catch (NumberFormatException e) {
				System.out.println("Debe ingresar un número entero.");
			}
		}
	}

	public static String leerTexto(Scanner keyboard, String etiqueta) {
		while (true) {
			System.out.print(etiqueta + ": ");
			String linea = keyboard.nextLine().trim();
			if (!linea.isEmpty())
				return linea;
			System.out.println("El campo es obligatorio.");
		}
	}

	public static String leerTextoOpcional(Scanner keyboard, String etiqueta) {
		System.out.print(etiqueta + " (Enter para dejar vacío): ");
		String linea = keyboard.nextLine().trim();
		return linea.isEmpty() ? null : linea;
	}

	public static String leerGenero(Scanner keyboard) {
		while (true) {
			String genero = leerTexto(keyboard, "Género (M/F)").toUpperCase();
			if (genero.equals("M") || genero.equals("F"))
				return genero;
			System.out.println("Solo se permite M o F.");
		}
	}

	public static LocalDate leerFechaOpcional(Scanner keyboard, String etiqueta) {
		while (true) {
			String linea = leerTextoOpcional(keyboard, etiqueta + " [yyyy-MM-dd]");
			if (linea == null)
				return null;
			try {
				return LocalDate.parse(linea);
			} catch (DateTimeParseException e) {
				System.out.println("Formato inválido, use yyyy-MM-dd.");
			}
		}
	}
}
