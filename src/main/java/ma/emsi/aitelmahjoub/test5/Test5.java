package ma.emsi.aitelmahjoub.test5;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;


import java.time.Duration;
import java.util.Scanner;

public class Test5 {

    // Assistant conversationnel
    interface Assistant {
        // Prend un message de l'utilisateur et retourne une réponse du LLM.
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String llmKey = System.getenv("GEMINI_KEY");
        if (llmKey == null || llmKey.isBlank()) {
            System.err.println("GEMINI_KEY environment variable is not set.");
            return;
        }
        // Mettre une température qui ne dépasse pas 0,3.
        // Le RAG sert à mieux contrôler l'exactitude des informations données par le LLM
        // et il est donc logique de diminuer la température.
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.3)
                .timeout(Duration.ofSeconds(300))
                .build();

        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(llmKey)
                .modelName("text-embedding-004")
                .build();

        // Chargement du document, sous la forme d'embeddings, dans une base vectorielle en mémoire
        String nomDocument = "langchain4j.pdf";
        System.out.println("Chargement du document " + nomDocument + "...");
        Document document = FileSystemDocumentLoader.loadDocument(nomDocument);
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        // Calcule les embeddings et les enregistre dans la base vectorielle
        System.out.println("Découpage du document et calcul des embeddings...");
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 100))
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();
        ingestor.ingest(document);
        System.out.println("Le document est prêt.");

        // Création de l'assistant conversationnel, avec une mémoire.
        // L'implémentation de Assistant est faite par LangChain4j.
        // L'assistant gardera en mémoire les 10 derniers messages.
        // La base vectorielle en mémoire est utilisée pour retrouver les embeddings.
        Assistant assistant =

                AiServices.builder(Assistant.class)
                        .chatLanguageModel(model)
                        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                        .contentRetriever(EmbeddingStoreContentRetriever.builder()
                                .embeddingStore(embeddingStore)
                                .embeddingModel(embeddingModel)
                                .maxResults(5)
                                .build())
                        .build();
        // Boucle de conversation
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nPosez votre question : ");
                String question = scanner.nextLine();

                if ("exit".equalsIgnoreCase(question) || "quit".equalsIgnoreCase(question)) {
                    break;
                }


                String reponse = assistant.chat(question);
                // Affiche la réponse du LLM.
                System.out.println("Réponse : " + reponse);
            }
        }
    }

}
