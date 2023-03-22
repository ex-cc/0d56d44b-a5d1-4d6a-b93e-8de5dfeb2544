package com.kakaobank.place.search.infrastructure.repository;

public class HttpDefaultSearchFailedException extends  RuntimeException{
    private final String supplier;
    private final String keyword;

    public HttpDefaultSearchFailedException(String supplier, String keyword) {
        super(String.format("fail to search on %s. keyword=%s", supplier, keyword));
        this.supplier = supplier;
        this.keyword = keyword;
    }

    public String getSupplier() {
        return this.supplier;
    }

    public String getKeyword() {
        return this.keyword;
    }
}
