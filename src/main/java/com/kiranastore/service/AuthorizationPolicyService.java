package com.kiranastore.service;

import com.kiranastore.exception.ForbiddenException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AuthorizationPolicyService {

    public boolean isCustomer(Collection<String> authorities) {
        return authorities != null && authorities.contains("ROLE_CUSTOMER");
    }

    public void assertTransactionAccess(String transactionOwnerUserId, String requesterUserId,
                                        Collection<String> requesterAuthorities) {
        if (isCustomer(requesterAuthorities) && !transactionOwnerUserId.equals(requesterUserId)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
