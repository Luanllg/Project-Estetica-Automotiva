-- =====================================================================
-- CRUD Manager + Estética Automotiva
-- ATENÇÃO: este script recria o banco do zero (DROP DATABASE).
-- Login de teste: emerson@mail.com / 123456 (todos os usuários usam 123456)
-- =====================================================================
DROP DATABASE IF EXISTS crud_manager;

CREATE DATABASE IF NOT EXISTS crud_manager
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE crud_manager;

-- ---------------------------------------------------------------------
-- Tabelas do projeto base
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    sexo ENUM('M', 'F'),
    email VARCHAR(150) NOT NULL UNIQUE,   -- UNIQUE: o e-mail é o login
    senha CHAR(64) NOT NULL               -- hash SHA-256 em hexadecimal (64 caracteres)
);

CREATE TABLE IF NOT EXISTS posts(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    post_date DATE NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id)
    REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS companies (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(128) NOT NULL,
    `role` VARCHAR(128) NOT NULL,
    `start` DATE NOT NULL,
    `end` DATE,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id)
    REFERENCES users(id)
);

-- ---------------------------------------------------------------------
-- NOVO CRUD 1: Veículos (FK -> users)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS vehicles (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(100) NOT NULL,                                    -- input text
    model_year INT NOT NULL,                                        -- input number
    color CHAR(7) NOT NULL,                                         -- input color (#RRGGBB)
    category ENUM('HATCH','SEDAN','SUV','PICKUP') NOT NULL,         -- input radio
    ceramic_coating BOOLEAN NOT NULL DEFAULT FALSE,                 -- input checkbox
    user_id INT NOT NULL,                                           -- select (FK)
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ---------------------------------------------------------------------
-- NOVO CRUD 2: Serviços de estética (FK -> vehicles)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS services (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    service_type ENUM('LAVAGEM','POLIMENTO','VITRIFICACAO','HIGIENIZACAO') NOT NULL, -- input radio
    service_date DATE NOT NULL,                                     -- input date
    price DECIMAL(10,2) NOT NULL,                                   -- input number
    details TEXT,                                                   -- textarea
    finished BOOLEAN NOT NULL DEFAULT FALSE,                        -- input checkbox
    vehicle_id INT NOT NULL,                                        -- select (FK)
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- ---------------------------------------------------------------------
-- Dados de exemplo (a ordem respeita as chaves estrangeiras)
-- ---------------------------------------------------------------------
INSERT INTO users VALUES
(DEFAULT, "Emerson Carvalho", "M", "emerson@mail.com", SHA2('123456', 256)),
(DEFAULT, "Luiza Carvalho", "F", "lu@mail.com", SHA2('123456', 256)),
(DEFAULT, "Elenice Carvalho", "F", "le@mail.com", SHA2('123456', 256)),
(DEFAULT, "Noé Carvalho", "M", "noe@mail.com", SHA2('123456', 256)),
(DEFAULT, "Rosânia Carvalho", "F", "ro@mail.com", SHA2('123456', 256));

INSERT INTO posts VALUES
(DEFAULT, "Olá do Emerson", CURDATE(), 1),
(DEFAULT, "Olá da Luiza", CURDATE(), 2),
(DEFAULT, "Olá da Denise", CURDATE(), 3),
(DEFAULT, "Olá do Noé", CURDATE(), 4),
(DEFAULT, "Olá da Rosânia", CURDATE(), 5),
(DEFAULT, "Olá da Rosânia 1", CURDATE(), 5),
(DEFAULT, "Olá da Rosânia 2", CURDATE(), 5),
(DEFAULT, "Olá da Rosânia 3", CURDATE(), 5);

INSERT INTO companies VALUES
(DEFAULT, "Oficina Central", "Mecânico", '2018-03-01', '2020-06-30', 1),
(DEFAULT, "Auto Brilho", "Detalhador", '2020-07-01', NULL, 1),
(DEFAULT, "Lava Rápido Sul", "Atendente", '2019-01-15', '2021-12-31', 2),
(DEFAULT, "Pneus & Cia", "Vendedora", '2017-05-10', NULL, 3),
(DEFAULT, "Concessionária Machado", "Consultor", '2021-02-01', NULL, 4),
(DEFAULT, "Funilaria Mineira", "Pintor", '2016-08-20', '2019-08-19', 5),
(DEFAULT, "Estética Premium", "Gerente", '2022-01-10', NULL, 5);

INSERT INTO vehicles VALUES
(DEFAULT, 'Honda Civic', 2019, '#1F1F1F', 'SEDAN', TRUE, 1),
(DEFAULT, 'VW Gol', 2015, '#C0C0C0', 'HATCH', FALSE, 2),
(DEFAULT, 'Toyota Hilux', 2022, '#FFFFFF', 'PICKUP', TRUE, 3),
(DEFAULT, 'Jeep Compass', 2021, '#8B0000', 'SUV', TRUE, 4),
(DEFAULT, 'Fiat Argo', 2020, '#0047AB', 'HATCH', FALSE, 5),
(DEFAULT, 'Chevrolet Onix', 2018, '#FF8C00', 'HATCH', FALSE, 1),
(DEFAULT, 'Hyundai Creta', 2023, '#2F4F4F', 'SUV', TRUE, 2);

INSERT INTO services VALUES
(DEFAULT, 'POLIMENTO', CURDATE(), 450.00, 'Polimento técnico em 3 etapas', FALSE, 1),
(DEFAULT, 'LAVAGEM', CURDATE(), 80.00, 'Lavagem detalhada com cera', TRUE, 2),
(DEFAULT, 'VITRIFICACAO', CURDATE(), 1800.00, 'Vitrificação cerâmica de pintura, garantia de 2 anos', FALSE, 3),
(DEFAULT, 'HIGIENIZACAO', CURDATE(), 220.00, 'Higienização interna completa dos bancos e carpete', TRUE, 4),
(DEFAULT, 'LAVAGEM', CURDATE(), 60.00, 'Lavagem simples', TRUE, 5),
(DEFAULT, 'POLIMENTO', CURDATE(), 380.00, 'Polimento comparativo e remoção de riscos leves', FALSE, 6),
(DEFAULT, 'VITRIFICACAO', CURDATE(), 1500.00, 'Vitrificação de pintura e vidros', TRUE, 7);
