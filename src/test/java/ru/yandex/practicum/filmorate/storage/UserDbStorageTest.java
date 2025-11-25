package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserMapper;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import({UserDbStorage.class, UserMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    private User testUser;
    private User savedUser;

    @BeforeEach
    public void setUp() {
        // Очищаем базу перед каждым тестом
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM users");

        testUser = User.builder()
                .name("User Name")
                .login("userlogin")
                .email("user@example.com")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        savedUser = userDbStorage.addUser(testUser);
    }

    @Test
    public void createUser() {
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser).hasFieldOrPropertyWithValue("name", testUser.getName());
        assertThat(savedUser).hasFieldOrPropertyWithValue("login", testUser.getLogin());
        assertThat(savedUser).hasFieldOrPropertyWithValue("email", testUser.getEmail());
        assertThat(savedUser).hasFieldOrPropertyWithValue("birthday", testUser.getBirthday());
    }

    @Test
    public void getUserById() {
        User userFromDB = userDbStorage.getUserById(savedUser.getId());

        assertThat(userFromDB).hasFieldOrPropertyWithValue("id", savedUser.getId());
        assertThat(userFromDB).hasFieldOrPropertyWithValue("name", savedUser.getName());
        assertThat(userFromDB).hasFieldOrPropertyWithValue("login", savedUser.getLogin());
        assertThat(userFromDB).hasFieldOrPropertyWithValue("email", savedUser.getEmail());
        assertThat(userFromDB).hasFieldOrPropertyWithValue("birthday", savedUser.getBirthday());
    }

    @Test
    public void getAllUsers() {
        Collection<User> users = userDbStorage.getAllUsers();

        assertEquals(1, users.size());
    }

    @Test
    public void updateUser() {
        User userToUpdate = User.builder()
                .id(savedUser.getId())
                .name("Updated Name")
                .login("updatedlogin")
                .email("updated@example.com")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        User updatedUser = userDbStorage.updateUser(userToUpdate);

        assertThat(updatedUser).hasFieldOrPropertyWithValue("id", savedUser.getId());
        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "Updated Name");
        assertThat(updatedUser).hasFieldOrPropertyWithValue("login", "updatedlogin");
        assertThat(updatedUser).hasFieldOrPropertyWithValue("email", "updated@example.com");
        assertThat(updatedUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
    }

    @Test
    public void getUserById_NotFound() {
        User userFromDB = userDbStorage.getUserById(999);

        assertThat(userFromDB).isNull();
    }
}