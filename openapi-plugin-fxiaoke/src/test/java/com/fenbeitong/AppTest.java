package com.fenbeitong;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    public static void main(String[] args) {
        Pattern airportCodePattern = Pattern.compile("[A-Z]{3}");
        System.out.println(airportCodePattern.matcher("ABC").matches());
        System.out.println(airportCodePattern.matcher("1ABC").matches());
        System.out.println(airportCodePattern.matcher("cAB").matches());
        System.out.println(airportCodePattern.matcher("CAB1").matches());
    }
}
