package ru.timofeev.recservice.component.dynamicRule.handler;

import ru.timofeev.recservice.model.enums.ProductTypeEnum;
import ru.timofeev.recservice.model.enums.TransactionTypeEnum;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RuleParseUtil {

    private RuleParseUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final List<String> COMPARISON_OPERATIONS = List.of(">", ">=", "=", "<=", "<");

    static void checkUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not to be null");
        }
    }

    static void checkArgsSize(List<String> args, int size, String transaction) {
        if (args == null || args.size() != size) {
            throw new IllegalArgumentException("%s must contain exactly %d arguments".formatted(transaction, size));
        }
    }

    static ProductTypeEnum parseProductType(String value) {
        try {
            return ProductTypeEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            String allowedValues = Arrays.stream(ProductTypeEnum.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Invalid product type: " + value + ". Allowed values: " + allowedValues
            );
        }
    }

    static TransactionTypeEnum parseTransactionType(String value) {
        try {
            return TransactionTypeEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            String allowedValues = Arrays.stream(TransactionTypeEnum.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Invalid transaction type: " + value + ". Allowed values: " + allowedValues
            );
        }
    }

    static String parseComparisonOperation(String value) {
        if (!COMPARISON_OPERATIONS.contains(value)) {
            throw new IllegalArgumentException("Invalid comparison operation: " + value);
        }
        return value;
    }

    static int parseSum(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Sum must not be null or blank");
        }

        final int sum;
        try {
            sum = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid sum: %s. Must be a valid integer".formatted(value), e);
        }

        if (sum <= 0) {
            throw new IllegalArgumentException("Invalid sum: %s. Must be greater than 0".formatted(value));
        }

        return sum;
    }

    static boolean compare(int left, String operation, int right) {
        return switch (operation) {
            case ">" -> left > right;
            case ">=" -> left >= right;
            case "=" -> left == right;
            case "<=" -> left <= right;
            case "<" -> left < right;
            default -> throw new IllegalArgumentException("Invalid comparison operation: " + operation);
        };
    }
}
