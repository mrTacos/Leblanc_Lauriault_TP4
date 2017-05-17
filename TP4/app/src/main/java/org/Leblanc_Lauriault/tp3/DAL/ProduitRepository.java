package org.Leblanc_Lauriault.tp3.DAL;

import android.content.Context;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.Leblanc_Lauriault.tp3.Exception.BadlyFormedUPCException;
import org.Leblanc_Lauriault.tp3.Exception.EmptyInventoryException;
import org.Leblanc_Lauriault.tp3.Exception.ProductNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Makes one file per product. Uses the id as the file name.
 */
public class ProduitRepository implements CRUD<Produit>
{

    Gson gson = new Gson();
    Class<Produit> classe = Produit.class;
    Context context;
    private File fullPath;

    public ProduitRepository(Context c)
    {
        this.fullPath = new File(c.getFilesDir().getPath() + "/Produit");
        this.context = c;
        this.createStorage();
    }

    public List<Produit> getAll()
    {
        synchronized (classe)
        {
            List<Produit> res = new ArrayList<Produit>();
            for (File f : this.fullPath.listFiles())
            {
                try
                {
                    if (f.isDirectory())
                        continue;
                    String content = FileUtils.readFileToString(f);
                    Produit p = gson.fromJson(content, classe);
                    res.add(p);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            return res;
        }
    }

    /*public Produit getRandomProduct() throws EmptyInventoryException
    {
        synchronized (classe)
        {
            List<Produit> products = this.getAll();
            Random r = new Random();
            if (products.size() == 0)
                throw new EmptyInventoryException("Il n'y a pas de produit dans l'inventaire");

            return (products.get(r.nextInt(products.size())));
        }
    }*/

    public Produit getByUPC(String pUPC) throws ProductNotFoundException
    {
        synchronized (classe)
        {
            if (pUPC.length() != 12)
                throw new BadlyFormedUPCException("Le upc en paramètre n'est pas un upc valide");

            if (!Pattern.matches("[0-9]+", pUPC))
                throw new IllegalArgumentException("Le upc contient des lettres !");

            List<Produit> allProducts = this.getAll();
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
    }

    @Override
    public void deleteOne(Long o)
    {
        synchronized (classe)
        {
            File fullPathWithID = new File(this.fullPath.getPath() + "/" + o + ".produit");
            fullPathWithID.delete();
        }
    }

    public long save(Produit a)
    {
        synchronized (classe)
        {
            // set the id
            if (a.getId() == -1)
                a.setId(this.nextAvailableId());
            //
            String serialise = gson.toJson(a);
            try
            {
                File fullPathWithID = new File(this.fullPath.getPath() + "/" + a.getId()+".produit");
                FileUtils.writeStringToFile(fullPathWithID, serialise);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return a.getId();
        }
    }

    @Override
    public Produit getById(Long id)
    {
        synchronized (classe)
        {
            String content;
            try
            {
                File fullPathWithID = new File(this.fullPath.getPath() + "/" +  id+".produit");
                if (!fullPathWithID.exists()) return null;
                content = FileUtils.readFileToString(fullPathWithID);
                Produit a = gson.fromJson(content, classe);
                return a;
            }
            catch (IOException e)
            {
                return null;
            }
        }
    }

    public void deleteAll()
    {
        deleteStorage();
        createStorage();
    }


    private long nextAvailableId()
    {
        synchronized (classe)
        {
            long max = 0;
            for (Produit a : getAll()){
                if (a.getId() > max) max = a.getId();
            }
            return max+1;
        }
    }

    public void deleteStorage()
    {
        deleteFolder(this.fullPath);
    }

    public void createStorage()
    {
        this.fullPath.mkdirs();
    }

    private static void deleteFolder(File folder)
    {
        try
        {
            File[] files = folder.listFiles();
            if(files!=null)
            {
                for(File f: files)
                {
                    if(f.isDirectory())
                        deleteFolder(f);
                    else
                        f.delete();
                }
            }
            folder.delete();
        }
        catch(Exception e)
        {

        }
    }

}
