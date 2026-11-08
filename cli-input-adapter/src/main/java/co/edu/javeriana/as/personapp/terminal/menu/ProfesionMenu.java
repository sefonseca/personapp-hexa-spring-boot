package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfesionMenu {

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


	public void iniciarMenu(ProfesionInputAdapterCli inputAdapterCli, Scanner keyboard) {
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
					inputAdapterCli.setProfessionOutputPortInjection("MARIA");
					menuOpciones(inputAdapterCli, keyboard);
					break;
				case PERSISTENCIA_MONGODB:
					inputAdapterCli.setProfessionOutputPortInjection("MONGO");
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

	private void menuOpciones(ProfesionInputAdapterCli inputAdapterCli, Scanner keyboard) {
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
					inputAdapterCli.buscar(LectorConsola.leerEntero(keyboard, "Id de la profesión"));
					break;
				case OPCION_CREAR:
					inputAdapterCli.crear(leerProfesion(keyboard, LectorConsola.leerEntero(keyboard, "Id de la profesión")));
					break;
				case OPCION_EDITAR:
					inputAdapterCli.editar(leerProfesion(keyboard, LectorConsola.leerEntero(keyboard, "Id a editar")));
					break;
				case OPCION_ELIMINAR:
					inputAdapterCli.eliminar(LectorConsola.leerEntero(keyboard, "Id a eliminar"));
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
		System.out.println(OPCION_VER_TODO + " para ver todos los registros de profesiones");
		System.out.println(OPCION_BUSCAR + " para buscar");
		System.out.println(OPCION_CREAR + " para crear");
		System.out.println(OPCION_EDITAR + " para editar");
		System.out.println(OPCION_ELIMINAR + " para eliminar");
		System.out.println(OPCION_CONTAR + " para contar");

		System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
	}

	private void mostrarMenuMotorPersistencia() {
		System.out.println("----------------------");
		System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
		System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
		System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
	}

	private ProfesionModelCli leerProfesion(Scanner keyboard, Integer id) {
		ProfesionModelCli profesion = new ProfesionModelCli();
		profesion.setId(id);
		profesion.setNombre(LectorConsola.leerTexto(keyboard, "Nombre"));
		profesion.setDescripcion(LectorConsola.leerTextoOpcional(keyboard, "Descripción"));
		return profesion;
	}
}
