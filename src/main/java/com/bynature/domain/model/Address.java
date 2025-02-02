package com.bynature.domain.model;

public class Address {
    private String streetNumber;
    private String street;
    private String city;
    private String region;
    private String postalCode;
    private String country;

    public Address(String streetNumber, String street, String city, String region, String postalCode, String country) {

        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code cannot be null or blank");
        }
        // French postal codes must consist of exactly 5 digits.
        if (!postalCode.matches("\\d{5}")) {
            throw new IllegalArgumentException("Invalid French postal code: " + postalCode);
        }

        if (country == null || country.isBlank() || !country.equalsIgnoreCase("France")) {
            throw new IllegalArgumentException("Country cannot be null or blank and must be France");
        }

        this.streetNumber = streetNumber;
        this.street = street;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getStreet() {
        return street;
    }


    public String getCity() {
        return city;
    }


    public String getRegion() {
        return region;
    }


    public String getPostalCode() {
        return postalCode;
    }


    public String getCountry() {
        return country;
    }

}
