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

import org.Leblanc_Lauriault.tp3.Event.MinusChangeEvent;
import org.Leblanc_Lauriault.tp3.Event.PlusMinusChangeEvent;
import org.Leblanc_Lauriault.tp3.Exception.exception_TP2.ChangeNegatifException;
import org.Leblanc_Lauriault.tp3.R;
import org.Leblanc_Lauriault.tp3.change.ArgentObjet;
import org.Leblanc_Lauriault.tp3.change.Change;
import org.Leblanc_Lauriault.tp3.change.ChangeLL;
import org.Leblanc_Lauriault.tp3.change.TiroirCaisse;

public class PayAdapter extends ArrayAdapter<ArgentObjet>
{
    public Bus bus = new Bus(ThreadEnforcer.ANY);
    private TiroirCaisse _tcll;
    public Change cll;
    public PayAdapter(Context pContext, TiroirCaisse tcLL, Change cll)
    {
        super(pContext, R.layout.paylist_item,ArgentObjet.getAllMoney());
        this._tcll = tcLL;
        this.cll = new ChangeLL();
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

            moneyName.setText(String.format("%.2f",ao.valeur()) + " $: ");
            quantitySelected.setText("X " +String.valueOf(this.cll.nombreItemsPour(ao)));

            plusButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try {
                        cll.ajouterItem(ao,1);
                    } catch (ChangeNegatifException e) {
                        e.printStackTrace();
                    }
                    PlusMinusChangeEvent pce = new PlusMinusChangeEvent();
                    pce.argentObjet = ao;
                    bus.post(pce);
                }
            });
            minusButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try {
                        cll.ajouterItem(ao,-1);
                    } catch (ChangeNegatifException e) {
                        e.printStackTrace();
                    }
                    PlusMinusChangeEvent pce = new PlusMinusChangeEvent();
                    pce.argentObjet = ao;
                    bus.post(pce);
                }
            });
        }
        return v;
    }

}
