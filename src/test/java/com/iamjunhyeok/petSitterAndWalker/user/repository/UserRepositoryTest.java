package com.iamjunhyeok.petSitterAndWalker.user.repository;

import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일로 사용자 조회")
    void testFindByEmail() {
        // Arrange
        User user = User.builder()
                .name("전준혁")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("13169")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        testEntityManager.persistAndFlush(user);

        // Act
        User findUser = userRepository.findByEmail("jeonjhyeok@gmail.com").get();

        // Assert
        assertEquals(user.getName(), findUser.getName());
        assertEquals(user.getEmail(), findUser.getEmail());
        assertEquals(user.getPassword(), findUser.getPassword());
        assertEquals(user.getPhoneNumber(), findUser.getPhoneNumber());
        assertEquals(user.getZipCode(), findUser.getZipCode());
        assertEquals(user.getAddress1(), findUser.getAddress1());
        assertEquals(user.getAddress2(), findUser.getAddress2());
    }
}