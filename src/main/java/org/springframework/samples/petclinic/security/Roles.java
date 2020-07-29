package org.springframework.samples.petclinic.security;

import org.springframework.stereotype.Component;

@Component
public class Roles
{
    @SuppressWarnings({ "checkstyle:MemberName", "checkstyle:VisibilityModifier" })
    public final String OWNER_ADMIN = "ROLE_OWNER_ADMIN";

    @SuppressWarnings({ "checkstyle:MemberName", "checkstyle:VisibilityModifier" })
    public final String VET_ADMIN = "ROLE_VET_ADMIN";

    @SuppressWarnings({ "checkstyle:MemberName", "checkstyle:VisibilityModifier" })
    public final String ADMIN = "ROLE_ADMIN";
}
