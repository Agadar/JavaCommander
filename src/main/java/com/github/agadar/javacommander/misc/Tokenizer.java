package com.github.agadar.javacommander.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Parses a string to a list of argument tokens.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class Tokenizer {

    /**
     * Parses a string to a list of argument tokens.
     *
     * @param input A string to parse to a list of argument tokens.
     * @return A list of argument tokens.
     */
    public Collection<List<String>> tokenize(String input) {

        var tokenLists = new ArrayList<List<String>>();

        if (input == null) {
            return tokenLists;
        }
        input = input.trim();
        if (input.length() < 1) {
            return tokenLists;
        }

        var curTokens = new ArrayList<String>(); // current token list
        var lastToken = new StringBuilder(); // current token
        boolean insideQuote = false; // are we currently within quotes?
        boolean escapeNextChar = false; // must we escape the current char?

        // Iterate over all chars in the string
        for (char c : input.toCharArray()) {
            // If we are to escape the next char, then append it to the token
            // and turn off the escape option.
            if (escapeNextChar) {
                lastToken.append(c);
                escapeNextChar = false;
            } // Else if the character is a quote mark, then toggle insideQuote.
            else if (c == '"' || c == '\'') {
                insideQuote = !insideQuote;
            } // Else if the character is the escape character, then set escapeNextChar on.
            else if (c == '\\') {
                escapeNextChar = true;
            } // Else if the character is a space, '=' or ':'...
            else if (c == ' ' || c == '=' || c == ':') {
                // ...and we're not in a quote...
                if (!insideQuote) {
                    // ...and the current token is at least 1 character long,
                    // then the current token is finished.
                    if (lastToken.length() > 0) {
                        curTokens.add(lastToken.toString());
                        lastToken.delete(0, lastToken.length());
                    }
                } // ...and we're in a quote, then append it to the token.
                else {
                    lastToken.append(c);
                }
            } // Else if the character is ';', then that means we're going for a new token
              // list.
            else if (c == ';') {
                if (lastToken.length() > 0) {
                    curTokens.add(lastToken.toString());
                    lastToken.delete(0, lastToken.length());
                }
                if (curTokens.size() > 0) {
                    tokenLists.add(curTokens);
                    curTokens = new ArrayList<>();
                }
            } // Else, append to the token.
            else {
                lastToken.append(c);
            }
        }
        // Add the last token to the list and then return the list.
        if (lastToken.length() > 0) {
            curTokens.add(lastToken.toString());
        }
        if (curTokens.size() > 0) {
            tokenLists.add(curTokens);
        }
        return tokenLists;
    }
}
