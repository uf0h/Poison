package me.ufo.poison.hub;

public final class Text {

  private Text() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  public static final char SECTION_CHAR = '\u00A7'; // §
  public static final char AMPERSAND_CHAR = '&';

  public static String colorize(String s) {
    return s == null ? null : translateAlternateColorCodes(AMPERSAND_CHAR, SECTION_CHAR, s);
  }

  public static String decolorize(String s) {
    return s == null ? null : translateAlternateColorCodes(SECTION_CHAR, AMPERSAND_CHAR, s);
  }

  private static String translateAlternateColorCodes(char from, char to, String textToTranslate) {
    char[] b = textToTranslate.toCharArray();
    for (int i = 0; i < b.length - 1; i++) {
      if (b[i] == from && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
        b[i] = to;
        b[i+1] = Character.toLowerCase(b[i+1]);
      }
    }
    return new String(b);
  }

}
