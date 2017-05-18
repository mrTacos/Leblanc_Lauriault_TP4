package org.Leblanc_Lauriault.tp3;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import junit.framework.Assert;

import org.Leblanc_Lauriault.tp3.DAL.Achat;
import org.Leblanc_Lauriault.tp3.DAL.AchatProduitService;
import org.Leblanc_Lauriault.tp3.DAL.GenericRepository;
import org.Leblanc_Lauriault.tp3.DAL.ProduitRepository;
import org.Leblanc_Lauriault.tp3.DAL.TaxeType;
import org.Leblanc_Lauriault.tp3.Exception.ProductNotFoundException;
import org.Leblanc_Lauriault.tp3.GUI.MainPage;
import org.Leblanc_Lauriault.tp3.change.Change;
import org.Leblanc_Lauriault.tp3.change.ServiceArgentLL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.Leblanc_Lauriault.tp3.DAL.Produit;
import org.Leblanc_Lauriault.tp3.Exception.BadlyFormedUPCException;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class TestProductService
{
    private AchatProduitService paService;
    private List<Produit> testProductList;
    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        this.testProductList = new ArrayList<>();
        this.paService = new AchatProduitService(appContext, testProductList);
        paService.removeInventory();
    }
    @After
    public void tearDown()
    {
        this.paService.removeInventory();
        this.paService = null;
    }

    /**
     * Savegarde et getAll
     */
    @Test
    public void testAddProductAndGetAll(){
        Produit p = new Produit();
        p.setNom("Produit Test");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("221122221122");
        this.paService.addProduct(p);
        assertEquals(this.paService.getAllProducts().size(), 1);
    }
    /**
     * save un produit avec un nom incorrect
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveBadProductName()
    {
        Produit p = new Produit();
        p.setNom("");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("221122221122");
        this.paService.addProduct(p);
    }
    /**
     * save un produit avec une prix incorrect
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveBadProductPrice()
    {
        Produit p = new Produit();
        p.setNom("test");
        p.setPrixAvantTaxe(-10);
        p.setQuantite(1);
        p.setUpc("221122221122");
        this.paService.addProduct(p);
    }

    /**
     * save un produit avec une quantité incorrect
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveBadProductQuantity()
    {
        Produit p = new Produit();
        p.setNom("test");
        p.setPrixAvantTaxe(10);
        p.setQuantite(-1);
        p.setUpc("221122221122");
        this.paService.addProduct(p);
    }

    /**
     * save un produit avec un UPC incorrect
     */
    @Test(expected = BadlyFormedUPCException.class)
    public void testSaveBadProductUPCshort()
    {
        Produit p = new Produit();
        p.setNom("test");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("11");
        this.paService.addProduct(p);
    }
    /**
     * save un produit avec un UPC incorrect
     */
    @Test(expected = BadlyFormedUPCException.class)
    public void testSaveBadProductUPClong()
    {
        Produit p = new Produit();
        p.setNom("test");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("1111111111111111111");
        this.paService.addProduct(p);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveBadProductId()
    {
        Produit p = new Produit();
        p.setId(-1);
        p.setNom("test");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("111111111111");
        this.paService.addProduct(p);
    }
    /**
     * Code UPC non valide avec des char
     */
    @Test(expected = BadlyFormedUPCException.class)
    public void kOtestSaveBadProductUPCchar()
    {
        Produit p = new Produit();
        p.setNom("Produit Test");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("1111111111a1");
        this.paService.addProduct(p);
    }



    /**
     * Code UPC valide
     */
    @Test
    public void oKtestGetProductByUPC() throws ProductNotFoundException
    {
        Produit p = new Produit();
        p.setNom("Produit Test");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("221122221122");
        this.paService.addProduct(p);
        p = this.paService.getProductFromUPC("221122221122");
        assertFalse(p == null);
    }


    /**
     * Code de recherche invalide
     */
    @Test(expected = BadlyFormedUPCException.class)
    public void kOtestGetProductByUPCchar() throws IllegalArgumentException, ProductNotFoundException
    {
        Produit p = new Produit();
        p.setNom("Produit Test");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("221122221122");
        this.paService.addProduct(p);
        p = this.paService.getProductFromUPC("22112222112a");
    }





    /**
     * Code UPC non existant
     */
    @Test(expected = ProductNotFoundException.class)
    public void kOtestGetProductByUPC() throws ProductNotFoundException
    {
        Produit p = this.paService.getProductFromUPC("892685001003");
    }

    /**
     * Code UPC non valide
     */
    @Test (expected = BadlyFormedUPCException.class)
    public void kOtestGetProductByUPCNotValid() throws BadlyFormedUPCException, ProductNotFoundException
    {
        Produit p = this.paService.getProductFromUPC("11");
    }


    @Test
    public void testDeleteAllProducts()
    {
        Produit p = new Produit();
        p.setNom("Produit Test 1");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("892685001003");
        this.paService.addProduct(p);
        this.paService.removeInventory();
        assertEquals(0,this.paService.getAllProducts().size());
    }

    /**
     * Récupère un produit aléatoire avec aucun produit
     */
    @Test
    public void testGetRandomProduct()
    {
        Produit p = null;
        p = new Produit();
        p.setNom("Produit Test 1");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("987654321098");
        this.paService.addProduct(p);
        p = new Produit();
        p.setNom("Produit Test 2");
        p.setPrixAvantTaxe(10);
        p.setQuantite(1);
        p.setUpc("892685001003");
        this.paService.addProduct(p);
        p = this.paService.getRandomProduct();
        assertFalse(p == null);
        assertTrue(p.getUpc().equals("987654321098") ||p.getUpc().equals("892685001003") );
        this.paService.removeInventory();
        p = this.paService.getRandomProduct();
        assertEquals(null,p);
    }

    @Test
    public void testRabaisMoinsDeCent()
    {
        List<Produit> lst = new ArrayList<Produit>();
        Produit p = null;
        p = new Produit();
        p.setNom("Produit Test 1");
        p.setPrixAvantTaxe(10);
        p.setQuantite(4);
        p.setUpc("987654321098");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        lst.add(p);
        p = new Produit();
        p.setNom("Produit Test 2");
        p.setPrixAvantTaxe(10);
        p.setQuantite(5);
        p.setUpc("892685001003");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        lst.add(p);
        Achat achat = new Achat();
        achat.setProduits(lst);
        double a = achat.getTotal();
        double b = 52.5;
        Assert.assertEquals(b,a);
    }
    @Test
    public void testRabaisCentetPlus()
    {
        List<Produit> lst = new ArrayList<Produit>();
        Produit p = null;
        p = new Produit();
        p.setNom("Produit Test 1");
        p.setPrixAvantTaxe(10);
        p.setQuantite(20);
        p.setUpc("987654321098");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        lst.add(p);
        p = new Produit();
        p.setNom("Produit Test 2");
        p.setPrixAvantTaxe(10);
        p.setQuantite(20);
        p.setUpc("892685001003");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        lst.add(p);
        Achat achat = new Achat();
        achat.setProduits(lst);
        double a = achat.getTotal();
        double b = p.getPrixAvantTaxe() *20;
        Assert.assertEquals(b,a);
    }
    @Test
    public void testTaxeCinq() throws Exception {
        List<Produit> lst = new ArrayList<Produit>();
        Produit p = null;
        p = new Produit();
        p.setNom("Produit Test 1");
        p.setPrixAvantTaxe(15.5);
        p.setQuantite(10);
        p.setUpc("987654321098");
        p.setTypeTaxe(TaxeType.taxeEssentiel);
        lst.add(p);
        Achat achat = new Achat();
        achat.setProduits(lst);
        ServiceArgentLL sv = new ServiceArgentLL();
        Change a = sv.calculerChange(achat.getTotal());
        double b = 81.4d;
        Assert.assertEquals(b,a.valeurTotale());
    }
    @Test
    public void testTaxeTrenteCinq() throws Exception {
        List<Produit> lst = new ArrayList<Produit>();
        Produit p = null;
        p = new Produit();
        p.setNom("Produit Test 1");
        p.setPrixAvantTaxe(15.5);
        p.setQuantite(10);
        p.setUpc("987654321098");
        p.setTypeTaxe(TaxeType.taxeAutre);
        lst.add(p);
        Achat achat = new Achat();
        achat.setProduits(lst);
        ServiceArgentLL sv = new ServiceArgentLL();
        Change a = sv.calculerChange(achat.getTotal());
        double b = 104.6d;
        Assert.assertEquals(b,a.valeurTotale());
    }
    /*@Test
    public void modifierRabais() throws Exception {
        Produit p = null;
        p = new Produit();
        p.setNom("Produit Test 1");
        p.setPrixAvantTaxe(15.5);
        p.setQuantite(10);
        p.setUpc("987654321098");
        p.setTypeTaxe(TaxeType.taxeAutre);
        p.setDeuxPourUn(true);
        paService.addProduct(p);
        p = new Produit();
        p.setNom("Produit Test 1");
        p.setPrixAvantTaxe(15.5);
        p.setQuantite(10);
        p.setUpc("987654321098");
        p.setTypeTaxe(TaxeType.taxeAutre);
        p.setDeuxPourUn(false);
        paService.addProduct(p);
        p = paService.getProductCountById(1L);
        paService.modifierProduit(p.);


        Achat achat = new Achat();
        achat.setProduits(lst);
        ServiceArgentLL sv = new ServiceArgentLL();
        Change a = sv.calculerChange(achat.getTotal());
        double b = 104.6d;
        Assert.assertEquals(b,a.valeurTotale());
    }*/
}
