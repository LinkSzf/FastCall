package priv.szf.fastcall.test.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class JsonObject {

    private String name;

    private int age;

    private LocalDateTime now;
}
