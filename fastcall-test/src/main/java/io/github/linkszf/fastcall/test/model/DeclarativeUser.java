package io.github.linkszf.fastcall.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeclarativeUser {

    private Long id;

    private String name;

    private List<DeclarativeUser> children;
}

