package pixie.parser;

import pixie.parser.values.BoolValue;

public interface Operable<V> {
     V get();
     Operable add(Operable other) throws SyntaxException;
     Operable sub(Operable other) throws SyntaxException;
     Operable mul(Operable other) throws SyntaxException;
     Operable div(Operable other) throws SyntaxException;
     Operable pow(Operable other) throws SyntaxException;
     Operable and(Operable other) throws SyntaxException;
     Operable or(Operable other) throws SyntaxException;
     BoolValue more(Operable other) throws SyntaxException;
     BoolValue less(Operable other) throws SyntaxException;
     BoolValue moreEqu(Operable other) throws SyntaxException;
     BoolValue lessEqu(Operable other) throws SyntaxException;
     BoolValue equals(Operable other) throws SyntaxException;
     BoolValue notEquals(Operable other) throws SyntaxException;
     Operable inv() throws SyntaxException;
}
