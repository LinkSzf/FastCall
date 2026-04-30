package priv.szf.fastcall.core.declarative;

import cn.hutool.core.util.StrUtil;
import priv.szf.fastcall.common.exception.FastCallException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class FcUriTemplateValidator {

    private FcUriTemplateValidator() {
    }

    static void validate(
            Class<?> interfaceType,
            Method method,
            String uri,
            List<FcClientMethodMetadata.ParamBinding> bindings
    ) {
        Map<String, Integer> templatePathNames = parseUriTemplatePlaceholders(interfaceType, method, uri);
        Map<String, Integer> bindingPathNames = collectPathBindings(interfaceType, method, bindings);

        for (String placeholder : templatePathNames.keySet()) {
            if (!bindingPathNames.containsKey(placeholder)) {
                throw new FastCallException(
                        "Method[{}#{}] URI template [{}] requires @FcPath binding for placeholder [{}]",
                        interfaceType.getName(), method.getName(), uri, placeholder
                );
            }
        }

        for (String bindingName : bindingPathNames.keySet()) {
            if (!templatePathNames.containsKey(bindingName)) {
                throw new FastCallException(
                        "Method[{}#{}] parameter binding @FcPath(\"{}\") has no matching placeholder in URI template [{}]",
                        interfaceType.getName(), method.getName(), bindingName, uri
                );
            }
        }
    }

    private static Map<String, Integer> parseUriTemplatePlaceholders(
            Class<?> interfaceType,
            Method method,
            String uri
    ) {
        Map<String, Integer> placeholders = new HashMap<>();
        int openIndex = -1;
        for (int i = 0; i < uri.length(); i++) {
            char ch = uri.charAt(i);
            if (ch == '{') {
                if (openIndex >= 0) {
                    throw new FastCallException(
                            "Method[{}#{}] URI template [{}] contains nested or unbalanced placeholder braces",
                            interfaceType.getName(), method.getName(), uri
                    );
                }
                openIndex = i;
                continue;
            }
            if (ch == '}') {
                if (openIndex < 0) {
                    throw new FastCallException(
                            "Method[{}#{}] URI template [{}] contains unmatched closing brace",
                            interfaceType.getName(), method.getName(), uri
                    );
                }

                String placeholder = StrUtil.trim(uri.substring(openIndex + 1, i));
                if (StrUtil.isBlank(placeholder)) {
                    throw new FastCallException(
                            "Method[{}#{}] URI template [{}] contains empty placeholder braces",
                            interfaceType.getName(), method.getName(), uri
                    );
                }

                if (placeholder.indexOf('{') >= 0 || placeholder.indexOf('}') >= 0) {
                    throw new FastCallException(
                            "Method[{}#{}] URI template [{}] contains malformed placeholder [{}]",
                            interfaceType.getName(), method.getName(), uri, placeholder
                    );
                }

                placeholders.putIfAbsent(placeholder, 0);
                openIndex = -1;
            }
        }

        if (openIndex >= 0) {
            throw new FastCallException(
                    "Method[{}#{}] URI template [{}] contains unmatched opening brace",
                    interfaceType.getName(), method.getName(), uri
            );
        }

        return placeholders;
    }

    private static Map<String, Integer> collectPathBindings(
            Class<?> interfaceType,
            Method method,
            List<FcClientMethodMetadata.ParamBinding> bindings
    ) {
        Map<String, Integer> pathBindings = new HashMap<>();
        for (FcClientMethodMetadata.ParamBinding binding : bindings) {
            if (binding.getKind() != FcClientMethodMetadata.ParamKind.PATH) {
                continue;
            }

            String name = binding.getName();
            if (StrUtil.isBlank(name)) {
                throw new FastCallException(
                        "Method[{}#{}] contains blank @FcPath binding name",
                        interfaceType.getName(), method.getName()
                );
            }

            Integer previousIndex = pathBindings.putIfAbsent(name, binding.getIndex());
            if (Objects.nonNull(previousIndex)) {
                throw new FastCallException(
                        "Method[{}#{}] contains duplicate @FcPath binding for placeholder [{}]",
                        interfaceType.getName(), method.getName(), name
                );
            }
        }
        return pathBindings;
    }
}
