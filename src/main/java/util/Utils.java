package util;

import datasource.CategoryRepo;
import model.Template;
import model.Token;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String SentenceFromTemplate(String guild, Template template, CategoryRepo categoryRepo) {
        StringBuilder builder = new StringBuilder();
        for (Token token : template.getSentence()) {
            if (token.getType().equals(Token.Type.PLAIN_TEXT)) {
                builder.append(token.getValue()).append(" ");
            } else {
                String fillWord = categoryRepo.getRandomFromCategory(guild, token.getValue());
                builder.append(fillWord).append(" ");
            }
        }
        return builder.toString();
    }

    public static List<Token> stringToTokenList(String sentence) {
        ArrayList<Token> tokens = new ArrayList<>();
        String[] pieces = sentence.split(" ");

        for (String s : pieces) {
            tokens.add(Token.fromString(s));
        }

        return tokens;
    }
}
