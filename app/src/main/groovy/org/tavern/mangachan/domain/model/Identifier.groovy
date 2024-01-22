package org.tavern.mangachan.domain.model

abstract class Identifier {
    private final String id;

    protected Identifier() {}

    Identifier(String id) {
        this.id = id
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Identifier that = (Identifier) o

        if (id != that.id) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }

    @Override
    String toString() {
        return getId();
    }

    String getId() {
        return id
    }
}
