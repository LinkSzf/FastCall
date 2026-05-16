package priv.szf.fastcall.api.service;

public interface IFcSourceCodeResolver {

    String getSystemCodeBySystemId(Long systemId);

    String getSystemCodeByApiId(Long apiId);
}

