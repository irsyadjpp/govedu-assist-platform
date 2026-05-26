package id.go.govedu.assist.constants;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BankConstants {

    private static final Set<String> VALID_BANK_CODES = Set.of(
            "MANDIRI",
            "BRI",
            "BNI",
            "BTN"
    );

    public static boolean isValidBankCode(String bankCode) {
        return bankCode != null && VALID_BANK_CODES.contains(bankCode.toUpperCase());
    }

    public static List<String> getValidBankCodes() {
        return VALID_BANK_CODES.stream().sorted().collect(Collectors.toList());
    }
}
