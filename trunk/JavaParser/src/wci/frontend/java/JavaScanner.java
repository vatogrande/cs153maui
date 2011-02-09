package wci.frontend.java;


import static wci.frontend.Source.EOF;
import static wci.frontend.java.JavaErrorCode.INVALID_CHARACTER;
import wci.frontend.EofToken;
import wci.frontend.Scanner;
import wci.frontend.Source;
import wci.frontend.Token;
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
		  //skipWhiteSpace();

		
	        Token token;
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
	        else if (currentChar == '"') {
	            token = new JavaStringToken(source);
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

}
