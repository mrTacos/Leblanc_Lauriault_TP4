package org.lauriault.tp3;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lauriault.tp3.DAL.Achat;
import org.lauriault.tp3.DAL.AchatService;
import org.lauriault.tp3.DAL.Produit;
import org.lauriault.tp3.DAL.ProduitService;
import org.lauriault.tp3.Exception.BadProductException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 4/18/2017.
 */

public class TestBuyService
{
    private AchatService aService;
    private ProduitService pService;
    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<Produit> testProductList = new ArrayList<>();
        this.aService = new AchatService(appContext, testProductList);
        this.aService.deleteAllBuyLog();
        this.pService = new ProduitService(appContext,testProductList);
        this.pService.removeInventory();
    }
    @After
    public void tearDown()
    {
        this.aService.deleteAllBuyLog();
        this.aService = null;
    }
    @Test
    public void testCreateBuyAndGetAll()
    {
        List<Produit> pToAdd = new ArrayList<>();
        Produit p;
        for (int i = 1; i < 6; i++)
        {
            p = new Produit();
            p.setId(i);
            p.setQuantite(1);
            p.setUpc("11111111111" + i);
            p.setPrixUnitaire(10);
            p.setNom("Produit " + i);
            pToAdd.add(p);
            pService.addProduct(p);
        }
        Date before = new Date();
        long seconds = before.getTime()/1000l;
        aService = new AchatService(InstrumentationRegistry.getTargetContext(),pToAdd);
        aService.payForProducts();
        Assert.assertEquals(1,aService.getAllBuys().size());
        Assert.assertEquals(50d,aService.getAllBuys().get(0).getTotal(),0.0);
        Assert.assertTrue(aService.getAllBuys().get(0).getCompleteDescription() != "");
        Assert.assertEquals(seconds,aService.getAllBuys().get(0).getDateAchat().getTime()/1000l,1);
    }

    @Test
    public void testCheckTotal()
    {
        List<Produit> pToAdd = new ArrayList<>();
        Produit p;
        for (int i = 1; i < 6; i++)
        {
            p = new Produit();
            p.setId(i);
            p.setQuantite(1);
            p.setUpc("11111111111" + i);
            p.setPrixUnitaire(10);
            p.setNom("Produit " + i);
            pToAdd.add(p);
            pService.addProduct(p);
        }
        aService = new AchatService(InstrumentationRegistry.getTargetContext(),pToAdd);
        aService.payForProducts();
        Assert.assertEquals(50d,aService.getAllBuys().get(0).getTotal(),0.0);
    }
    @Test
    public void testDeleteAll()
    {
        List<Produit> pToAdd = new ArrayList<>();
        Produit p;
        for (int i = 1; i < 6; i++)
        {
            p = new Produit();
            p.setId(i);
            p.setQuantite(1);
            p.setUpc("11111111111" + i);
            p.setPrixUnitaire(10);
            p.setNom("Produit " + i);
            pToAdd.add(p);
            pService.addProduct(p);
        }
        aService = new AchatService(InstrumentationRegistry.getTargetContext(),pToAdd);
        aService.payForProducts();
        aService.deleteAllBuyLog();
        Assert.assertEquals(0,aService.getAllBuys().size());
    }
    @Test
    public void testEmptyListAfterBuy()
    {
        List<Produit> pToAdd = new ArrayList<>();
        Produit p;
        for (int i = 1; i < 6; i++)
        {
            p = new Produit();
            p.setId(i);
            p.setQuantite(1);
            p.setUpc("11111111111" + i);
            p.setPrixUnitaire(10);
            p.setNom("Produit " + i);
            pToAdd.add(p);
            pService.addProduct(p);
        }
        aService = new AchatService(InstrumentationRegistry.getTargetContext(),pToAdd);
        aService.payForProducts();

        Assert.assertEquals(0,pToAdd.size());
    }

    @Test(expected = BadProductException.class)
    public void kOtestCreateEmptyProductBuy()
    {
        List<Produit> pToAdd = new ArrayList<>();
        Produit p = null;
        pToAdd.add(p);

        aService = new AchatService(InstrumentationRegistry.getTargetContext(),pToAdd);
        aService.payForProducts();
    }

    @Test
    public void testCreateEmptyBuy()
    {
        List<Produit> pToAdd = new ArrayList<>();
        aService = new AchatService(InstrumentationRegistry.getTargetContext(),pToAdd);
        aService.payForProducts();
        Assert.assertEquals(0,aService.getAllBuys().size());
    }
}
