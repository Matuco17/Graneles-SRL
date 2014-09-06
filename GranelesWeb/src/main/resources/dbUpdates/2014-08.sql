CREATE TABLE `mov_cta_cte_tons` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `empresa` bigint(20) NOT NULL,
  `valor` decimal(19,2) NOT NULL,
  `observaciones` varchar(256) DEFAULT NULL,
  `carga_turno` bigint(20) DEFAULT NULL,
  `tipo` int(11) DEFAULT NULL,
  `manual` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_mov_cta_cte_tons_1` (`empresa`),
  KEY `fk_mov_cta_cte_tons_2` (`tipo`),
  KEY `fk_mov_cta_cte_tons_3` (`carga_turno`),
  CONSTRAINT `fk_mov_cta_cte_tons_1` FOREIGN KEY (`empresa`) REFERENCES `empresa` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_mov_cta_cte_tons_2` FOREIGN KEY (`tipo`) REFERENCES `tipo_jornal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_mov_cta_cte_tons_3` FOREIGN KEY (`carga_turno`) REFERENCES `carga_turno` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8;

ALTER TABLE `graneles`.`embarque_cargadores` 
ADD COLUMN `es_cliente` TINYINT(1) NULL DEFAULT 1 AFTER `mercaderia`;

ALTER TABLE `graneles`.`embarque` 
ADD COLUMN `estibamos_nosotros` TINYINT(1) NULL DEFAULT 1 AFTER `facturado`;

ALTER TABLE `graneles`.`mov_cta_cte` 
DROP FOREIGN KEY `fk_mov_cta_cte_4`;

ALTER TABLE `graneles`.`mov_cta_cte` 
DROP COLUMN `tipo_valor`,
DROP INDEX `fk_mov_cta_cte_4_idx` ;