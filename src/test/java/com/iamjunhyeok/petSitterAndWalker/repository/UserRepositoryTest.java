package com.iamjunhyeok.petSitterAndWalker.repository;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Optional<User> find = userRepository.findByEmail("jeonjhyeok@gmail.com");

        // Assert
        assertTrue(find.isPresent());
        assertEquals(user.getName(), find.get().getName());
        assertEquals(user.getEmail(), find.get().getEmail());
        assertEquals(user.getPassword(), find.get().getPassword());
        assertEquals(user.getPhoneNumber(), find.get().getPhoneNumber());
        assertEquals(user.getZipCode(), find.get().getZipCode());
        assertEquals(user.getAddress1(), find.get().getAddress1());
        assertEquals(user.getAddress2(), find.get().getAddress2());
    }
}