package org.Leblanc_Lauriault.tp3.change;


import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.ArgentManquantException;
import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.EmplacementPleinException;
import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.RetraitPiece1sException;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class TiroirCaisseLL implements TiroirCaisse
{

    // Un dictionnaire avec la monaie, la quantité et la capacité
    private final int maxBillet = 40;
    private final int maxPiece = 50;


    private Map<ArgentObjet, Integer> m_capaciteTiroir;
    /**
     * Renvoie le nombre d'items de ce type contenu dans cet objet
     * @param m type
     * @return
     */
    public int nombreItemsPour(ArgentObjet m)
    {
        if (this.m_capaciteTiroir.containsKey(m))
            return this.m_capaciteTiroir.get(m);
        else
            return 0;
    }




    /**
     * Ajouter un objet du type spécifié m fois.
     * @param m
     * @param nombre
     */
    public void ajouterItem(ArgentObjet m, int nombre) throws Exception
    {
        if (nombre < 0)
            throw new InvalidParameterException("Les nombres négatif ne sont pas permis.");

        if (nombre == 0)
            return;

        boolean isPiece = (m.nomLisible.contains("pièce"));
        //if ((isPiece && this.m_capaciteTiroir.get(m) < this.maxPiece)||(!isPiece && this.m_capaciteTiroir.get(m) < this.maxBillet))
        //{
            this.m_capaciteTiroir.put(m,this.m_capaciteTiroir.get(m) + nombre);
        //}
        //else
        //{
            throw new EmplacementPleinException("Le tiroir caisse comprend déja un nombre maxium de billet ou de pièce.");
       // }
    }

    /**
     * Renvoie la valeur totale de l'objet en dollars
     * @return
     */
    public double valeurTotale()
    {
        int totalValue = 0;
        for (Map.Entry<ArgentObjet, Integer> entry : m_capaciteTiroir.entrySet())
        {
            totalValue += this.obtenirValeurPourLeChange(entry.getKey(),entry.getValue());
        }
        return totalValue;
    }




    /**
     * Renvoie le nombre total d'items contenu dans cet objet.
     * @return
     */
    public int nombreTotalItems()
    {
        int tempValue = 0;
        for (Map.Entry<ArgentObjet, Integer> entry : m_capaciteTiroir.entrySet())
        {
            tempValue+= entry.getValue();
        }
        return tempValue;
    }




    /**
     * Renvoie la capacité maximale de ce Tiroir pour ce type d'items
     * @param m
     * @return
     */
    public int capaciteMaxPour(ArgentObjet m)
    {
        if (m.nomLisible.contains("pièce"))
            return this.maxPiece;
        return this.maxBillet;
    }

    public TiroirCaisseLL()
    {
        this(false,false);
    }

    public TiroirCaisseLL(boolean estMoitierPlein)
    {
        this(estMoitierPlein,false);
    }
    public TiroirCaisseLL(boolean estMoitierPlein, boolean aucunePiece)
    {
        if (estMoitierPlein)
        {
            this.m_capaciteTiroir = new HashMap<ArgentObjet, Integer>();
            this.m_capaciteTiroir.put(ArgentObjet.billet100,this.maxBillet/2);
            this.m_capaciteTiroir.put(ArgentObjet.billet50,this.maxBillet/2);
            this.m_capaciteTiroir.put(ArgentObjet.billet20,this.maxBillet/2);
            this.m_capaciteTiroir.put(ArgentObjet.billet10,this.maxBillet/2);
            this.m_capaciteTiroir.put(ArgentObjet.billet5,this.maxBillet/2);

            if (!aucunePiece)
            {
                this.m_capaciteTiroir.put(ArgentObjet.piece2,this.maxPiece/2);
                this.m_capaciteTiroir.put(ArgentObjet.piece1,this.maxPiece/2);
                this.m_capaciteTiroir.put(ArgentObjet.piece25s,this.maxPiece/2);
                this.m_capaciteTiroir.put(ArgentObjet.piece10s,this.maxPiece/2);
                this.m_capaciteTiroir.put(ArgentObjet.piece5s,this.maxPiece/2);
                this.m_capaciteTiroir.put(ArgentObjet.piece1s,0);
            }

        }
        else
        {
            this.m_capaciteTiroir = new HashMap<ArgentObjet, Integer>();
            this.m_capaciteTiroir.put(ArgentObjet.billet100,this.maxBillet);
            this.m_capaciteTiroir.put(ArgentObjet.billet50,this.maxBillet);
            this.m_capaciteTiroir.put(ArgentObjet.billet20,this.maxBillet);
            this.m_capaciteTiroir.put(ArgentObjet.billet10,this.maxBillet);
            this.m_capaciteTiroir.put(ArgentObjet.billet5,this.maxBillet);

            if (!aucunePiece)
            {
                this.m_capaciteTiroir.put(ArgentObjet.piece2,this.maxPiece);
                this.m_capaciteTiroir.put(ArgentObjet.piece1,this.maxPiece);
                this.m_capaciteTiroir.put(ArgentObjet.piece25s,this.maxPiece);
                this.m_capaciteTiroir.put(ArgentObjet.piece10s,this.maxPiece);
                this.m_capaciteTiroir.put(ArgentObjet.piece5s,this.maxPiece);
                this.m_capaciteTiroir.put(ArgentObjet.piece1s,0);
            }

        }


    }




    /**
     * Retire plusieurs items du meme type
     * @param m le type
     * @param nombre le nombre d'items à retirer
     */
    public void retirerItems(ArgentObjet m, int nombre) throws Exception
    {
        if (nombre < 0)
            throw new InvalidParameterException("Les nombres négatif ne sont pas permis.");

        if (this.m_capaciteTiroir.get(m) < nombre)
            throw new ArgentManquantException("Il n'y a pas assez de billet ou de pièce dans la caisse.");

        if (m.equals(ArgentObjet.piece1s))
            throw new RetraitPiece1sException("Il est impossible de remettre en circulation des pièces de 1s.");

        this.m_capaciteTiroir.put(m,this.m_capaciteTiroir.get(m) - nombre);
    }
    private double obtenirValeurPourLeChange(ArgentObjet m, int nombre)
    {
        return(m.valeur() * nombre);
    }
}
