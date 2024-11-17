package pl.ochnios.samurai.model.entities.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.ochnios.samurai.model.dtos.PatchDto;
import pl.ochnios.samurai.model.dtos.user.UserDto;
import pl.ochnios.samurai.model.entities.PatchableEntity;
import pl.ochnios.samurai.model.entities.conversation.Conversation;
import pl.ochnios.samurai.model.mappers.UserMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User implements UserDetails, PatchableEntity {

    @Id
    private String username;

    @Nationalized
    private String firstname;

    @Nationalized
    private String lastname;

    @Column(unique = true)
    private String email;

    @ToString.Exclude
    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.User;

    @Builder.Default
    @ToString.Exclude
    @OrderBy("createdAt desc")
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Conversation> conversations = new ArrayList<>();

    @Builder.Default
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    private boolean isLocked = false;

    @Builder.Default
    private boolean isEnabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.authority));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    public boolean hasAdminRole() {
        return this.role.equals(Role.Admin);
    }

    public boolean hasModRole() {
        return this.role.equals(Role.Mod);
    }

    @Override
    public PatchDto getPatchDto() {
        var userMapper = Mappers.getMapper(UserMapper.class);
        return userMapper.map(this);
    }

    @Override
    public void apply(PatchDto patchDto) {
        var userPatchDto = (UserDto) patchDto;
        role = userPatchDto.getRole();
    }

    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public void setId(UUID id) {}
}
