package org.Leblanc_Lauriault.tp3.GUI;

import android.app.Dialog;
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
import org.Leblanc_Lauriault.tp3.DAL.CRUD;
import org.Leblanc_Lauriault.tp3.DAL.Produit;
import org.Leblanc_Lauriault.tp3.Event.CheckEvent;
import org.Leblanc_Lauriault.tp3.Event.ListUpdatedEvent;
import org.Leblanc_Lauriault.tp3.Event.MinusChangeEvent;
import org.Leblanc_Lauriault.tp3.Event.MinusEvent;
import org.Leblanc_Lauriault.tp3.Event.PlusEvent;
import org.Leblanc_Lauriault.tp3.Event.PlusMinusChangeEvent;
import org.Leblanc_Lauriault.tp3.Exception.ProductNotFoundException;
import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.ArrondiNombreNegatifException;
import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.EmplacementPleinException;
import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.MontantInatteignableException;
import org.Leblanc_Lauriault.tp3.R;
import org.Leblanc_Lauriault.tp3.Helper.ToastHelper;
import org.Leblanc_Lauriault.tp3.change.ArgentObjet;
import org.Leblanc_Lauriault.tp3.change.ChangeLL;
import org.Leblanc_Lauriault.tp3.change.ServiceArgentLL;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {

    private List<Produit> currentProductList = new ArrayList<>();
    private AchatProduitService apService;
    private CustomAdapter customAdapter;
    private DiscountAdapter discountAdapter;
    private PayAdapter payAdapter;
    private ServiceArgentLL serviceArgentLL;
    private View payView;
    private Dialog payDialog;
    private View discountView;
    private Dialog discountDialog;
    public Bus bus = new Bus(ThreadEnforcer.ANY);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.apService = new AchatProduitService(this,this.currentProductList);
        this.serviceArgentLL = new ServiceArgentLL(true);
        setContentView(R.layout.activity_main_page);

        //Register the custom Adapter
        ListView lv = (ListView) findViewById(R.id.listView);
        customAdapter = new CustomAdapter(this,currentProductList);
        lv.setAdapter(customAdapter);

        discountAdapter = new DiscountAdapter(this,apService.getAllProducts());
        this.createPayInterface();
        this.createDiscountInterface();
        //payAdapter = new PayAdapter(this,this.serviceArgentLL.getTiroir(),null);

        //Change the app name
        this.setTitle("TP4");
        this.calculerTotalAvantTaxe();

        Button pay = (Button)findViewById(R.id.payButton);
        Button scanner = (Button)findViewById(R.id.scannerButton);

        pay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                if (currentProductList.size() != 0)
                    showPayInterface();
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
    protected void onPause()
    {
        payAdapter.bus.unregister(this);
        discountAdapter.bus.unregister(this);
        customAdapter.bus.unregister(this);
        this.apService.bus.unregister(this);
        super.onPause();
    }
    @Override
    protected void onResume()
    {
        discountAdapter.bus.register(this);
        payAdapter.bus.register(this);
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
                        this.calculerTotalAvantTaxe();
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
            this.calculerTotalAvantTaxe();
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
        this.calculerTotalAvantTaxe();
    }

    /***
     * Réagit à l'ajout ou le retrait d'un billet ou d'une pièce
     * @param s L'event
     */
    @Subscribe
    public void PlusMinusChangeEvent(PlusMinusChangeEvent s)
    {
        Double number2 = payAdapter.cll.valeurTotale();
        final TextView totalSelected = (TextView) payView.findViewById(R.id.totalChangeSelected);
        totalSelected.setText("Sélectionné: " + String.format("%.2f",number2));
        payAdapter.notifyDataSetChanged();
    }


    @Subscribe
    public void ListUpdatedAction(ListUpdatedEvent s)
    {
        this.calculerTotalAvantTaxe();
        this.customAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_createInventory)
        {
            createDiscountInterface();
            discountAdapter.bus.register(this);
            //createPayInterface();
            this.apService.seedDatabase();
            Toast.makeText(this,R.string.toastCreateInv , Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_emptyInventory)
        {
            createDiscountInterface();
            discountAdapter.bus.register(this);
            //createPayInterface();
            this.apService.emptyInventory();
            Toast.makeText(this, R.string.toastEmpty, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_createList)
        {
            createDiscountInterface();
            discountAdapter.bus.register(this);
            //createPayInterface();
            this.apService.createRandomBuyingList(/*this.currentProductList,this.customAdapter*/);
            Toast.makeText(this, R.string.toastCreateList, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_deleteDatabase)
        {
            createDiscountInterface();
            discountAdapter.bus.register(this);
            //createPayInterface();
            this.apService.removeInventory();
            Toast.makeText(this, R.string.toastDeleteInv, Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id == R.id.action_modifyDiscount)
        {
            createDiscountInterface();
            discountAdapter.bus.register(this);
            //createPayInterface();
            this.showDiscountInterface();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
        for (Produit p:this.currentProductList)
        {
            if(s.produit.equals(p))
            {
                p.setDeuxPourUn(s.produit.isDeuxPourUn());
            }
        }
    }
    private void createPayInterface()
    {
        payAdapter = new PayAdapter(this,this.serviceArgentLL.getTiroir(),null);
        View view = getLayoutInflater().inflate(R.layout.dialog_pay, null);
        this.payView = view;
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
        builder.setView(view);
        final ListView lv2 = (ListView) view.findViewById(R.id.payListView);
        lv2.setAdapter(payAdapter);
        payDialog = builder.create();
    }

    private void createDiscountInterface()
    {
        //
        discountAdapter = new DiscountAdapter(this,this.apService.getAllProducts());

        View view = getLayoutInflater().inflate(R.layout.activity_discount, null);
        this.discountView = view;
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
        builder.setView(view);
        final ListView lv2 = (ListView) view.findViewById(R.id.discountListView);
        lv2.setAdapter(discountAdapter);
        discountDialog = builder.create();
    }
    private void showDiscountInterface()
    {
        final ListView lv2 = (ListView) discountView.findViewById(R.id.discountListView);
        Button save = (Button)discountView.findViewById(R.id.saveButtonDiscount);
        discountDialog.show();
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                discountDialog.dismiss();
            }
        });
    }
    private void showPayInterface()
    {
        Double number = null;
        try {
            number =  this.serviceArgentLL.arrondiA5sous(Achat.getTotalFromProducts(currentProductList));
        } catch (ArrondiNombreNegatifException e) {
            e.printStackTrace();
        }
        Double number2 = payAdapter.cll.valeurTotale();

        //View view = getLayoutInflater().inflate(R.layout.dialog_pay, null);
        //final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);

        final TextView totalTV = (TextView) payView.findViewById(R.id.totalAmoutToPay);
        final TextView totalSelected = (TextView) payView.findViewById(R.id.totalChangeSelected);
        final Button payButton = (Button) payView.findViewById(R.id.payButton);
        final Button plusButton = (Button) payView.findViewById(R.id.plusButton);
        final Button minusButton = (Button) payView.findViewById(R.id.minusButton);

        //builder.setView(view);
        //final ListView lv2 = (ListView) view.findViewById(R.id.payListView);

        /*try
        {
            payAdapter = new PayAdapter(this,this.serviceArgentLL.getTiroir(),null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
        totalTV.setText("       À payer: " + String.format("%.2f",number));
        totalSelected.setText("Sélectionné: " + String.format("%.2f",number2));
        //lv2.setAdapter(payAdapter);
        //payAdapter.notifyDataSetChanged();
        //final AlertDialog dialog = builder.create();
        payDialog.show();

        //Le moment de payer les achats
        payButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                if (payAdapter.cll.valeurTotale() < Achat.getTotalFromProducts(currentProductList))
                {
                    Toast.makeText(MainPage.this, "Le montant donné par le client ne couvre pas la totalité des frais", Toast.LENGTH_LONG).show();
                    return;  
                }
                try
                {
                    serviceArgentLL.ajouterChangeDuClient(payAdapter.cll);
                    serviceArgentLL.calculerChange(payAdapter.cll.valeurTotale() - Achat.getTotalFromProducts(currentProductList));
                }
                catch (EmplacementPleinException | MontantInatteignableException e)
                {
                    if (e.getClass() == EmplacementPleinException.class)
                        Toast.makeText(MainPage.this, "Impossible d'ajouter ce montant, car un des emplacements est plein", Toast.LENGTH_LONG).show();
                    if (e.getClass() == MontantInatteignableException.class)
                        Toast.makeText(MainPage.this, "Impossible de complèter la vente, car la caisse nom comprend pas les pièces et les billet nécessaire à la transaction", Toast.LENGTH_LONG).show();
                    payAdapter.cll = new ChangeLL();
                    payAdapter.notifyDataSetChanged();
                    Double number2 = payAdapter.cll.valeurTotale();
                    final TextView totalSelected = (TextView) payView.findViewById(R.id.totalChangeSelected);
                    totalSelected.setText("Sélectionné: " + String.format("%.2f",number2));
                    return;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                apService.payerLesProduit();

                payAdapter.cll = new ChangeLL();
                payAdapter.notifyDataSetChanged();
                Double number2 = payAdapter.cll.valeurTotale();
                final TextView totalSelected = (TextView) payView.findViewById(R.id.totalChangeSelected);
                totalSelected.setText("Sélectionné: " + String.format("%.2f",number2));
                payDialog.dismiss();
            }
        });


    }
    private void calculateTotalOrderPrice()
    {
        TextView t = (TextView)findViewById(R.id.totalPrice);
        if (customAdapter.getCount() == 0)
        {
            t.setText(String.format("%.2f$",0.0d));
            return;
        }
        /*Produit prod;
        double count = 0;
        for (int i =0;i <customAdapter.getCount();i++ ){

            prod = (Produit) customAdapter.getItem(i);
            count += prod.getPrixAvantTaxe() * prod.getQuantite();
        }
        String mT = String.valueOf(count);*/

        t.setText(String.format("%.2f$",Achat.getTotalFromProducts(this.currentProductList)));
    }

    private void calculerTotalAvantTaxe()
    {
        TextView t = (TextView)findViewById(R.id.totalPrice);
        double count = 0;
        for (Produit p:this.currentProductList)
        {
            count += p.getPrixAvantTaxe() * p.getQuantite();
        }
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