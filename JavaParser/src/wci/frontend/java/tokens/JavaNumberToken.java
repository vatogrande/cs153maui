package wci.frontend.java.tokens;

import static wci.frontend.java.JavaErrorCode.INVALID_NUMBER;
import static wci.frontend.java.JavaErrorCode.RANGE_INTEGER;
import static wci.frontend.java.JavaErrorCode.RANGE_REAL;
import static wci.frontend.java.JavaTokenType.ERROR;
import static wci.frontend.java.JavaTokenType.*;
import wci.frontend.Source;
import wci.frontend.java.JavaToken;

/**
 * <h1>PascalNumberToken</h1>
 *
 * <p>Pascal number tokens (integer and real).</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 * modified by Chio Saeteurn
 * 
 */
public class JavaNumberToken extends JavaToken
{
    private static final int MAX_EXPONENT = 37;

    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public JavaNumberToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a Pascal number token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        StringBuilder textBuffer = new StringBuilder();  // token's characters
        extractNumber(textBuffer);
        text = textBuffer.toString();
    }

    /**
     * Extract a Pascal number token from the source.
     * @param textBuffer the buffer to append the token's characters.
     * @throws Exception if an error occurred.
     */
    protected void extractNumber(StringBuilder textBuffer)
        throws Exception
    {
        String wholeDigits = null;     // digits before the decimal point
        String fractionDigits = null;  // digits after the decimal point
        String exponentDigits = null;  // exponent digits
        char exponentSign = '+';       // exponent sign '+' or '-'
        boolean sawDotDot = false;     // true if saw .. token
        char currentChar;              // current character

        type = INTEGER;  // assume INTEGER token type for now
        currentChar = currentChar();
        
        //Check for zero '0' at beginning
        
        if(currentChar == '0'){

        	if (peekChar() == 'x'){
        		type = HEX;
        		String hexDigits = extractHexNumber(textBuffer);
        		if (type == ERROR){
        			return;
        		}
        		int integerValue = getHexValue(hexDigits);

                if (type != ERROR) {
                    value = new Integer(integerValue);
                    return;
                }
        	}
        	else{
        		type = OCT;
        		String octDigits = extractOctNumber(textBuffer);
        		if(type == ERROR){return;}
        		
        		int intergerValue = getOctValue(octDigits);
        		 if (type != ERROR  && type != REAL){
        			 value = new Integer(intergerValue);
        			 
        			 return;
        		 }
        	}
        } 

        // Extract the digits of the whole part of the number.
       if (Character.isDigit(currentChar)){
	        wholeDigits = unsignedIntegerDigits(textBuffer);
	        if (type == ERROR) {
	            return;
	        }
       }
        

        
        	

        // Is there a . ?
        // It could be a decimal point or the start of a .. token.
        currentChar = currentChar();
        if (currentChar == '.') {
                type = REAL;  // decimal point, so token type is REAL
                textBuffer.append(currentChar);
                currentChar = nextChar();  // consume decimal point

                // Collect the digits of the fraction part of the number.
                fractionDigits = unsignedIntegerDigits(textBuffer);
                currentChar = currentChar();
                
                if(currentChar == '.'){
                	type = ERROR;
                	value = INVALID_NUMBER;
                	textBuffer.append(currentChar);
                	currentChar = nextChar();
                	
                	while(Character.isDigit(currentChar) || currentChar == '.'){
                		textBuffer.append(currentChar);
                		currentChar = nextChar();
                	}
                	
                }
                if (type == ERROR) {
                    return;
                }
            
        }
        
        
        // Is there an exponent part?
        // There cannot be an exponent if we already saw a ".." token.
        if (!sawDotDot && ((currentChar == 'E') || (currentChar == 'e'))) {
            type = REAL;  // exponent, so token type is REAL
            textBuffer.append(currentChar);
            currentChar = nextChar();  // consume 'E' or 'e'

            // Exponent sign?
            if ((currentChar == '+') || (currentChar == '-')) {
                textBuffer.append(currentChar);
                exponentSign = currentChar;
                currentChar = nextChar();  // consume '+' or '-'
            }

            // Extract the digits of the exponent.
            exponentDigits = unsignedIntegerDigits(textBuffer);
        }

        // Compute the value of an integer number token.
        if (type == INTEGER) {
            int integerValue = computeIntegerValue(wholeDigits);

            if (type != ERROR) {
                value = new Integer(integerValue);
            }
        }

        // Compute the value of a real number token.
        else if (type == REAL) {
            float floatValue = computeFloatValue(wholeDigits, fractionDigits,
                                                 exponentDigits, exponentSign);
            if (type != ERROR) {
                value = new Float(floatValue);
            }
        }
    }

    
    //Extract and return the digits of a hex number
    private String extractHexNumber (StringBuilder textBuffer)
    	throws Exception{
    	char currentChar = currentChar();
    	
        // Must have at least one digit.
        if (!Character.isDigit(currentChar) || peekChar() != 'x') {
            type = ERROR;
            value = INVALID_NUMBER;
            return null;
        }
        else{
        	type = HEX;
            StringBuilder digits = new StringBuilder();
        	//take the first two char (should be '0x')
            textBuffer.append(currentChar);
        	digits.append(currentChar);
        	currentChar = nextChar();
        	textBuffer.append(currentChar);
        	digits.append(currentChar);
            // Extract hex digits.

            boolean hexDigit = true;
            while (hexDigit) {
            	
                currentChar = nextChar();  // consume digit
                hexDigit = false;
                switch (currentChar){
                case 'a':
                case 'A':
                case 'b':
                case 'B':                
                case 'c':                
                case 'C':                
                case 'd':
                case 'D':
                case 'e':
                case 'E':
                case 'f':
                case 'F':
                	hexDigit = true;
                	break;
                default:
                	
                }
                
                if((hexDigit == true) || Character.isDigit(currentChar)){
                	textBuffer.append(currentChar);
                	digits.append(currentChar);
                	hexDigit = true;
                }             	
            }

            return digits.toString();
        	
        }	
    }
    
    /**
     * returns the decimal value of a string of hex digits
     * @param hexDigits string of hex digits
     * @return int value of hex string
     */
    private int getHexValue(String hexDigits){
    	if (hexDigits == null){
    		return 0;
    	}
    	 
    	//remove hex id "0x"
    	hexDigits = hexDigits.replaceFirst("0x","");
    	
    	int currentHexCharValue = 0;
    	int integerValue = 0;
         int prevValue = -1;    // overflow occurred if prevValue > integerValue
         int index = 0;

         // Loop over the digits to compute the integer value
         // as long as there is no overflow.
         hexDigits.toLowerCase();
         char currentHexChar =' ';
         while ((index < hexDigits.length()) && (integerValue >= prevValue)) {
             prevValue = integerValue;
             currentHexChar = hexDigits.charAt(index);
             switch (currentHexChar){
             case 'a':
             case 'A': 
            	 currentHexCharValue = 10;
            	 break;
             case 'b':
             case 'B': 
            	 currentHexCharValue = 11;
            	 break;
             case 'c':                
             case 'C': 
            	 currentHexCharValue = 12;
            	 break;
             case 'd':
             case 'D':
            	 currentHexCharValue = 13;
            	 break;
             case 'e':
             case 'E':
            	 currentHexCharValue = 14;
            	 break;
             case 'f':
             case 'F':
            	 currentHexCharValue = 15;
            	 break;
             default:
             
            	 currentHexCharValue = Character.getNumericValue(currentHexChar);
             }
             integerValue = 16*integerValue + currentHexCharValue;
             index++;
         }

         // No overflow:  Return the integer value.
         if (integerValue >= prevValue) {
        	 //System.out.println("hex : " + hexDigits+" hex value :" + integerValue);
             return integerValue;
         }

         // Overflow:  Set the integer out of range error.
         else {
        	 //System.out.println("hex had an error");
             type = ERROR;
             value = RANGE_INTEGER;
             return 0;
         }

    }
    
    /**
     * Extracts character for a number in oct
     * @param textBuffer buffer for numbers to be stored
     * @return retuns the number in string formate
     * @throws Exception
     */
    private String extractOctNumber(StringBuilder textBuffer)
    throws Exception{
    	
    	char currentChar = currentChar();
    	
        // Must have at least one digit.
        if (!Character.isDigit(currentChar) || currentChar != '0') {
            type = ERROR;
            value = INVALID_NUMBER;
            return null;
        }
        else{
        	type = OCT;
            StringBuilder digits = new StringBuilder();

            // Extract oct digits.
            boolean octDigit = true;
            while (octDigit) {
            	if( !Character.isDigit(currentChar)){
            	    octDigit = false;
            	}else{
            		System.out.print(currentChar);
	                if((octDigit == true) || Character.isDigit(currentChar)){
	                	if(currentChar == '9' || currentChar == '8'){
	            	      	type = ERROR;
	            	    	value = RANGE_INTEGER;
	            	    	
	            	    }
	                	textBuffer.append(currentChar);
	                	digits.append(currentChar);
	                	currentChar = nextChar();
	                }
            	}
            }
            return digits.toString();
        }
    }
    
    private int getOctValue(String octDigits){
    	if (octDigits == null){
    		return 0;
    	}
    	     	
    	int integerValue = 0;
         int prevValue = -1;    // overflow occurred if prevValue > integerValue
         int index = 0;

         // Loop over the digits to compute the integer value
         // as long as there is no overflow.
         while ((index < octDigits.length()) && (integerValue >= prevValue)) {
             prevValue = integerValue;
             integerValue = 8*integerValue +
                            Character.getNumericValue(octDigits.charAt(index++));
         }

         // No overflow:  Return the integer value.
         if (integerValue >= prevValue) {
        	 //System.out.println("hex : " + hexDigits+" hex value :" + integerValue);
             return integerValue;
         }

         // Overflow:  Set the integer out of range error.
         else {
        	 //System.out.println("hex had an error");
             type = ERROR;
             value = RANGE_INTEGER;
             return 0;
         }

    	
    }
    
    /**
     * Extract and return the digits of an unsigned integer.
     * @param textBuffer the buffer to append the token's characters.
     * @return the string of digits.
     * @throws Exception if an error occurred.
     */
    private String unsignedIntegerDigits(StringBuilder textBuffer)
        throws Exception
    {
        char currentChar = currentChar();

        // Must have at least one digit.
        if (!Character.isDigit(currentChar)) {
            type = ERROR;
            value = INVALID_NUMBER;
            return null;
        }

        // Extract the digits.
        StringBuilder digits = new StringBuilder();
        while (Character.isDigit(currentChar)) {
            textBuffer.append(currentChar);
            digits.append(currentChar);
            currentChar = nextChar();  // consume digit
        }

        return digits.toString();
    }

    /**
     * Compute and return the integer value of a string of digits.
     * Check for overflow.
     * @param digits the string of digits.
     * @return the integer value.
     */
    private int computeIntegerValue(String digits)
    {
        // Return 0 if no digits.
        if (digits == null) {
            return 0;
        }

        int integerValue = 0;
        int prevValue = -1;    // overflow occurred if prevValue > integerValue
        int index = 0;

        // Loop over the digits to compute the integer value
        // as long as there is no overflow.
        while ((index < digits.length()) && (integerValue >= prevValue)) {
            prevValue = integerValue;
            integerValue = 10*integerValue +
                           Character.getNumericValue(digits.charAt(index++));
        }

        // No overflow:  Return the integer value.
        if (integerValue >= prevValue) {
            return integerValue;
        }

        // Overflow:  Set the integer out of range error.
        else {
            type = ERROR;
            value = RANGE_INTEGER;
            return 0;
        }
    }

    /**
     * Compute and return the float value of a real number.
     * @param wholeDigits the string of digits before the decimal point.
     * @param fractionDigits the string of digits after the decimal point.
     * @param exponentDigits the string of exponent digits.
     * @param exponentSign the exponent sign.
     * @return the float value.
     */
    private float computeFloatValue(String wholeDigits, String fractionDigits,
                                    String exponentDigits, char exponentSign)
    {
        double floatValue = 0.0;
        int exponentValue = computeIntegerValue(exponentDigits);
        String digits = wholeDigits;  // whole and fraction digits

        // Negate the exponent if the exponent sign is '-'.
        if (exponentSign == '-') {
            exponentValue = -exponentValue;
        }

        // If there are any fraction digits, adjust the exponent value
        // and append the fraction digits.
        if (fractionDigits != null) {
            exponentValue -= fractionDigits.length();
            digits += fractionDigits;
        }

        // Check for a real number out of range error.
        if (Math.abs(exponentValue + wholeDigits.length()) > MAX_EXPONENT) {
            type = ERROR;
            value = RANGE_REAL;
            return 0.0f;
        }

        // Loop over the digits to compute the float value.
        int index = 0;
        while (index < digits.length()) {
            floatValue = 10*floatValue +
                         Character.getNumericValue(digits.charAt(index++));
        }

        // Adjust the float value based on the exponent value.
        if (exponentValue != 0) {
            floatValue *= Math.pow(10, exponentValue);
        }

        return (float) floatValue;
    }
}
