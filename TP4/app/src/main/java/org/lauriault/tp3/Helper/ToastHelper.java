package org.lauriault.tp3.Helper;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Alex on 4/18/2017.
 */

public class ToastHelper
{
    private final static String noMoreItems = "Il n'y a plus de ce type en inventaire";

    public static void showNoMoreItems(Context pContext)
    {
        Toast.makeText(pContext, noMoreItems, Toast.LENGTH_SHORT).show();
    }
}
