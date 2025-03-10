package model;

import util.Utils;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Template implements Serializable {
    private final Long id;
    private final List<Token> sentence;
    private final UUID uuid;

    public Template(Long id, List<Token> sentence) {
        this(UUID.randomUUID(), id, sentence);
    }

    public Template(UUID uuid, Long id, List<Token> sentence) {
        this.uuid = uuid;
        this.id = id;
        this.sentence = sentence;
    }

    public Template(String uuid, Long id, List<Token> sentence) {
        this.uuid = UUID.fromString(uuid);
        this.id = id;
        this.sentence = sentence;
    }

    public Template(Long id, String sentence) {
        this.uuid = UUID.randomUUID();
        this.id = id;
        this.sentence = Utils.stringToTokenList(sentence);
    }

    public Template(String uuid, Long id, String sentence) {
        this(UUID.fromString(uuid), id, Utils.stringToTokenList(sentence));
    }

    public Long getId() {
        return id;
    }

    public List<Token> getSentence() {
        return sentence;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getSentenceString() {
        StringBuilder builder = new StringBuilder();
        sentence.forEach(token -> {
            if (token.getType().equals(Token.Type.PLAIN_TEXT)) {
                builder.append(token.getValue()).append(" ");
            } else {
                builder.append("[").append(token.getValue()).append("] ");
            }
        });
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", sentence=" + sentence +
                '}';
    }
}
