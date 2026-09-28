package com.playhavior.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A user account (table: player_profile).
 * RELATIONSHIP: one player has many PlayerCases (they point here with player_id).
 * TODAY: only the demo player seeded by ReferenceDataConfiguration exists.
 * NOTE: fields use snake_case names; newer entities use camelCase + @Column(name = ...).
 */
@Entity
@Table(name = "player_profile")
public class Player {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long player_id;
    private String display_name;
    private String email;
    // TODO: hash the password (BCryptPasswordEncoder) when sign-up is built;
    //       never store the raw password
    private String password;

    // Required by Hibernate: it creates an empty object, then fills it from the row
    public Player() {
    }

    public Long getPlayerID() {
        return player_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
