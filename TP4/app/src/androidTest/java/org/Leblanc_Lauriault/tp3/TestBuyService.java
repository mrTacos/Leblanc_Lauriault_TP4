package org.Leblanc_Lauriault.tp3;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.Leblanc_Lauriault.tp3.DAL.AchatService;
import org.Leblanc_Lauriault.tp3.DAL.Produit;
import org.Leblanc_Lauriault.tp3.DAL.ProduitService;
import org.Leblanc_Lauriault.tp3.Exception.BadProductException;

import java.util.ArrayList;
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

        p = new Produit();
        p.setId(1);
        p.setQuantite(1);
        p.setUpc("892685001003");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 1");
        pToAdd.add(p);
        pService.addProduct(p);

        p = new Produit();
        p.setId(2);
        p.setQuantite(1);
        p.setUpc("036000291452");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 2");
        pToAdd.add(p);
        pService.addProduct(p);

        p = new Produit();
        p.setId(3);
        p.setQuantite(1);
        p.setUpc("188114771211");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 3");
        pToAdd.add(p);
        pService.addProduct(p);

        Date before = new Date();
        long seconds = before.getTime()/1000l;
        aService = new AchatService(InstrumentationRegistry.getTargetContext(),pToAdd);
        aService.payForProducts();
        Assert.assertEquals(1,aService.getAllBuys().size());
        Assert.assertEquals(30d,aService.getAllBuys().get(0).getTotal(),0.0);
        Assert.assertTrue(aService.getAllBuys().get(0).getCompleteDescription() != "");
        Assert.assertEquals(seconds,aService.getAllBuys().get(0).getDateAchat().getTime()/1000l,1);
    }

    @Test
    public void testCheckTotal()
    {
        List<Produit> pToAdd = new ArrayList<>();
        Produit p;

        p = new Produit();
        p.setId(1);
        p.setQuantite(1);
        p.setUpc("892685001003");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 1");
        pToAdd.add(p);
        pService.addProduct(p);

        p = new Produit();
        p.setId(2);
        p.setQuantite(1);
        p.setUpc("036000291452");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 2");
        pToAdd.add(p);
        pService.addProduct(p);

        p = new Produit();
        p.setId(3);
        p.setQuantite(1);
        p.setUpc("188114771211");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 3");
        pToAdd.add(p);
        pService.addProduct(p);
        aService = new AchatService(InstrumentationRegistry.getTargetContext(),pToAdd);
        aService.payForProducts();
        Assert.assertEquals(30d,aService.getAllBuys().get(0).getTotal(),0.0);
    }
    @Test
    public void testDeleteAll()
    {
        List<Produit> pToAdd = new ArrayList<>();
        Produit p;
        p = new Produit();
        p.setId(1);
        p.setQuantite(1);
        p.setUpc("892685001003");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 1");
        pToAdd.add(p);
        pService.addProduct(p);

        p = new Produit();
        p.setId(2);
        p.setQuantite(1);
        p.setUpc("036000291452");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 2");
        pToAdd.add(p);
        pService.addProduct(p);

        p = new Produit();
        p.setId(3);
        p.setQuantite(1);
        p.setUpc("188114771211");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 3");
        pToAdd.add(p);
        pService.addProduct(p);

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
        p = new Produit();
        p.setId(1);
        p.setQuantite(1);
        p.setUpc("892685001003");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 1");
        pToAdd.add(p);
        pService.addProduct(p);

        p = new Produit();
        p.setId(2);
        p.setQuantite(1);
        p.setUpc("036000291452");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 2");
        pToAdd.add(p);
        pService.addProduct(p);

        p = new Produit();
        p.setId(3);
        p.setQuantite(1);
        p.setUpc("188114771211");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 3");
        pToAdd.add(p);
        pService.addProduct(p);
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
