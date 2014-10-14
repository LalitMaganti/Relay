package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;

import javax.inject.Inject;

import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.parser.rfc.QuitParser;
import co.fusionx.relay.parser.rfc.WelcomeParser;

public class SessionLevelObserver implements WelcomeParser.WelcomeObserver,
        QuitParser.QuitObserver {

    private final InternalStatusManager mInternalStatusManager;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final RegistrationFacade mRegistrationFacade;

    @Inject
    public SessionLevelObserver(final InternalStatusManager internalStatusManager,
            final RegistrationFacade registrationFacade,
            final InternalUserChannelGroup userChannelGroup) {
        mInternalStatusManager = internalStatusManager;
        mRegistrationFacade = registrationFacade;
        mUserChannelGroup = userChannelGroup;
    }

    @Override
    public void onWelcome(final String target, final int code, final String message) {
        if (code != ReplyCodes.RPL_WELCOME) {
            return;
        }

        // We are now registered - set the nick of our user
        mUserChannelGroup.getUser().setNick(target);

        // Send the post register messages
        mRegistrationFacade.postRegister();

        // And let us now be connected
        mInternalStatusManager.onConnected();
    }

    @Override
    public void onQuit(final String prefix, final Optional<String> optionalReason) {
    }
}