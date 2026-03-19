package dk.jnie.dragfolder.domain.util;

import org.immutables.value.Value;

@Value.Style(
        typeImmutable = "*",
        typeAbstract = "*Def"
)
public @interface ObjectStyle {
}