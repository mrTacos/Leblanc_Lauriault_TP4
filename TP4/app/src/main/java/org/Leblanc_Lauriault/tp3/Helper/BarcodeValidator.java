package org.Leblanc_Lauriault.tp3.Helper;

/**
 * Created by 1277236 on 2017-05-08.
 */

public class BarcodeValidator {

    /**
     * Voir  http://en.wikipedia.org/wiki/Universal_Product_Code#Check_digits
     * @param s
     * @return
     */
    public static boolean isUPCValid(String s){
        if (s.length() != 12) return false;
        //Pattern.Pattern.compile("[0-9]*");
        if (!s.matches("[0-9]*")) return false;

        boolean odd = true;
        //Add the digits in the odd-numbered positions (first, third, fifth, etc.) together and multiply by three.
        int sumOdd = 0 ;
        //Add the digits in the even-numbered positions (second, fourth, sixth, etc.) to the result.
        int sumEven = 0;
        char[] array = s.toCharArray();
        for (int i = 0 ; i < array.length - 1 ; i++){
            int digit = Integer.parseInt(array[i]+"");
            //System.out.println("Digit " + digit);
            if (i%2==0) sumOdd += digit;
            else sumEven += digit;
            odd = !odd;
        }
        //Find the result modulo 10 (i.e. the remainder when divided by 10.. 10 goes into 58 5 times with 8 leftover).
        int result = (3*sumOdd + sumEven)%10;
        //If the result is not zero, subtract the result from ten.
        result = result!=0?10-result:0;
        // get the last digit value that should be equal to this result.
        int lastDigit = Integer.parseInt(array[array.length-1]+"");
        return result == lastDigit;
    }
}
