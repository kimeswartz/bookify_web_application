// StaffMember.java
package com.bookify.bookify_app.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Set;
import java.util.List;

@Setter
@Getter
@Document("staff_members")
public class StaffMember {
    @Id
    private String id;

    private String clinicId;

    @Indexed(unique = true)
    private String email;

    private String name;
    private Set<String> roles;
    private List<String> skills;

}
