package com.example.silviabova.mylogin;

/**
 * Created by silvia bova on 16/05/2018.
 */

public class UserChat {
    private String name;
    private String URLimage;
    private String bio;

    public UserChat(String name, String URLimage,String bio){
        this.name = name;
        this.bio = bio;
        this.URLimage = URLimage;
    }
    public UserChat(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURLimage() {
        return URLimage;
    }

    public void setURLimage(String URLimage) {
        this.URLimage = URLimage;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
