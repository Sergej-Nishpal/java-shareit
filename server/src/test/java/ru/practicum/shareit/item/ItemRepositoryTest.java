package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    User user1;
    User user2;
    User user3;

    Item item1;
    Item item2;
    Item item3;
    Item item4;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(testEntityManager);
    }

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();
        userRepository.save(user1);

        user2 = User.builder()
                .name("John Doe")
                .email("john.doe@mail.com")
                .build();
        userRepository.save(user2);

        user3 = User.builder()
                .name("User 3")
                .email("user3@mail.com")
                .build();
        userRepository.save(user3);

        item1 = Item.builder()
                .owner(user2)
                .name("Test item 1 SpEcIaL name")
                .description("Test item 1 description")
                .available(true).build();
        testEntityManager.persist(item1);

        item2 = Item.builder()
                .owner(user1)
                .name("Test item 2 name")
                .description("Test item 2 description")
                .available(false).build();
        testEntityManager.persist(item2);

        item3 = Item.builder()
                .owner(user3)
                .name("Test item 3 name")
                .description("Test item 3 special description")
                .available(true).build();
        testEntityManager.persist(item3);

        item4 = Item.builder()
                .owner(user1)
                .name("Test item 4 name")
                .description("Test item 4 description")
                .available(false).build();
        testEntityManager.persist(item4);
    }

    @Test
    void testFindAllByOwnerIdOrderByIdAsc() {
        Collection<Item> itemsByOwnerId = itemRepository.findAllByOwnerIdOrderByIdAsc(user1.getId());
        Long firstItemIdInCollection = itemsByOwnerId.stream().findFirst().orElseThrow().getId();
        assertThat(itemsByOwnerId).hasSize(2).contains(item2, item4);
        assertEquals(item2.getId(), firstItemIdInCollection);
    }

    @Test
    void testSearchByText() {
        final String textToSearch = "PEciA";
        Collection<Item> itemsSpecialAndAvailable = itemRepository.search(textToSearch);
        assertThat(itemsSpecialAndAvailable).hasSize(2).contains(item1, item3);
    }
}