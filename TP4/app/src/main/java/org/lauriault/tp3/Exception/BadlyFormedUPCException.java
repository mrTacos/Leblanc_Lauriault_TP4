package org.lauriault.tp3.Exception;

/**
 * Created by Alex on 4/18/2017.
 */

public class BadlyFormedUPCException extends RuntimeException
{
    public BadlyFormedUPCException(String pMessage)
    {
        super(pMessage);
    }
}
