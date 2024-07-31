package net.dima_community.CommunityProject.common.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.common.port.BCryptEncoderHolder;

@Component
@RequiredArgsConstructor
public class BCryptEncoderHolderImpl implements BCryptEncoderHolder {

    public final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String encodedPassword(String pw) {
        return bCryptPasswordEncoder.encode(pw);

    }
}
