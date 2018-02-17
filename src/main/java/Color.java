import java.util.Optional;

class Color {
  private final long hex;
  static Color makeFromRGB(String rgb) {
    return new Color(Integer.parseInt(rgb, 16));
  }
  static Optional<Color> makeFromPalette(int red, int green, int blue) {
    if (red < 0 || red > 255 ||
        green < 0 || green > 255 ||
        blue < 0 || blue > 255)
    {
        return Optional.empty();
    }

    return Optional.of(new Color((red << 16) + (green << 8) + blue));
  }
  static Color makeFromHex(int h) {
    return new Color(h);
  }
  private Color(int h) {
    this.hex = h;
  }

  public String toString() {
      return "Color: " + hex;
  }

  public static void main(String[] args) {
    Color blue = Color.makeFromPalette(0, 0, 255).get();

    System.out.println("Color: " +
      Color.makeFromPalette(128, 73, 2));
    System.out.println("Color: " +
      Color.makeFromPalette(1128, 73, 2).orElse(blue));
  }
}
