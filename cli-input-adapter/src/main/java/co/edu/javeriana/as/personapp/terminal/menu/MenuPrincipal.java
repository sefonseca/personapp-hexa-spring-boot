package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.javeriana.as.personapp.terminal.adapter.EstudioInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Antes estaba anotada con @Controller (estereotipo de Spring MVC); en una
 * aplicacion de consola el estereotipo correcto es @Component.
 */
@Slf4j
@Component
public class MenuPrincipal {
	
	//Beans
	@Autowired
	private PersonaInputAdapterCli personaInputAdapterCli;

	@Autowired
	private ProfesionInputAdapterCli profesionInputAdapterCli;

	@Autowired
	private TelefonoInputAdapterCli telefonoInputAdapterCli;

	@Autowired
	private EstudioInputAdapterCli estudioInputAdapterCli;

	private static final int SALIR = 0;
	private static final int MODULO_PERSONA = 1;
	private static final int MODULO_PROFESION = 2;
	private static final int MODULO_TELEFONO = 3;
	private static final int MODULO_ESTUDIO = 4;

	//Menus
	private final PersonaMenu personaMenu;
	private final ProfesionMenu profesionMenu;
	private final TelefonoMenu telefonoMenu;
	private final EstudioMenu estudioMenu;
	private final Scanner keyboard;

    public MenuPrincipal() {
        this.personaMenu = new PersonaMenu();
        this.profesionMenu = new ProfesionMenu();
        this.telefonoMenu = new TelefonoMenu();
        this.estudioMenu = new EstudioMenu();
        this.keyboard = new Scanner(System.in);
    }

	public void inicio() {
		boolean isValid = false;
		do {
			mostrarMenu();
			int opcion = LectorConsola.leerOpcion(keyboard);
			switch (opcion) {
			case SALIR:
				isValid = true;
				break;
			case MODULO_PERSONA:
				personaMenu.iniciarMenu(personaInputAdapterCli, keyboard);
				break;
			case MODULO_PROFESION:
				profesionMenu.iniciarMenu(profesionInputAdapterCli, keyboard);
				break;
			case MODULO_TELEFONO:
				telefonoMenu.iniciarMenu(telefonoInputAdapterCli, keyboard);
				break;
			case MODULO_ESTUDIO:
				estudioMenu.iniciarMenu(estudioInputAdapterCli, keyboard);
				break;
			default:
				System.out.println("La opción elegida no es válida.");
			}

		} while (!isValid);
		keyboard.close();
	}

	private void mostrarMenu() {
		System.out.println("----------------------");
		System.out.println(MODULO_PERSONA + " para trabajar con el Modulo de Personas");
		System.out.println(MODULO_PROFESION + " para trabajar con el Modulo de Profesiones");
		System.out.println(MODULO_TELEFONO + " para trabajar con el Modulo de Telefonos");
		System.out.println(MODULO_ESTUDIO + " para trabajar con el Modulo de Estudios");
		System.out.println(SALIR + " para Salir");
	}

}
