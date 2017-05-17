package org.Leblanc_Lauriault.tp3.change;


import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.ChangeNegatifException;

import java.util.HashMap;
import java.util.Map;

/**
 * Représente du change (en ensemble de billet et de monnaie représentant une valeur total)
 */
public class ChangeLauriault implements Change
{

    // Liste contenant des tupple avec comme clee de l'argent et comme valeur le nombre de fois que l'argent apparait.
    private Map<ArgentObjet, Integer> m_changeTotal;


    /**
     * Renvoie le nombre d'items de ce type contenu dans cet objet
     * @param m type
     * @return
     */
    public int nombreItemsPour(ArgentObjet m)
    {
        if (this.m_changeTotal.containsKey(m))
            return this.m_changeTotal.get(m);
        else
            return 0;
    }



    /**
     * Ajouter un objet du type spécifié m fois.
     * @param m
     * @param nombre
     */
    public void ajouterItem(ArgentObjet m, int nombre) throws ChangeNegatifException {
        if (nombre < 0)
            throw new ChangeNegatifException();

        if (this.m_changeTotal.containsKey(m))
        {
            this.m_changeTotal.put(m,this.m_changeTotal.get(m)+nombre);
        }
        else
        {
            this.m_changeTotal.put(m,nombre);
        }

    }



    /**
     * Renvoie la valeur totale de l'objet en dollars
     * @return
     */
    public double valeurTotale()
    {

        int totalValue = 0;
        for (Map.Entry<ArgentObjet, Integer> entry : m_changeTotal.entrySet())
        {
            totalValue += ((double)entry.getKey().valeurEnCents * (double)entry.getValue());
        }
        return (totalValue/100d);
    }



    /**
     * Renvoie le nombre total d'items contenu dans cet objet.
     * @return
     */
    public int nombreTotalItems()
    {
        int tempValue = 0;
        for (Map.Entry<ArgentObjet, Integer> entry : m_changeTotal.entrySet())
        {
            tempValue += entry.getValue();
        }
        return tempValue;
    }

    public ChangeLauriault()
    {
        this.m_changeTotal = new HashMap<ArgentObjet, Integer>();
    }
}
