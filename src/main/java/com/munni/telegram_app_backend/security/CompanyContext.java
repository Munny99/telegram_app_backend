package com.munni.telegram_app_backend.security;//package com.monaum.Rapid_Global.security;
//
//import com.monaum.Rapid_Global.module.master.company.Company;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//public class CompanyContext {
//
//    public static Company getActiveCompany() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated()) return null;
//
//        if (auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
//            return userDetails.getActiveCompany();
//        }
//
//        return null;
//    }
//}
