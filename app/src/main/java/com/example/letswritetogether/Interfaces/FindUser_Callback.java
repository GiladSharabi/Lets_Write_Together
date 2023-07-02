package com.example.letswritetogether.Interfaces;

import com.example.letswritetogether.Models.User;

public interface FindUser_Callback {
    void user_found(User user);
    void new_user_created(User user);
    void error();
}
