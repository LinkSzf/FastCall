package priv.szf.fastcall.test.model;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JsonObject2 {

    @JsonAlias("name")
    private String username;

    @JsonAlias("age")
    private int ageNum;

    @JsonAlias("now")
    private LocalDateTime nowtime;
}
