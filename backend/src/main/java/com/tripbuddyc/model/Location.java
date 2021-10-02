package com.tripbuddyc.model;

import com.tripbuddyc.config.db.StringListConverter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "locations", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String continent;

    @Convert(converter = StringListConverter.class)
    @Column(name = "countries", length = 4096)
    @NotBlank
    private List<String> countries;


    public Location() {

    }

    public Location(String continent) {
        this.continent = continent;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countires) {
        this.countries = countires;
    }
}
