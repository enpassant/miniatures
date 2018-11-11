package cart;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class Milk implements Item {}
class Butter implements Item {}
class Bread implements Item {}

class MasterCard implements Payment {
}

public class ShoppingCartImpl {
  private ShoppingCartImpl() {};

  public static AddToEmpty addToEmpty = (emptyCart, item) -> {
    return new ActiveCart(Arrays.asList(item));
  };

  public static AddToActive addToActive = (activeCart, item) -> {
    List<Item> items = activeCart.getUnpaidItems().collect(Collectors.toList());
    items.add(item);
    return new ActiveCart(items);
  };

  public static RemoveFromActive removeFromActive = (activeCart, item) -> {
    List<Item> items =
      activeCart.getUnpaidItems()
        .filter(i -> !i.equals(item))
        .collect(Collectors.toList());
    if (items.isEmpty()) return new EmptyCart();
    else return new ActiveCart(items);
  };

  public static PayActive payActive = (activeCart, payment) -> {
    List<Item> items = activeCart.getUnpaidItems().collect(Collectors.toList());
    return new PaidCart(items, payment);
  };

  public static void main(String[] args) {
    Milk milk = new Milk();
    Bread bread = new Bread();
    MasterCard masterCard = new MasterCard();

    EmptyCart emptyCart = new EmptyCart();
    ActiveCart activeCartMilk =
      addToEmpty.apply(emptyCart, milk);

    ActiveCart activeCartMilkAndBread =
      addToActive.apply(activeCartMilk, bread);

    EmptyOrActiveCart emptyOrActiveCart =
      removeFromActive.apply(activeCartMilkAndBread, milk);

    if (emptyOrActiveCart instanceof ActiveCart) {
      PaidCart paidCart =
        payActive.apply((ActiveCart) emptyOrActiveCart, masterCard);
      paidCart.getPaidItems().forEach(System.out::println);
    }
  }
}
