package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    Collection<User> findAll();

    User getById(Long userId);

    User create(User user);

    User update(Long userId, User user);

    void deleteById(Long userId);
}