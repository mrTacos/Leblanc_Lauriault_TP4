package org.lauriault.tp3.GUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.lauriault.tp3.DAL.Produit;
import org.lauriault.tp3.Event.MinusEvent;
import org.lauriault.tp3.Event.PlusEvent;
import org.lauriault.tp3.R;

import java.util.List;


public class CustomAdapter extends ArrayAdapter<Produit>
{

    public Bus bus = new Bus(ThreadEnforcer.ANY);

    public CustomAdapter(Context pContext, List<Produit> products)
    {
        super(pContext, R.layout.list_item,products);
        bus.register(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater li = LayoutInflater.from(getContext());
        View v = li.inflate(R.layout.list_item,parent,false);
        if (getCount() > 0)
        {
            final Produit s = getItem(position);
            final TextView itemName = (TextView) v.findViewById(R.id.item_produit);
            final TextView itemQuantite = (TextView) v.findViewById(R.id.item_quantite);
            final Button plusButton = (Button)v.findViewById(R.id.plusButton);
            plusButton.setTag(s);
            final Button minusButton = (Button)v.findViewById(R.id.minusButton);
            minusButton.setTag(s);

            itemName.setText(s.getNom());
            itemQuantite.setText(s.getQuantite().toString());


            plusButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view)
                {
                    PlusEvent e = new PlusEvent();
                    e.product =(Produit)plusButton.getTag();
                    bus.post(e);
                }
            });
            minusButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view)
                {
                    MinusEvent e = new MinusEvent();
                    e.product = (Produit)minusButton.getTag();
                    bus.post(e);
                }
            });
        }
        return v;
    }

}
