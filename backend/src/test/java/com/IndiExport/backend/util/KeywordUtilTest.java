package com.IndiExport.backend.util;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class KeywordUtilTest {

    @Test
    void testExtractKeywords() {
        String name = "Cotton T-Shirt Export Quality";
        String description = "High quality cotton shirts for the international market.";
        
        Set<String> keywords = KeywordUtil.extractKeywords(name, description);
        
        // Check for expected keywords
        assertTrue(keywords.contains("cotton"));
        assertTrue(keywords.contains("shirt"));
        assertTrue(keywords.contains("shirts"));
        assertTrue(keywords.contains("international"));
        assertTrue(keywords.contains("market"));
        
        // Check for excluded stop words and small words
        assertFalse(keywords.contains("for"));
        assertFalse(keywords.contains("the"));
        assertFalse(keywords.contains("quality")); // Added to stop words in my implementation
        assertFalse(keywords.contains("india")); // Added to stop words
        assertFalse(keywords.contains("a"));
        assertFalse(keywords.contains("an"));
    }

    @Test
    void testEmptyInput() {
        Set<String> keywords = KeywordUtil.extractKeywords("", null);
        assertTrue(keywords.isEmpty());
    }

    @Test
    void testSmallWords() {
        Set<String> keywords = KeywordUtil.extractKeywords("a an to it of");
        assertTrue(keywords.isEmpty());
    }
}
