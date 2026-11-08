package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonaMenu {

	private static final int OPCION_REGRESAR_MODULOS = 0;
	private static final int PERSISTENCIA_MARIADB = 1;
	private static final int PERSISTENCIA_MONGODB = 2;

	private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
	private static final int OPCION_VER_TODO = 1;
	private static final int OPCION_BUSCAR = 2;
	private static final int OPCION_CREAR = 3;
	private static final int OPCION_EDITAR = 4;
	private static final int OPCION_ELIMINAR = 5;
	private static final int OPCION_CONTAR = 6;
	private static final int OPCION_TELEFONOS = 7;
	private static final int OPCION_ESTUDIOS = 8;

	public void iniciarMenu(PersonaInputAdapterCli inputAdapterCli, Scanner keyboard) {
		boolean isValid = false;
		do {
			try {
				mostrarMenuMotorPersistencia();
				int opcion = LectorConsola.leerOpcion(keyboard);
				switch (opcion) {
				case OPCION_REGRESAR_MODULOS:
					isValid = true;
					break;
				case PERSISTENCIA_MARIADB:
					inputAdapterCli.setPersonOutputPortInjection("MARIA");
					menuOpciones(inputAdapterCli, keyboard);
					break;
				case PERSISTENCIA_MONGODB:
					inputAdapterCli.setPersonOutputPortInjection("MONGO");
					menuOpciones(inputAdapterCli, keyboard);
					break;
				default:
					System.out.println("La opción elegida no es válida.");
				}
			} catch (InvalidOptionException e) {
				log.warn(e.getMessage());
			}
		} while (!isValid);
	}

	private void menuOpciones(PersonaInputAdapterCli inputAdapterCli, Scanner keyboard) {
		boolean isValid = false;
		do {
			try {
				mostrarMenuOpciones();
				int opcion = LectorConsola.leerOpcion(keyboard);
				switch (opcion) {
				case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
					isValid = true;
					break;
				case OPCION_VER_TODO:
					inputAdapterCli.historial();
					break;
				case OPCION_BUSCAR:
					inputAdapterCli.buscar(LectorConsola.leerEntero(keyboard, "Cédula"));
					break;
				case OPCION_CREAR:
					inputAdapterCli.crear(leerPersona(keyboard, LectorConsola.leerEntero(keyboard, "Cédula")));
					break;
				case OPCION_EDITAR:
					inputAdapterCli.editar(leerPersona(keyboard, LectorConsola.leerEntero(keyboard, "Cédula a editar")));
					break;
				case OPCION_ELIMINAR:
					inputAdapterCli.eliminar(LectorConsola.leerEntero(keyboard, "Cédula a eliminar"));
					break;
				case OPCION_TELEFONOS:
					inputAdapterCli.telefonos(LectorConsola.leerEntero(keyboard, "Cédula"));
					break;
				case OPCION_ESTUDIOS:
					inputAdapterCli.estudios(LectorConsola.leerEntero(keyboard, "Cédula"));
					break;
				case OPCION_CONTAR:
					inputAdapterCli.contar();
					break;
				default:
					System.out.println("La opción elegida no es válida.");
				}
			} catch (RuntimeException e) {
				// cualquier error de una operacion no debe tumbar la aplicacion
				System.out.println("Error: " + e.getMessage());
			}
		} while (!isValid);
	}

	private void mostrarMenuOpciones() {
		System.out.println("----------------------");
		System.out.println(OPCION_VER_TODO + " para ver todos los registros de personas");
		System.out.println(OPCION_BUSCAR + " para buscar");
		System.out.println(OPCION_CREAR + " para crear");
		System.out.println(OPCION_EDITAR + " para editar");
		System.out.println(OPCION_ELIMINAR + " para eliminar");
		System.out.println(OPCION_CONTAR + " para contar");
		System.out.println(OPCION_TELEFONOS + " para ver los telefonos de una persona");
		System.out.println(OPCION_ESTUDIOS + " para ver los estudios de una persona");
		System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
	}

	private void mostrarMenuMotorPersistencia() {
		System.out.println("----------------------");
		System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
		System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
		System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
	}

	private PersonaModelCli leerPersona(Scanner keyboard, Integer cc) {
		PersonaModelCli persona = new PersonaModelCli();
		persona.setCc(cc);
		persona.setNombre(LectorConsola.leerTexto(keyboard, "Nombre"));
		persona.setApellido(LectorConsola.leerTexto(keyboard, "Apellido"));
		persona.setGenero(LectorConsola.leerGenero(keyboard));
		persona.setEdad(LectorConsola.leerEnteroOpcional(keyboard, "Edad"));
		return persona;
	}
}
