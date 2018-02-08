import java.util.Scanner;
import java.util.Locale;
import java.text.NumberFormat;

public class DecimalFormat {

    public static void main(String[] args) {
        Locale[] locales = NumberFormat.getAvailableLocales();
        System.out.println("Kérem a Celsius-fok értéket: ");
        Scanner be = new Scanner(System.in);
        double C = be.nextDouble();
     for (int i = 0; i < locales.length; ++i) {
         if (locales[i].getCountry().length() == 0) {
            continue; // Skip language-only locales
         }
         System.out.print(locales[i].getDisplayName());
         //NumberFormat form = NumberFormat.getInstance(locales[i]);
         NumberFormat form = NumberFormat.getInstance(Locale.forLanguageTag("hu-HU"));
         System.out.println("Country: " + Locale.forLanguageTag("hu-HU").getDisplayName());
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#.0#");
        double szam2 = (double)9/5*C+32;
        System.out.println("Fahrenheitben: " +form.format(szam2)+" fok");
        System.out.println("Fahrenheitben: " +formatter.format(szam2)+" fok");
     }

    }
}


