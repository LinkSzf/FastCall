package priv.szf.fastcall.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FcMediaType {

    ALL("*/*"),

    APPLICATION_JSON("application/json"),

    APPLICATION_ATOM_XML("application/atom+xml"),

    APPLICATION_CBOR("application/cbor"),

    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),

    APPLICATION_GRAPHQL("application/graphql+json");


    private final String name;
}
