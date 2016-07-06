package com.dummyc0m.bukkit.townhall.core.util;


/**
 * Created by Dummyc0m on 3/9/16.
 */
public class I18N {
    private static StringTranslator translator = StringTranslator.getTranslator();

    /**
     * Translate a key to current language.
     */
    public static synchronized String translateKey(String key) {
        return translator.translateKey(key);
    }

    /**
     * Translate a key to current language applying String.format()
     */
    public static synchronized String translateKeyFormat(String key, Object... varargs) {
        return translator.translateKeyFormat(key, varargs);
    }

    /**
     * Returns true if the passed key is in the translation table.
     */
    public static synchronized boolean isKeyTranslated(String key) {
        return translator.isKeyTranslated(key);
    }
}
