package ma.emsi.aitelmahjoub.test4;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Le RAG facile
 */
public class Test4 {

    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String llmKey = System.getenv("GEMINI_KEY");

        // Température basse pour plus de précision avec le RAG
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.3)
                .build();

        // Création du modèle d'embeddings
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        // Chargement du document
        String nomDocument = "infos.txt";
        Document document;
        try {
            String content = Files.readString(Paths.get(nomDocument));
            document = Document.from(content, Metadata.from("source", nomDocument));
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement du fichier: " + nomDocument, e);
        }

        // Découpage du document en segments
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);

        // Création de la base vectorielle en mémoire
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Calcul des embeddings et stockage
        for (TextSegment segment : segments) {
            Embedding embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
        }

        // Création du content retriever
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.5)
                .build();

        // Création de l'assistant conversationnel
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(contentRetriever)
                .build();

        // Test
        String question = "Pierre appelle son chat. Qu'est-ce qu'il pourrait dire ?";
        String reponse = assistant.chat(question);

        System.out.println("Question: " + question);
        System.out.println("Réponse: " + reponse);
    }
}