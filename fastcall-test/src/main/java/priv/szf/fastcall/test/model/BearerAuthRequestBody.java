package priv.szf.fastcall.test.model;


import lombok.Data;

@Data
public class BearerAuthRequestBody {

    private String user;

    private String pwd;

}
