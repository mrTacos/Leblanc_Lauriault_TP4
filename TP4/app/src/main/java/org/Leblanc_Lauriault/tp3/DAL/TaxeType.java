package org.Leblanc_Lauriault.tp3.DAL;


public enum TaxeType
{
    taxeEssentiel(1.05),
    taxeAutre(1.35);

    public final double valeurEnPourcentage;

    TaxeType(double pPercent)
    {
        this.valeurEnPourcentage = pPercent;
    }
}
