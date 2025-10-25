package ma.emsi.aitelmahjoub.prompt;

import java.util.Map;

/**
 * Very small PromptTemplate implementation for the exercise.
 * Replaces occurrences of placeholders in the form {name} with provided values.
 */
public class PromptTemplate {

    private final String template;

    public PromptTemplate(String template) {
        this.template = template;
    }

    /**
     * Create a Prompt by substituting variables into the template.
     */
    public Prompt createPrompt(Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> e : variables.entrySet()) {
            if (e.getValue() == null) continue;
            result = result.replace("{" + e.getKey() + "}", e.getValue());
        }
        return new Prompt(result);
    }

    /** Convenience for a single variable. */
    public Prompt createPrompt(String key, String value) {
        return createPrompt(Map.of(key, value));
    }
}
