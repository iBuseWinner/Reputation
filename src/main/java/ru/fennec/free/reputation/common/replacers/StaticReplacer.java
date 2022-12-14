package ru.fennec.free.reputation.common.replacers;

import java.util.HashMap;
import java.util.Map;

public class StaticReplacer extends Replacer {

    private Map<String, Object> mapping = new HashMap<>();

    @Override
    public String apply(String str) {
        return replace(str, mapping);
    }

    @Override
    public String apply(String str, Object context) {
        return apply(str);
    }

    public StaticReplacer set(String placeholder, Object replacement) {
        mapping.put(placeholder, String.valueOf(replacement));
        return this;
    }

    public static StaticReplacer replacer() {
        return new StaticReplacer();
    }
}
