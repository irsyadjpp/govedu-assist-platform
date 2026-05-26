package id.go.govedu.assist.validation;

import id.go.govedu.assist.constants.BankConstants;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BankCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBankCode {

    String message() default "Invalid bank_code. Supported codes: MANDIRI, BRI, BNI, BTN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
