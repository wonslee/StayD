package org.example.stayd.common;

import org.example.stayd.domain.user.dto.UserDTO;

/**
 * SessionManager는 현재 로그인된 사용자의 정보를 관리하는 싱글톤 클래스입니다.
 * 로그인된 사용자의 정보를 저장하고 가져오는 기능을 제공합니다.
 */
public class SessionManager {

    // 싱글톤 인스턴스
    private static SessionManager instance;

    // 로그인된 사용자 정보
    private UserDTO loggedInUser;

    // 생성자를 private으로 설정하여 외부에서 직접 인스턴스를 생성하지 못하게 함
    private SessionManager() {
    }

    /**
     * SessionManager 싱글톤 인스턴스를 반환합니다.
     * @return SessionManager 인스턴스
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * 로그인된 사용자 정보를 설정합니다.
     * @param user 로그인한 사용자 정보
     */
    public void setLoggedInUser(UserDTO user) {
        this.loggedInUser = user;
    }

    /**
     * 로그인된 사용자 정보를 반환합니다.
     * @return 로그인된 사용자(UserDTO)
     */
    public UserDTO getLoggedInUser() {
        return this.loggedInUser;
    }

    /**
     * 사용자가 로그인한 상태인지 확인합니다.
     * @return 로그인 상태일 경우 true, 아니면 false
     */
    public boolean isUserLoggedIn() {
        return loggedInUser != null;
    }
}
