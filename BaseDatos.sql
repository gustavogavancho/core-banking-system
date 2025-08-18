-- BaseDatos.sql - Esquema MySQL para Core Banking System
-- Compatible con las entidades JPA de los módulos client y account

-- Crear base de datos (usa backticks por el guion en el nombre)
CREATE DATABASE IF NOT EXISTS `core-banking-system`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE `core-banking-system`;

-- Tabla: person
-- Entidad: com.swiftline.client.infrastructure.persistence.entity.PersonEntity
DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `gender` VARCHAR(255) NULL,
  `age` INT NULL,
  `identification` VARCHAR(255) NULL,
  `address` VARCHAR(255) NULL,
  `phone_number` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: client (PK compartida con person.id)
-- Entidad: com.swiftline.client.infrastructure.persistence.entity.ClientEntity
DROP TABLE IF EXISTS `client`;
CREATE TABLE `client` (
  `id` BIGINT NOT NULL,
  `password` VARCHAR(255) NULL,
  `status` TINYINT(1) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_client_person` FOREIGN KEY (`id`) REFERENCES `person` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: account
-- Entidad: com.swiftline.account.infrastructure.persistence.entity.AccountEntity
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account_number` VARCHAR(255) NOT NULL,
  `account_type` VARCHAR(255) NOT NULL,
  `initial_balance` DECIMAL(19,2) NOT NULL,
  `status` TINYINT(1) NOT NULL,
  `client_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_account_number` (`account_number`),
  KEY `idx_account_client_id` (`client_id`),
  CONSTRAINT `fk_account_client` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: account_transaction
-- Entidad: com.swiftline.account.infrastructure.persistence.entity.TransactionEntity
DROP TABLE IF EXISTS `account_transaction`;
CREATE TABLE `account_transaction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `date` DATETIME(6) NOT NULL,
  `transaction_type` VARCHAR(255) NOT NULL,
  `amount` DECIMAL(19,2) NOT NULL,
  `balance` DECIMAL(19,2) NOT NULL,
  `account_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tx_account_id` (`account_id`),
  KEY `idx_tx_date` (`date`),
  CONSTRAINT `fk_tx_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices adicionales para optimización
CREATE INDEX `idx_person_identification` ON `person` (`identification`);
CREATE INDEX `idx_client_status` ON `client` (`status`);
CREATE INDEX `idx_account_status` ON `account` (`status`);
CREATE INDEX `idx_tx_type` ON `account_transaction` (`transaction_type`);

-- Datos de ejemplo (opcional)
-- Insertar persona de ejemplo
INSERT INTO `person` (`name`, `gender`, `age`, `identification`, `address`, `phone_number`)
VALUES ('Juan Pérez', 'M', 30, 'CC-12345678', 'Calle 123 #45-67', '555-1234');

-- Insertar cliente de ejemplo (usando el ID de la persona)
INSERT INTO `client` (`id`, `password`, `status`)
VALUES (LAST_INSERT_ID(), 'password123', TRUE);

-- Insertar cuenta de ejemplo
INSERT INTO `account` (`account_number`, `account_type`, `initial_balance`, `status`, `client_id`)
VALUES ('0000000001-ABCDEF', 'AHORROS', 0.00, TRUE, LAST_INSERT_ID());

-- Fin del script
