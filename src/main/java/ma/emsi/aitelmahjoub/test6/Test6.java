package ma.emsi.aitelmahjoub.test6;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import ma.emsi.aitelmahjoub.tools.meteo.MeteoTool;

import java.util.Scanner;

/**
 * Assistant avec outil météo
 */
public class Test6 {

    // Interface de l'assistant météo
    interface AssistantMeteo {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String llmKey = System.getenv("GEMINI_KEY");

        // ChatLanguageModel
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-2.0-flash-exp")
                .temperature(0.7)
                .build();

        // Création de l'assistant avec l'outil météo
        AssistantMeteo assistant = AiServices.builder(AssistantMeteo.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(new MeteoTool()) 
                .build();

        System.out.println("=== Assistant Météo ===");
        System.out.println("Posez des questions sur la météo (tapez 'q' pour quitter)\n");

        
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Question: ");
            String question = scanner.nextLine().trim();

            // Condition de sortie
            if (question.equalsIgnoreCase("q")) {
                System.out.println("Au revoir!");
                break;
            }

            // Ignorer les entrées vides
            if (question.isEmpty()) {
                continue;
            }

            // Obtenir et afficher la réponse
            System.out.println("\nAssistant: ");
            String reponse = assistant.chat(question);
            System.out.println(reponse);
            System.out.println("\n" + "-".repeat(80) + "\n");
        }

        scanner.close();
    }
}
