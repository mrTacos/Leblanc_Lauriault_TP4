package org.Leblanc_Lauriault.tp3.DAL;

import android.content.Context;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Makes one file per product. Uses the id as the file name.
 */
public class AchatRepository implements CRUD<Achat>
{

    Gson gson = new Gson();
    Class<Achat> classe = Achat.class;
    Context context;
    private File fullPath;


    /**
     * Créer un nouveau repository d'achat
     * @param c le context
     */
    public AchatRepository(Context c)
    {
        this.fullPath = new File(c.getFilesDir().getPath() + "/Achat");
        this.context = c;
        this.createStorage();
    }


    /**
     * Permet d'obtenir tout les achats dans le fichier.
     * @return tout les achats sauvegardés
     */
    public List<Achat> getAll()
    {
        synchronized (classe)
        {
            List<Achat> res = new ArrayList<Achat>();
            File base = context.getFilesDir();

            for (File f : this.fullPath.listFiles())
            {
                try
                {
                    if (f.isDirectory())
                        continue;

                    String content = FileUtils.readFileToString(f);
                    Achat a = gson.fromJson(content, classe);
                    res.add(a);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            return res;
        }
    }





    /**
     * Supprime un achat (un fichier) avec un certain ID
     * @param o le ID de l'achat à supprimer.
     */
    @Override
    public void deleteOne(Long o)
    {
        synchronized (classe)
        {
            File f = new File(this.fullPath.getPath() + ".achat");
            f.delete();
        }
    }

    /**
     * Insère un achat dans les fichier.
     * Un dossier Achat est créé si jamais il n'existe pas
     * Le dossier sert de classification pour organiser le contenu et augmenter la vitesse des "requêtes"
     * @param a achat à inséré dans les fichiers
     * @return retourne le ID de l'achat créé
     */
    public long save(Achat a)
    {
        synchronized (classe)
        {
            // set the id
            if (a.getId() == null)
                a.setId(this.nextAvailableId());

            String serialise = gson.toJson(a);
            if (!this.fullPath.exists())
            {
                this.fullPath.mkdir();
            }
            try
            {
                String fullPathWithId = this.fullPath.getPath()+ "/" + a.getId()+".achat";
                FileUtils.writeStringToFile(new File(fullPathWithId),serialise);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return a.getId();
        }
    }







    /**
     * Cherche un achat avec son ID dans le sytème de fichier.
     * @param id le ID de l'achat à rechercher
     * @return retourne l'achat avec le ID correspondant
     */
    @Override
    public Achat getById(Long id)
    {
        synchronized (classe)
        {
            String content;
            try
            {
                String fullPathWithId = this.fullPath.getPath() + "/" + id+".achat";
                File f = new File(fullPathWithId);
                if (!f.exists())
                    return null;

                content = FileUtils.readFileToString(new File(f.getPath()));
                Achat a = gson.fromJson(content, classe);
                return a;
            }
            catch (IOException e)
            {
                return null;
            }
        }
    }


    /**
     * Supprime le storage au complet avec tout les achats à l'intérieur.
     */
    public void deleteStorage()
    {
        deleteFolder(this.fullPath);
    }

    /**
     * Supprime tout les achats et recréer le storage avec le système de fichier.
     */
    public void deleteAll()
    {
        deleteStorage();
        createStorage();
    }

    /**
     * Créer le storage avec le système de fichier complet.
     */
    public void createStorage()
    {
        this.fullPath.mkdirs();
    }


    /**
     * Permet d'obtenir le prochain ID disponible pour nommer le fichier.
     * @return
     */
    private long nextAvailableId()
    {
        synchronized (classe)
        {
            long max = 0;
            for (Achat a : this.getAll()){
                if (a.getId() > max) max = a.getId();
            }
            return max+1;
        }
    }


    /**
     * Supprime le dossier d'achat au complet
     * @param folder
     */
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
