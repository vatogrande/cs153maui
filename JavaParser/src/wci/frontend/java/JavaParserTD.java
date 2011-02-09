package wci.frontend.java;

import static wci.frontend.java.JavaErrorCode.IO_ERROR;
import static wci.frontend.java.JavaTokenType.ERROR;
import static wci.message.MessageType.PARSER_SUMMARY;
import static wci.message.MessageType.TOKEN;
import wci.frontend.EofToken;
import wci.frontend.Parser;
import wci.frontend.Scanner;
import wci.frontend.Token;
import wci.frontend.TokenType;
import wci.frontend.java.JavaErrorCode;
import wci.frontend.java.JavaErrorHandler;
import wci.message.Message;
/*
* <h1>JavaParserTD</h1>
*
* <p>The top-down Java parser.</p>
*
* <p>Copyright (c) 2011 Team MakPak</p>
* <p>For instructional purposes only.  No warranties.</p>
*/
public class JavaParserTD extends Parser {

	 protected static JavaErrorHandler errorHandler = new JavaErrorHandler();

	    /**
	     * Constructor.
	     * @param scanner the scanner to be used with this parser.
	     */
	    public JavaParserTD(Scanner scanner)
	    {
	        super(scanner);
	    }

	    /**
	     * Parse a Java source program and generate the symbol table
	     * and the intermediate code.
	     */
	    public void parse()
	        throws Exception
	    {
	        Token token;
	        long startTime = System.currentTimeMillis();

	        try {
	            // Loop over each token until the end of file.
	            while (!((token = nextToken()) instanceof EofToken)) {
	                TokenType tokenType = token.getType();

	                if (tokenType != ERROR) {

	                    // Format each token.
	                    sendMessage(new Message(TOKEN,
	                                            new Object[] {token.getLineNumber(),
	                                                          token.getPosition(),
	                                                          tokenType,
	                                                          token.getText(),
	                                                          token.getValue()}));
	                }
	                else {
	                    errorHandler.flag(token, (JavaErrorCode) token.getValue(),
	                                      this);
	                }

	            }

	            // Send the parser summary message.
	            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
	            sendMessage(new Message(PARSER_SUMMARY,
	                                    new Number[] {token.getLineNumber(),
	                                                  getErrorCount(),
	                                                  elapsedTime}));
	        }
	        catch (java.io.IOException ex) {
	            errorHandler.abortTranslation(IO_ERROR, this);
	        }
	    }

	    /**
	     * Return the number of syntax errors found by the parser.
	     * @return the error count.
	     */
	    public int getErrorCount()
	    {
	        return errorHandler.getErrorCount();
	    }
}
