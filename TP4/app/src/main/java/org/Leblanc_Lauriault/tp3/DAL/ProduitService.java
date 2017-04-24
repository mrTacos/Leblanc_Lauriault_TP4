package org.Leblanc_Lauriault.tp3.DAL;


import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.Leblanc_Lauriault.tp3.Exception.EmptyInventoryException;
import org.Leblanc_Lauriault.tp3.Event.ListUpdatedEvent;
import org.Leblanc_Lauriault.tp3.Exception.ProductNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ProduitService
{
    private ProduitRepository produitRepository;
    private AchatRepository achatRepository;
    //private CustomAdapter customAdapter;
    private List<Produit> products;
    private Context context;
    public Bus bus = new Bus(ThreadEnforcer.ANY);

    public ProduitService(Context pContext/*, CustomAdapter pCustomAdapter*/, List<Produit> pProducts)
    {
        this.produitRepository = new ProduitRepository(pContext);
        this.achatRepository = new AchatRepository(pContext);
        //this.customAdapter = pCustomAdapter;
        this.products = pProducts;
        this.context = pContext;
    }


    /**
     * Sets the quantity of every product in the inventory to 0
     */
    public void emptyInventory()
    {
        this.clearList();
        for (Produit pItem:this.produitRepository.getAll())
        {
            pItem.setQuantite(0);
            this.produitRepository.save(pItem);
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
        this.produitRepository.deleteAll();
        Produit p;
        String[] productName = {"Sac de patate","Beurre","Lait 1% 2L.","Barre de chocolat","Sac de chips","Café cheap","Pain maison","Jus d'orange","Jus de pomme"};
        String[] productUPC = {"123456789128","111111111117","222222222224","333333333331","444444444448","555555555555","666666666662","777777777779","888888888886"};
        Double[] productPrice = {5.00d,2.25d,1.75d,1.00d,2.00d,0.75d,10.25d,2.15d,3.15d};
        for (int i=0; i < 9; i ++)
        {
            p = new Produit();
            p.setNom(productName[i]);
            p.setUpc(productUPC[i]);
            p.setPrixUnitaire(productPrice[i]);
            p.setQuantite(10);
            this.produitRepository.save(p);
        }
        ListUpdatedEvent e = new ListUpdatedEvent();
        bus.post(e);
    }

    public List<Produit> getAllProducts()
    {
        return this.produitRepository.getAll();
    }

    public Produit getRandomProduct()
    {
        try
        {
            return this.produitRepository.getRandomProduct();
        }
        catch (EmptyInventoryException e)
        {
            return null;
        }
    }

    public Produit getProductFromUPC(String pUPC)
    {
        try
        {
            return this.produitRepository.getByUPC(pUPC);
        }
        catch (ProductNotFoundException e)
        {
            return null;
        }
    }

    /**
     * Creates a random buying list with product in it
     */
    public void createRandomBuyingList()
    {
        this.seedDatabase();
        List<Produit> allProducts = this.produitRepository.getAll();
        Collections.shuffle(allProducts);
        for (int i = 0; i < 5; i++)
        {
            Random r = new Random();
            Produit p = allProducts.get(i);
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
        this.produitRepository.deleteAll();
        ListUpdatedEvent e = new ListUpdatedEvent();
        bus.post(e);
    }

    public Integer getProductCountById(Long pId)
    {
        return (this.produitRepository.getById(pId).getQuantite());
    }
    public void addProduct(Produit pToAdd)
    {
        this.produitRepository.save(pToAdd);
    }


    /*@Subscribe
    public void MinusEventAction(MinusEvent s)
    {
        int indexToModify = this.products.indexOf(s.product);
        int passedQuantity = this.products.get(indexToModify).getQuantite();
        if (passedQuantity > 1)
        {
            this.products.get(indexToModify).setQuantite(--passedQuantity);
        }
        else
        {
            this.products.remove(indexToModify);
        }
        ListUpdatedEvent e = new ListUpdatedEvent();
        bus.post(e);
    }*/

}
