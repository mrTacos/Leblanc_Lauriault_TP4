package org.Leblanc_Lauriault.tp3.Exception;

/**
 * Created by Alex on 4/18/2017.
 */

public class BadProductException extends RuntimeException
{
    public BadProductException(String pMessage)
    {
        super(pMessage);
    }
}
