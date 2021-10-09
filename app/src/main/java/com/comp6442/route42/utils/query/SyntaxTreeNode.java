package com.comp6442.route42.utils.query;

import static com.comp6442.route42.utils.query.Token.TokenType.AND;
import static com.comp6442.route42.utils.query.Token.TokenType.FILTER;
import static com.comp6442.route42.utils.query.Token.TokenType.LBRA;
import static com.comp6442.route42.utils.query.Token.TokenType.OR;
import static com.comp6442.route42.utils.query.Token.TokenType.RBRA;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SyntaxTreeNode {
  private String key;
  private SyntaxTreeNode left = null, right = null;
  private String value = null;
  private Set<String> supportedAttributes = new HashSet<>(Arrays.asList("username", "hashtags"));

  private SyntaxTreeNode(String key) {
    this.key = key;
  }

  public SyntaxTreeNode(String key, String value) {
    if (!supportedAttributes.contains(key)) throw new ClassCastException("invalid attribute");
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setLeft(SyntaxTreeNode left) {
    this.left = left;
  }

  public void setRight(SyntaxTreeNode right) {
    this.right = right;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @NonNull
  @Override
  public String toString() {
    if (this.value == null) {
      return String.format("{ %s: [\n\t%s\n\t%s\n\t]\n}\n", key, left.toString(1), right.toString(1));
    } else {
      return String.format("{ %s: %s }\n", key, value);
    }
  }

  public String toString(int level) {
    String indent = "";
    for(int i=0; i<level; i++) indent += "\t";
    if (this.value == null) {
      return String.format("%s{ %s%s: [\n\t%s%s\n\t%s%s\n\t\t%s]\n\t%s},", indent, indent, key, indent, left.toString(level+1), indent, right.toString(level+1), indent, indent);
    } else {
      return String.format("%s{ %s: %s },", indent, key, value);
    }
  }

  public static SyntaxTreeNode fromToken(Token token) {
    switch (token.getType()) {
      case AND:
        return new SyntaxTreeNode(token.getValue());
      case OR:
        return new SyntaxTreeNode(token.getValue());
      case FILTER:
        String[] groups = token.getValue().split(":");
        if(groups.length == 2) return new SyntaxTreeNode(groups[0].trim(), groups[1].trim());
        else throw new ClassCastException("error " + token.toString());
    }
    return null;
  }

  public static SyntaxTreeNode fromTokens(List<Token> tokens) {

    if (tokens.size() == 1) return SyntaxTreeNode.fromToken(tokens.get(0));

    // remove first and last parenthesis if they are of the same set
    if (tokens.get(0).getType() == LBRA) {
      int level = 0;
      int idx = 0;
      boolean remove = false;
      while (idx < tokens.size()) {
        if(tokens.get(idx).getType() == LBRA) level++;
        else if(tokens.get(idx).getType() == RBRA) level--;

        if (level == 0 && tokens.get(idx).getType() == RBRA) {
          remove = true;
          break;
        }

        idx++;
      }
      if (remove && idx == tokens.size() - 1) {
        tokens = tokens.subList(1, tokens.size() - 1);
      }
    }

    // ensure parentheses match
    List<Integer> levels = new ArrayList<>();
    int level = 0;
    for (int i=0; i<tokens.size(); i++) {
      Token token = tokens.get(i);
      if (token.getType() == LBRA) {
        levels.add(level);
        level++;
      } else if (token.getType() == RBRA) {
        level--;
        levels.add(level);
      } else {
        levels.add(level);
      }
    }
    assert level == 0;

    // look for first level 0 AND or OR
    int idx = 0;
    while(idx < tokens.size() && !(levels.get(idx) == 0 && (tokens.get(idx).getType() == AND || tokens.get(idx).getType() == OR))) {
      idx++;
    }
    if (idx < tokens.size() - 1) {
      SyntaxTreeNode node = SyntaxTreeNode.fromToken(tokens.get(idx));
      node.setLeft(
              SyntaxTreeNode.fromTokens(new ArrayList<>(tokens.subList(0, idx)))
      );
      node.setRight(
              SyntaxTreeNode.fromTokens(new ArrayList<>(tokens.subList(idx+1, tokens.size())))
      );
      return node;
    }

    return null;
  }
}
