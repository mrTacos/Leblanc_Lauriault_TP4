package org.Leblanc_Lauriault.tp3;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.Leblanc_Lauriault.tp3.DAL.Produit;
import org.Leblanc_Lauriault.tp3.DAL.ProduitService;
import org.Leblanc_Lauriault.tp3.Exception.BadlyFormedUPCException;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class TestProductService
{
    private ProduitService pService;
    private List<Produit> testProductList;
    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        this.testProductList = new ArrayList<>();
        this.pService = new ProduitService(appContext, testProductList);
        pService.removeInventory();
    }
    @After
    public void tearDown()
    {
        this.pService.removeInventory();
        this.pService = null;
    }

    /**
     * Savegarde et getAll
     */
    @Test
    public void testAddProductAndGetAll(){
        Produit p = new Produit();
        p.setNom("Produit Test");
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("221122221122");
        this.pService.addProduct(p);
        assertEquals(this.pService.getAllProducts().size(), 1);
    }

    /**
     * save un produit avec un nom incorrect
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveBadProductName()
    {
        Produit p = new Produit();
        p.setNom("");
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("221122221122");
        this.pService.addProduct(p);
    }
    /**
     * save un produit avec une prix incorrect
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveBadProductPrice()
    {
        Produit p = new Produit();
        p.setNom("test");
        p.setPrixUnitaire(-10);
        p.setQuantite(1);
        p.setUpc("221122221122");
        this.pService.addProduct(p);
    }

    /**
     * save un produit avec une quantité incorrect
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveBadProductQuantity()
    {
        Produit p = new Produit();
        p.setNom("test");
        p.setPrixUnitaire(10);
        p.setQuantite(-1);
        p.setUpc("221122221122");
        this.pService.addProduct(p);
    }

    /**
     * save un produit avec un UPC incorrect
     */
    @Test(expected = BadlyFormedUPCException.class)
    public void testSaveBadProductUPCshort()
    {
        Produit p = new Produit();
        p.setNom("test");
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("11");
        this.pService.addProduct(p);
    }
    /**
     * save un produit avec un UPC incorrect
     */
    @Test(expected = BadlyFormedUPCException.class)
    public void testSaveBadProductUPClong()
    {
        Produit p = new Produit();
        p.setNom("test");
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("1111111111111111111");
        this.pService.addProduct(p);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveBadProductId()
    {
        Produit p = new Produit();
        p.setId(-1);
        p.setNom("test");
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("111111111111");
        this.pService.addProduct(p);
    }
    /**
     * Code UPC non valide avec des char
     */
    @Test(expected = BadlyFormedUPCException.class)
    public void kOtestSaveBadProductUPCchar()
    {
        Produit p = new Produit();
        p.setNom("Produit Test");
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("1111111111a1");
        this.pService.addProduct(p);
    }



    /**
     * Code UPC valide
     */
    @Test
    public void oKtestGetProductByUPC()
    {
        Produit p = new Produit();
        p.setNom("Produit Test");
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("221122221122");
        this.pService.addProduct(p);
        p = this.pService.getProductFromUPC("221122221122");
        assertFalse(p == null);
    }


    /**
     * Code de recherche invalide
     */
    @Test(expected = IllegalArgumentException.class)
    public void kOtestGetProductByUPCchar()
    {
        Produit p = new Produit();
        p.setNom("Produit Test");
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("221122221122");
        this.pService.addProduct(p);
        p = this.pService.getProductFromUPC("22112222112a");
    }





    /**
     * Code UPC non existant
     */
    @Test
    public void kOtestGetProductByUPC()
    {
        Produit p = this.pService.getProductFromUPC("111111111111");
        assertTrue(p == null);
    }

    /**
     * Code UPC non valide
     */
    @Test (expected = BadlyFormedUPCException.class)
    public void kOtestGetProductByUPCNotValid()
    {
        Produit p = this.pService.getProductFromUPC("11");
    }


    @Test
    public void testDeleteAllProducts()
    {
        Produit p = new Produit();
        p.setNom("Produit Test 1");
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("112233445566");
        this.pService.addProduct(p);
        this.pService.removeInventory();
        assertEquals(0,this.pService.getAllProducts().size());
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
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("112233445566");
        this.pService.addProduct(p);
        p = new Produit();
        p.setNom("Produit Test 2");
        p.setPrixUnitaire(10);
        p.setQuantite(1);
        p.setUpc("665544332211");
        this.pService.addProduct(p);
        p = this.pService.getRandomProduct();
        assertFalse(p == null);
        assertTrue(p.getUpc().equals("112233445566") ||p.getUpc().equals("665544332211") );
        this.pService.removeInventory();
        p = this.pService.getRandomProduct();
        assertEquals(null,p);
    }

}
