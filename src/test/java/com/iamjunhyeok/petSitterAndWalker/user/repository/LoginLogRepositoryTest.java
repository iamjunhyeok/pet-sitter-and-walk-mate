package com.iamjunhyeok.petSitterAndWalker.user.repository;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.LoginStatus;
import com.iamjunhyeok.petSitterAndWalker.user.domain.LoginLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class LoginLogRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private LoginLogRepository loginLogRepository;

    @Test
    @DisplayName("이메일과 상태로 로그인 시도 조회")
    void testFindByEmailAndStatus() {
        // Arrange
        LoginLog loginLog1 = new LoginLog("jeonjhyeok@gmail.com", "127.0.0.1", "Chrome");
        LoginLog loginLog2 = new LoginLog("jeonjhyeok@gmail.com", "127.0.0.1", "Chrome");
        loginLog2.changeStatus(LoginStatus.FAILED, "비밀번호 오류");
        testEntityManager.persist(loginLog1);
        testEntityManager.persist(loginLog2);

        // Act
        LoginLog result = loginLogRepository.findByEmailAndStatus("jeonjhyeok@gmail.com", LoginStatus.PENDING).get();

        // Assert
        assertEquals(loginLog1.getEmail(), result.getEmail());
        assertEquals(loginLog1.getLoginDate(), result.getLoginDate());
        assertEquals(loginLog1.getIpAddress(), result.getIpAddress());
        assertEquals(loginLog1.getUserAgent(), result.getUserAgent());
        assertEquals(loginLog1.getStatus(), result.getStatus());
    }
}