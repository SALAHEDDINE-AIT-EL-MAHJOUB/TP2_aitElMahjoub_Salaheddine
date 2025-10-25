package ma.emsi.aitelmahjoub.test1;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class Test1 {
	public static void main(String[] args) {
		// Récupère la clé d'API depuis la variable d'environnement GEMINI_KEY
		String cle = System.getenv("GEMINI_KEY");

		// Création du modèle via le pattern builder
		ChatModel modele = GoogleAiGeminiChatModel
				.builder()
				.apiKey(cle)
				.modelName("gemini-2.5-flash")
				.temperature(0.7)
				.build();

		// Pose une question au modèle
		String reponse = modele.chat("Quelle est la capitale de la France ?");

		// Affiche la réponse du modèle
		System.out.println(reponse);
	}

}
