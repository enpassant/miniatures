package builder;

import java.util.Optional;
import java.util.function.Consumer;

public class Contact {
    public final Optional<String> email;
    public final Optional<String> phone;

    private Contact(Optional<String> email, Optional<String> phone) {
        this.email = email;
        this.phone = phone;
    }

    public static Optional<Contact> create(Consumer<ContactBuilder> build) {
        ContactBuilder builder = new ContactBuilder();

        build.accept(builder);

        if (builder.email.isPresent() || builder.phone.isPresent()) {
            return Optional.of(new Contact(builder.email, builder.phone));
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return (!email.isPresent() ? "" : "Email: " + email.get() + " ")
            + (!phone.isPresent() ? "" : "Phone: " + phone.get() + " ");
    }

    private static Consumer<ContactBuilder> companyPhone() {
        return builder -> builder.phone("55/555-555");
    }

    public static void main(String[] args) {
        Optional<Contact> contact1 = Contact.create(builder ->
            builder.email("test@company.com"));
        System.out.println("Contact1: " + contact1);

        Optional<Contact> contact2 = Contact.create(builder -> {});
        System.out.println("Contact2: " + contact2);

        Optional<Contact> contact3 = Contact.create(
            companyPhone().andThen(builder -> builder.email("test@company.com")));
        System.out.println("Contact3: " + contact3);
    }

    public static class ContactBuilder {
        private Optional<String> email = Optional.empty();
        private Optional<String> phone = Optional.empty();

        private ContactBuilder() {};

        public ContactBuilder email(String email) {
            this.email = Optional.of(email);
            return this;
        }
        public ContactBuilder phone(String phone) {
            this.phone = Optional.of(phone);
            return this;
        }
    }
}
