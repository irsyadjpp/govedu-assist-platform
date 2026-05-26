package id.go.govedu.assist.validation;

import id.go.govedu.assist.constants.BankConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BankCodeValidator implements ConstraintValidator<ValidBankCode, String> {

    @Override
    public boolean isValid(String bankCode, ConstraintValidatorContext context) {
        if (bankCode == null) {
            return true;
        }
        return BankConstants.isValidBankCode(bankCode);
    }
}
