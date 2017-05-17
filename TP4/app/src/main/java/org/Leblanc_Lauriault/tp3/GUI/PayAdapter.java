package org.Leblanc_Lauriault.tp3.GUI;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.Leblanc_Lauriault.tp3.DAL.Produit;
import org.Leblanc_Lauriault.tp3.Event.MinusEvent;
import org.Leblanc_Lauriault.tp3.Event.PlusEvent;
import org.Leblanc_Lauriault.tp3.Exception.BadlyFormedUPCException;
import org.Leblanc_Lauriault.tp3.R;
import org.Leblanc_Lauriault.tp3.change.ArgentObjet;
import org.Leblanc_Lauriault.tp3.change.ChangeLL;
import org.Leblanc_Lauriault.tp3.change.TiroirCaisseLL;

import java.util.ArrayList;
import java.util.List;

public class PayAdapter extends ArrayAdapter<ArgentObjet>
{
    public Bus bus = new Bus(ThreadEnforcer.ANY);
    private TiroirCaisseLL _tcll;
    private ChangeLL cll;
    public PayAdapter(Context pContext, TiroirCaisseLL tcLL, ChangeLL cll)
    {
        super(pContext, R.layout.paylist_item,ArgentObjet.getAllMoney());
        this._tcll = tcLL;
        this.cll = cll;
        bus.register(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater li = LayoutInflater.from(getContext());
        View v = li.inflate(R.layout.paylist_item,parent,false);
        if (getCount() > 0)
        {
            final ArgentObjet ao = getItem(position);
            final TextView moneyName = (TextView) v.findViewById(R.id.moneyName);
            final Button plusButton = (Button) v.findViewById(R.id.plusButton);
            final Button minusButton = (Button)v.findViewById(R.id.minusButton);
            final TextView quantitySelected = (TextView) v.findViewById(R.id.selectedQuantity);
            plusButton.setTag(ao);
            minusButton.setTag(ao);

            moneyName.setText(ao.nomLisible + ": ");
            quantitySelected.setText("X " +String.valueOf(this.cll.nombreItemsPour(ao)));


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
