package com.IndiExport.backend.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class KeywordUtil {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "if", "then", "else", "when", "at", "from", "by", "for", "with", "in", "on", "to", "of", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "do", "does", "did", "which", "that", "this", "these", "those", "i", "you", "he", "she", "it", "we", "they", "my", "your", "his", "her", "its", "our", "their", "need", "want", "buy", "sell", "looking", "urgent", "required", "quality", "export", "import", "india"
    ));

    public static Set<String> extractKeywords(String... texts) {
        if (texts == null || texts.length == 0) {
            return new HashSet<>();
        }

        StringBuilder combinedText = new StringBuilder();
        for (String text : texts) {
            if (text != null) {
                combinedText.append(text).append(" ");
            }
        }

        String[] words = combinedText.toString().toLowerCase()
                .split("[^a-zA-Z0-9]+");

        return Arrays.stream(words)
                .filter(word -> word.length() >= 3)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toSet());
    }
}
