package wci.frontend.java;


import static wci.frontend.Source.EOF;
import static wci.frontend.Source.EOL;
import static wci.frontend.java.JavaErrorCode.INVALID_CHARACTER;
import static wci.frontend.java.JavaErrorCode.MISSING_CLOSE_COMMENT;
import wci.frontend.EofToken;
import wci.frontend.Scanner;
import wci.frontend.Source;
import wci.frontend.Token;
import wci.frontend.java.tokens.JavaCharToken;
import wci.frontend.java.tokens.JavaWordToken;
import wci.frontend.java.tokens.JavaNumberToken;
import wci.frontend.java.tokens.JavaStringToken;
import wci.frontend.java.tokens.JavaSpecialSymbolToken;
import wci.frontend.java.tokens.JavaErrorToken;



public class JavaScanner extends Scanner {

	public JavaScanner(Source source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Token extractToken() throws Exception {
		  	
		    Token token;
		    try
		    {
		    	skipWhiteSpace();
		    }
		    catch (MalformedCommentException mce)
		    {
		    	 token = new JavaErrorToken(source, MISSING_CLOSE_COMMENT,
                         "EOF");

		    }
			
		
	        
	        char currentChar = currentChar();

	        // Construct the next token.  The current character determines the
	        // token type.
	        if (currentChar == EOF) {
	            token = new EofToken(source);
	        }
	        else if (Character.isLetter(currentChar)) {
	            token = new JavaWordToken(source);
	        }
	        else if (Character.isDigit(currentChar)) {
	            token = new JavaNumberToken(source);
	        }
	        else if (currentChar == '.') {
	        	// We need to check if the next char is a number
	        	// If so, send it to NumberToken, if not then send it
	        	// to SpecialSymbol
	        	if(Character.isDigit(source.peekChar()))
	        		token = new JavaNumberToken(source);
	        	else
	        		token = new JavaSpecialSymbolToken(source);
	        }
	        else if (currentChar == '"') {
	            token = new JavaStringToken(source);
	        }
	        else if (currentChar == '\'') {
	        	token = new JavaCharToken(source);
	        }
	        else if (JavaTokenType.SPECIAL_SYMBOLS
	                 .containsKey(Character.toString(currentChar))) {
	            token = new JavaSpecialSymbolToken(source);
	        }
	        else {
	            token = new JavaErrorToken(source, INVALID_CHARACTER,
	                                         Character.toString(currentChar));
	            nextChar();  // consume character
	        }

	        return token;
       
	}
	
    /**
     * Skip whitespace characters by consuming them.  A comment is whitespace.
     * @throws Exception if an error occurred.
     */
    private void skipWhiteSpace()
        throws Exception
    {
        char currentChar = currentChar();

        while (Character.isWhitespace(currentChar) || (currentChar == '/')) {

            // Start of a comment?
            if (currentChar == '/') 
            {
            	currentChar = nextChar(); // consume the *
            
            	if(currentChar == '*') 
            	{            		 	
            		do 
            		{            			
            			currentChar = nextChar();  // consume comment characters
            			if(currentChar == '*')
            			{
            				currentChar = nextChar();
            				if(currentChar == '/')
                			{
            					break;
                			}
            			}
            		} while (currentChar != EOF);

            		// Found no closing '*/'?
            		if (currentChar == EOF) 
            		{
            			throw new MalformedCommentException("Unexpected End Of File.");
            		}	
            	}
            	else if(currentChar == '/')
            	{
            		do
            		{
            			currentChar = nextChar();  // consume comment characters
              		}while(currentChar != EOL && currentChar != EOF);
            	}
            }

            // Not a comment.
            else {
                currentChar = nextChar();  // consume whitespace character
            }
        }
    }

}
