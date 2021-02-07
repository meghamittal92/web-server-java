package com.client.calorieserver.repository;

import com.client.calorieserver.domain.dto.db.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void TestTableMade(){
        assert (userRepository != null);
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("user1");
        userDTO.setPassword("password");
        userDTO.setId(1L);
        userRepository.save(userDTO);
        assert (userRepository.findByUsername("user1").isPresent());
    }
}
