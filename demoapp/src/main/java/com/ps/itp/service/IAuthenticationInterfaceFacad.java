package com.ps.itp.service;

import org.springframework.security.core.Authentication;

interface IAuthenticationFacade {
    Authentication getAuthentication();
}
