package ma.emsi.aitelmahjoub.test4;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
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

/**
 * Le RAG facile !
 */
public class Test4 {

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
                .timeout(Duration.ofSeconds(120))
                .build();

        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(llmKey)
                .modelName("text-embedding-004")
                .build();

        // Chargement du document, sous la forme d'embeddings, dans une base vectorielle en mémoire
        String nomDocument = "infos.txt";
        Document document = FileSystemDocumentLoader.loadDocument(nomDocument);
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        // Calcule les embeddings et les enregistre dans la base vectorielle
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();
        ingestor.ingest(document);

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
                                .build())
                        .build();

        // Le LLM va utiliser l'information du fichier infos.txt pour répondre à la question.
        String question = "Comment s'appelle le chat de Pierre ?";

        String reponse = assistant.chat(question);
        // Affiche la réponse du LLM.
        System.out.println(reponse);
    }

}
