package com.stetho.suprmrkt.model;

public interface ProcessScanInterface {
    /**
     * appelée quand cab a deja été scanné
     *
     * @param cabAlreadyScanned : le cab déjà scanné pour traitement éventuel.
     */
    void cabAlreadyScanned(String cabAlreadyScanned);

    /**
     * appelée quand le cab n'est pas valide
     *
     * @param cab : le cab scanné pour traitement éventuel
     */
    void cabRejete(String cab);

    /**
     * appelée quand <b>colis</b> a été ajouté avec succès.
     *
     * @param cab : le cab qui vient d'etre scanne.
     */
    void cabSuccessfullyTraite(String cab);
}