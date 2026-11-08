// Antes este script hacia "use prueba_db", por lo que los datos quedaban en una
// base distinta a la que usa la aplicacion (persona_db).
db = db.getSiblingDB("persona_db");

db.persona.insertMany([
	{
		"_id": NumberInt(123456789),
		"nombre": "Pepe",
		"apellido": "Perez",
		"genero": "M",
		"edad": NumberInt(30),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(987654321),
		"nombre": "Pepito",
		"apellido": "Perez",
		"genero": "M",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(321654987),
		"nombre": "Pepa",
		"apellido": "Juarez",
		"genero": "F",
		"edad": NumberInt(30),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(147258369),
		"nombre": "Pepita",
		"apellido": "Juarez",
		"genero": "F",
		"edad": NumberInt(10),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(963852741),
		"nombre": "Fede",
		"apellido": "Perez",
		"genero": "M",
		"edad": NumberInt(18),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	}
], { ordered: false });

db.profesion.insertMany([
	{ "_id": NumberInt(1), "nom": "Ingenieria de Sistemas", "des": "Diseno, desarrollo y gestion de sistemas de informacion y software", "_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument" },
	{ "_id": NumberInt(2), "nom": "Medicina", "des": "Prevencion, diagnostico y tratamiento de enfermedades", "_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument" },
	{ "_id": NumberInt(3), "nom": "Derecho", "des": "Estudio y aplicacion de las normas juridicas", "_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument" },
	{ "_id": NumberInt(4), "nom": "Arquitectura", "des": "Diseno y construccion de edificaciones y espacios", "_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument" }
], { ordered: false });

// primaryDuenio guarda el _id de la persona (@DocumentReference)
db.telefono.insertMany([
	{ "_id": "3001234567", "oper": "Claro", "primaryDuenio": NumberInt(123456789), "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" },
	{ "_id": "3109876543", "oper": "Movistar", "primaryDuenio": NumberInt(123456789), "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" },
	{ "_id": "3201112233", "oper": "Tigo", "primaryDuenio": NumberInt(321654987), "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" },
	{ "_id": "3157778899", "oper": "WOM", "primaryDuenio": NumberInt(963852741), "_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument" }
], { ordered: false });

// _id = "cc-idProfesion", igual que lo arma EstudiosMapperMongo.
// La fecha se guarda a mediodia UTC para que al convertirla a LocalDate en la
// zona horaria local (p. ej. America/Bogota, UTC-5) no se corra un dia.
db.estudios.insertMany([
	{ "_id": "123456789-1", "primaryPersona": NumberInt(123456789), "primaryProfesion": NumberInt(1), "fecha": ISODate("2015-12-10T12:00:00Z"), "univer": "Pontificia Universidad Javeriana", "_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument" },
	{ "_id": "123456789-4", "primaryPersona": NumberInt(123456789), "primaryProfesion": NumberInt(4), "fecha": ISODate("2019-06-15T12:00:00Z"), "univer": "Universidad del Rosario", "_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument" },
	{ "_id": "321654987-2", "primaryPersona": NumberInt(321654987), "primaryProfesion": NumberInt(2), "fecha": ISODate("2018-06-20T12:00:00Z"), "univer": "Universidad Nacional de Colombia", "_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument" },
	{ "_id": "963852741-3", "primaryPersona": NumberInt(963852741), "primaryProfesion": NumberInt(3), "fecha": ISODate("2024-11-30T12:00:00Z"), "univer": "Universidad de los Andes", "_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument" }
], { ordered: false });
