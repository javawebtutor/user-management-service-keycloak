package com.javawebt.user.mgmt.service;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;

import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.models.UserModel.RequiredAction;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.javawebt.user.mgmt.common.Constants;
import com.javawebt.user.mgmt.config.KeyCloakConfig;
import com.javawebt.user.mgmt.request.SignUpRequest;
import com.javawebt.user.mgmt.response.SignUpResponse;
import com.javawebt.user.mgmt.response.SuccessResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Getter
@Setter
@Slf4j
public class SignUpService {

	@Value("${keycloak.realm}")
	private String realm;

	@Autowired
	private KeyCloakConfig keyCloakConfig;

	public ResponseEntity<?> createAccount(SignUpRequest user) {

		if (!(user.getPassword().equals(user.getConfirmPassword()))) {

			return new ResponseEntity<>(
					new SuccessResponse(Constants.PASSWORD_MISMATCH, HttpStatus.BAD_REQUEST.value()),
					HttpStatus.BAD_REQUEST);
		}
		UsersResource usersResource = keyCloakConfig.getInstance().realm(keyCloakConfig.realm).users();
		List<UserRepresentation> verifyUser = usersResource.search(user.getEmailId());
		if (!verifyUser.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(Constants.EMAIL_EXITS, HttpStatus.CONFLICT.value()),
					HttpStatus.CONFLICT);
		}

		UserRepresentation kcUser = new UserRepresentation();
		log.info("creating a keycloak instance in the realm to add users to the realm");
		CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());
		kcUser.setUsername(user.getEmailId());
		kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
		kcUser.setFirstName(user.getFirstName());
		kcUser.setLastName(user.getLastName());
		kcUser.setEmail(user.getEmailId());
		kcUser.setEnabled(true);
		kcUser.setEmailVerified(false);
		kcUser.setRequiredActions(List.of(RequiredAction.VERIFY_EMAIL.toString()));
		log.info("Adding the user to keycloak");
		Response response = usersResource.create(kcUser);
//		if (response.getStatus() == 201) {
//			String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
//
//			usersResource.get(userId).sendVerifyEmail();
//			log.info("sent email verification link to {} for account activation", user.getEmailId());
//		}
		
		return new ResponseEntity<>(
				new SignUpResponse(Constants.REGISTRATION_SUCCESS, HttpStatus.OK.value(), kcUser.getEmail()),
				HttpStatus.OK);

	}

	private CredentialRepresentation createPasswordCredentials(String password) {
		CredentialRepresentation passwordCredentials = new CredentialRepresentation();
		passwordCredentials.setTemporary(false);
		passwordCredentials.setType(CredentialRepresentation.PASSWORD);
		passwordCredentials.setValue(password);
		return passwordCredentials;
	}

}
