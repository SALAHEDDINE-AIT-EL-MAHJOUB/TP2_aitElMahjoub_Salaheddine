package ma.emsi.aitelmahjoub.prompt;

/** Simple container for the final prompt text. */
public class Prompt {

    private final String text;

    public Prompt(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
