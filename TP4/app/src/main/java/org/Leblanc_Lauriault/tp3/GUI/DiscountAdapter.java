package org.Leblanc_Lauriault.tp3.GUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

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

    public DiscountAdapter(Context pContext, List<Produit> products)
    {
        super(pContext, R.layout.discount_item,products);
        bus.register(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater li = LayoutInflater.from(getContext());
        View v = li.inflate(R.layout.discount_item,parent,false);
        final Produit s = getItem(position);
        final TextView itemName = (TextView) v.findViewById(R.id.item_produit);
        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.produit_checkbox);

        itemName.setText(s.getNom());
        checkBox.setChecked(!checkBox.isChecked());

        checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                CheckEvent e = new CheckEvent();
                e.produit = (Produit)checkBox.getTag();
                bus.post(e);
            }
        });

        return v;
    }
}
