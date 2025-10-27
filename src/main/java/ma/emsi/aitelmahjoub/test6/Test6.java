package ma.emsi.aitelmahjoub.test6;

import ma.emsi.aitelmahjoub.tools.meteo.MeteoTool;

import java.util.Scanner;

/**
 * Test simple de l'outil météo - l'utilisateur saisit le nom de la ville
 */
public class Test6 {
    public static void main(String[] args) {
        MeteoTool meteoTool = new MeteoTool();

        System.out.println("===  météo ===\n");
        System.out.println("Entrez le nom d'une ville (tapez 'q' pour quitter)\n");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Ville: ");
            String ville = scanner.nextLine().trim();

            // Condition de sortie
            if (ville.equalsIgnoreCase("q")) {
                System.out.println("Au revoir!");
                break;
            }

            // Ignorer les entrées vides
            if (ville.isEmpty()) {
                continue;
            }

            // Appeler l'outil météo directement avec le nom de la ville
            String resultat = meteoTool.donneMeteo(ville);
            System.out.println(resultat);
            System.out.println();
        }

        scanner.close();
    }
}