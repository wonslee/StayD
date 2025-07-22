package org.example.stayd.common;

import org.example.stayd.domain.user.dto.UserDTO;

public class SessionManager {

    private static SessionManager instance;

    private UserDTO loggedInUser;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setLoggedInUser(UserDTO user) {
        this.loggedInUser = user;
    }

    public UserDTO getLoggedInUser() {
        return this.loggedInUser;
    }

    public boolean isUserLoggedIn() {
        return loggedInUser != null;
    }
}
