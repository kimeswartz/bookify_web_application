package com.bookify.bookify_app.model;

import com.bookify.bookify_app.util.UserRole;
import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;

    private Set<UserRole> roles;

    @Indexed
    private String clinicId; // From TenantContext

    /**
     * Indicates whether the user account has been activated
     * via email verification. Defaults to false registration.
     */

    @Builder.Default
    private boolean active = false;
}

