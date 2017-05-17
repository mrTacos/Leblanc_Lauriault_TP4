package org.Leblanc_Lauriault.tp3.GUI;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.Leblanc_Lauriault.tp3.DAL.Achat;
import org.Leblanc_Lauriault.tp3.DAL.AchatProduitService;
import org.Leblanc_Lauriault.tp3.DAL.AchatRepository;
import org.Leblanc_Lauriault.tp3.DAL.CRUD;
import org.Leblanc_Lauriault.tp3.DAL.Produit;
import org.Leblanc_Lauriault.tp3.Event.CheckEvent;
import org.Leblanc_Lauriault.tp3.Event.ListUpdatedEvent;
import org.Leblanc_Lauriault.tp3.Event.MinusEvent;
import org.Leblanc_Lauriault.tp3.Event.PlusEvent;
import org.Leblanc_Lauriault.tp3.Exception.ProductNotFoundException;
import org.Leblanc_Lauriault.tp3.R;
import org.Leblanc_Lauriault.tp3.Helper.ToastHelper;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {

    private List<Produit> currentProductList = new ArrayList<>();
    private AchatProduitService apService;
    private CustomAdapter customAdapter;
    private DiscountAdapter discountAdapter;
    private CRUD<Achat> achatRepo;
    public Bus bus = new Bus(ThreadEnforcer.ANY);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.apService = new AchatProduitService(this,this.currentProductList);
        this.achatRepo = new AchatRepository(this);
        setContentView(R.layout.activity_main_page);

        //Register the custom Adapter
        ListView lv = (ListView) findViewById(R.id.listView);
        customAdapter = new CustomAdapter(this,currentProductList);
        lv.setAdapter(customAdapter);

<<<<<<< HEAD
        discountAdapter = new DiscountAdapter(this,apService.getAllProducts());
=======
        discountAdapter = new DiscountAdapter(this,currentProductList);
>>>>>>> 5d10168a340c8d2b215298512ca4a7726214a33f

        //Change the app name
        this.setTitle("CashDroid");
        this.calculateTotalOrderPrice();

        Button pay = (Button)findViewById(R.id.payButton);
        Button scanner = (Button)findViewById(R.id.scannerButton);

        pay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                apService.payerLesProduit();
            }
        });

        scanner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                IntentIntegrator integrator = new IntentIntegrator(MainPage.this);
                integrator.initiateScan();
            }
        });
    }

    //region Register / Unregister bus
    @Override
<<<<<<< HEAD
    protected void onPause() {
        discountAdapter.bus.register(this);
=======
    protected void onPause()
    {
>>>>>>> 5d10168a340c8d2b215298512ca4a7726214a33f
        customAdapter.bus.unregister(this);
        this.apService.bus.unregister(this);
        super.onPause();
    }
    @Override
    protected void onResume()
    {
        discountAdapter.bus.register(this);
        customAdapter.bus.register(this);
        this.apService.bus.register(this);
        super.onResume();
    }
    //endregion

    /**
     * Creates the options menu in the right corner
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Receive the activity result from the scanner
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null)
        {
            final String barCode = scanResult.getContents();
            //On back
            if (barCode == null)
                return;

            //Gets the product from the barcode
            Produit findProduct = null;

            try
            {
                findProduct = this.apService.getProductFromUPC(barCode);
            }
            catch (ProductNotFoundException e)
            {
                findProduct = null;
            }

            if (findProduct != null )
            {
                findProduct.setQuantite(1);
                int index = this.currentProductList.indexOf(findProduct);
                int repoQuantity = this.apService.getProductCountById(findProduct.getId());
                int currentQuantity = findProduct.getQuantite();

                //if the items exist in the list
                if (index > -1)
                    currentQuantity += this.currentProductList.get(index).getQuantite();

                if (repoQuantity>=currentQuantity)
                {
                    //checks if the product is aleready in the list.
                    if (this.currentProductList.contains(findProduct))
                    {
                        int quantitiy = this.currentProductList.get(index).getQuantite();
                        this.currentProductList.get(index).setQuantite(++quantitiy);
                    }
                    else
                    {
                        this.currentProductList.add(findProduct);
                        this.calculateTotalOrderPrice();
                    }
                }
                else
                {
                    ToastHelper.showNoMoreItems(this);
                }
            }
            else
            {
                showCreateItemMenu(barCode);
            }
        }
    }

    @Subscribe
    public void PlusEventAction(PlusEvent s)
    {
        int indexToModify = this.currentProductList.indexOf(s.product);
        int passedQuantity = this.currentProductList.get(indexToModify).getQuantite();
        int repoQuantity = this.apService.getProductCountById(s.product.getId());
        if (this.currentProductList.get(indexToModify).getQuantite() < repoQuantity)
        {
            this.currentProductList.get(indexToModify).setQuantite(++passedQuantity);
            customAdapter.notifyDataSetChanged();
            this.calculateTotalOrderPrice();
        }
        else
        {
            ToastHelper.showNoMoreItems(this);
        }
    }
    @Subscribe
    public void MinusEventAction(MinusEvent s)
    {
        int indexToModify = this.currentProductList.indexOf(s.product);
        int passedQuantity = this.currentProductList.get(indexToModify).getQuantite();
        if (passedQuantity > 1)
        {
            this.currentProductList.get(indexToModify).setQuantite(--passedQuantity);
            customAdapter.notifyDataSetChanged();
        }
        else
        {
            this.currentProductList.remove(indexToModify);
            customAdapter.notifyDataSetChanged();
        }
        this.calculateTotalOrderPrice();
    }

    @Subscribe
    public void ListUpdatedAction(ListUpdatedEvent s)
    {
        this.calculateTotalOrderPrice();
        this.customAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_createInventory)
        {
            this.apService.seedDatabase();
            Toast.makeText(this, "Inventaire créé !", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_emptyInventory)
        {
            this.apService.emptyInventory();
            Toast.makeText(this, "Inventaire vidé (produit mit à zéro)", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_createList)
        {
            this.apService.createRandomBuyingList(/*this.currentProductList,this.customAdapter*/);
            Toast.makeText(this, "Liste de produit créée !", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_deleteDatabase)
        {
            this.apService.removeInventory();
            Toast.makeText(this, "Produits de l'inventaire supprimés !", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id == R.id.action_modifyDiscount)
        {
            View view = getLayoutInflater().inflate(R.layout.activity_discount, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
            builder.setView(view);
<<<<<<< HEAD
            final ListView lv2 = (ListView) view.findViewById(R.id.discountListView);
=======
            ListView lv2 = (ListView) view.findViewById(R.id.discountListView);
            discountAdapter = new DiscountAdapter(this, apService.getAllProducts());
            discountAdapter.notifyDataSetChanged();
>>>>>>> 5d10168a340c8d2b215298512ca4a7726214a33f
            lv2.setAdapter(discountAdapter);
            Button save = (Button)view.findViewById(R.id.saveButtonDiscount);
            final AlertDialog dialog = builder.create();
            dialog.show();

            save.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view)
                {
                    dialog.dismiss();
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

<<<<<<< HEAD
    @Subscribe
    public void CheckUpdate(CheckEvent s)
    {
        for (Produit p:apService.getAllProducts())
        {
            if(s.produit.equals(p))
            {
                p.setDeuxPourUn(s.produit.isDeuxPourUn());
            }
        }
    }

=======
>>>>>>> 5d10168a340c8d2b215298512ca4a7726214a33f
    private void calculateTotalOrderPrice()
    {
        TextView t = (TextView)findViewById(R.id.totalPrice);
        if (customAdapter.getCount() == 0)
        {
            t.setText(String.format("%.2f$",0.0d));
            return;
        }
        Produit prod;
        double count = 0;
        for (int i =0;i <customAdapter.getCount();i++ ){

            prod = (Produit) customAdapter.getItem(i);
            count += prod.getPrixAvantTaxe() * prod.getQuantite();
        }
        String mT = String.valueOf(count);
        t.setText(String.format("%.2f$",count));
    }


    /**
     * Create and handle the item creation menu
     * @param pBarCode
     */
    private void showCreateItemMenu(String pBarCode)
    {
        final String barCode = pBarCode;
        View view = getLayoutInflater().inflate(R.layout.activity_create_item, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
        builder.setView(view);
        Button save = (Button)view.findViewById(R.id.saveButton);
        final EditText itemName = (EditText)view.findViewById(R.id.itemName);
        final EditText itemPrice = (EditText)view.findViewById(R.id.itemPrice);
        final EditText itemUPC = (EditText)view.findViewById(R.id.itemUPC);
        itemUPC.setText(barCode);
        final AlertDialog dialog = builder.create();
        dialog.show();
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                if (itemName.getText().toString() != ""&& itemPrice.getText().toString() != "" && itemUPC.getText().toString() != "")
                {
                    Double price;
                    try {
                        price = Double.parseDouble(itemPrice.getText().toString());
                    }
                    catch (Exception e)
                    {
                        itemPrice.setText("");
                        return;
                    }

                    if (itemUPC.getText().toString().length() != 12)
                    {
                        itemUPC.setText("");
                        return;
                    }

                    Produit p = new Produit();
                    p.setUpc(barCode);
                    p.setPrixAvantTaxe(Double.parseDouble(itemPrice.getText().toString()));
                    p.setNom(itemName.getText().toString());
                    p.setQuantite(10);
                    apService.addProduct(p);
                    dialog.dismiss();
                }
            }
        });
    }
}