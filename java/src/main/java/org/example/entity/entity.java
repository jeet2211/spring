package org.example.entity;

public class entity {
    @Entity
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Existing fields...

        @Column(unique = true, nullable = false)
        private String username;

        @Column(nullable = false)
        private String password;

        // Constructors, getters, and setters...
    }
}
