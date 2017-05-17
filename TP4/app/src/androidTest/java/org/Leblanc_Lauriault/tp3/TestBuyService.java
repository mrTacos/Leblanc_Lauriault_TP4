package org.Leblanc_Lauriault.tp3;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.Leblanc_Lauriault.tp3.DAL.AchatProduitService;
import org.Leblanc_Lauriault.tp3.DAL.TaxeType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.Leblanc_Lauriault.tp3.DAL.Produit;
import org.Leblanc_Lauriault.tp3.Exception.BadProductException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestBuyService
{

    private AchatProduitService apService;
    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<Produit> testProductList = new ArrayList<>();
        this.apService = new AchatProduitService(appContext, testProductList);
        this.apService.supprimerTousLesLogs();
        this.apService.removeInventory();
    }
    @After
    public void tearDown()
    {
        this.apService.supprimerTousLesLogs();
        this.apService = null;
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
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);

        p = new Produit();
        p.setId(2);
        p.setQuantite(1);
        p.setUpc("036000291452");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 2");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);

        p = new Produit();
        p.setId(3);
        p.setQuantite(1);
        p.setUpc("188114771211");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 3");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);

        Date before = new Date();
        long seconds = before.getTime()/1000l;
        apService.setProductList(pToAdd);
        apService.payerLesProduit();
        Assert.assertEquals(1,apService.obtenirTousLesAchats().size());
        Assert.assertEquals(31.5,apService.obtenirTousLesAchats().get(0).getTotal(),0.0);
        Assert.assertTrue(apService.obtenirTousLesAchats().get(0).getCompleteDescription() != "");
        Assert.assertEquals(seconds,apService.obtenirTousLesAchats().get(0).getDateAchat().getTime()/1000l,1);
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
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);

        p = new Produit();
        p.setId(2);
        p.setQuantite(1);
        p.setUpc("036000291452");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 2");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);

        p = new Produit();
        p.setId(3);
        p.setQuantite(1);
        p.setUpc("188114771211");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 3");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);
        apService.setProductList(pToAdd);
        apService.payerLesProduit();
        Assert.assertEquals(31.5,apService.obtenirTousLesAchats().get(0).getTotal(),0.0);
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
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);

        p = new Produit();
        p.setId(2);
        p.setQuantite(1);
        p.setUpc("036000291452");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 2");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);

        p = new Produit();
        p.setId(3);
        p.setQuantite(1);
        p.setUpc("188114771211");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 3");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);
        apService.setProductList(pToAdd);
        apService.payerLesProduit();
        apService.supprimerTousLesLogs();
        Assert.assertEquals(0,apService.obtenirTousLesAchats().size());
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
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);

        p = new Produit();
        p.setId(2);
        p.setQuantite(1);
        p.setUpc("036000291452");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 2");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);

        p = new Produit();
        p.setId(3);
        p.setQuantite(1);
        p.setUpc("188114771211");
        p.setPrixAvantTaxe(10);
        p.setNom("Produit 3");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        pToAdd.add(p);
        apService.addProduct(p);
        apService.setProductList(pToAdd);
        apService.payerLesProduit();

        Assert.assertEquals(0,pToAdd.size());
    }

    @Test(expected = BadProductException.class)
    public void kOtestCreateEmptyProductBuy()
    {
        List<Produit> pToAdd = new ArrayList<>();
        Produit p = null;
        pToAdd.add(p);
        apService.setProductList(pToAdd);
        apService.payerLesProduit();
    }

    @Test
    public void testCreateEmptyBuy()
    {
        List<Produit> pToAdd = new ArrayList<>();
        apService.setProductList(pToAdd);
        apService.payerLesProduit();
        Assert.assertEquals(0,apService.obtenirTousLesAchats().size());
    }
}
