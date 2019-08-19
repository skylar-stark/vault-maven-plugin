package vault.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vault.ActiveVault;
import vault.backend.secret.generic.KV;
import vault.exception.VaultHealthException;
import vault.plugin.config.Secret;
import vault.plugin.config.VaultProperties;

@Mojo(name = "read-vault-properties", defaultPhase = LifecyclePhase.INITIALIZE)
public class ReadVaultPropertiesMojo extends AbstractMojo {
	private final Logger log = LoggerFactory.getLogger(ReadVaultPropertiesMojo.class);

	@Parameter(defaultValue = "${project}", readonly = true, required = true )
	private MavenProject project;

	@Parameter(defaultValue = "false")
	private boolean overwriteExisting;

	@Parameter(required = true)
	private List<VaultProperties> vaultProperties;

	@Parameter(defaultValue = "false")
	private boolean skip;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Map<String, String> combinedSecretData = new HashMap<>();
		if (!this.skip) {
			for (VaultProperties vaultProperty : this.vaultProperties) {
				ActiveVault vault = getActiveVault(vaultProperty);
				combineSecretData(vaultProperty, vault, combinedSecretData);
			}

			this.log.info("Found {} unique properties in Vault", Integer.valueOf(combinedSecretData.size()));
			// If overwriting of existing project properties is enabled, can "put" them; otherwise, "putIfAbsent"
			combinedSecretData.forEach(this.overwriteExisting ? this.project.getProperties()::put : this.project.getProperties()::putIfAbsent);
		}
	}

	private ActiveVault getActiveVault(VaultProperties vaultProperty) {
		try {
			return ActiveVault.newAndInitialize(vaultProperty.getVaultConfiguration());
		} catch (VaultHealthException e) {
			this.log.error("Error reading Vault Health; exiting plugin execution.", e);
		}

		return null;
	}

	private void combineSecretData(VaultProperties vaultProperty, ActiveVault vault, Map<String, String> combinedSecretData) {
		if (vault != null && !vaultProperty.isSkip()) {
			for (Secret secret : vaultProperty.getSecrets()) {
				if (!secret.isSkip()) {
					KV kv = vault.getKVBackend(secret.getBackend());
					for (String path : secret.getPaths()) {
						Map<String, String> secretData = kv.readSecret(path);
						this.log.info("Found {} properties in Vault at path {}/{}", Integer.valueOf(secretData.size()), secret.getBackend(), path);
	
						// Paths listed first have the highest priority - so only add properties to the combined list if they aren't already there
						secretData.forEach(combinedSecretData::putIfAbsent);
					}
				}
			}
		}
	}
}