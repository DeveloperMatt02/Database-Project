package it.uniroma2.asteonline.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLIView {

    protected static int getAndValidateInput(int maxNumber) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("Seleziona un'opzione: ");
            int choice = Integer.parseInt(reader.readLine());
            if (choice >= 1 && choice <= maxNumber) {
                return choice;
            }else{
                System.out.println("Opzione non valida");
            }
        }
    }

    protected static String getNotEmptyInput(String text) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        while (true) {
            System.out.print(text);
            input = reader.readLine();
            if (!input.isEmpty()){
                break;
            } else{
                System.out.println("E' stato inserito un input vuoto");
            }
        }
        return input;
    }

    protected static void printBackOption(int optionNumber){
        System.out.println(optionNumber+") Torna indietro");
    }
}
