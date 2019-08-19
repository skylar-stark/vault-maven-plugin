package vault.plugin.config;

import static vault.util.Util.copyList;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Secret implements Serializable {
	private static final long serialVersionUID = 2306181202103324270L;

	private String backend;
	private List<String> paths;
	private boolean skip = false;

	public Secret() {
		super();
	}

	public String getBackend() {
		return this.backend;
	}

	public void setBackend(String backend) {
		this.backend = backend;
	}

	public List<String> getPaths() {
		return this.paths;
	}

	public void setPaths(List<String> paths) {
		this.paths = copyList(paths);
	}

	public boolean isSkip() {
		return this.skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.backend, this.paths, Boolean.valueOf(this.skip));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Secret other = (Secret) obj;
		return Objects.equals(this.backend, other.backend) && Objects.equals(this.paths, other.paths) && this.skip == other.skip;
	}

	@Override
	public String toString() {
		return "Secret [backend=" + this.backend + ", paths=" + this.paths + ", skip=" + this.skip + "]";
	}
}