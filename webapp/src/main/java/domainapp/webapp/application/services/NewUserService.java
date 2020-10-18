package domainapp.webapp.application.services;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.isis.applib.annotation.OrderPrecedence;
import org.apache.isis.extensions.secman.api.events.UserCreatedEvent;
import org.apache.isis.testing.fixtures.applib.fixturescripts.FixtureScripts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import domainapp.webapp.application.fixture.SeedDelegateUserFixtureScript;
import lombok.extern.log4j.Log4j2;

@Service
@Named("simpleApp.NewUserService")
@Order(OrderPrecedence.MIDPOINT)
@Qualifier("Default")
@Log4j2

public class NewUserService {
    private final FixtureScripts fixtureScripts;
    
//    @Inject  SecurityModuleConfig  securityModuleConfig;

    @Inject
    public NewUserService(final FixtureScripts fixtureScripts) {
        this.fixtureScripts = fixtureScripts;
    }

    @EventListener(UserCreatedEvent.class)
    public void onAppLifecycleEvent(UserCreatedEvent event) {
    	log.info("NewUserService received UserCreatedEvent: " + event);

		if (event.isDelegated()) {
            log.info("seeding delegate user");

            List<String> rolesList = new ArrayList<String>();
            // Workaround for securityModuleConfig not being available in BDD tests
            final String regularUserRoleName = "isis-module-security-regular-user";
//            final String regularUserRoleName = securityModuleConfig.getRegularUserRoleName();
            rolesList.add(regularUserRoleName);
			fixtureScripts.run(new SeedDelegateUserFixtureScript(event,
				rolesList)
				);
        }
    }

}
