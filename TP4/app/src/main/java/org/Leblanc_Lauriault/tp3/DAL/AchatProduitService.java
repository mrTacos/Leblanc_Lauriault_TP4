package org.Leblanc_Lauriault.tp3.DAL;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.Leblanc_Lauriault.tp3.Event.ListUpdatedEvent;
import org.Leblanc_Lauriault.tp3.Exception.BadlyFormedUPCException;
import org.Leblanc_Lauriault.tp3.Exception.EmptyInventoryException;
import org.Leblanc_Lauriault.tp3.Exception.ProductNotFoundException;
import org.Leblanc_Lauriault.tp3.Helper.BarcodeValidator;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;


public class AchatProduitService
{
    private GenericRepository<Produit> pRepo;
    private GenericRepository<Achat> aRepo;
    private List<Produit> products;
    public Bus bus = new Bus(ThreadEnforcer.ANY);
    private Context context;



    public AchatProduitService(Context pContext, List<Produit> pProducts)
    {
        pRepo = new GenericRepository<Produit>(pContext,Produit.class);
        aRepo = new GenericRepository<Achat>(pContext,Achat.class);
        this.products = pProducts;
        this.context = pContext;
    }

    public void setProductList(List<Produit> pList)
    {
        this.products = pList;
    }

    //region ACHAT SERVICE
    public void supprimerTousLesLogs()
    {
        this.aRepo.deleteAll();
    }

    public List<Achat> obtenirTousLesAchats()
    {
        return this.aRepo.getAll();
    }


    public void payerLesProduit()
    {
        if(products.size() == 0)
            return;

        Achat a = new Achat();
        a.setDateAchat(new Date());
        a.setProduits(products);
        aRepo.save(a);

        //removes the product in the inventory
        for (Produit pItem:products)
        {
            int qInRepo = pRepo.getById(pItem.getId()).getQuantite();
            Produit p = pRepo.getById(pItem.getId());
            p.setQuantite(qInRepo-pItem.getQuantite());
            pRepo.save(p);
        }
        this.viderListe();
        ListUpdatedEvent e = new ListUpdatedEvent();
        bus.post(e);
        this.loggerTousLesAchats();
    }
    private void viderListe()
    {
        this.products.clear();
    }
    public void loggerTousLesAchats()
    {
        for (Achat a:this.aRepo.getAll())
        {
            int total = 0;
            for (Produit p: a.getProduits())
            {
                total += p.getPrixAvantTaxe() * p.getQuantite();
            }
            int total2 = 0;
            for (Produit p: a.getProduits())
            {
                total2 += p.getPrixApresTaxe() * p.getQuantite();
            }
            Log.i("ACHAT","Montant avant taxe: " +total + "\nTaxes: " + (a.getTotal() - total) + "\nMontant total: " + a.getTotal().toString() + "\nMontant dû aux rabais: " + (a.getTotal() - total2) +  "\nDate: " + a.getDateAchat().toString() + "\nLes produits:\n" +
                    a.getCompleteDescription());
        }
    }
    public void modifierProduit(Produit produit)
    {
        this.pRepo.save(produit);
    }
    public void viderTousLesAchats()
    {
        this.aRepo.deleteAll();
    }
    //endregion

    public void emptyInventory()
    {
        this.clearList();
        for (Produit pItem:this.pRepo.getAll())
        {
            pItem.setQuantite(0);
            this.pRepo.save(pItem);
        }
        ListUpdatedEvent e = new ListUpdatedEvent();
        bus.post(e);
    }

    /**
     * Seed the database with data for testing
     */
    public void seedDatabase()
    {

        this.clearList();
        this.pRepo.deleteAll();
        Produit p;
        String[] productName = {"Sac de patate","Beurre","Lait 1% 2L.","Barre de chocolat","Sac de chips","Café cheap","Pain maison","Jus d'orange","Jus de pomme"};
        String[] productUPC = {"123456789128","111111111117","222222222224","333333333331","444444444448","555555555555","666666666662","777777777779","888888888886"};
        Double[] productPrice = {5.00d,2.25d,1.75d,1.00d,2.00d,0.75d,10.25d,2.15d,3.15d};
        for (int i=0; i < 9; i ++)
        {
            p = new Produit();
            p.setNom(productName[i]);
            p.setUpc(productUPC[i]);
            p.setPrixAvantTaxe(productPrice[i]);
            p.setQuantite(10);
            p.setDeuxPourUn(false);
            p.setTypeTaxe(TaxeType.taxeEssentiel);
            this.pRepo.save(p);
        }
        ListUpdatedEvent e = new ListUpdatedEvent();
        bus.post(e);
    }

    public List<Produit> getAllProducts()
    {
        return this.pRepo.getAll();
    }

    public Produit getRandomProduct()
    {
        List<Produit> products = pRepo.getAll();
        Random r = new Random();
        if (products.size() == 0)
            return null;
        return (products.get(r.nextInt(products.size())));
    }


    public Produit getProductFromUPC(String pUPC) throws ProductNotFoundException
    {
        if (pUPC.length() != 12 ||!BarcodeValidator.isUPCValid(pUPC))
            throw new BadlyFormedUPCException("Le upc en paramètre n'est pas un upc valide");

        if (!Pattern.matches("[0-9]+", pUPC))
            throw new IllegalArgumentException("Le upc contient des lettres !");

        List<Produit> allProducts = pRepo.getAll();
        Produit pToReturn = null;
        for (Produit produit : allProducts)
        {
            if (produit.getUpc().equals(pUPC))
                pToReturn = produit;
        }

        if (pToReturn == null)
            throw new ProductNotFoundException("Il n'y a aucun produit avec ce UPC");
        else
            return pToReturn;
    }

    /**
     * Creates a random buying list with product in it
     */
    public void createRandomBuyingList()
    {
        if (this.pRepo.getAll().size() == 0)
        {
            Toast.makeText(context, "Impossible de créer une liste sans inventaire. Veuillez créer l'inventaire avant !", Toast.LENGTH_LONG).show();
            return;
        }
        this.clearList();
        List<Produit> allProducts = this.pRepo.getAll();
        Collections.shuffle(allProducts);

        for (int i = 0; i < 5; i++)
        {
            Random r = new Random();
            Produit p = allProducts.get(i);
            p.setQuantite(10);
            this.pRepo.save(p);
            allProducts.remove(p);
            p.setQuantite(r.nextInt(5)+1);
            products.add(p);
            Collections.shuffle(allProducts);
        }
        ListUpdatedEvent e = new ListUpdatedEvent();
        bus.post(e);
    }

    private void clearList()
    {
        this.products.clear();
        ListUpdatedEvent e = new ListUpdatedEvent();
        bus.post(e);
        //this.customAdapter.notifyDataSetChanged();
    }

    public void removeInventory()
    {
        this.clearList();
        this.pRepo.deleteAll();
        ListUpdatedEvent e = new ListUpdatedEvent();
        bus.post(e);
    }

    public Integer getProductCountById(Long pId)
    {
        return (this.pRepo.getById(pId).getQuantite());
    }
    public void addProduct(Produit pToAdd)
    {
        this.pRepo.save(pToAdd);
    }





}
