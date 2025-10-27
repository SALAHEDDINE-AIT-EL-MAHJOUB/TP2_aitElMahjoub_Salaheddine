package ma.emsi.aitelmahjoub.test6;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import ma.emsi.aitelmahjoub.tools.meteo.MeteoTool;

import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 Assistant avec outil météo et logging
 */
public class Test6 {

    // Interface de l'assistant météo
    interface AssistantMeteo {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        // Configuration du logging
        setupLogging();

        // Récupération de la clé API
        String llmKey = System.getenv("GEMINI_KEY");

        // Création du modèle LLM avec logging 
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-2.0-flash")
                .temperature(0.7)
                .logRequestsAndResponses(true) 
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

            if (question.equalsIgnoreCase("q")) {
                System.out.println("Au revoir!");
                break;
            }

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

    // Méthode pour configurer le logging LangChain4j
    private static void setupLogging() {
        Logger logger = Logger.getLogger("dev.langchain4j");
        logger.setLevel(Level.FINE);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        logger.addHandler(handler);

       
    }
}
