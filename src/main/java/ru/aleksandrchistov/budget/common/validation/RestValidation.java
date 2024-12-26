package ru.aleksandrchistov.budget.common.validation;

import lombok.experimental.UtilityClass;
import ru.aleksandrchistov.budget.common.HasId;
import ru.aleksandrchistov.budget.common.error.IllegalRequestDataException;

@UtilityClass
public class RestValidation {

    public static void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must be new (id=null)");
        }
    }

    public static void assureIdConsistent(HasId bean, int id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must has id=" + id);
        }
    }

}