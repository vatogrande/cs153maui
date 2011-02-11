package wci.frontend.java.tokens;

import wci.frontend.*;
import wci.frontend.java.*;

import static wci.frontend.Source.EOL;
import static wci.frontend.Source.EOF;
import static wci.frontend.java.JavaTokenType.*;
import static wci.frontend.java.JavaErrorCode.*;

/**
 * <h1>JavaStringToken</h1>
 *
 * <p> Java string tokens.</p>
 *
 * <p>Copyright (c) 2011 by Chris Douglass</p>
 * <p>Based on code originally written by Ron Mak.</p>
 */
public class JavaStringToken extends JavaToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public JavaStringToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a Java string token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        StringBuilder textBuffer = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();

        char currentChar = nextChar();  // consume initial quote
        textBuffer.append('"');

        // Get string characters.
        do {
        	// If we hit an EOL, we need to check if we previously saw a \ to indicate line spanning
        	// If not, then we need to bail.
            if(currentChar == EOL && textBuffer.charAt(textBuffer.length() - 1) != '\\') {
            	break;
            }
            else if(currentChar == EOL)
            {
            	// Here we need to remove the last \ from the textBuffer because it doesn't belong in the string,
            	// then we consume.
            	textBuffer.deleteCharAt(textBuffer.length() - 1);
            	valueBuffer.deleteCharAt(valueBuffer.length() - 1);
            	currentChar = nextChar();
            }
        	
        	// Replace any whitespace character with a blank.
            if (Character.isWhitespace(currentChar)) {
                currentChar = ' ';
            }
            
            // Our condition includes the last character of the textBuffer because we need to know
            // if the double quotes were escaped.
            if (!(currentChar == '"' && textBuffer.charAt(textBuffer.length() - 1) != '\\') && (currentChar != EOF)) {
                textBuffer.append(currentChar);
                valueBuffer.append(currentChar);
                currentChar = nextChar();  // consume character
            }
        } while (!(currentChar == '"' && textBuffer.charAt(textBuffer.length() - 1) != '\\') && (currentChar != EOF));

        if (currentChar == '"') {
            nextChar();  // consume final quote
            textBuffer.append('"');

            type = STRING;
            value = valueBuffer.toString();
        }
        else if (currentChar == EOL) {
        	// If we hit an end of line without a \, the currentChar will be an EOL
            type = ERROR;
            value = UNEXPECTED_EOL;
        }
        else {
        	// Only other condition is EOF
            type = ERROR;
            value = UNEXPECTED_EOF;
        }

        text = textBuffer.toString();
    }
}
