package domainapp.webapp.application.fixture;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.core.commons.internal.exceptions._Exceptions;
import org.apache.isis.extensions.secman.api.user.ApplicationUser;
import org.apache.isis.extensions.secman.jdo.dom.role.ApplicationRole;
import org.apache.isis.extensions.secman.jdo.dom.role.ApplicationRoleRepository;
import org.apache.isis.extensions.secman.jdo.dom.user.ApplicationUserRepository;
import org.apache.isis.extensions.secman.jdo.seed.scripts.GlobalTenancy;
import org.apache.isis.testing.fixtures.applib.fixturescripts.FixtureScript;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SeedDelegateUserFixtureScript extends FixtureScript {
	@Inject
	private ApplicationUserRepository applicationUserRepository;
	@Inject
	private ApplicationRoleRepository applicationRoleRepository;

	@NonNull
	org.apache.isis.extensions.secman.api.events.UserCreatedEvent event;
	@NonNull
	List<String> roles;

	@Override
	protected void execute(final ExecutionContext executionContext) {

		// create user if does not exist, and assign to the role
		ApplicationUser applicationUser = applicationUserRepository.findByUsername(event.getUserName()).orElse(null);
		if (applicationUser == null) {
			throw _Exceptions.unrecoverableFormatted("failed to find user '%s'", event.getUserName());
		}

		// update tenancy (repository checks for null)
		applicationUser.setAtPath(GlobalTenancy.TENANCY_PATH);

		for (final String roleName : roles) {
			final ApplicationRole securityRole = applicationRoleRepository.findByName(roleName).orElse(null);

			if (securityRole != null) {
				applicationRoleRepository.addRoleToUser(securityRole, applicationUser);
			} else {
				throw _Exceptions.unrecoverable("role not found by name: " + roleName);
			}

		}

	}
}
