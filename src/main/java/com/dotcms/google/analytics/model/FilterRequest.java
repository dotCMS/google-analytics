package com.dotcms.google.analytics.model;

public class FilterRequest {

    private final String field;
    private final String operator;
    private final String value;

    public FilterRequest(final String field, final String operator, final String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }
}
