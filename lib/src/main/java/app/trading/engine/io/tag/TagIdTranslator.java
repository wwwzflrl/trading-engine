package app.trading.engine.io.tag;

import app.trading.engine.core.api.tag.TagId;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Set;

public final class TagIdTranslator {
    public static final String BASE_PACKAGE = "app.trading";
    private static final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
    @VisibleForTesting
    static Set<BeanDefinition> components;
    static {
        scanAndRegister();
    }

    private static void scanAndRegister() {
        provider.addIncludeFilter(new AssignableTypeFilter(TagId.class));
        components = provider.findCandidateComponents(BASE_PACKAGE);
    }

    public static int translate(final TagId tagId) {
        return translate(tagId, tagId.team().ordinal() << 12);
    }

    private static int translate(final TagId tagId, final int offset) {
        final int base = offset | (tagId.ordinal() << 2);
        return base + tagId.type().ordinal();
    }

    private TagIdTranslator() {

    }
}
