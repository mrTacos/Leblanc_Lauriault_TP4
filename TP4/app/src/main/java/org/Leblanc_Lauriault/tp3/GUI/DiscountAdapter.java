package org.Leblanc_Lauriault.tp3.GUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.Leblanc_Lauriault.tp3.DAL.AchatProduitService;
import org.Leblanc_Lauriault.tp3.DAL.Produit;
import org.Leblanc_Lauriault.tp3.Event.CheckEvent;
import org.Leblanc_Lauriault.tp3.Event.MinusEvent;
import org.Leblanc_Lauriault.tp3.Event.PlusEvent;
import org.Leblanc_Lauriault.tp3.R;

import java.util.List;

/**
 * Created by 1066856 on 2017-05-08.
 */

public class DiscountAdapter extends ArrayAdapter<Produit> {

    public Bus bus = new Bus(ThreadEnforcer.ANY);
    private AchatProduitService apService;

    public DiscountAdapter(Context pContext, List<Produit> products)
    {
        super(pContext, R.layout.discount_item,products);
        this.apService = new AchatProduitService(pContext,products);
        bus.register(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater li = LayoutInflater.from(getContext());
        View v = li.inflate(R.layout.discount_item,parent,false);
        final Produit s = getItem(position);
        final TextView itemName = (TextView) v.findViewById(R.id.discount_produit);
        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.produit_checkbox);
        itemName.setText(s.getNom());
        checkBox.setChecked(s.isDeuxPourUn());
        checkBox.setTag(s);

        checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                CheckEvent e = new CheckEvent();
                e.produit = (Produit)checkBox.getTag();
                e.produit.setDeuxPourUn(!e.produit.isDeuxPourUn());
                bus.post(e);
                apService.modifierProduit(e.produit);
            }
        });
        return v;
    }
}
