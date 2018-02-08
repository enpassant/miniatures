import java.security.Provider;
import java.security.Security;

public class SecurityListing {
    public static class Test {};
    public static class Test2 extends Test {};

    public static void main(String[] args) {
        for (Provider provider : Security.getProviders()) {
            System.out.println("Provider: " + provider.getName());
            for (Provider.Service service : provider.getServices()) {
                System.out.println("  Algorithm: " + service.getAlgorithm());
            }
        }

        Test test = getValue("test", Test.class);
    }

    public static Object getObject() {
        return new Test2();
    }

    public static <T> T getValue(String name, Class<T> valueType) {
      try {
          return valueType.newInstance();
      } catch(Exception e) {
          throw new RuntimeException("");
      }
   }
}
