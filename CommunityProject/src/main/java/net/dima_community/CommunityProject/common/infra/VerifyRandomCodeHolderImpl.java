package net.dima_community.CommunityProject.common.infra;

import java.util.Random;

import org.springframework.stereotype.Component;

import net.dima_community.CommunityProject.common.port.VerifyRandomCodeHolder;

@Component
public class VerifyRandomCodeHolderImpl implements VerifyRandomCodeHolder {

    String generatedString = new Random().ints(48, 123)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(20)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();

    @Override
    public String setRandomCode() {
        return generatedString;
    }
}
