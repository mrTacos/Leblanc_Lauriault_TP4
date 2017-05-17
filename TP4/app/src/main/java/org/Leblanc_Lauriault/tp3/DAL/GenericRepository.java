package org.Leblanc_Lauriault.tp3.DAL;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.internal.ObjectConstructor;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenericRepository<T extends IEntity> implements CRUD<T>
{
    Gson gson = new Gson();
    private Class<T> classe;
    Context context;
    private File fullPath;

    public GenericRepository(Context c, Class<T> typeOfRepo)
    {
        this.classe = typeOfRepo;
        this.fullPath = new File(c.getFilesDir().getPath() + "/" + classe.getSimpleName());
        this.context = c;
        this.createStorage();
    }

    public long save(T o)
    {
        synchronized (classe)
        {
            // set the id
            if (o.getId() == -1)
                o.setId(this.nextAvailableId());

            String serialise = gson.toJson(o);
            if (!this.fullPath.exists())
            {
                this.fullPath.mkdir();
            }
            try
            {
                String fullPathWithId = this.fullPath.getPath()+ "/" + o.getId()+"."+classe.getSimpleName();
                FileUtils.writeStringToFile(new File(fullPathWithId),serialise);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return o.getId();
        }
    }

    public T getById(Long id)
    {
        synchronized (classe)
        {
            String content;
            try
            {
                String fullPathWithId = this.fullPath.getPath() + "/" + id+"."+classe.getSimpleName();
                File f = new File(fullPathWithId);
                if (!f.exists())
                    return null;

                content = FileUtils.readFileToString(new File(f.getPath()));
                Object a = gson.fromJson(content, classe);
                return (T)a;
            }
            catch (IOException e)
            {
                return null;
            }
        }
    }

    public List<T> getAll()
    {
        synchronized (classe)
        {
            List<T> res = new ArrayList<T>();
            File base = context.getFilesDir();

            for (File f : this.fullPath.listFiles())
            {
                try
                {
                    if (f.isDirectory())
                        continue;

                    String content = FileUtils.readFileToString(f);
                    T a = gson.fromJson(content, classe);
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

    public void deleteOne(Long o)
    {
        synchronized (classe)
        {
            File f = new File(this.fullPath.getPath() + "." + classe.getSimpleName());
            f.delete();
        }
    }

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
     * @return
     */
    private long nextAvailableId()
    {
        synchronized (classe)
        {
            long max = 0;
            for (T a : this.getAll()){
                if (a.getId() > max) max = a.getId();
            }
            return max+1;
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
