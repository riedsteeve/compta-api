package org.sebsy.compta.service;

/** Something the client named does not exist. Translated to 404 by the web layer. */
public class Introuvable extends RuntimeException {

    public Introuvable(String message) {
        super(message);
    }
}
