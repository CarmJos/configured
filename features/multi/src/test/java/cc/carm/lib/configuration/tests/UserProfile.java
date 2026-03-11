package cc.carm.lib.configuration.tests;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class UserProfile {

    protected final @NotNull UUID uuid;
    protected final @NotNull String name;
    protected final @NotNull String email;
    protected final @NotNull String phone;

    public UserProfile(@NotNull UUID uuid, @NotNull String name, @NotNull String email, @NotNull String phone) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public @NotNull String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserProfile)) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, phone);
    }
}
