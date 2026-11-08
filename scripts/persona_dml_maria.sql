INSERT INTO 
	`persona_db`.`persona`(`cc`,`nombre`,`apellido`,`genero`,`edad`) 
VALUES
	(123456789,'Pepe','Perez','M',30),
	(987654321,'Pepito','Perez','M',null),
	(321654987,'Pepa','Juarez','F',30),
	(147258369,'Pepita','Juarez','F',10),
	(963852741,'Fede','Perez','M',18);
--
INSERT INTO
	`persona_db`.`profesion`(`id`,`nom`,`des`)
VALUES
	(1,'Ingenieria de Sistemas','Diseno, desarrollo y gestion de sistemas de informacion y software'),
	(2,'Medicina','Prevencion, diagnostico y tratamiento de enfermedades'),
	(3,'Derecho','Estudio y aplicacion de las normas juridicas'),
	(4,'Arquitectura','Diseno y construccion de edificaciones y espacios');
--
INSERT INTO
	`persona_db`.`telefono`(`num`,`oper`,`duenio`)
VALUES
	('3001234567','Claro',123456789),
	('3109876543','Movistar',123456789),
	('3201112233','Tigo',321654987),
	('3157778899','WOM',963852741);
--
INSERT INTO
	`persona_db`.`estudios`(`id_prof`,`cc_per`,`fecha`,`univer`)
VALUES
	(1,123456789,'2015-12-10','Pontificia Universidad Javeriana'),
	(4,123456789,'2019-06-15','Universidad del Rosario'),
	(2,321654987,'2018-06-20','Universidad Nacional de Colombia'),
	(3,963852741,'2024-11-30','Universidad de los Andes');
--
COMMIT;
