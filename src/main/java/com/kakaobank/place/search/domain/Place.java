package com.kakaobank.place.search.domain;

import java.util.Objects;

public class Place {
    private String title;
    private String address;

    public Place() {}

    public Place(String title) {
        this(title, "");
    }

    public Place(String title, String address) {
        this.title = title;
        this.address = address;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitleWithNoTag() {
        return this.title.replaceAll("<[^>]*>", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return this.getTitleWithNoTag().trim().equals(place.getTitleWithNoTag().trim())
                && this.address.equals(place.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, address);
    }

    @Override
    public String toString() {
        return "Place{" +
                "title='" + title + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
