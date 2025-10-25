package ma.emsi.aitelmahjoub.test2;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

import java.util.Map;

public class Test2 {
    public static void main(String[] args) {
        // Récupère la clé d'API depuis la variable d'environnement GEMINI_KEY
        String cle = System.getenv("GEMINI_KEY");

        // Création du modèle via le pattern builder
        ChatLanguageModel modele = GoogleAiGeminiChatModel
                .builder()
                .apiKey(cle)
                .modelName("gemini-2.0-flash-exp")
                .temperature(0.7)
                .build();

        // Exemple de texte à traduire
        String texteFrancais = "Bonjour, comment allez-vous ?";

        // Création du prompt directement
        String prompt = "Traduis le texte suivant en anglais : " + texteFrancais;

        // Envoie le prompt au modèle
        String reponse = modele.generate(prompt);

        // Affiche le prompt envoyé et la réponse
        System.out.println("Prompt envoyé : " + prompt);
        System.out.println("Réponse du modèle : " + reponse);
    }
}