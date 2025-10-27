package ma.emsi.aitelmahjoub.test5;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
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

import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Le RAG facile - Version corrigée
 */
public class Test5 {

    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String llmKey = System.getenv("GEMINI_KEY");

        
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.3)
                .build();

        // Création du modèle d'embeddings
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        // Chargement du document PDF 
        String nomDocument = "rag.PDF";
        Document document = FileSystemDocumentLoader.loadDocument(
                Paths.get(nomDocument),
                new ApachePdfBoxDocumentParser()
        );

        // Découpage du document en segments avec overlap
        DocumentSplitter splitter = DocumentSplitters.recursive(
                300,  // taille du chunk
                50    // overlap 
        );
        List<TextSegment> segments = splitter.split(document);

        System.out.println("Nombre de segments créés: " + segments.size());

        // Création de la base vectorielle 
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

        System.out.println("Posez une questions  (tapez 'q' pour quitter)\n");

        
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("questions: ");
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

            // Afficher la réponse
            System.out.println("\nAssistant: ");
            String reponse = assistant.chat(question);
            System.out.println(reponse);
            System.out.println("\n" + "-".repeat(80) + "\n");
        }

        scanner.close();
    }

}

