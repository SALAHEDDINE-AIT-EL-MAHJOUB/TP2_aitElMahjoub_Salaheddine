package ma.emsi.aitelmahjoub.test3;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;

public class Test3 {
    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Erreur : La variable d'environnement GEMINI_KEY n'est pas définie.");
            return;
        }

        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("text-embedding-004")  
                .timeout(Duration.ofSeconds(60))
                .maxRetries(2)
                .build();

        System.out.println("=== Test de similarité sémantique ===\n");

        testSimilarity(embeddingModel, "Bonjour, comment ça va ?", "Salut, ça va bien ?");
        testSimilarity(embeddingModel, "J'aime programmer en Java.", "La programmation en Java est ma passion.");
        testSimilarity(embeddingModel, "Le ciel est bleu.", "La voiture est rouge.");
        testSimilarity(embeddingModel, "Quel temps fait-il aujourd'hui ?", "Où se trouve la gare la plus proche ?");
    }

    private static void testSimilarity(EmbeddingModel model, String text1, String text2) {
        System.out.println("--------------------");
        System.out.println("Phrase 1: " + text1);
        System.out.println("Phrase 2: " + text2);

        try {
            Response<Embedding> response1 = model.embed(text1);
            Response<Embedding> response2 = model.embed(text2);

            Embedding embedding1 = response1.content();
            Embedding embedding2 = response2.content();

            double similarity = CosineSimilarity.between(embedding1, embedding2);

            System.out.printf("Similarité cosinus: %.4f", similarity);

            // Interprétation
            if (similarity > 0.8) {
                System.out.println(" → Très similaire ✓");
            } else if (similarity > 0.5) {
                System.out.println(" → Moyennement similaire");
            } else {
                System.out.println(" → Peu similaire");
            }

        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
        System.out.println("--------------------\n");
    }
}
