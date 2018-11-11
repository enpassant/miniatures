package cart;

import java.util.List;
import java.util.stream.Stream;

interface Item {}
interface Error {}
interface Payment {}

public interface ShoppingCart {}

interface EmptyOrActiveCart extends ShoppingCart {}

class EmptyCart implements EmptyOrActiveCart {
}

class ActiveCart implements EmptyOrActiveCart {
  private final List<Item> unpaidItems;

  public ActiveCart(List<Item> unpaidItems) {
    this.unpaidItems = unpaidItems;
  }

  public Stream<Item> getUnpaidItems() {
    return unpaidItems.stream();
  }
}

class PaidCart {
  private final List<Item> paidItems;
  private final Payment payment;

  public PaidCart(List<Item> paidItems, Payment payment) {
    this.paidItems = paidItems;
    this.payment = payment;
  }

  public Stream<Item> getPaidItems() {
    return paidItems.stream();
  }

  public Payment getPayment() {
    return payment;
  }
}

interface AddToEmpty {
  ActiveCart apply(EmptyCart emptyCart, Item item);
}

interface AddToActive {
  ActiveCart apply(ActiveCart activeCart, Item item);
}

interface RemoveFromActive {
  EmptyOrActiveCart apply(ActiveCart activeCart, Item item);
}

interface PayActive {
  PaidCart apply(ActiveCart activeCart,Payment payment);
}
