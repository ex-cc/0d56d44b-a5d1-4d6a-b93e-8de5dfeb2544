package com.kakaobank.place.search.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Keyword {

    @Id
    private String name;
    private Integer count;

    @Version
    private Integer version;

    public Keyword() {
        this("", 1);
    }

    public Keyword(String name) {
        this(name, 1);
    }

    public Keyword(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Keyword incrementCount() {
        return new Keyword(this.getName(), this.getCount() + 1);
    }

    @Override
    public String toString() {
        return "Keyword{" +
                "name='" + name + '\'' +
                ", count=" + count +
                '}';
    }

    public static Keyword of(final String name) {
        return new Keyword(name);
    }
}
