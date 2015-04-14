/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A "sed -e" like reg exp.
 * @author Christopher Deckers
 */
public class SedRegExpPattern {

  private static class SedRegExp {
    private Pattern pattern;
    private String replacement;
    private boolean isGlobal;
    public SedRegExp(String regExp, int index1, int index2, String originalRegExp) {
      String toFind = regExp.substring(0, index1);
      replacement = index2 == -1? null: regExp.substring(index1, index2);
      String modifiers = index2 == -1? regExp.substring(index1): regExp.substring(index2);
      isGlobal = false;
      int flags = 0;
      for(int i=0; i<modifiers.length(); i++) {
        char c = modifiers.charAt(i);
        switch(c) {
          case 'g': isGlobal = true; break;
          case 'i': flags |= Pattern.CASE_INSENSITIVE; break;
          case 'd': flags |= Pattern.UNIX_LINES; break;
          case 'm': flags |= Pattern.MULTILINE; break;
          case 's': flags |= Pattern.DOTALL; break;
          case 'u': flags |= Pattern.UNICODE_CASE; break;
          case 'x': flags |= Pattern.COMMENTS; break;
          default:
            throw new IllegalArgumentException("Invalid expression format: " + originalRegExp);
        }
      }
      pattern = Pattern.compile(toFind, flags);
    }
    public Pattern getPattern() {
      return pattern;
    }
    public String getReplacement() {
      return replacement;
    }
    public boolean isGlobal() {
      return isGlobal;
    }
  }
  
  private List<SedRegExp> sedRegExpList;
  
  /**
   * A "sed -e" like reg exp, of the form:<br/>
   * - /regexp/flags: find and output the matches.<br/>
   * - /regexp/replacement/flags: replace the matches and output the resulting string.<br/>
   * Flags can be left empty or any combinations of the characters 'gidmsux' (g performs a replace all instead of just the first match. For other flags, refer to the Javadoc of Pattern).
   * It is also possible to chain the output using ';' to perform multiple replacements.<br/>
   * If the regexp contains capturing groups, a find operation would only retain those; for a replace operation, the replacement string can refer to capturing groups with a syntax like '$1'.
   */
  public SedRegExpPattern(String regExp) {
    String originalRegExp = regExp;
    if(!regExp.startsWith("/")) {
      throw new IllegalArgumentException("Invalid expression format: " + originalRegExp);
    }
    regExp = regExp.substring(1);
    StringBuilder sb = new StringBuilder();
    char[] chars = regExp.toCharArray();
    int index1 = -1;
    int index2 = -1;
    sedRegExpList = new ArrayList<SedRegExp>(3);
    for(int i=0; i<chars.length; i++) {
      char c = chars[i];
      switch(c) {
        case ';':
          sedRegExpList.add(new SedRegExp(sb.toString(), index1, index2, originalRegExp));
          index1 = -1;
          index2 = -1;
          sb = new StringBuilder();
          i++;
          if(i >= chars.length || chars[i] != '/') {
            throw new IllegalArgumentException("Invalid expression format: " + originalRegExp);
          }
          break;
        case '\\':
          i++;
          if(i >= chars.length) {
            throw new IllegalArgumentException("Invalid expression format: " + originalRegExp);
          }
          switch(chars[i]) {
            case '/': sb.append('/'); break;
            case ';': sb.append(';'); break;
            default: sb.append('\\').append(chars[i]); break;
          }
          break;
        case '/':
          if(index1 == -1) {
            index1 = sb.length();
          } else if(index2 == -1) {
            index2 = sb.length();
          } else {
            throw new IllegalArgumentException("Invalid expression format: " + originalRegExp);
          }
          break;
        default: sb.append(c); break;
      }
    }
    if(index1 == -1) {
      throw new IllegalArgumentException("Invalid expression format: " + originalRegExp);
    }
    sedRegExpList.add(new SedRegExp(sb.toString(), index1, index2, originalRegExp));
  }
  
  public String apply(String text) {
    for(SedRegExp sedRegExp: sedRegExpList) {
      text = applySedRegularExpression(sedRegExp, text);
    }
    return text;
  }

  private static String applySedRegularExpression(SedRegExp sedRegExp, String text) {
    Pattern pattern = sedRegExp.getPattern();
    String replacement = sedRegExp.getReplacement();
    boolean isGlobal = sedRegExp.isGlobal();
    Matcher matcher = pattern.matcher(text);
    if(replacement == null) {
      // Just returning the matches, no replacement
      int groupCount = matcher.groupCount();
      StringBuilder sb = new StringBuilder();
      while(matcher.find()) {
        if(groupCount > 0) {
          for(int i=0; i<groupCount; i++) {
            String group = matcher.group(i + 1);
            if(group != null) {
              sb.append(group);
            }
          }
        } else {
          String group = matcher.group();
          if(group != null) {
            sb.append(group);
          }
        }
        if(!isGlobal) {
          break;
        }
      }
      return sb.toString();
    }
    if(isGlobal) {
      return matcher.replaceAll(replacement);
    }
    return matcher.replaceFirst(replacement);
  }

}
