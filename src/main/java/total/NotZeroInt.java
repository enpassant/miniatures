package total;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class NotZeroInt {
    public final int number;

    private NotZeroInt(int number) {
        this.number = number;
    }

    public static Optional<NotZeroInt> of(int number) {
        if (number == 0) {
            return Optional.empty();
        } else {
            return Optional.of(new NotZeroInt(number));
        }
    }

    @Override
    public String toString() {
        return "NotZeroInt(" + number + ")";
    }

    @Override
    public boolean equals(Object value) {
        if (value == this) return true;
        if (value instanceof NotZeroInt) {
            @SuppressWarnings("unchecked")
            NotZeroInt notZeroInt = (NotZeroInt) value;
            return Objects.equals(number, notZeroInt.number);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
