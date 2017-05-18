package org.Leblanc_Lauriault.tp3.Helper;

import android.content.Context;
import android.widget.Toast;

import org.Leblanc_Lauriault.tp3.R;

/**
 * Created by Alex on 4/18/2017.
 */

public class ToastHelper
{
    public static void showNoMoreItems(Context pContext)
    {
        Toast.makeText(pContext, R.string.noMoreOfThisProduct, Toast.LENGTH_SHORT).show();
    }
}
