// Room.java
package com.bookify.bookify_app.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document("rooms")
public class Room {
    @Id
    private String id;

    private String clinicId;

    @Indexed(unique = true)
    private String name;

    private int capacity = 1;

}
