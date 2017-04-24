package org.Leblanc_Lauriault.tp3.DAL;


import android.content.Context;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.Leblanc_Lauriault.tp3.Event.ListUpdatedEvent;

import java.util.Date;
import java.util.List;

public class AchatService
{
    private ProduitRepository produitRepository;
    private AchatRepository achatRepository;
    public Bus bus = new Bus(ThreadEnforcer.ANY);
    private List<Produit> products;

    public AchatService(Context pContext, List<Produit> pProducts)
    {
        produitRepository = new ProduitRepository(pContext);
        achatRepository = new AchatRepository(pContext);
        this.products = pProducts;
    }

    public void deleteAllBuyLog()
    {
        this.achatRepository.deleteAll();
    }

    public List<Achat> getAllBuys()
    {
        return this.achatRepository.getAll();
    }

    public void payForProducts()
    {
        if(products.size() == 0)
            return;

        Achat a = new Achat();
        a.setDateAchat(new Date());
        a.setProduits(products);
        achatRepository.save(a);
        //removes the product in the inventory
        for (Produit pItem:products)
        {
            int qInRepo = produitRepository.getById(pItem.getId()).getQuantite();
            Produit p = produitRepository.getById(pItem.getId());
            p.setQuantite(qInRepo-pItem.getQuantite());
            produitRepository.save(p);
        }
        this.clearList();
        ListUpdatedEvent e = new ListUpdatedEvent();
        bus.post(e);
        this.logAllBuy();
        //Log.i("ACHAT","Montant total: " + achatRepository.getById(a.getId()).getTotal().toString() + "\nDate: " + achatRepository.getById(a.getId()).getDateAchat().toString() + "\nLes produits:\n" + achatRepository.getById(a.getId()).getCompleteDescription());
    }
    private void clearList()
    {
        this.products.clear();
    }
    public void logAllBuy()
    {
        for (Achat a:this.achatRepository.getAll())
        {
            Log.i("ACHAT","Montant total: " + a.getTotal().toString() + "\nDate: " + a.getDateAchat().toString() + "\nLes produits:\n" + a.getCompleteDescription());
        }
    }
    public void clearAllBuy()
    {
        this.achatRepository.deleteAll();
    }






}
