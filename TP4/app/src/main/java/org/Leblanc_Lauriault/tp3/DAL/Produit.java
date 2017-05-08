package org.Leblanc_Lauriault.tp3.DAL;

import org.Leblanc_Lauriault.tp3.Exception.BadlyFormedUPCException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

import static android.R.attr.description;

public class Produit {
	
	private Long id;
	private String nom;
	private String upc;
	private Integer quantite;
	private double prixAvantTaxe;
	private TaxeType typeTaxe;
	private double prixUnitaire;
	private boolean deuxPourUn;



	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc)
	{
		if (upc == null)
			throw new IllegalArgumentException("UPC null");
		if (upc.length() != 12)
			throw new BadlyFormedUPCException("Le UPC doit être exactement 12 chiffre de long");
		if (!Pattern.matches("[0-9]+", upc))
			throw new BadlyFormedUPCException("Le upc contient des lettres !");
		this.upc = upc;
	}


	public Integer getQuantite()
	{
		return quantite;
	}

	public void setQuantite(Integer quantite)
	{
		if (quantite == null)
			throw new IllegalArgumentException("quantité null");
		if (quantite < 0)
			throw new IllegalArgumentException("Une quantité négative est impossible");
		this.quantite = quantite;
	}



	public void setId(long i)
	{
		if (i  < 1)
			throw new IllegalArgumentException("Le Id doit être de 1 et plus");
		this.id = i;
	}
	public Long getId() {
		return id;
	}




	public String getNom() {
		return nom;
	}
	public void setNom(String nom)
	{
		if (nom == null)
			throw new IllegalArgumentException("Nom null");
		if (nom == "")
			throw  new IllegalArgumentException("le nom ne peut pas être vide");

		this.nom = nom;
	}

    public TaxeType getTypeTaxe()
    {
        return typeTaxe;
    }

    public void setTypeTaxe(TaxeType typeTaxe)
    {
        this.typeTaxe = typeTaxe;
    }

    public double getPrixAvantTaxe()
	{
		BigDecimal bd = new BigDecimal(prixAvantTaxe);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	public void setPrixAvantTaxe(double prixAvantTaxe)
	{
		if (prixAvantTaxe < 0.01)
			throw new IllegalArgumentException("Le prix ne peut être plus petit qu'un sous");

		BigDecimal bd = new BigDecimal(prixAvantTaxe);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		this.prixAvantTaxe = bd.doubleValue();
	}
	public double getPrixApresTaxe()
	{
		return this.prixAvantTaxe * this.typeTaxe.valeurEnPourcentage;
	}

	public boolean isDeuxPourUn() {
		return deuxPourUn;
	}

	public void setDeuxPourUn(boolean deuxPourUn) {
		this.deuxPourUn = deuxPourUn;
	}

	@Override
	public String toString() {
		return "Produit [id=" + id + ", nom=" + nom
				+ ", description=" + description +  ", prixAvantTaxe=" + prixAvantTaxe +  ", quantité=" + quantite + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Produit produit = (Produit) o;

		if (!id.equals(produit.id)) return false;
		return upc.equals(produit.upc);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + upc.hashCode();
		return result;
	}
}
