-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema aste
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `aste` ;

-- -----------------------------------------------------
-- Schema aste
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `aste` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `aste` ;

-- -----------------------------------------------------
-- Table `aste`.`categoria`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `aste`.`categoria` (
  `Nome` VARCHAR(50) NOT NULL,
  `Livello` INT NOT NULL,
  `CategoriaSuperiore` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`Nome`),
  INDEX `fk_supcategoria_idx` (`CategoriaSuperiore` ASC) COMMENT '\'\'\'evita full scan sulla tabella per ricercare tutte le categorie figlie di una data categoria padre\'\'\'' VISIBLE,
  CONSTRAINT `fk_supcategoria`
    FOREIGN KEY (`CategoriaSuperiore`)
    REFERENCES `aste`.`categoria` (`Nome`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
COMMENT = '	';


-- -----------------------------------------------------
-- Table `aste`.`utenteamministratore`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `aste`.`utenteamministratore` (
  `CF` CHAR(16) NOT NULL,
  `Nome` VARCHAR(30) NOT NULL,
  `Cognome` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`CF`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
COMMENT = '	';


-- -----------------------------------------------------
-- Table `aste`.`cartadicredito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `aste`.`cartadicredito` (
  `Numero` CHAR(64) NOT NULL COMMENT 'uso SHA2 per questo motivo ho 64 caratteri da memorizzare anziché 16',
  `CVV` CHAR(3) NOT NULL,
  `DataScadenza` DATE NOT NULL,
  PRIMARY KEY (`Numero`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
COMMENT = '			';


-- -----------------------------------------------------
-- Table `aste`.`utentebase`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `aste`.`utentebase` (
  `CF` CHAR(16) NOT NULL,
  `Nome` VARCHAR(30) NOT NULL,
  `Cognome` VARCHAR(30) NOT NULL,
  `Indirizzo` VARCHAR(45) NOT NULL,
  `CAP` CHAR(5) NOT NULL,
  `Città` VARCHAR(45) NOT NULL,
  `DataNascita` DATE NOT NULL,
  `CittàNascita` VARCHAR(45) NOT NULL,
  `CartaCredito` CHAR(64) NOT NULL COMMENT 'utilizzo SHA2 quindi ho 64 caratteri memorizzati anziché 16 canonici',
  PRIMARY KEY (`CF`),
  UNIQUE INDEX `CartaCredito_UNIQUE` (`CartaCredito` ASC) VISIBLE,
  INDEX `fk_cartadicredito_utentebase_idx` (`CartaCredito` ASC) VISIBLE,
  CONSTRAINT `fk_cartadicredito_utentebase`
    FOREIGN KEY (`CartaCredito`)
    REFERENCES `aste`.`cartadicredito` (`Numero`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `aste`.`asta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `aste`.`asta` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `Dimensioni` VARCHAR(15) NOT NULL,
  `Data` DATETIME NOT NULL,
  `Durata` INT NOT NULL,
  `Descrizione` VARCHAR(128) NOT NULL,
  `PrezzoBase` DECIMAL(7,2) NOT NULL,
  `StatoAsta` VARCHAR(10) NOT NULL,
  `NumOfferte` INT NOT NULL DEFAULT '0',
  `OffertaMassima` DECIMAL(7,2) NOT NULL DEFAULT '0.00',
  `CondizioniArticolo` VARCHAR(45) NOT NULL,
  `Categoria` VARCHAR(50) NOT NULL DEFAULT 'Default',
  `UtenteAmministratore` CHAR(16) NOT NULL,
  `UtenteBase` CHAR(16) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `fk_categoria_idx` (`Categoria` ASC) INVISIBLE,
  INDEX `fk_utenteamministratore_idx` (`UtenteAmministratore` ASC) VISIBLE,
  INDEX `idx_asta_categoria` (`Categoria` ASC) VISIBLE,
  INDEX `idx_asta_utente_stato` (`UtenteBase` ASC, `StatoAsta` ASC) VISIBLE,
  INDEX `idx_asta_amministratore_stato` (`UtenteAmministratore` ASC, `StatoAsta` ASC) VISIBLE,
  INDEX `idx_asta_stato` (`StatoAsta` ASC) VISIBLE,
  CONSTRAINT `fk_categoria_asta`
    FOREIGN KEY (`Categoria`)
    REFERENCES `aste`.`categoria` (`Nome`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_utenteamministratore_asta`
    FOREIGN KEY (`UtenteAmministratore`)
    REFERENCES `aste`.`utenteamministratore` (`CF`),
  CONSTRAINT `fk_utentebase_asta`
    FOREIGN KEY (`UtenteBase`)
    REFERENCES `aste`.`utentebase` (`CF`))
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `aste`.`login`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `aste`.`login` (
  `username` VARCHAR(50) NOT NULL,
  `password` CHAR(64) NOT NULL COMMENT 'utilizzo la cifratura SHA2 a 256 bit che restituisce una password cifrata da 64 caratteri',
  `ruolo` ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
  `CF` CHAR(16) NOT NULL COMMENT 'voglio gestire utente diversi con ruoli distinti, perciò mantengo un legame tra le tabella tramite CF salvato anche nella tabella di autenticazione anziché scrivere lo username nelle tabelle degli utenti',
  PRIMARY KEY (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
COMMENT = '	';


-- -----------------------------------------------------
-- Table `aste`.`offerta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `aste`.`offerta` (
  `UtenteBase` CHAR(16) NOT NULL,
  `Asta` INT NOT NULL,
  `Data` DATE NOT NULL,
  `Ora` TIME NOT NULL,
  `Importo` DECIMAL(7,2) NOT NULL,
  `Automatica` TINYINT NOT NULL DEFAULT '0',
  `ImportoControfferta` DECIMAL(7,2) NULL DEFAULT NULL,
  PRIMARY KEY (`UtenteBase`, `Asta`, `Data`, `Ora`),
  INDEX `idx_offerta_asta_importo` (`Asta` ASC, `Importo` DESC, `Data` DESC, `Ora` DESC) INVISIBLE,
  INDEX `idx_offerta_utente_asta` (`UtenteBase` ASC, `Asta` ASC) VISIBLE,
  INDEX `idx_offerta_asta_controfferta` (`Asta` ASC, `ImportoControfferta` ASC) VISIBLE,
  CONSTRAINT `fk_asta_off`
    FOREIGN KEY (`Asta`)
    REFERENCES `aste`.`asta` (`ID`),
  CONSTRAINT `fk_utentebase_off`
    FOREIGN KEY (`UtenteBase`)
    REFERENCES `aste`.`utentebase` (`CF`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
COMMENT = '		';

USE `aste` ;

-- -----------------------------------------------------
-- Placeholder table for view `aste`.`vista_aste_attive`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `aste`.`vista_aste_attive` (`ID` INT, `Descrizione` INT, `Dimensioni` INT, `Data` INT, `Durata` INT, `Categoria` INT, `PrezzoBase` INT, `StatoAsta` INT, `NumOfferte` INT, `OffertaMassima` INT, `CondizioniArticolo` INT, `UtenteAmministratore` INT, `TempoRimanenteSec` INT);

-- -----------------------------------------------------
-- procedure aggiorna_indirizzo_consegna
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `aggiorna_indirizzo_consegna`(
	in var_cf CHAR(16),
    in var_indirizzo VARCHAR(45),
    in var_citta VARCHAR(45),
    in var_cap CHAR(5),
    out result INT
)
BEGIN
	declare righe_aggiornate int;
    declare exit handler for sqlexception
    begin
		rollback;
        set result = 0;
	end;
    
    set transaction isolation level read committed;
    start transaction;

    update utentebase
    set 
        Indirizzo = var_indirizzo,
        Città = var_citta,
        CAP = var_cap
    where CF = var_cf;

    set righe_aggiornate = row_count();

    if righe_aggiornate > 0 then
		commit;
        set result = 1; -- aggiornamento riuscito
    else
		rollback;
        set result = 0; -- nessun utente trovato con quel CF
    end if;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure aggiungiCat
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `aggiungiCat`(
	in var_nome VARCHAR(50),
    in var_livello INT,
    in var_catsup VARCHAR(50)
)
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

	INSERT INTO categoria (
        Nome, Livello, CategoriaSuperiore
    )
    VALUES (
        var_nome, var_livello, var_catsup
    );
    
    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure aggiungiOfferta
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `aggiungiOfferta`(
    IN var_utenteBase CHAR(16),
    IN var_idAsta INT,
    IN var_importo DECIMAL(7,2),
    IN var_automatica BOOLEAN,
    IN var_maxControfferta DECIMAL(7,2)
)
BEGIN
    DECLARE incremento DECIMAL(7,2) DEFAULT 0.50;
    DECLARE miglior_attuale CHAR(16);
    DECLARE offerta_massima DECIMAL(7,2);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
    START TRANSACTION;
    
    -- controllo se è già il miglior offerente
    SELECT UtenteBase INTO miglior_attuale
    FROM asta
    WHERE ID = var_idAsta;
    
    IF miglior_attuale = var_utenteBase THEN
        SIGNAL SQLSTATE '45900'
        SET MESSAGE_TEXT = 'Sei già il miglior offerente e non puoi rilanciare.';
    END IF;
    
    -- inserimento dell'offerta
    INSERT INTO offerta (UtenteBase, Asta, Data, Ora, Importo, Automatica, ImportoControfferta)
    VALUES (var_utenteBase, var_idAsta, CURDATE(), CURRENT_TIME(6), var_importo, var_automatica, var_maxControfferta);
    
    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure asteVinteUtente
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `asteVinteUtente`(
    IN var_cf CHAR(16)
)
BEGIN
    SELECT ID, Descrizione, Dimensioni, CondizioniArticolo, Categoria, PrezzoBase, OffertaMassima, Data, Durata, NumOfferte, UtenteAmministratore
    FROM asta
    WHERE UtenteBase = var_cf AND StatoAsta = 'TERMINATA';
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure chiudiAsta
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `chiudiAsta`(
	IN var_idAsta INT
)
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

	UPDATE asta
    SET StatoAsta = 'TERMINATA'
    WHERE ID = var_idAsta AND StatoAsta = 'ATTIVA';
    
    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure creaAsta
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `creaAsta`(
    IN var_Dimensioni VARCHAR(15),
    IN var_Data DATETIME,
    IN var_Durata INT,
    IN var_Descrizione VARCHAR(128),
    IN var_PrezzoBase DECIMAL(7,2),
    IN var_StatoAsta VARCHAR(10),
    IN var_CondizioniArticolo VARCHAR(45),
    IN var_Categoria VARCHAR(50),
    IN var_UtenteAmministratore CHAR(16)
)
BEGIN
	DECLARE errno INT DEFAULT 0;
    DECLARE msg TEXT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
			errno = MYSQL_ERRNO, 
            msg = MESSAGE_TEXT;
        ROLLBACK;
        
        SIGNAL SQLSTATE '45201' SET MESSAGE_TEXT = msg;
    END;

	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

    INSERT INTO asta (
        Dimensioni, Data, Durata, Descrizione,
        PrezzoBase, StatoAsta,
        CondizioniArticolo, Categoria,
        UtenteAmministratore
    )
    VALUES (
        var_Dimensioni, var_Data, var_Durata, var_Descrizione,
        var_PrezzoBase, var_StatoAsta,
        var_CondizioniArticolo, var_Categoria,
        var_UtenteAmministratore
    );

    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure dettagli_utente_cf
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `dettagli_utente_cf`(
	in var_cf CHAR(16),
    in var_ruolo ENUM('ADMIN', 'USER')
)
BEGIN
	if var_ruolo = 'USER' then
        select * 
        from UtenteBase
        where CF = var_cf;
    else
        select * 
        from UtenteAmministratore
        where CF = var_cf;
    end if;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure eliminaCat
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `eliminaCat`(
	IN nome_categoria VARCHAR(50),
    OUT num_del INT,
    OUT num_reassign INT
)
BEGIN
	DECLARE done INT DEFAULT FALSE;
    DECLARE cat_nome VARCHAR(50);
    DECLARE default_categoria_nome VARCHAR(50) DEFAULT 'Default';
    
    -- dichiaro il cursore per poter iterare sui figli della categoria da eliminare
    DECLARE cur_figli CURSOR FOR
        SELECT Nome
        FROM categoria
        WHERE CategoriaSuperiore IN (
            SELECT Nome FROM TmpCategorieDaEliminare
        );

    -- handler per gestire le eccezioni
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
		ROLLBACK;
        RESIGNAL;
	END;
    
     -- handler per quando il cursore finisce i risultati elaborare
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- creo la tabella temporanea per le categorie da eliminare
	DROP TEMPORARY TABLE IF EXISTS TmpCategorieDaEliminare;
    CREATE TEMPORARY TABLE TmpCategorieDaEliminare (
        Nome VARCHAR(50) PRIMARY KEY
    );
    
    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
    START TRANSACTION;

    -- prima verifico che esista effettivamente la categoria di default per riassegnare le aste associate alle categorie cancellate
    IF NOT EXISTS (
        SELECT 1
        FROM Categoria
        WHERE Nome = default_categoria_nome
            AND Livello = 1
            AND CategoriaSuperiore IS NULL
    ) THEN
        SIGNAL SQLSTATE '45300'
        SET MESSAGE_TEXT = 'Categoria di default non valida o mancante';
    END IF;

    -- inserisce la categoria input nella tabella temporanea
    INSERT IGNORE INTO TmpCategorieDaEliminare VALUES (nome_categoria);

    -- loop
    REPEAT
        SET done = FALSE;

        OPEN cur_figli;
        read_loop: LOOP
            FETCH cur_figli INTO cat_nome;
            IF done THEN
                LEAVE read_loop;
            END IF;

            INSERT IGNORE INTO TmpCategorieDaEliminare VALUES (cat_nome);
        END LOOP;
        CLOSE cur_figli;

    UNTIL done END REPEAT;
    
    -- conta quante aste verranno riassegnate prima di aggiornare
    SELECT COUNT(*) INTO num_reassign
    FROM asta
    WHERE Categoria IN (SELECT Nome FROM TmpCategorieDaEliminare);

    -- riassegna le aste a "Default"
    UPDATE asta
    SET Categoria = default_categoria_nome
    WHERE Categoria IN (SELECT Nome FROM TmpCategorieDaEliminare);

    -- elimina le categorie trovate
    DELETE FROM categoria
    WHERE Nome IN (SELECT Nome FROM TmpCategorieDaEliminare);
    
    -- conta le categorie che sono state eliminate
    SELECT COUNT(*) INTO num_del FROM TmpCategorieDaEliminare;

    -- pulisce la tabella temporanea
    DROP TEMPORARY TABLE IF EXISTS TmpCategorieDaEliminare;

    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getControfferteAttive
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getControfferteAttive`(
    IN var_idAsta INT,
    IN var_migliorOfferente CHAR(16),
    IN var_importoAttuale DECIMAL(7,2)
)
BEGIN
	SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
    START TRANSACTION;

    SELECT o.*
    FROM offerta o
    INNER JOIN (
        SELECT UtenteBase, MAX(ImportoControfferta) as MaxImporto
        FROM offerta
        WHERE Asta = var_idAsta
          AND ImportoControfferta IS NOT NULL
          AND ImportoControfferta > var_importoAttuale
          AND UtenteBase <> var_migliorOfferente
        GROUP BY UtenteBase
    ) AS maxOfferte ON o.UtenteBase = maxOfferte.UtenteBase 
                   AND o.ImportoControfferta = maxOfferte.MaxImporto
                   AND o.Asta = var_idAsta
    ORDER BY o.ImportoControfferta DESC;
    
    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getMigliorOfferta
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getMigliorOfferta`(
    IN var_idAsta INT
)
BEGIN
	SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
	START TRANSACTION;

    SELECT *
    FROM offerta
    WHERE Asta = var_idAsta
    ORDER BY Importo DESC, Data DESC, Ora DESC
    LIMIT 1;
    
    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure listaAmministratori
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `listaAmministratori`()
BEGIN
	SELECT CF, Nome, Cognome
    FROM utenteamministratore;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure listaCategorie
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `listaCategorie`()
BEGIN
	SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
    START TRANSACTION;

	SELECT Nome, Livello, CategoriaSuperiore
    FROM categoria;
    
    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure login
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `login`(in var_username VARCHAR(50), in var_password VARCHAR(32), out var_ruolo ENUM('ADMIN', 'USER'), out var_CF CHAR(16))
BEGIN
	DECLARE var_check_username VARCHAR(50);
    
    SELECT username, ruolo, CF INTO var_check_username, var_ruolo, var_CF
    FROM login
    WHERE username = var_username AND password = sha2(var_password, 256);
    
    IF var_check_username IS NULL THEN
		SIGNAL SQLSTATE '45000' SET message_text = 'Credenziali di accesso errate';
	END IF;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure modificaCat
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `modificaCat`(
	IN old_nome VARCHAR(50),
    IN new_nome VARCHAR(50)
)
BEGIN
	SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
    START TRANSACTION;
    
    -- inserisco una categoria temporanea
    INSERT INTO categoria (Nome, Livello, CategoriaSuperiore) VALUES ('TEMP_CAT', 1, NULL);
    
    -- aggiorno i figli ad un valore temporaneo
    UPDATE categoria
	SET CategoriaSuperiore = 'TEMP_CAT'
	WHERE CategoriaSuperiore = old_nome;
        
	-- aggiorno il padre
	UPDATE categoria
    SET Nome = new_nome
    WHERE Nome = old_nome;
    
	-- aggiorno i figli al valore nuovo del padre
	UPDATE categoria
	SET CategoriaSuperiore = new_nome
	WHERE CategoriaSuperiore = 'TEMP_CAT';
    
    -- elimino la categoria temporanea
    DELETE FROM categoria WHERE Nome = 'TEMP_CAT';
    
    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure registerUser
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `registerUser`(
	in var_username VARCHAR(50), 
    in var_password VARCHAR(32), 
    in var_cf CHAR(16), 
    in var_nome VARCHAR(30), 
    in var_cognome VARCHAR(30), 
    in var_indirizzo VARCHAR(45), 
    in var_cap CHAR(5), 
    in var_citta VARCHAR(45), 
    in var_data_nascita DATE, 
    in var_citta_nascita VARCHAR(45), 
    in var_carta_credito VARCHAR(16), 
    in var_cvv CHAR(3), 
    in var_data_scadenza_carta DATE
)
BEGIN
	DECLARE errno INT DEFAULT 0;
    DECLARE msg TEXT;
    DECLARE hashed_cc CHAR(64);
    DECLARE hashed_password CHAR(64);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            errno = MYSQL_ERRNO,
            msg = MESSAGE_TEXT;
        ROLLBACK;
    
        -- Gestione errore di chiave duplicata
        IF errno = 1062 THEN
            IF msg LIKE '%login.UNIQUE_username%' OR msg LIKE '%login.PRIMARY%' THEN
                SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Errore: username esistente', MYSQL_ERRNO = 1062;
            ELSEIF msg LIKE '%utentebase.UNIQUE_cf%' OR msg LIKE '%utentebase.PRIMARY%' THEN
                SIGNAL SQLSTATE '45002' SET MESSAGE_TEXT = 'Errore: codice fiscale registrato', MYSQL_ERRNO = 1062;
            ELSEIF msg LIKE '%cartadicredito.UNIQUE_numero%' OR msg LIKE '%cartadicredito.PRIMARY%' THEN
                SIGNAL SQLSTATE '45003' SET MESSAGE_TEXT = 'Errore: carta di credito presente', MYSQL_ERRNO = 1062;
            ELSE
                SIGNAL SQLSTATE '45004' SET MESSAGE_TEXT = 'Errore: valore duplicato', MYSQL_ERRNO = 1062;
            END IF;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = msg, MYSQL_ERRNO = errno;
        END IF;
    END;
    
    SET hashed_cc = sha2(var_carta_credito, 256);
    SET hashed_password = sha2(var_password, 256);
    
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED; -- Si vuole escludere la possibilità di violazioni del tipo lettura sporca (dirty reads)
    START TRANSACTION;
    
  -- Validazioni da effettuare sui dati in ingresso
	IF CHAR_LENGTH(var_username) < 4 THEN
		SIGNAL SQLSTATE '45005' SET MESSAGE_TEXT = 'Username troppo corto';
	END IF;

	IF CHAR_LENGTH(var_password) < 8 THEN
		SIGNAL SQLSTATE '45006' SET MESSAGE_TEXT = 'Password troppo corta';
	END IF;
    
    INSERT INTO cartadicredito (
        Numero, CVV, DataScadenza
    ) VALUES (
        hashed_cc, var_cvv, var_data_scadenza_carta
    );
    
    INSERT INTO utentebase (
        CF, Nome, Cognome, Indirizzo, CAP, Città, DataNascita, CittàNascita, CartaCredito
    ) VALUES (
        var_cf, var_nome, var_cognome, var_indirizzo, var_cap, var_citta, var_data_nascita, var_citta_nascita, hashed_cc
    );

    INSERT INTO login (
        username, password, ruolo, CF
    ) VALUES (
        var_username, hashed_password, 'USER', var_cf
    );
    
    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure visualizzaAsteFiltrate
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `visualizzaAsteFiltrate`(
	IN var_categoria VARCHAR(50),
    IN var_amministratore CHAR(16)
)
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

    SELECT *
    FROM vista_aste_attive
    WHERE 
        (var_categoria IS NULL OR Categoria = var_categoria)
        AND (var_amministratore IS NULL OR UtenteAmministratore = var_amministratore)
    ORDER BY TempoRimanenteSec;

    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure visualizzaAstePartecipate
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `visualizzaAstePartecipate`(
	IN var_cf CHAR(16)
)
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

    SELECT 
        va.*,
        o.Importo AS ImportoUltimaOfferta,
        o.Data AS DataUltimaOfferta,
        o.Ora AS OraUltimaOfferta,
        o.Automatica AS ControffertaAttiva,
        o.ImportoControfferta AS ImportoControfferta
    FROM vista_aste_attive va
    JOIN (
        SELECT o.*
        FROM offerta o
        INNER JOIN (
            SELECT Asta, MAX(CONCAT(Data, ' ', Ora)) AS MaxDateTime
            FROM offerta
            WHERE UtenteBase = var_cf
            GROUP BY Asta
        ) ultimaOff
        ON o.Asta = ultimaOff.Asta AND CONCAT(o.Data, ' ', o.Ora) = ultimaOff.MaxDateTime
        WHERE o.UtenteBase = var_cf
    ) o ON va.ID = o.Asta
    ORDER BY va.TempoRimanenteSec;

    COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure visualizza_aste_stato
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `visualizza_aste_stato`(
	IN var_cf CHAR(16),
    IN var_stato VARCHAR(10)
)
BEGIN

	SELECT *
    FROM asta
    WHERE UtenteAmministratore = var_cf AND StatoAsta = var_stato
    ORDER BY asta.Data DESC;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure visualizza_offerte_asta
-- -----------------------------------------------------

DELIMITER $$
USE `aste`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `visualizza_offerte_asta`(
	IN idAsta INT,
    IN soloAutomatiche BOOLEAN
)
BEGIN
	SELECT o.UtenteBase, o.Asta, o.Data, o.Ora, o.Importo, o.Automatica
    FROM offerta o
    WHERE o.Asta = idAsta
		AND (soloAutomatiche = FALSE OR o.Automatica = 1)
    ORDER BY o.Data, o.Ora;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- View `aste`.`vista_aste_attive`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `aste`.`vista_aste_attive`;
USE `aste`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `aste`.`vista_aste_attive` AS select `aste`.`asta`.`ID` AS `ID`,`aste`.`asta`.`Descrizione` AS `Descrizione`,`aste`.`asta`.`Dimensioni` AS `Dimensioni`,`aste`.`asta`.`Data` AS `Data`,`aste`.`asta`.`Durata` AS `Durata`,`aste`.`asta`.`Categoria` AS `Categoria`,`aste`.`asta`.`PrezzoBase` AS `PrezzoBase`,`aste`.`asta`.`StatoAsta` AS `StatoAsta`,`aste`.`asta`.`NumOfferte` AS `NumOfferte`,`aste`.`asta`.`OffertaMassima` AS `OffertaMassima`,`aste`.`asta`.`CondizioniArticolo` AS `CondizioniArticolo`,`aste`.`asta`.`UtenteAmministratore` AS `UtenteAmministratore`,timestampdiff(SECOND,now(),(`aste`.`asta`.`Data` + interval `aste`.`asta`.`Durata` day)) AS `TempoRimanenteSec` from `aste`.`asta` where (`aste`.`asta`.`StatoAsta` = 'ATTIVA') order by timestampdiff(SECOND,now(),(`aste`.`asta`.`Data` + interval `aste`.`asta`.`Durata` day));
USE `aste`;

DELIMITER $$
USE `aste`$$
CREATE TRIGGER controllo_livello_categoria
BEFORE INSERT ON categoria
FOR EACH ROW
BEGIN
    IF NEW.Livello > 3 THEN
        SIGNAL SQLSTATE '45300'
        SET MESSAGE_TEXT = 'Non è possibile inserire una categoria con livello superiore a 3.';
    END IF;
END$$

USE `aste`$$
CREATE TRIGGER controllo_durata_asta
BEFORE INSERT ON asta
FOR EACH ROW
BEGIN
    IF NEW.Durata < 1 OR NEW.Durata > 7 THEN
        SIGNAL SQLSTATE '45101'
        SET MESSAGE_TEXT = 'La durata dell\'asta deve essere compresa tra 1 e 7 giorni.';
    END IF;
END$$

USE `aste`$$
CREATE TRIGGER controllo_prezzo_base
BEFORE INSERT ON asta
FOR EACH ROW
BEGIN
    IF NEW.PrezzoBase <= 0 THEN
        SIGNAL SQLSTATE '45102'
        SET MESSAGE_TEXT = 'Il prezzo base deve essere maggiore di 0.';
    END IF;
END$$

USE `aste`$$
CREATE TRIGGER verifica_importo_offerta
BEFORE INSERT ON offerta
FOR EACH ROW
BEGIN
    DECLARE prezzo_base DECIMAL(7,2);
    DECLARE num_offerte INT;
    SELECT PrezzoBase INTO prezzo_base FROM asta WHERE ID = New.Asta;
    SELECT NumOfferte INTO num_offerte FROM asta WHERE ID = New.Asta;

    IF num_offerte = 0 AND New.Importo <= prezzo_base THEN
        SIGNAL SQLSTATE '45003'
        SET MESSAGE_TEXT = 'L\’offerta deve essere maggiore del prezzo base.';
    END IF;
END$$

USE `aste`$$
CREATE TRIGGER verifica_incremento_offerta
BEFORE INSERT ON offerta
FOR EACH ROW
BEGIN
    DECLARE offerta_massima_corrente DECIMAL(7,2) DEFAULT 0.00;
    DECLARE incremento_minimo DECIMAL(7,2) DEFAULT 0.50;

    -- recupera l'offerta massima attuale per l'asta
    SELECT OffertaMassima
    INTO offerta_massima_corrente
    FROM asta
    WHERE ID = NEW.Asta;

    IF offerta_massima_corrente > 0 AND NEW.Importo < (offerta_massima_corrente + incremento_minimo) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'L\'offerta deve essere almeno 0,50€ superiore all\'offerta massima attuale.';
    END IF;
END$$

USE `aste`$$
CREATE TRIGGER aggiornamento_offerta_vincitore
AFTER INSERT ON offerta
FOR EACH ROW
BEGIN
    UPDATE Asta
    SET 
        OffertaMassima = New.Importo,
        UtenteBase = New.UtenteBase,
        NumOfferte = NumOfferte + 1
    WHERE ID = New.Asta;
END$$

USE `aste`$$
CREATE TRIGGER verifica_stato_asta
BEFORE INSERT ON offerta
FOR EACH ROW
BEGIN
    DECLARE stato_asta VARCHAR(10);

    SELECT StatoAsta INTO stato_asta FROM asta WHERE ID = NEW.Asta;

    IF stato_asta = 'Terminata' THEN
        SIGNAL SQLSTATE '45600'
        SET MESSAGE_TEXT = 'Non è possibile fare offerte su un\'asta terminata.';
    END IF;
END$$


DELIMITER ;

SET SQL_MODE = '';
DROP USER IF EXISTS aste_admin;
SET SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
CREATE USER 'aste_admin' IDENTIFIED BY 'aste_admin';

GRANT EXECUTE ON PROCEDURE aste.creaAsta TO 'aste_admin';
GRANT EXECUTE ON PROCEDURE aste.aggiungiCat TO 'aste_admin';
GRANT EXECUTE ON PROCEDURE aste.modificaCat TO 'aste_admin';
GRANT EXECUTE ON PROCEDURE aste.eliminaCat TO 'aste_admin';
GRANT EXECUTE ON PROCEDURE aste.listaCategorie TO 'aste_admin';
GRANT EXECUTE ON PROCEDURE aste.visualizza_aste_stato TO 'aste_admin';
GRANT EXECUTE ON PROCEDURE aste.visualizza_offerte_asta TO 'aste_admin';
GRANT EXECUTE ON PROCEDURE aste.chiudiAsta TO 'aste_admin';

SET SQL_MODE = '';
DROP USER IF EXISTS aste_login;
SET SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
CREATE USER 'aste_login' IDENTIFIED BY 'aste_login';

GRANT EXECUTE ON PROCEDURE aste.registerUser TO 'aste_login';
GRANT EXECUTE ON PROCEDURE aste.login TO 'aste_login';
GRANT EXECUTE ON PROCEDURE aste.dettagli_utente_cf TO 'aste_login';

SET SQL_MODE = '';
DROP USER IF EXISTS aste_user;
SET SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
CREATE USER 'aste_user' IDENTIFIED BY 'aste_user';

GRANT EXECUTE ON PROCEDURE aste.aggiorna_indirizzo_consegna TO 'aste_user';
GRANT EXECUTE ON PROCEDURE aste.listaCategorie TO 'aste_user';
GRANT EXECUTE ON PROCEDURE aste.listaAmministratori TO 'aste_user';
GRANT EXECUTE ON PROCEDURE aste.asteVinteUtente TO 'aste_user';
GRANT EXECUTE ON PROCEDURE aste.visualizzaAstePartecipate TO 'aste_user';
GRANT EXECUTE ON PROCEDURE aste.visualizzaAsteFiltrate TO 'aste_user';
GRANT EXECUTE ON PROCEDURE aste.aggiungiOfferta TO 'aste_user';
GRANT EXECUTE ON PROCEDURE aste.getMigliorOfferta TO 'aste_user';
GRANT EXECUTE ON PROCEDURE aste.getControfferteAttive TO 'aste_user';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

SET GLOBAL event_scheduler = ON;

DELIMITER $$
CREATE EVENT IF NOT EXISTS pulizia_dati_obsoleti
    ON SCHEDULE EVERY 1 MONTH
    STARTS '2025-01-01 00:00:01'
    COMMENT 'Eliminazione dati con più di 10 anni'
DO
BEGIN
    -- elimina offerte delle aste obsolete
    DELETE o FROM offerta o
    INNER JOIN asta a ON o.Asta = a.ID
    WHERE a.Data < DATE_SUB(CURDATE(), INTERVAL 10 YEAR);
    
    -- elimina aste vecchie
    DELETE FROM asta 
    WHERE Data < DATE_SUB(CURDATE(), INTERVAL 10 YEAR);
END$$

DELIMITER ;

SET GLOBAL event_scheduler = ON;

DELIMITER $$
CREATE EVENT IF NOT EXISTS aggiorna_stato_aste
	ON SCHEDULE EVERY 1 MINUTE
    STARTS '2025-01-01 00:00:01'
	COMMENT 'Aggiornamento automatico stato aste'
DO
BEGIN
    UPDATE asta
    SET StatoAsta = 'ATTIVA'
    WHERE StatoAsta = 'FUTURA' AND NOW() >= Data;

    UPDATE asta
    SET StatoAsta = 'TERMINATA'
    WHERE StatoAsta = 'ATTIVA' AND NOW() >= DATE_ADD(Data, INTERVAL Durata DAY);
END$$

DELIMITER ;

START TRANSACTION;
USE aste;

-- -----------------------------------------------------
-- Aggiunta manuale di 20 utenti amministratori di default
-- -----------------------------------------------------

INSERT INTO utenteamministratore (CF, Nome, Cognome) VALUES
('AAAAMM85A01H501A', 'Marco', 'Bianchi'),
('AAABBB85A02H501B', 'Luca', 'Verdi'),
('AAACCC85A03H501C', 'Giulia', 'Neri'),
('AAADDD85A04H501D', 'Elena', 'Galli'),
('AAAEEE85A05H501E', 'Fabio', 'Russo'),
('AAAFFF85A06H501F', 'Laura', 'Romano'),
('AAAGGG85A07H501G', 'Alessio', 'Costa'),
('AAAHHH85A08H501H', 'Anna', 'Fontana'),
('AAAIII85A09H501I', 'Simone', 'Ferri'),
('AAAJJJ85A10H501J', 'Sara', 'Rizzo'),
('AAAKKK85A11H501K', 'Daniele', 'Grassi'),
('AAALLL85A12H501L', 'Francesca', 'Testa'),
('AAAMMM85A13H501M', 'Stefano', 'Lombardi'),
('AAANNN85A14H501N', 'Irene', 'Serra'),
('AAAOOO85A15H501O', 'Matteo', 'De Luca'),
('AAAPPP85A16H501P', 'Valeria', 'Conti'),
('AAAQQQ85A17H501Q', 'Nicola', 'Basile'),
('AAARRR85A18H501R', 'Alice', 'Gentile'),
('AAASSS85A19H501S', 'Andrea', 'Guerra'),
('AAATTT85A20H501T', 'Martina', 'Sartori');

INSERT INTO login (Username, Password, Ruolo, CF) VALUES
('admin1', SHA2('adminpass1', 256), 'ADMIN', 'AAAAMM85A01H501A'),
('admin2', SHA2('adminpass2', 256), 'ADMIN', 'AAABBB85A02H501B'),
('admin3', SHA2('adminpass3', 256), 'ADMIN', 'AAACCC85A03H501C'),
('admin4', SHA2('adminpass4', 256), 'ADMIN', 'AAADDD85A04H501D'),
('admin5', SHA2('adminpass5', 256), 'ADMIN', 'AAAEEE85A05H501E'),
('admin6', SHA2('adminpass6', 256), 'ADMIN', 'AAAFFF85A06H501F'),
('admin7', SHA2('adminpass7', 256), 'ADMIN', 'AAAGGG85A07H501G'),
('admin8', SHA2('adminpass8', 256), 'ADMIN', 'AAAHHH85A08H501H'),
('admin9', SHA2('adminpass9', 256), 'ADMIN', 'AAAIII85A09H501I'),
('admin10', SHA2('adminpass10', 256), 'ADMIN', 'AAAJJJ85A10H501J'),
('admin11', SHA2('adminpass11', 256), 'ADMIN', 'AAAKKK85A11H501K'),
('admin12', SHA2('adminpass12', 256), 'ADMIN', 'AAALLL85A12H501L'),
('admin13', SHA2('adminpass13', 256), 'ADMIN', 'AAAMMM85A13H501M'),
('admin14', SHA2('adminpass14', 256), 'ADMIN', 'AAANNN85A14H501N'),
('admin15', SHA2('adminpass15', 256), 'ADMIN', 'AAAOOO85A15H501O'),
('admin16', SHA2('adminpass16', 256), 'ADMIN', 'AAAPPP85A16H501P'),
('admin17', SHA2('adminpass17', 256), 'ADMIN', 'AAAQQQ85A17H501Q'),
('admin18', SHA2('adminpass18', 256), 'ADMIN', 'AAARRR85A18H501R'),
('admin19', SHA2('adminpass19', 256), 'ADMIN', 'AAASSS85A19H501S'),
('admin20', SHA2('adminpass20', 256), 'ADMIN', 'AAATTT85A20H501T');

-- -----------------------------------------------------
-- Aggiunta manuale di categorie
-- -----------------------------------------------------

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Default', 1, NULL), -- cateogoria di default
('Elettronica', 1, NULL),
('Arredamento', 1, NULL),
('Abbigliamento', 1, NULL),
('Auto e Moto', 1, NULL),
('Gioielli e Orologi', 1, NULL),
('Collezionismo', 1, NULL),
('Arte e Antiquariato', 1, NULL),
('Sport e Tempo Libero', 1, NULL),
('Casa e Giardino', 1, NULL),
('Musica e Strumenti Musicali', 1, NULL);

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Smartphone', 2, 'Elettronica'),
('Computer e Laptop', 2, 'Elettronica'),
('Televisori e Audio', 2, 'Elettronica');

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Mobili per Soggiorno', 2, 'Arredamento'),
('Mobili per Camera da Letto', 2, 'Arredamento'),
('Arredamento da Ufficio', 2, 'Arredamento');

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Abbigliamento Uomo', 2, 'Abbigliamento'),
('Abbigliamento Donna', 2, 'Abbigliamento'),
('Accessori e Scarpe', 2, 'Abbigliamento');

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Auto', 2, 'Auto e Moto'),
('Moto', 2, 'Auto e Moto'),
('Ricambi e Accessori', 2, 'Auto e Moto');

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Orologi da Polso', 2, 'Gioielli e Orologi'),
('Collane e Bracciali', 2, 'Gioielli e Orologi'),
('Anelli e Fedi Nuziali', 2, 'Gioielli e Orologi');

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Monete e Banconote', 2, 'Collezionismo'),
('Cartoline e Stampe', 2, 'Collezionismo'),
('Oggetti Rari', 2, 'Collezionismo');

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Dipinti e Sculture', 2, 'Arte e Antiquariato'),
('Antichità', 2, 'Arte e Antiquariato'),
('Arredi Antichi', 2, 'Arte e Antiquariato');

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Attrezzature Sportive', 2, 'Sport e Tempo Libero'),
('Abbigliamento Sportivo', 2, 'Sport e Tempo Libero'),
('Sport e Tempo Libero Outdoor', 2, 'Sport e Tempo Libero');

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Arredamento Giardino', 2, 'Casa e Giardino'),
('Utensili da Giardino', 2, 'Casa e Giardino'),
('Decorazioni per la Casa', 2, 'Casa e Giardino');

INSERT INTO Categoria (Nome, Livello, CategoriaSuperiore) VALUES
('Strumenti Musicali', 2, 'Musica e Strumenti Musicali'),
('Dischi e Vinili', 2, 'Musica e Strumenti Musicali'),
('Accessori Musicali', 2, 'Musica e Strumenti Musicali');

COMMIT;


ALTER TABLE offerta MODIFY COLUMN Ora TIME(6);