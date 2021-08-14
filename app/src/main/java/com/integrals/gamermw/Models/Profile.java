package com.integrals.gamermw.Models;

public class Profile {
    private String email;
    private String profilePic;
    private String username;
    private String statenumber;

    public Profile(String email, String profilepic, String username, String statenumber) {
        this.email = email;
        this.profilePic = profilepic;
        this.username = username;
        this.statenumber = statenumber;
    }

    public Profile() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilepic() {
        return profilePic;
    }

    public void setProfilepic(String profilepic) {
        this.profilePic = profilepic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatenumber() {
        return statenumber;
    }

    public void setStatenumber(String statenumber) {
        this.statenumber = statenumber;
    }
}
