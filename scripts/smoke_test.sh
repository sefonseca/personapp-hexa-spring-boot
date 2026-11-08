#!/bin/bash
# Prueba de humo: ejecuta el CRUD completo de las 4 entidades contra MARIA y MONGO.
# Uso: bash scripts/smoke_test.sh   (opcional: BASE_URL=http://host:3000/api/v1)
# Deja los datos como estaban: todo lo que crea lo elimina al final.
B=${BASE_URL:-http://localhost:3000/api/v1}
c() { local m=$1 u=$2 d=$3; if [ -n "$d" ]; then r=$(curl -s -w '\n%{http_code}' -X $m -H 'Content-Type: application/json' -d "$d" "$B$u"); else r=$(curl -s -w '\n%{http_code}' -X $m "$B$u"); fi; code=$(echo "$r"|tail -1); body=$(echo "$r"|sed '$d'|head -c 230); echo "[$code] $m $u  ->  $body"; }
for db in MARIA MONGO; do
echo "================ $db ================"
c GET /persona/$db
c GET /persona/$db/count
c GET /persona/$db/123456789
c GET /persona/$db/123456789/telefonos
c GET /persona/$db/123456789/estudios
c POST /persona "{\"dni\":\"1010\",\"firstName\":\"Ana\",\"lastName\":\"Gomez\",\"age\":\"25\",\"sex\":\"F\",\"database\":\"$db\"}"
c POST /persona "{\"dni\":\"1010\",\"firstName\":\"Ana\",\"lastName\":\"Gomez\",\"age\":\"25\",\"sex\":\"F\",\"database\":\"$db\"}"
c POST /persona "{\"dni\":\"abc\",\"firstName\":\"Ana\",\"lastName\":\"Gomez\",\"sex\":\"X\",\"database\":\"$db\"}"
c PUT /persona/$db/1010 '{"firstName":"Ana Maria","lastName":"Gomez","age":"26","sex":"F"}'
c GET /persona/$db/999
c GET /profesion/$db
c POST /profesion "{\"id\":\"50\",\"name\":\"Fisica\",\"description\":\"Ciencias\",\"database\":\"$db\"}"
c PUT /profesion/$db/50 '{"name":"Fisica Teorica","description":"Ciencias basicas"}'
c GET /profesion/$db/50
c GET /telefono/$db
c POST /telefono "{\"number\":\"3000000001\",\"company\":\"Claro\",\"ownerId\":\"1010\",\"database\":\"$db\"}"
c POST /telefono "{\"number\":\"3000000002\",\"company\":\"Claro\",\"ownerId\":\"777\",\"database\":\"$db\"}"
c PUT /telefono/$db/3000000001 '{"company":"Movistar","ownerId":"1010"}'
c GET /telefono/$db/3000000001
c GET /estudio/$db
c POST /estudio "{\"personId\":\"1010\",\"professionId\":\"50\",\"graduationDate\":\"2020-05-10\",\"universityName\":\"Javeriana\",\"database\":\"$db\"}"
c PUT /estudio/$db/1010/50 '{"graduationDate":"2021-06-11","universityName":"Universidad Javeriana"}'
c GET /estudio/$db/1010/50
c GET /persona/$db/1010/telefonos
c GET /persona/$db/1010/estudios
c GET /profesion/$db/count
c DELETE /telefono/$db/3000000001
c DELETE /estudio/$db/1010/50
c POST /telefono "{\"number\":\"3000000003\",\"company\":\"Tigo\",\"ownerId\":\"1010\",\"database\":\"$db\"}"
c DELETE /persona/$db/1010
c GET /telefono/$db/3000000003
c DELETE /profesion/$db/50
c DELETE /persona/$db/1010
c GET /persona/OTRA
done
