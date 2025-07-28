CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                                  UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);

CREATE TABLE `jv_jdbc_taxi`.`cars` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `model` VARCHAR(255) NOT NULL,
                                       `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                       `manufacturer_id` BIGINT NOT NULL,
                                       PRIMARY KEY (`id`),
                                       UNIQUE INDEX `model_UNIQUE` (`model` ASC) VISIBLE);
CREATE TABLE `cars_drivers` (
                                `car_id` bigint DEFAULT NULL,
                                `driver_id` bigint DEFAULT NULL,
                                KEY `car_id_fk` (`car_id`),
                                KEY `driver_id_fk_idx` (`driver_id`),
                                CONSTRAINT `car_id_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `driver_id_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
