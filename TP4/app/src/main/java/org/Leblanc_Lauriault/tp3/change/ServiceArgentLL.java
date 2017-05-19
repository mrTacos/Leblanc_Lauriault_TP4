package org.Leblanc_Lauriault.tp3.change;


import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.ArrondiNombreNegatifException;
import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.MontantInatteignableException;
import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.TransactionMontantInvalidException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ServiceArgentLL implements ServiceArgent
{

    private TiroirCaisse tcl;

    public ServiceArgentLL()
    {
        this(false,false);
    }
    public ServiceArgentLL(boolean estTiroirSpecial)
    {
        this(estTiroirSpecial,false);
    }
    public ServiceArgentLL(boolean estTiroirSpecial, boolean aucunePiece)
    {
        this.tcl = new TiroirCaisseLL(estTiroirSpecial,aucunePiece);
    }



    /**
     * Calcul le change a rendre pour le montant fourni en utilisant ce qui est disponible
     * dans le TiroirCaisse.
     *
     * Le TiroirCaisse est modifié par la méthode qui y enlève des ArgentObjet qui se
     * retrouve dans le change.
     *
     * Les objets ArgentObjet du Change renvoyé sont ceux qui sont pris dans TiroirCaisse:
     * la somme des valeurs totales entre
     * - le tiroir avant
     * - le tiroir après + le change après
     * devraient être le même montant, et le même nombre d'objets pour chaque type d'objet.
     *
     * @param montant
     * @return
     * @throws ArgentException
     */
    public Change calculerChange(double montant) throws Exception
    {

        if (montant < 0)
        {
            throw new TransactionMontantInvalidException("Un montant négatif!!!");
        }
        List<ArgentObjet> tentativePasser = new ArrayList<ArgentObjet>();
        List<ArgentObjet> tentativeCourrant = new ArrayList<ArgentObjet>();

        Change change = new ChangeLL();
        double montantArrondi = montant;
        montantArrondi = arrondiA5sous(montantArrondi);
        int montantArrondiCent = (int) (montantArrondi * 100);


        ArgentObjet startingPointAfterFailure = null;

        while (montantArrondiCent >=4)
        {
            for (ArgentObjet ag : ArgentObjet.values())
            {

                if (tentativePasser.contains(ag))
                    continue;

                //On est rendu au plus petit montant.
                if (ag.valeurEnCents == ArgentObjet.piece1s.valeurEnCents)
                {
                    if (this.aAsserDansLeReste(tentativeCourrant.get(0),montant) /*&& tentativePasser.contains(ag)*/)
                    {

                        ArgentObjet tempTest = null;
                        for (ArgentObjet test:ArgentObjet.values())
                        {
                            if(test.ordinal() == tentativeCourrant.get(0).ordinal() && tempTest == null)
                            {
                                tempTest = test;
                            }
                        }
                        for (ArgentObjet objet:ArgentObjet.values())
                        {
                            this.tcl.ajouterItem(objet,change.nombreItemsPour(objet));
                        }
                        tentativePasser.add(tentativeCourrant.get(0));
                        tentativeCourrant = new ArrayList<ArgentObjet>();

                        change = new ChangeLL();
                        montantArrondiCent = (int) (montantArrondi * 100);
                        startingPointAfterFailure = tempTest;
                        continue;
                    }

                    for (ArgentObjet objet:ArgentObjet.values())
                    {
                        this.tcl.ajouterItem(objet,change.nombreItemsPour(objet));
                    }
                    throw new MontantInatteignableException();
                }
                if (((this.aAsserDansLeReste(startingPointAfterFailure,montant)) && montantArrondiCent > ag.valeurEnCents && tcl.nombreItemsPour(ag) > 0 )||montantArrondiCent >= ag.valeurEnCents && tcl.nombreItemsPour(ag) > 0  )
                {
                    tentativeCourrant.add(ag);
                    change.ajouterItem(ag, 1);
                    tcl.retirerItems(ag, 1);
                    montantArrondiCent -= ag.valeurEnCents;
                    break;
                }
            }
        }
        return change;
    }

    public void ajouterChangeDuClient(Change pChange) throws Exception {
        for (ArgentObjet ao:ArgentObjet.values())
        {
            if (pChange.nombreItemsPour(ao) >0)
            {
                this.tcl.ajouterItem(ao,pChange.nombreItemsPour(ao));
            }
        }
    }

    /**
     * Vérifie si dans le reste de la liste, il contient asser d'argent pour éviter une erreur.
     * @return
     */
    private boolean aAsserDansLeReste(ArgentObjet objetDepart, double montantCible)
    {

        TiroirCaisse temp = this.tcl;
        double tempMontant = 0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        for (ArgentObjet objet:ArgentObjet.values())
        {
            try
            {
                if (objet.valeurEnCents < objetDepart.valeurEnCents)
                {
                    //temp.retirerItems(objet,temp.nombreItemsPour(objet));
                    tempMontant += (temp.nombreItemsPour(objet) * objet.valeur());
                }
            }
            catch (Exception e)
            {

            }
        }
        return (tempMontant >= Double.valueOf(df.format(montantCible)));
    }

    public double arrondiA5sous(double montant) throws ArrondiNombreNegatifException {
        if (montant == 0.01d || montant == 0.02d)
            return 0.00d;

        if (montant <= 0)
            throw new ArrondiNombreNegatifException("Impossible d'arrondir un nombre négatif");

        int restant = (int) Math.round((montant % 0.1) * 100);
        int demi = 5;
        int plein = 10;

        if (restant == 1 || restant == 2) {
            return (montant - ((double) restant / 100));
        } else if (restant == 3 || restant == 4) {
            return (montant + ((double) (demi - restant) / 100));
        } else if (restant == 6 || restant == 7) {
            double test = (double) (restant - demi) / 100;
            return (double) Math.round((montant - test) * 100) / 100;
        } else if (restant == 8 || restant == 9) {
            return (double) Math.round((montant + ((double) (plein - restant) / 100)) * 100) / 100;
        }
        return montant;
    }

    /**
     * Renvoie une référence vers le tiroir caisse de ce service.
     * Permet de consulter l'état du tiroir ou d'y rajouter de l'argent.
     * @return
     */
    public TiroirCaisse getTiroir()
    {
        return this.tcl;
    }

    /**
     * Doit renvoyer le nom de l'étudiant dont ceci est le travail.
     * @return
     */
    public String nomEtudiant()
    {
        return "Alexandre Lauriault";
    }

    /**
     * Doit renvoyer un nouvel objet service qui contient un TiroirCaisse:
     * - à moitié plein pour tous les objets (billets ou pièces)
     * - vide pour les pièces de 1 sou (puisqu'on ne peut pas en rendre, uniquement)
     * @return
     */
    public ServiceArgent serviceAvecTiroirMoitiePlein()
    {
        ServiceArgent tempSA = new ServiceArgentLL(true);
        return tempSA;
    }
}

