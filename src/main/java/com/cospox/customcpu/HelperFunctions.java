package com.cospox.customcpu;

import java.util.Objects;

public class HelperFunctions {
	public static boolean isHex(String text) {
	    Objects.requireNonNull(text);
	    if (!text.startsWith("0x")) { return false; }
	    text = text.substring(2);
	    if (text.length() < 1) { return false; }
	    char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	            'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F' };

	    for (char symbol : text.toCharArray()) {
	        boolean found = false;
	        for (char hexDigit : hexDigits) {
	            if (symbol == hexDigit) {
	                found = true;
	                break;
	            }
	        }
	        if (!found) { return false; }
	    }
	    return true;
	}
}
