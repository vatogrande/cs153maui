package wci.frontend.java;

import java.util.Hashtable;
import java.util.HashSet;

import wci.frontend.TokenType;

/**
 * <h1>JavaTokenType</h1>
 *
 * <p>Java token types.</p>
 *
 * <p>Copyright (c) 2011 by MakPak</p>
 * <p>HW2 Assignment</p>
 */
public enum JavaTokenType implements TokenType
{
    // Reserved words.
    ABSTRACT, BREAK, CASE, CHAR, CLASS, CONST, CONTINUE, DO, DOUBLE, ELSE, 
    ENUM, EXTENDS, FLOAT, FOR, GOTO, IF, INT, LONG, NATIVE, RETURN, SHORT, 
    PACKAGE, PROTECTED, STATIC, SWITCH, SUPER, THIS, THROW, VOID, VOLATILE, 
    WHILE,

    // Special symbols.
    TILDE("~"), EXCLAMATION("!"), AT_SIGN("@"), PERCENT("%"), CARROT("^"), 
    AMPERSAND("&"), STAR("*"), PLUS("+"), MINUS("-"), EQUALS("="), PIPE("|"),
    SLASH("/"), SEMICOLON(";"), COLON(":"), QUESTION_MARK("?"), LESS_THAN("<"), 
    LESS_EQUALS("<="), GREATER_EQUALS(">="), GREATER_THAN(">"), DOT("."), 
    COMMA(","), QUOTE("'"), DOUBLE_QUOTE("\""), LEFT_PAREN("("), RIGHT_PAREN(")"),
    LEFT_BRACKET("["), RIGHT_BRACKET("]"), LEFT_BRACE("{"), RIGHT_BRACE("}"),
    PLUS_PLUS("++"), MINUS_MINUS("--"), LEFT_SHIFT("<<"), RIGHT_SHIFT(">>"), 
    PLUS_EQUAL("+="), MINUS_EQUAL("-="), STAR_EQUAL("*="), SLASH_EQUAL("/="),
    EQUAL_EQUAL("=="), PIPE_EQUAL("|="), PERCENT_EQUAL("%="), AMPERSAND_EQUAL("&="),
    CARROT_EQUAL("^="), DOUBLE_PIPE("||"), DOUBLE_AMPERSAND("&&"), NOT_EQUAL("!="),
    DOUBLD_SLASH("//"), SLASH_STAR("/*"), STAR_SLASH("*/"), LEFT_SHIFT_EQUAL("<<="),
    RIGHT_SHIFT_EQUALS(">>="), 

    IDENTIFIER, CHARACTER, STRING, INTEGER,
    ERROR, END_OF_FILE;

    private static final int FIRST_RESERVED_INDEX = ABSTRACT.ordinal();
    private static final int LAST_RESERVED_INDEX  = WHILE.ordinal();

    private static final int FIRST_SPECIAL_INDEX = TILDE.ordinal();
    private static final int LAST_SPECIAL_INDEX  = RIGHT_SHIFT_EQUALS.ordinal();

    private String text;  // token text

    /**
     * Constructor.
     */
    JavaTokenType()
    {
        this.text = this.toString().toLowerCase();
    }

    /**
     * Constructor.
     * @param text the token text.
     */
    JavaTokenType(String text)
    {
        this.text = text;
    }

    /**
     * Getter.
     * @return the token text.
     */
    public String getText()
    {
        return text;
    }

    // Set of lower-cased Java reserved word text strings.
    public static HashSet<String> RESERVED_WORDS = new HashSet<String>();
    static {
        JavaTokenType values[] = JavaTokenType.values();
        for (int i = FIRST_RESERVED_INDEX; i <= LAST_RESERVED_INDEX; ++i) {
            RESERVED_WORDS.add(values[i].getText().toLowerCase());
        }
    }

    // Hash table of Java special symbols.  Each special symbol's text
    // is the key to its Java token type.
    public static Hashtable<String, JavaTokenType> SPECIAL_SYMBOLS =
        new Hashtable<String, JavaTokenType>();
    static {
        JavaTokenType values[] = JavaTokenType.values();
        for (int i = FIRST_SPECIAL_INDEX; i <= LAST_SPECIAL_INDEX; ++i) {
            SPECIAL_SYMBOLS.put(values[i].getText(), values[i]);
        }
    }
}
