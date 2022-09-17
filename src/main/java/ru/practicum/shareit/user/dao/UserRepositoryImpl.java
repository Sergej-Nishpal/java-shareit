package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long userRepoIdCounter;

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (user.getId() == null) {
            user.setId(userId);
        }

        User updatedUser = getUserById(userId);

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }

        users.put(userId, updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUserById(Long userId) {
        users.remove(userId);
    }

    private long getNextId() {
        return ++userRepoIdCounter;
    }
}