package com.dummyc0m.bukkit.townhall.core.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Dummyc0m on 3/9/16.
 * From Minecraft Class StringTranslate
 */
public class StringTranslator {
    /**
     * Pattern that matches numeric variable placeholders in a resource string, such as "%d", "%3$d", "%.2f"
     */
    private static final Pattern numericVariablePattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

    /**
     * A Splitter that splits a string on the first "=".  For example, "a=b=c" would split into ["a", "b=c"].
     */
    private static final Splitter equalSignSplitter = Splitter.on('=').limit(2);

    private static StringTranslator localized = new StringTranslator();

    private final Map<String, String> languageList = new HashMap<>();

    public StringTranslator() {
    }

    /**
     * Return the StringTranslator singleton instance
     */
    public static StringTranslator getTranslator() {
        return localized;
    }

    public void addLangFile(File langFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(langFile));
            addLangReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addLangResource(String domain, String lang) {
        InputStream langStream = StringTranslator.class.getResourceAsStream("/" + domain + "/" + lang + ".lang");
        if (langStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(langStream, Charset.forName("UTF-8")));
            addLangReader(reader);
        }
    }

    private void addLangReader(BufferedReader reader) {
        try {
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (!line.isEmpty() && line.charAt(0) != 35) {
                    String[] keyValPair = Iterables.toArray(equalSignSplitter.split(line), String.class);

                    if (keyValPair != null && keyValPair.length == 2) {
                        String value = numericVariablePattern.matcher(keyValPair[1]).replaceAll("%$1s");
                        this.languageList.put(keyValPair[0], value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Translate a key to current language.
     */
    public synchronized String translateKey(String key) {
        return this.tryTranslateKey(key);
    }

    /**
     * Translate a key to current language applying String.format()
     */
    public synchronized String translateKeyFormat(String key, Object... varargs) {
        String translation = tryTranslateKey(key);

        try {
            return String.format(translation, varargs);
        } catch (IllegalFormatException var5) {
            return "Format error: " + translation;
        }
    }

    /**
     * Tries to look up a translation for the given key; spits back the key if no result was found.
     */
    private String tryTranslateKey(String key) {
        String translation = this.languageList.get(key);
        return translation == null ? key : translation;
    }

    /**
     * Returns true if the passed key is in the translation table.
     */
    public synchronized boolean isKeyTranslated(String key) {
        return this.languageList.containsKey(key);
    }
}
