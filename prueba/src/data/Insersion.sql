-- 1. Asegúrate de estar en la base de datos correcta
USE InventarioVideojuegosDB;
GO

-- 2. Insertamos 15 registros de una sola vez.
-- No especificamos 'VideojuegoID' porque es IDENTITY (se genera solo).
INSERT INTO Videojuegos
    (Titulo, Genero, Plataforma, Stock, Precio)
VALUES
    ('Elden Ring', 'RPG de Acción', 'PC', 50, 59.99),
    ('Baldur''s Gate 3', 'RPG', 'PC', 40, 59.99),
    ('The Legend of Zelda: Tears of the Kingdom', 'Aventura', 'Switch', 60, 69.99),
    ('Marvel''s Spider-Man 2', 'Acción', 'PS5', 30, 69.99),
    ('Starfield', 'RPG', 'Xbox Series X', 25, 59.99),
    ('Cyberpunk 2077', 'RPG de Acción', 'PC', 35, 39.99),
    ('Hogwarts Legacy', 'Aventura', 'PS5', 45, 69.99),
    ('God of War Ragnarök', 'Acción', 'PS5', 20, 59.99),
    ('Forza Motorsport', 'Carreras', 'Xbox Series X', 15, 69.99),
    ('Animal Crossing: New Horizons', 'Simulación', 'Switch', 50, 49.99),
    ('Call of Duty: Modern Warfare III', 'Shooter', 'PC', 70, 69.99),
    ('Resident Evil 4 (Remake)', 'Survival Horror', 'PS5', 22, 59.99),
    ('Hades', 'Roguelike', 'PC', 30, 24.99),
    ('Red Dead Redemption 2', 'Aventura', 'PC', 18, 39.99),
    ('Super Mario Bros. Wonder', 'Plataformas', 'Switch', 40, 59.99);
GO

PRINT '¡15 videojuegos insertados exitosamente!';
GO

-- 3. ¡Veámoslos!
-- Puedes usar tu procedimiento almacenado para verlos:
EXEC sp_ObtenerVideojuegos;

-- O puedes usar una consulta SELECT simple:
-- SELECT * FROM Videojuegos;
-- GO