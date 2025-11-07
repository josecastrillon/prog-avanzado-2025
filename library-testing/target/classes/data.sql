-- ============================================
-- Script SQL para cargar datos iniciales en H2
-- Se ejecuta automáticamente al iniciar la aplicación
-- ============================================

-- ============================================
-- DATOS DE EJEMPLO: BOOKS
-- ============================================
-- Nota: H2 genera automáticamente IDs con IDENTITY, así que no insertamos el ID manualmente

INSERT INTO book (title, author, isbn, available_copies, total_copies) VALUES
('Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', '978-0132350884', 5, 5),
('The Pragmatic Programmer: Your Journey to Mastery', 'David Thomas, Andrew Hunt', '978-0135957059', 3, 5),
('Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', '978-0201633610', 2, 3),
('Refactoring: Improving the Design of Existing Code', 'Martin Fowler', '978-0134757599', 4, 4),
('Domain-Driven Design: Tackling Complexity in the Heart of Software', 'Eric Evans', '978-0321125215', 1, 3),
('Head First Design Patterns', 'Eric Freeman, Elisabeth Robson', '978-0596007126', 6, 6),
('Test Driven Development: By Example', 'Kent Beck', '978-0321146530', 2, 2),
('Clean Architecture', 'Robert C. Martin', '978-0134494166', 3, 3);

-- ============================================
-- DATOS DE EJEMPLO: USERS
-- ============================================
-- Nota: La entidad User usa @Table(name = "library_user")
INSERT INTO library_user (name, email, has_fines) VALUES
('Juan Pérez', 'juan.perez@email.com', false),
('María López', 'maria.lopez@email.com', true),
('Carlos García', 'carlos.garcia@email.com', false),
('Ana Martínez', 'ana.martinez@email.com', false),
('Luis Rodríguez', 'luis.rodriguez@email.com', false);

-- ============================================
-- DATOS DE EJEMPLO: LOANS
-- ============================================
-- Préstamos activos (book_id y user_id se refieren a los IDs autogenerados arriba)
INSERT INTO loan (book_id, user_id, loan_date, return_date, active) VALUES
(1, 1, '2024-01-15', NULL, true),
(3, 3, '2024-01-18', NULL, true),
(5, 4, '2024-01-20', NULL, true);

-- Préstamos devueltos (históricos)
INSERT INTO loan (book_id, user_id, loan_date, return_date, active) VALUES
(2, 1, '2024-01-10', '2024-01-25', false),
(4, 5, '2024-01-12', '2024-01-26', false);
