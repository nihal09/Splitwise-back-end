package com.nihal.split.models;

import lombok.Data;

import java.util.List;

@Data
public class UserPassbook {
        private String name;
        private Double totalContribution;
        private Double getsBack;
        private List<String> transaction;
}
