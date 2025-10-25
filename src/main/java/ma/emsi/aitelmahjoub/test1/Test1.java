package ma.emsi.aitelmahjoub.test1;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class Test1 {
    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        if (cle == null || cle.isEmpty()) {
            System.err.println("Erreur : La variable GEMINI_KEY n'est pas définie !");
            return;
        }

        // Utiliser gemini-2.5-flash (stable et récent)
        ChatLanguageModel modele = GoogleAiGeminiChatModel
                .builder()
                .apiKey(cle)
                .modelName("gemini-2.5-flash")
                .temperature(0.7)
                .build();

        System.out.println("Question : Quelle est la capitale de la France ?");
        String reponse = modele.generate("Quelle est la capitale de la France ?");
        System.out.println("Réponse : " + reponse);
    }
}