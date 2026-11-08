// Se usa getSiblingDB en lugar de "use", que es un comando interactivo de la
// shell y no siempre se respeta cuando el script se ejecuta como archivo.
db = db.getSiblingDB("admin");

// Si el usuario ya existe se elimina para que el script sea re-ejecutable
if (db.getUser("persona_db") != null) {
  db.dropUser("persona_db");
}

db.createUser({
  user: "persona_db",
  pwd: "persona_db",
  roles: [
    { role: "read", db: "persona_db" },
    { role: "readWrite", db: "persona_db" },
    { role: "dbAdmin", db: "persona_db" }
  ],
  mechanisms: ["SCRAM-SHA-1","SCRAM-SHA-256"]
});
