package com.tl.spring_boot1.service;

import com.tl.spring_boot1.model.RefreshTokenStatus;
import com.tl.spring_boot1.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    /*
     REQUIRES_NEW only takes effect when the method is invoked through Spring's proxy,
     i.e. from ANOTHER bean. If this method lived inside AuthService and were called via
     this.revokeAllForUser(...),the new transaction would never be created, and the revoke would silently roll back with
     the outer throw. Keeping it in a separate bean that AuthService autowires guarantees
     the cross-bean, proxied call — so REQUIRES_NEW actually happens.
     This method Runs in a brand-new, independent transaction (REQUIRES_NEW) so the revoke COMMITS
     even though the calling refresh() method throws immediately afterwards to return a
     401. Without REQUIRES_NEW, the caller's rollback would erase this revoke.
     */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeAllForUser(Integer userId) {
        refreshTokenRepository.revokeAllByUserId(userId, RefreshTokenStatus.REVOKED);
    }
}
