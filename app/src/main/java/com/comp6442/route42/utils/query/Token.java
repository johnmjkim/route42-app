package com.comp6442.route42.utils.query;

import androidx.annotation.NonNull;

public class Token {
  private final TokenType tokenType;
  private final String value;

  public Token(TokenType tokenType, String value) {
    this.tokenType = tokenType;
    this.value = value;
  }

  public TokenType getType() {
    return tokenType;
  }

  public String getValue() {
    return value;
  }

  public static Token parseToken(String text) {
    text = text.trim();
    if (text.startsWith("(")) return new Token(TokenType.LBRA, "(");
    else if (text.startsWith(")")) return new Token(TokenType.RBRA, ")");
    else if (text.toLowerCase().startsWith("and")) return new Token(TokenType.AND, "AND");
    else if (text.toLowerCase().startsWith("or")) return new Token(TokenType.OR, "OR");
    else {
      if (text.contains(":")) return new Token(TokenType.FILTER, text);
      else return new Token(TokenType.FILTER, String.format("hashtags:%s", text));
    }
  }

  @NonNull
  @Override
  public String toString() {
    return "Token{" +
            "tokenType=" + tokenType +
            ", value='" + value + '\'' +
            '}';
  }

  enum TokenType {LBRA, RBRA, AND, OR, FILTER}
}
