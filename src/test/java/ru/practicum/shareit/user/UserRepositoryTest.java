package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(testEntityManager);
    }

    @Test
    void testNoUsersIfRepoIsEmpty() {
        Collection<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    void testSaveUser() {
        final User user = User.builder()
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();

        final User savedUser = userRepository.save(user);

        assertThat(savedUser)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("name", "Sergej Nishpal")
                .hasFieldOrPropertyWithValue("email", "sergej.nishpal@yandex.ru");
    }

    @Test
    void testFindAllUsers() {
        final User user1 = User.builder()
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();
        testEntityManager.persist(user1);

        final User user2 = User.builder()
                .name("John Doe")
                .email("john.doe@mail.com")
                .build();
        testEntityManager.persist(user2);

        Collection<User> users = userRepository.findAll();
        assertThat(users).hasSize(2).contains(user1, user2);
    }

    @Test
    void testFindUserById() {
        final User user1 = User.builder()
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();
        testEntityManager.persist(user1);

        final User user2 = User.builder()
                .name("John Doe")
                .email("john.doe@mail.com")
                .build();
        testEntityManager.persist(user2);

        User foundUser = userRepository.findById(user2.getId()).orElseThrow();
        assertEquals(user2, foundUser);
    }

    @Test
    void testUpdateUserById() {
        final User user1 = User.builder()
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();
        testEntityManager.persist(user1);

        final User user2 = User.builder()
                .name("John Doe")
                .email("john.doe@mail.com")
                .build();
        testEntityManager.persist(user2);

        final User updatedUser = User.builder()
                .name("John D")
                .email("j.doe@mail.com")
                .build();

        User foundUser = userRepository.findById(user2.getId()).orElseThrow();
        foundUser.setName(updatedUser.getName());
        foundUser.setEmail(updatedUser.getEmail());
        userRepository.save(foundUser);

        final User checkedUser = userRepository.findById(user2.getId()).orElseThrow();

        assertThat(checkedUser.getId()).isEqualTo(user2.getId());
        assertThat(checkedUser.getName()).isEqualTo(updatedUser.getName());
        assertThat(checkedUser.getEmail()).isEqualTo(updatedUser.getEmail());
    }

    @Test
    void testDeleteUserById() {
        final User user1 = User.builder()
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();
        testEntityManager.persist(user1);

        final User user2 = User.builder()
                .name("John Doe")
                .email("john.doe@mail.com")
                .build();
        testEntityManager.persist(user2);

        final User user3 = User.builder()
                .name("User 3")
                .email("user3@mail.com")
                .build();
        testEntityManager.persist(user3);

        userRepository.deleteById(user2.getId());
        Collection<User> users = userRepository.findAll();
        assertThat(users).hasSize(2).contains(user1, user3);
    }

    @Test
    void testDeleteAllUsers() {
        final User user1 = User.builder()
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();
        testEntityManager.persist(user1);

        final User user2 = User.builder()
                .name("John Doe")
                .email("john.doe@mail.com")
                .build();
        testEntityManager.persist(user2);

        userRepository.deleteAll();
        assertThat(userRepository.findAll()).isEmpty();
    }
}