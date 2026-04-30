package priv.szf.fastcall.api.service.port;

public interface IFcSourceCodeResolver {

    String getSystemCodeBySystemId(Long systemId);

    String getSystemCodeByApiId(Long apiId);
}

