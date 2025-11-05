package com.cop_3060.util;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to apply paging and sorting to in-memory collections.
 */
public class PagingUtil {

    /**
     * Apply paging and sorting to a list.
     *
     * @param items List of items to page
     * @param page  Zero-based page index
     * @param size  Number of items per page
     * @param sort  List of field names (prefix "-" for descending)
     * @param <T>   Type of the items
     * @return A map containing envelope: content, page, size, totalElements, totalPages
     */
    public static <T> Map<String, Object> pageAndSort(
            List<T> items, int page, int size, List<String> sort) {

        // Apply sorting
        if (sort != null && !sort.isEmpty()) {
            Comparator<T> comparator = null;

            for (String field : sort) {
                boolean descending = field.startsWith("-");
                String fieldName = descending ? field.substring(1) : field;

                Comparator<T> fieldComparator = Comparator.comparing(
                        t -> getFieldValue(t, fieldName),
                        Comparator.nullsLast(Comparator.naturalOrder())
                );

                if (descending) fieldComparator = fieldComparator.reversed();

                comparator = comparator == null ? fieldComparator : comparator.thenComparing(fieldComparator);
            }

            if (comparator != null) {
                items.sort(comparator);
            }
        }

        // Apply paging
        int totalElements = items.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<T> content = items.subList(fromIndex, toIndex);

        // Build envelope
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("content", content);
        envelope.put("page", page);
        envelope.put("size", size);
        envelope.put("totalElements", totalElements);
        envelope.put("totalPages", (int) Math.ceil((double) totalElements / size));

        return envelope;
    }

    /**
     * Use reflection to get a property value from a POJO.
     *
     * @param obj       Object to read
     * @param fieldName Field name
     * @return Value of the field, or null if not found
     */
    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            // Convert first letter to uppercase for getter
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
