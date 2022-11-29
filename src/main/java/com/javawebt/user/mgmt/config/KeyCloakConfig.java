package com.javawebt.user.mgmt.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.NoArgsConstructor;

@Configuration
@NoArgsConstructor
public class KeyCloakConfig {

	@Value("${keycloak.auth-server-url}")
	public String serverURL;

	@Value("${keycloak.realm}")
	public String realm;

	@Value("${keycloak.resource}")
	public String clientID;

	//@Value("${keycloak.credentials.secret}")
	//public String clientSecret;

	final static String userName = "admin";
	final static String password = "admin";

	private static Keycloak keycloak = null;

	public Keycloak getInstance() {
		if (keycloak == null) {

			keycloak = KeycloakBuilder.builder()
					.serverUrl(serverURL)
					.realm(realm)
					.grantType(OAuth2Constants.PASSWORD)
					.username(userName)
					.password(password)
					.clientId(clientID)
					//.clientSecret(clientSecret)
					.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
		}
		return keycloak;
	}

	public KeycloakBuilder newKeycloakBuilderWithPasswordCredentials(String username, String password) {
		return KeycloakBuilder.builder().realm(realm).serverUrl(serverURL).clientId(clientID)
				//.clientSecret(clientSecret)
				.username(username).password(password);
	}

}
