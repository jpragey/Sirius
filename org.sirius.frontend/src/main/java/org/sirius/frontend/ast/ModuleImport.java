package org.sirius.frontend.ast;

import java.util.Map;
import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;

public class ModuleImport implements Verifiable {
	private boolean shared = false; 
	private Optional<AstToken> origin;
	
	private Optional<QName> qname;
	private Optional<String> qnameString;
	
	
	// The real value, even for if parsed as QName
	private String groupIdString;
	// May be null
//		private AstToken groupId; 
//		private AstToken artefactId; 
	private AstToken version;
	private String versionString;
//		private Map<String, AstToken> equivalents;
	
	public ModuleImport(/*Reporter reporter, */boolean shared /*, Map<String, AstToken> equivalents*/,
			Optional<AstToken> origin,
			Optional<QName> qname,
			Optional<String> qnameString,
			Token versionTk, // null if version is a String
			Token versionStringTk
			) {
		super();
//			this.reporter = reporter;
		this.shared = shared;
		this.origin = origin;
		
		this.qname = qname;
		this.qnameString = qnameString;
		
		assert(versionTk != null || versionStringTk != null);
		if(versionTk != null) {
			this.version = new AstToken(versionTk);
			this.versionString = version.getText();
		} else {
			assert(versionStringTk != null);
			String vs = versionStringTk.getText();
			this.versionString = vs.substring(1, vs.length()-1).trim();
		}
	}
	
	@Override
	public void verify(int featureFlags) {// TODO
	}
	
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	public Optional<AstToken> getOrigin() {
		return origin;
	}
	public Optional<String> getOriginString() {
		return origin.map(tk -> {String s = tk.getText(); return s.substring(1, s.length()-1).trim();});
	}
	public AstToken getVersion() {
		return version;
	}
	/** version, without double-quotes, and trimmed */
	public String getVersionString() {
		return versionString;
	}
	public void setVersion(Token version) {
		assert(version != null);
		this.version = new AstToken(version);
		this.versionString = version.getText();
	}
	public void setVersionString(Token version) {
		assert(version != null);
		this.version = new AstToken(version);
		String vs = version.getText();
		this.versionString = vs.substring(1, vs.length()-1).trim();
	}
	/** when version is a reference to an equivalent */
//		public void setVersionRef(Token version) {
//			String refName = version.getText();
//			AstToken realVersion = equivalents.get(refName);
//			if(realVersion == null) {
//				reporter.error("Undefined reference to import version.", new AstToken(version));
//				this.version = new AstToken(0,0,0,0,"","");
//			} else {
//				this.version = realVersion;
//			}
//		}
	public String getGroupIdString() {
		return groupIdString;
	}
	public void setGroupIdString(String groupIdString) {
		this.groupIdString = groupIdString;
	}
	public Optional<QName> getQname() {
		return qname;
	}
	public Optional<String> getQnameString() {
		return qnameString;
	}
	
}