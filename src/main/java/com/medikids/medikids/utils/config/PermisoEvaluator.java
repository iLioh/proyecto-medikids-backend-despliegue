package com.medikids.medikids.utils.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("permiso")
public class PermisoEvaluator {

    public boolean has(String codigo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        Object details = auth.getDetails();
        if (!(details instanceof Map)) return false;

        Object rawPermisos = ((Map<?, ?>) details).get("permisos");
        if (!(rawPermisos instanceof List)) return false;

        List<?> permisos = (List<?>) rawPermisos;
        return permisos.contains(codigo);
    }
}
