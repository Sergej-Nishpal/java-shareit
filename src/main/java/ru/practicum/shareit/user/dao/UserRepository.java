package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    Collection<User> findAllUsers();

    User getUserById(Long userId);

    User createUser(User user);

    User updateUser(Long userId, User user);

    void deleteUserById(Long userId);
}
