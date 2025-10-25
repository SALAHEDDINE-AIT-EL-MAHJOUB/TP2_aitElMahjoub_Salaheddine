package ma.emsi.aitelmahjoub.test2;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import ma.emsi.aitelmahjoub.prompt.PromptTemplate;
import ma.emsi.aitelmahjoub.prompt.Prompt;

import java.util.Map;

public class Test2 {
	public static void main(String[] args) {
		// Récupère la clé d'API depuis la variable d'environnement GEMINI_KEY
		String cle = System.getenv("GEMINI_KEY");

		// Création du modèle via le pattern builder (mêmes paramètres que Test1)
		ChatModel modele = GoogleAiGeminiChatModel
				.builder()
				.apiKey(cle)
				.modelName("gemini-2.5-flash")
				.temperature(0.7)
				.build();

		// Définition d'un PromptTemplate adapté à un traducteur
		// Le placeholder {text} sera remplacé par le texte à traduire
		PromptTemplate template = new PromptTemplate("Traduis le texte suivant en anglais : {text}");

		// Exemple de texte à traduire (on pourrait aussi le prendre depuis args)
		String texteFrancais = "Bonjour, comment allez-vous ?";

		// Crée un Prompt à partir du template
		Prompt prompt = template.createPrompt(Map.of("text", texteFrancais));

		// Envoie le prompt au modèle
		String reponse = modele.chat(prompt.getText());

		// Affiche le prompt envoyé et la réponse
		System.out.println("Prompt envoyé : " + prompt.getText());
		System.out.println("Réponse du modèle : " + reponse);
	}
}
