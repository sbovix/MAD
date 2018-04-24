package com.example.silviabova.mylogin;

/**
 * Created by silvia bova on 14/04/2018.
 */

public class UserInfo {

    private String name;
    private String bio;
    private String age;
    private String URLimage;

    public UserInfo(String name,String bio, String age, String URLimage){
        this.name = name;
        this.age = age;
        this.bio = bio;
        this.URLimage = URLimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getURLimage() {
        return URLimage;
    }

    public void setURLimage(String URLimage) {
        this.URLimage = URLimage;
    }
}
