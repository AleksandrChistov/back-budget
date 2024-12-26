package ru.aleksandrchistov.budget.access;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.aleksandrchistov.budget.AbstractControllerTest;
import ru.aleksandrchistov.budget.common.util.JsonUtil;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.aleksandrchistov.budget.access.AccessController.REST_URL;
import static ru.aleksandrchistov.budget.access.AccessTestData.*;

class AccessControllerTest extends AbstractControllerTest {
    @Autowired
    private AccessRepository accessRepository;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(List.of(admin, manager, analyst)));
    }

    @Test
    void createAccess() throws Exception {
        User newUser = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(AccessTestData.jsonWithPassword(newUser, newUser.getPassword())))
                .andDo(print())
                .andExpect(status().isCreated());

        User created = USER_MATCHER.readFromJson(action);
        int newId = created.id();
        newUser.setId(newId);

        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(accessRepository.getExisted(newId), newUser);
    }

    @Test
    void createAccessInvalid() throws Exception {
        User newUser = new User(null, null, "email@mail.ru", "pass", Role.ANALYST);
        perform(MockMvcRequestBuilders.post(REST_URL)
        .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newUser)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteAccess() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/" + manager.id()))
                .andExpect(status().isNoContent());
        USER_MATCHER.assertMatch(accessRepository.findAll(), admin, analyst);
    }

    @Test
    void deleteAccessInvalid() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/100"))
                .andExpect(status().isNotFound());
    }
}