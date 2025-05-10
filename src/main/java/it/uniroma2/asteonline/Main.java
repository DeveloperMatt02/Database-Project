package it.uniroma2.asteonline;

import it.uniroma2.asteonline.controller.MainController;

public class Main {

    public static void main(String[] args) {
        MainController applicationController = new MainController();
        applicationController.start();
    }
}

//TODO:: utileee
/*

    FLUSH PRIVILEGES;
    SHOW GRANTS FOR 'aste_login'@'localhost';
    GRANT EXECUTE ON PROCEDURE aste.registerUser TO 'aste_login'@'localhost';

 */