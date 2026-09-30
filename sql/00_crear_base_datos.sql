-- Ejecutar conectado como usuario administrador postgres.

CREATE USER tienda_app
WITH PASSWORD 'tienda1234';

CREATE DATABASE tienda_ud2;

GRANT CONNECT
ON DATABASE tienda_ud2
TO tienda_app;

-- Después de ejecutar este script:
-- 1. Conectarse a tienda_ud2.
-- 2. Ejecutar 01_crear_esquema.sql.
