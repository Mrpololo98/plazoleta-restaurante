# Ayuda rápida

Guía breve para montar el microservicio en tu máquina. Revisa esta lista antes
de perder tiempo con un error de arranque.

## Importar en IntelliJ IDEA

1. `File → Open...` y selecciona la carpeta raíz del proyecto (la que contiene `pom.xml`).
2. Espera a que IntelliJ sincronice Maven. Si no arranca solo: botón derecho
   sobre `pom.xml → Add as Maven Project`.
3. Confirma que el JDK del proyecto es 17 en `File → Project Structure → Project`.

## Variables de entorno

Configura `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `APP_JWT_SECRET` antes de
ejecutar; el `APP_JWT_SECRET` debe ser EXACTAMENTE el mismo del microservicio
de usuarios. En IntelliJ: `Run → Edit Configurations... → Environment variables`.
En PowerShell:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/plazoleta_restaurante"
$env:DB_USERNAME="plazoleta"
$env:DB_PASSWORD="plazoleta"
$env:APP_JWT_SECRET="Plazoleta2026*Seguro-Compartido-Entre-Micros*OK"
$env:USUARIO_API_URL="http://localhost:8080"
```

## Problemas comunes

| Síntoma | Causa probable | Solución |
|---|---|---|
| `Access denied for user` al arrancar | El usuario de MySQL no tiene permisos sobre `plazoleta_restaurante` | `GRANT ALL PRIVILEGES ON plazoleta_restaurante.* TO 'plazoleta'@'%'; FLUSH PRIVILEGES;` |
| `Port 8081 was already in use` | Otro proceso ocupa el puerto | Libera el puerto o cambia `server.port` |
| `Failed to configure a DataSource` | Falta alguna variable `DB_*` | Define las tres variables `DB_*` en la configuración de ejecución |
| 401 en cada petición | El `APP_JWT_SECRET` difiere del de usuarios | Usa el mismo secreto en los dos microservicios |
| 404 al validar propietarios | El microservicio de usuarios no está arriba o su base está vacía | Arranca primero usuarios con su base creada |
| `Public Key Retrieval is not allowed` | MySQL 8 sobre SSL no verificado | Agrega `allowPublicKeyRetrieval=true` a la cadena JDBC |

## Pruebas sin MySQL

Los tests corren sobre H2 en memoria (`ddl-auto=create-drop`); no necesitan
ninguna base de datos externa ni el microservicio de usuarios:

```bash
./mvnw test
```
