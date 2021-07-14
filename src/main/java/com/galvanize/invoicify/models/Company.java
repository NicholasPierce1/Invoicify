package com.galvanize.invoicify.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Transient
    private List<String> Companies;

    public List<String> getCompanies() {
        return Companies;
    }

    public void setCompanies(List<String> companies) {
        Companies = companies;
    }

    public Company() {
    }

    public Company(Long id) {
        this.id = id;
    }

    public Company(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
