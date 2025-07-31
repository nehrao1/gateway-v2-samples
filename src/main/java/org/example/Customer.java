// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package org.example;

import java.util.Objects;

public class Customer {
    public String id;
    public String partitionKey;
    public String name;
    public String city;

    public Customer() {}

    public Customer(String id, String partitionKey, String name, String city) {
        this.id = id;
        this.partitionKey = partitionKey;
        this.name = name;
        this.city = city;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", partitionKey='" + partitionKey + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (!Objects.equals(id, customer.id)) return false;
        if (!Objects.equals(partitionKey, customer.partitionKey)) return false;
        if (!Objects.equals(name, customer.name)) return false;
        return Objects.equals(city, customer.city);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (partitionKey != null ? partitionKey.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        return result;
    }
}
