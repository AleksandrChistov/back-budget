package ru.aleksandrchistov.budget.access;

import ru.aleksandrchistov.budget.MatcherFactory;
import ru.aleksandrchistov.budget.common.util.JsonUtil;
import ru.aleksandrchistov.budget.pages.access.Role;
import ru.aleksandrchistov.budget.pages.access.UserEntity;

public class AccessTestData {
    public static final MatcherFactory.Matcher<UserEntity> USER_MATCHER = MatcherFactory.
            usingIgnoringFieldsComparator(UserEntity.class, "password");

    public static final int ADMIN_ID = 1;
    public static final int MANAGER_ID = 2;
    public static final int ANALYST_ID = 3;

    public static final UserEntity admin = new UserEntity(ADMIN_ID, "Иванов Иван Иванович",
            "admin@yandex.ru", "admin", Role.ADMIN);
    public static final UserEntity manager = new UserEntity(MANAGER_ID, "Петров Петр Петрович",
            "manager@yandex.ru", "manager", Role.MANAGER);
    public static final UserEntity analyst = new UserEntity(ANALYST_ID, "Маринова Марина Мариновна",
            "analyst@yandex.ru", "analyst", Role.ANALYST);

    public static UserEntity getNew() {
        return new UserEntity(null, "Самый Новый Пользователь",
                "user@yandex.ru", "user", Role.ANALYST);
    }

    public static String jsonWithPassword(UserEntity user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }
}
