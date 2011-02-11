package wci.frontend.java.tokens;

import wci.frontend.Source;
import wci.frontend.Token;

public class JavaCharToken extends Token {

	public JavaCharToken(Source source) throws Exception {
		super(source);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Extract a Java char token from the source.
	 * @throws Exception if an error occured.
	 */
	protected void extract() throws Exception
    {
		throw new UnsupportedOperationException("Char consuming not implemented yet!");
    }

}
