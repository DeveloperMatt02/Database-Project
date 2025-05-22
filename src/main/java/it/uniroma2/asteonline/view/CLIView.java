package it.uniroma2.asteonline.view;

import it.uniroma2.asteonline.model.domain.Categoria;
import it.uniroma2.asteonline.utils.LoggedUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLIView {
    protected static int getAndValidateInput(int maxNumber) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("\nSeleziona un'opzione: ");
            try {
                int choice = Integer.parseInt(reader.readLine());
                if (choice >= 1 && choice <= maxNumber) {
                    return choice;
                }else{
                    System.out.println("Errore: Opzione non valida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Errore: Non numerico.");
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
                System.out.println("Errore: È stato inserito un input vuoto.");
            }
        }
        return input;
    }

    protected static void showHeader() {
        crossSeparator();
        System.out.println("\nBenvenuto " + LoggedUser.getUsername() + " (" + LoggedUser.getCF() + ")");
        crossSeparator();
    }

    protected static void crossSeparator(){
        System.out.print("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
    }

    protected static void printLine(){
        System.out.println();
    }

    protected static void printBackOption(int optionNumber){
        System.out.println(optionNumber+". Torna indietro");
    }

    protected static void printCatTree(Categoria catTree, int indent) {
        if (catTree.getNomeCategoria().equals("Radice") && catTree.getFigli().isEmpty()) {
            System.out.print("\nNessuna categoria trovata nel database.");
        }

        if (!catTree.getNomeCategoria().equals("Radice")) {
            System.out.println("  ".repeat(indent) + indent + " - " + catTree.getNomeCategoria());
        }

        for (Categoria figlio : catTree.getFigli()) {
            //funzione ricorsiva per ciascun figlio
            printCatTree(figlio, indent + 1);
        }
    }

}
