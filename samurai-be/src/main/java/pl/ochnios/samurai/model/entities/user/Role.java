package pl.ochnios.samurai.model.entities.user;

public enum Role {
    Admin("ROLE_ADMIN"),
    Mod("ROLE_MOD"),
    User("ROLE_USER");

    public final String authority;

    Role(String authority) {
        this.authority = authority;
    }
}
