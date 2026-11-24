package org.sebsy.compta.domain;

import jakarta.persistence.Embeddable;

/**
 * Composite key of {@code compo}: PRIMARY KEY (id_art, id_bon). Mapping from Java · Accès aux données, TP 09.
 *
 * <p>A record: a key is immutable and defined by its values, which is exactly the equality a
 * record provides. No {@code Serializable}: Jakarta Persistence 3.2 no longer requires it of
 * primary key classes, and Hibernate 7.4 does not check it (Accès aux données, TP 09).
 *
 * <p>What binds a component to a column is {@code @MapsId} on {@link Ligne}, by component name.
 */
@Embeddable
public record LigneId(Long articleId, Long bonId) {
}
