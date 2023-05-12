package pixie.parser;

import pixie.parser.values.BoolValue;

public interface Operable<V> {
     V get(LineParser parser);
     Operable add(Operable other, LineParser parser) throws SyntaxException;
     Operable sub(Operable other, LineParser parser) throws SyntaxException;
     Operable mul(Operable other, LineParser parser) throws SyntaxException;
     Operable div(Operable other, LineParser parser) throws SyntaxException;
     Operable pow(Operable other, LineParser parser) throws SyntaxException;
     Operable mod(Operable other, LineParser parser) throws SyntaxException;
     Operable and(Operable other, LineParser parser) throws SyntaxException;
     Operable or(Operable other, LineParser parser) throws SyntaxException;
     BoolValue more(Operable other, LineParser parser) throws SyntaxException;
     BoolValue less(Operable other, LineParser parser) throws SyntaxException;
     BoolValue moreEqu(Operable other, LineParser parser) throws SyntaxException;
     BoolValue lessEqu(Operable other, LineParser parser) throws SyntaxException;
     BoolValue equals(Operable other, LineParser parser) throws SyntaxException;
     BoolValue notEquals(Operable other, LineParser parser) throws SyntaxException;
     Operable inv(LineParser parser) throws SyntaxException;
}
