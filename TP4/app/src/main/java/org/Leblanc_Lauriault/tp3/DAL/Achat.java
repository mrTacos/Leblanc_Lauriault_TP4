package org.Leblanc_Lauriault.tp3.DAL;

import org.Leblanc_Lauriault.tp3.Exception.BadProductException;

import java.util.Date;
import java.util.List;

public class Achat
{
    private Long ID;
    private List<Produit> produits;
    private Date dateAchat;

    public Double getTotal()
    {
        Double total = 0d;
        for (Produit item:this.produits)
        {
            total+= (item.getPrixAvantTaxe() * item.getQuantite());
        }
        return total;
    }
    public String getCompleteDescription()
    {
        String complete = " ";
        for (Produit item:this.produits)
        {
            complete+= item.toString();
            complete+= "\n ";
        }
        return complete;
    }
    public Long getId() {
        return ID;
    }
    public void setId(Long ID) {
        this.ID = ID;
    }
    public List<Produit> getProduits() {
        return produits;
    }
    public void setProduits(List<Produit> produits)
    {
        if (produits == null)
            throw new IllegalArgumentException("Produits null");

        for (Produit p:produits)
        {
            if (p == null)
                throw new BadProductException("Le produit est null ou manque des informations");
        }
        this.produits = produits;
    }
    public Date getDateAchat() {
        return dateAchat;
    }
    public void setDateAchat(Date dateAchat)
    {
        if (dateAchat == null)
            throw new IllegalArgumentException("Date null");
        this.dateAchat = dateAchat;
    }
}
