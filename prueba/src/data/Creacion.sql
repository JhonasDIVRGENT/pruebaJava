/*
================================================================================
 SCRIPT: INVENTARIO DE VIDEOJUEGOS
 DESCRIPCIÓN: Crea la tabla de inventario y los SPs para el CRUD.
================================================================================
*/

-- 1. CREACIÓN DE LA BASE DE DATOS (Opcional)
-- Si prefieres usar una base de datos existente, comenta o elimina esta sección
-- y asegúrate de ejecutar 'USE TuBaseDeDatosExistente;'

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'InventarioVideojuegosDB')
BEGIN
    CREATE DATABASE InventarioVideojuegosDB;
    PRINT 'Base de datos [InventarioVideojuegosDB] creada.';
END
GO

USE InventarioVideojuegosDB;
GO

-- 2. CREACIÓN DE LA TABLA DE VIDEOJUEGOS
-- Creamos la tabla si no existe

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Videojuegos' and xtype='U')
BEGIN
    CREATE TABLE Videojuegos (
        VideojuegoID INT PRIMARY KEY IDENTITY(1,1), -- ID único autoincremental
        Titulo NVARCHAR(150) NOT NULL,
        Genero NVARCHAR(50),
        Plataforma NVARCHAR(50),
        Stock INT NOT NULL DEFAULT 0,
        Precio DECIMAL(10, 2) NOT NULL -- Ej: 12345678.99
    );
    PRINT 'Tabla [Videojuegos] creada exitosamente.';
END
ELSE
BEGIN
    PRINT 'La tabla [Videojuegos] ya existe.';
END
GO

---
-- 3. CREACIÓN DE PROCEDIMIENTOS ALMACENADOS (CRUD)
---

-- ==== CREATE (Insertar nuevo videojuego) ====
IF OBJECT_ID('sp_InsertarVideojuego', 'P') IS NOT NULL
    DROP PROCEDURE sp_InsertarVideojuego;
GO

CREATE PROCEDURE sp_InsertarVideojuego
    @Titulo NVARCHAR(150),
    @Genero NVARCHAR(50),
    @Plataforma NVARCHAR(50),
    @Stock INT,
    @Precio DECIMAL(10, 2)
AS
BEGIN
    INSERT INTO Videojuegos (Titulo, Genero, Plataforma, Stock, Precio)
    VALUES (@Titulo, @Genero, @Plataforma, @Stock, @Precio);
END
GO
PRINT 'SP [sp_InsertarVideojuego] creado.';
GO

-- ==== READ (Obtener todos los videojuegos) ====
IF OBJECT_ID('sp_ObtenerVideojuegos', 'P') IS NOT NULL
    DROP PROCEDURE sp_ObtenerVideojuegos;
GO

CREATE PROCEDURE sp_ObtenerVideojuegos
AS
BEGIN
    SELECT VideojuegoID, Titulo, Genero, Plataforma, Stock, Precio
    FROM Videojuegos;
END
GO
PRINT 'SP [sp_ObtenerVideojuegos] creado.';
GO

-- ==== READ (Obtener un videojuego por ID) ====
IF OBJECT_ID('sp_ObtenerVideojuegoPorID', 'P') IS NOT NULL
    DROP PROCEDURE sp_ObtenerVideojuegoPorID;
GO

CREATE PROCEDURE sp_ObtenerVideojuegoPorID
    @VideojuegoID INT
AS
BEGIN
    SELECT VideojuegoID, Titulo, Genero, Plataforma, Stock, Precio
    FROM Videojuegos
    WHERE VideojuegoID = @VideojuegoID;
END
GO
PRINT 'SP [sp_ObtenerVideojuegoPorID] creado.';
GO

-- ==== UPDATE (Actualizar un videojuego) ====
IF OBJECT_ID('sp_ActualizarVideojuego', 'P') IS NOT NULL
    DROP PROCEDURE sp_ActualizarVideojuego;
GO

CREATE PROCEDURE sp_ActualizarVideojuego
    @VideojuegoID INT,
    @Titulo NVARCHAR(150),
    @Genero NVARCHAR(50),
    @Plataforma NVARCHAR(50),
    @Stock INT,
    @Precio DECIMAL(10, 2)
AS
BEGIN
    UPDATE Videojuegos
    SET
        Titulo = @Titulo,
        Genero = @Genero,
        Plataforma = @Plataforma,
        Stock = @Stock,
        Precio = @Precio
    WHERE
        VideojuegoID = @VideojuegoID;
END
GO
PRINT 'SP [sp_ActualizarVideojuego] creado.';
GO

-- ==== DELETE (Eliminar un videojuego) ====
IF OBJECT_ID('sp_EliminarVideojuego', 'P') IS NOT NULL
    DROP PROCEDURE sp_EliminarVideojuego;
GO

CREATE PROCEDURE sp_EliminarVideojuego
    @VideojuegoID INT
AS
BEGIN
    DELETE FROM Videojuegos
    WHERE VideojuegoID = @VideojuegoID;
END
GO
PRINT 'SP [sp_EliminarVideojuego] creado.';
GO

PRINT '=== Script completado ===';
GO

---
-- 4. EJEMPLOS DE CÓMO USAR LOS PROCEDURES
-- (Puedes ejecutar estas líneas una por una para probar)
---

/*
-- Insertar juegos
EXEC sp_InsertarVideojuego 'Elden Ring', 'RPG de Acción', 'PC', 10, 59.99;
EXEC sp_InsertarVideojuego 'Cyberpunk 2077', 'RPG de Acción', 'PS5', 5, 49.99;
EXEC sp_InsertarVideojuego 'Stardew Valley', 'Simulación', 'Switch', 20, 14.99;

-- Ver todos los juegos
EXEC sp_ObtenerVideojuegos;

-- Ver un juego específico (ID 1)
EXEC sp_ObtenerVideojuegoPorID 1;

-- Actualizar un juego (Cambiamos el stock y precio del ID 1)
EXEC sp_ActualizarVideojuego 1, 'Elden Ring - GOTY', 'RPG de Acción', 'PC', 12, 69.99;

-- Ver todos para confirmar el cambio
EXEC sp_ObtenerVideojuegos;

-- Eliminar un juego (ID 2)
EXEC sp_EliminarVideojuego 2;

-- Ver todos para confirmar la eliminación
EXEC sp_ObtenerVideojuegos;
*/