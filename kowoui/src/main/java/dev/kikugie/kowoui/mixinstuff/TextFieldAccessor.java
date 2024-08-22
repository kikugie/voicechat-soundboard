package dev.kikugie.kowoui.mixinstuff;

import com.google.common.base.Predicates;
import io.wispforest.owo.ui.core.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface TextFieldAccessor {
	default Predicate<String> soundboard$predicate() {
		return Predicates.alwaysTrue();
	}

	default @Nullable Color soundboard$color(@NotNull String text) {
		return null;
	}
}
