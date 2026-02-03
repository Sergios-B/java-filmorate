package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final Map<Long, Set<Long>> friendships = new HashMap<>();

    public void addFriend(Long userId, Long friendId) {
        friendships.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friendships.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (friendships.containsKey(userId)) friendships.get(userId).remove(friendId);
        if (friendships.containsKey(friendId)) friendships.get(friendId).remove(userId);
    }

    public Set<Long> getCommonFriends(Long userId1, Long userId2) {
        Set<Long> friends1 = friendships.getOrDefault(userId1, Collections.emptySet());
        Set<Long> friends2 = friendships.getOrDefault(userId2, Collections.emptySet());
        Set<Long> common = new HashSet<>(friends1);
        common.retainAll(friends2);
        return common;
    }

    public Set<Long> getMyFriends(Long id) {
        return friendships.getOrDefault(id, Collections.emptySet());
    }
}