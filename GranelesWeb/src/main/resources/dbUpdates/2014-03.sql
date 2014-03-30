#ISSUE 98

ALTER TABLE `graneles`.`concepto_recibo` 
ADD COLUMN `codigo` VARCHAR(3) NULL DEFAULT NULL AFTER `oficial`;

UPDATE graneles.concepto_recibo SET codigo="101" where id= 1;
UPDATE graneles.concepto_recibo SET codigo="201" where id= 2;
UPDATE graneles.concepto_recibo SET codigo="202" where id= 3;
UPDATE graneles.concepto_recibo SET codigo="204" where id= 4;
UPDATE graneles.concepto_recibo SET codigo="301" where id= 5;
UPDATE graneles.concepto_recibo SET codigo="205" where id= 6;
UPDATE graneles.concepto_recibo SET codigo="206" where id= 7;
UPDATE graneles.concepto_recibo SET codigo="102" where id= 8;
UPDATE graneles.concepto_recibo SET codigo="103" where id= 9;
UPDATE graneles.concepto_recibo SET codigo="104" where id= 10;
UPDATE graneles.concepto_recibo SET codigo="105" where id= 11;
UPDATE graneles.concepto_recibo SET codigo="106" where id= 12;
UPDATE graneles.concepto_recibo SET codigo="107" where id= 13;
UPDATE graneles.concepto_recibo SET codigo="104" where id= 14;
UPDATE graneles.concepto_recibo SET codigo="105" where id= 15;
UPDATE graneles.concepto_recibo SET codigo="106" where id= 16;
UPDATE graneles.concepto_recibo SET codigo="107" where id= 17;
UPDATE graneles.concepto_recibo SET codigo="102" where id= 18;
UPDATE graneles.concepto_recibo SET codigo="103" where id= 19;
UPDATE graneles.concepto_recibo SET codigo="201" where id= 20;
UPDATE graneles.concepto_recibo SET codigo="202" where id= 21;
UPDATE graneles.concepto_recibo SET codigo="203" where id= 22;
UPDATE graneles.concepto_recibo SET codigo="401" where id= 23;
UPDATE graneles.concepto_recibo SET codigo="402" where id= 24;
UPDATE graneles.concepto_recibo SET codigo="403" where id= 25;
UPDATE graneles.concepto_recibo SET codigo="108" where id= 26;
UPDATE graneles.concepto_recibo SET codigo="108" where id= 27;
UPDATE graneles.concepto_recibo SET codigo="109" where id= 28;

#ISSUE 91

CREATE TABLE `graneles`.`movctacte_factura` (
  `movimiento_cta_cta` BIGINT(20) NOT NULL,
  `factura` BIGINT(20) NOT NULL,
  PRIMARY KEY (`movimiento_cta_cta`, `factura`),
  INDEX `mov_cta_cte_factura_fk_fact_idx` (`factura` ASC),
  CONSTRAINT `mov_cta_cte_factura_fk_mov`
    FOREIGN KEY (`movimiento_cta_cta`)
    REFERENCES `graneles`.`mov_cta_cte` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `mov_cta_cte_factura_fk_fact`
    FOREIGN KEY (`factura`)
    REFERENCES `graneles`.`factura` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

ALTER TABLE `graneles`.`factura` 
    ADD COLUMN `pagada` TINYINT(1) NULL DEFAULT 0 AFTER `auditoria`;

