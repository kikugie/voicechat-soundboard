package dev.kikugie.soundboard.access;

import com.google.common.base.Predicates;

import java.util.function.Predicate;

public interface TextFieldAccessor {
    default Predicate<String> soundboard$predicate() { return Predicates.alwaysTrue(); }

    boolean soundboard$isValid(String text);
}
