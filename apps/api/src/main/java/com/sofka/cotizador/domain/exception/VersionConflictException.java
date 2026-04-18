package com.sofka.cotizador.domain.exception;

public class VersionConflictException extends RuntimeException {

    private final int currentVersion;
    private final int receivedVersion;
    private final String numeroFolio;

    public VersionConflictException(String numeroFolio, int currentVersion, int receivedVersion) {
        super("Conflicto de versión en folio " + numeroFolio
                + ": versión actual=" + currentVersion
                + ", versión recibida=" + receivedVersion);
        this.currentVersion = currentVersion;
        this.receivedVersion = receivedVersion;
        this.numeroFolio = numeroFolio;
    }

    public int getCurrentVersion() { return currentVersion; }
    public int getReceivedVersion() { return receivedVersion; }
    public String getNumeroFolio() { return numeroFolio; }
}
