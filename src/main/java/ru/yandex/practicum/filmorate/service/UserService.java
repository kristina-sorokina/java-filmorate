package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendshipNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.friendship.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.users.UsersDao;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UsersDao usersDao;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserService(@Qualifier("usersDaoImpl") UsersDao usersDao, FriendshipDao friendshipDao) {
        this.usersDao = usersDao;
        this.friendshipDao = friendshipDao;
    }

    public User create(User user) {
        log.info("Создание пользователя: {}", user);
        return usersDao.create(user);
    }

    public User update(User user) {
        log.info("Обновление пользователя: {}", user);
        return usersDao.update(user);
    }

    public List<User> getAll() {
        log.info("Получение всех пользователей");
        return usersDao.getAll();
    }

    public User get(long id) {
        log.info("Получение пользователя {}", id);
        return usersDao.get(id);
    }

    public void makeFriends(long userId1, long userId2) {
        Friendship friendship = friendshipDao.get(userId2, userId1);
        if (friendship == null) {
            friendship = friendshipDao.get(userId1, userId2);
            if (friendship == null) {
                friendshipDao.create(userId1, userId2);
            }
        } else {
            if (!friendship.isAccepted()) {
                friendshipDao.update(userId2, userId1, true);
            }
        }
    }

    public void stopBeingFriends(long userId1, long userId2) {
        Friendship friendship = friendshipDao.get(userId2, userId1);
        if (friendship == null) {
            friendship = friendshipDao.get(userId1, userId2);
            if (friendship == null) {
                throw new FriendshipNotFoundException();
            }
            friendshipDao.remove(userId1, userId2);
            friendshipDao.create(userId2, userId1);
        } else {
            friendshipDao.update(userId2, userId1, false);
        }
    }

    public List<User> getFriends(long userId) {
        return friendshipDao.getByUserId(userId).stream()
                        .map(friendship -> getFriedFromFriendship(friendship, userId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId1, long userId2) {
        Set<User> friendsOfUser1 = new HashSet<>(getFriends(userId1));
        Set<User> friendsOfUser2 = new HashSet<>(getFriends(userId2));
        friendsOfUser1.retainAll(friendsOfUser2);
        return new ArrayList<>(friendsOfUser1);
    }

    private User getFriedFromFriendship(Friendship friendship, long userId) {
        if (friendship.getActiveUserId() == userId) {
            return usersDao.get(friendship.getPassiveUserId());
        } else if (friendship.isAccepted()) {
            return usersDao.get(friendship.getActiveUserId());
        } else {
            return null;
        }
    }
}