package wci.frontend.java.tokens;

import wci.frontend.Source;
import wci.frontend.Token;

import static wci.frontend.Source.EOL;
import static wci.frontend.Source.EOF;
import static wci.frontend.java.JavaTokenType.*;
import static wci.frontend.java.JavaErrorCode.*;

public class JavaCharToken extends Token {
	
	char valueChar;
	
	public JavaCharToken(Source source) throws Exception {
		super(source);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Extract a Java char token from the source.
	 * @throws Exception if an error occurred.
	 */
	protected void extract() throws Exception
    {
		StringBuilder textBuffer = new StringBuilder();
		
		// Consume first '
		char currentChar = nextChar();
		textBuffer.append('\'');
		
		/*
		 * Things we need to be able to handle:
		 * 1) Consuming both ends of the single quote
		 * 2) Single characters
		 * 3) Escaped characters
		 * 4) Error: more than one character between 's
		 * 5) Hitting an EOL or EOF 
		 */
		if(currentChar == '\\') {
			// Escaped characters will come first, so we read the next one
			currentChar = nextChar();
			textBuffer.append('\\');
			textBuffer.append(currentChar);
			if(currentChar != EOL && currentChar != EOF) {
				// We don't want nasty characters
				switch(currentChar)
				{
					case 'n': valueChar = '\n'; break;
					case 't': valueChar = '\t'; break;
					default:  valueChar = currentChar; break;
				}
			}
		}
		else {
			// Simple case, letter read
			valueChar = currentChar;
			textBuffer.append(currentChar);
		}
		
		// Now we look at the next character and check if it's a '
		// If not, then our char is illegal
		currentChar = nextChar();
		
		if (currentChar == EOL) {
        	// If we hit an end of line without a \, the currentChar will be an EOL
            type = ERROR;
            value = UNEXPECTED_EOL;
        }
        else if (currentChar == EOF) {
        	// Hitting EOF
            type = ERROR;
            value = UNEXPECTED_EOF;
        }
		if(currentChar != '\'')
		{
			// We have extra characters, such as in the case of multiple chars within the same 's.
    		// Consume letters or numbers until we hit an ', EOL, or EOF and then set an ERROR.
			while(currentChar != '\'' && currentChar != EOL && currentChar != EOF) {
        		textBuffer.append(currentChar);
        		currentChar = nextChar();
        	}
        	textBuffer.append(currentChar);
        	currentChar = nextChar();
        	if(currentChar == '\'') {
        		// Ended with a ', need to consume it
        		nextChar();
        		textBuffer.append('\'');
        	}
        	type = ERROR;
        	value = INVALID_CHARACTER;
		}
		else if(currentChar == '\'')
		{
			nextChar(); // Consume final '
			textBuffer.append('\'');
			
			type = CHARACTER;
			value = new Character(valueChar);
		}
		
		if (currentChar == EOL) {
            type = ERROR;
            value = UNEXPECTED_EOL;
        }
        else if (currentChar == EOF) {
        	// Hitting EOF
            type = ERROR;
            value = UNEXPECTED_EOF;
        }
		text = textBuffer.toString();
    }

}
