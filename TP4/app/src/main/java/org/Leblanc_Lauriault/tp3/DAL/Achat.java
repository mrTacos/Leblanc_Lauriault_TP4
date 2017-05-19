package org.Leblanc_Lauriault.tp3.DAL;

import org.Leblanc_Lauriault.tp3.Exception.BadProductException;
import org.Leblanc_Lauriault.tp3.Exception.ProductNotFoundException;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class Achat implements IEntity {
    private Long ID;
    private List<Produit> produits;
    private Date dateAchat;

    public Double getTotal()
    {
        double total1 = 0;
        double total2 = 0;
        double total3 = 0;
        double total4 = 0;

        for (Produit p: this.produits
                ) {
            if(p.isDeuxPourUn() == true)
            {
                int a = p.getQuantite();
                while(a > 0 )
                {
                    if(a%2 == 0)
                    {
                        total1 += (a/2) * p.getPrixAvantTaxe();
                        break;
                    }
                    else
                    {
                        total1 += p.getPrixAvantTaxe();
                        a--;
                    }
                }
            }
            else
            {
                total1 += p.getPrixAvantTaxe() * p.getQuantite();
            }
        }

        for (Produit p: this.produits
                ) {
            if(p.isDeuxPourUn() == true)
            {
                int a = p.getQuantite();
                while(a > 0 )
                {
                    if(a%2 == 0)
                    {
                        total2 += (a/2) * p.getPrixApresTaxe();
                        break;
                    }
                    else
                    {
                        total2 += p.getPrixApresTaxe();
                        a--;
                    }
                }
            }
            else
            {
                total2 += p.getPrixApresTaxe() * p.getQuantite();
            }
        }

        for (Produit p: this.produits
                ) {
            total3 += p.getPrixAvantTaxe() * p.getQuantite();
        }
        for (Produit p: this.produits
                ) {
            total4 += p.getPrixApresTaxe() * p.getQuantite();
        }

        if(total1 < 100 || total3 <100)
        {
            if(total2 < total4)
            {
                return  total2;
            }
            return total4;
        }
        else
        {
            if(total1 < total3)
            {
                return total1;
            }
        }
        return total3;
    }

    public static Double getTotalFromProducts(List<Produit> pProduits)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        double total1 = 0;
        double total2 = 0;
        double total3 = 0;
        double total4 = 0;

        for (Produit p: pProduits
                ) {
            if(p.isDeuxPourUn() == true)
            {
                int a = p.getQuantite();
                while(a > 0 )
                {
                    if(a%2 == 0)
                    {
                        total1 += (a/2) * p.getPrixAvantTaxe();
                        break;
                    }
                    else
                    {
                        total1 += p.getPrixAvantTaxe();
                        a--;
                    }
                }
            }
            else
            {
                total1 += p.getPrixAvantTaxe() * p.getQuantite();
            }
        }

        for (Produit p: pProduits
                ) {
            if(p.isDeuxPourUn() == true)
            {
                int a = p.getQuantite();
                while(a > 0 )
                {
                    if(a%2 == 0)
                    {
                        total2 += (a/2) * p.getPrixApresTaxe();
                        break;
                    }
                    else
                    {
                        total2 += p.getPrixApresTaxe();
                        a--;
                    }
                }
            }
            else
            {
                total2 += p.getPrixApresTaxe() * p.getQuantite();
            }
        }

        for (Produit p: pProduits
                ) {
            total3 += p.getPrixAvantTaxe() * p.getQuantite();
        }
        for (Produit p: pProduits
                ) {
            total4 += p.getPrixApresTaxe() * p.getQuantite();
        }

        if(total1 < 100 || total3 <100)
        {
            if(total2 < total4)
            {
                return Double.valueOf(df.format(total2).replace(',','.'));
            }
            return Double.valueOf(df.format(total4).replace(',','.'));
        }
        else
        {
            if(total1 < total3)
            {
                return Double.valueOf(df.format(total1).replace(',','.'));
            }
        }

        return Double.valueOf(df.format(total3).replace(',','.'));
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
    public long getId() {
        return ID;
    }
    public void setId(long ID) {
        this.ID = ID;
    }
    public List<Produit> getProduits() {
        return produits;
    }
    public Achat()
    {
        this.ID = -1l;
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
