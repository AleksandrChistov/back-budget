package ru.aleksandrchistov.budget.access;

import ru.aleksandrchistov.budget.MatcherFactory;
import ru.aleksandrchistov.budget.common.util.JsonUtil;
import ru.aleksandrchistov.budget.pages.access.Role;
import ru.aleksandrchistov.budget.pages.access.User;

public class AccessTestData {
    public static final MatcherFactory.Matcher<User> USER_MATCHER = MatcherFactory.
            usingIgnoringFieldsComparator(User.class, "password");

    public static final int ADMIN_ID = 1;
    public static final int MANAGER_ID = 2;
    public static final int ANALYST_ID = 3;

    public static final User admin = new User(ADMIN_ID, "Иванов Иван Иванович",
            "admin@yandex.ru", "admin", Role.ADMIN);
    public static final User manager = new User(MANAGER_ID, "Петров Петр Петрович",
            "manager@yandex.ru", "manager", Role.MANAGER);
    public static final User analyst = new User(ANALYST_ID, "Маринова Марина Мариновна",
            "analyst@yandex.ru", "analyst", Role.ANALYST);

    public static User getNew() {
        return new User(null, "Самый Новый Пользователь",
                "user@yandex.ru", "user", Role.ANALYST);
    }

    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }
}
