package vault.plugin.config;

import java.util.List;

import vault.VaultConfiguration;

public class VaultProperties {

	private VaultConfiguration vaultConfiguration;
	private List<Secret> secrets;
	private boolean skip;

	public VaultProperties() {
		super();
	}

	public VaultConfiguration getVaultConfiguration() {
		return this.vaultConfiguration;
	}

	public void setVaultConfiguration(VaultConfiguration vaultConfiguration) {
		this.vaultConfiguration = vaultConfiguration;
	}

	public List<Secret> getSecrets() {
		return this.secrets;
	}

	public void setSecrets(List<Secret> secrets) {
		this.secrets = secrets;
	}

	public boolean isSkip() {
		return this.skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}
}