package net.dima_community.CommunityProject.mock;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.common.port.VerifyRandomCodeHolder;

@RequiredArgsConstructor
public class FakeVerifyRandomCodeHolder implements VerifyRandomCodeHolder {

    public final String code;

    @Override
    public String setRandomCode() {
        return code;
    }

}
