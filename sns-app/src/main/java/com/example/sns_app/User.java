package com.example.sns_app;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="ニックネームを入力してください")
    private String username;

    @NotBlank(message="パスワードを入力してください")
    @Size(min=4,max=100,message="パスワードは4文字以上で入力してください")
    private String password;

    @Column(name="profile_image")
    private String profileImage;

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }

   public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password =password;
    }
    public String getProfileImage(){
        return profileImage;
    }
    public void setProfileImage(String profileImage){
        this.profileImage=profileImage;
    }


}
