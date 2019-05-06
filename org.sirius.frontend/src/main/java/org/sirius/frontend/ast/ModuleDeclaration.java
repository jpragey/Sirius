package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.common.error.Reporter;

public class ModuleDeclaration implements Visitable {

	private QName qName = new QName();
	private AstToken version = new AstToken(0,0,0,0,"","");
	
	private Reporter reporter; 
	
	private Map<String, AstToken> equivalents = new HashMap<>();
	
	public class ModuleImport {
		private boolean shared = false; 
		private Optional<AstToken> origin = Optional.empty();
		// The real value, even for if parsed as QName
		private String groupIdString;
		// May be null
		private AstToken groupId; 
//		private AstToken artefactId; 
		private AstToken version;
		public ModuleImport(boolean shared) {
			super();
			this.shared = shared;
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
		public void setOrigin(Token origin) {
			this.origin = Optional.of(new AstToken(origin));
		}
//		public AstToken getGroupId() {
//			return groupId;
//		}
		public void setGroupId(Token groupId) {
			this.groupId = new AstToken(groupId);
			this.setGroupIdString(groupId.getText());
		}
		public void setGroupId(QName groupId) {
			this.groupId = groupId.getElements().get(0);
			assert(this.groupId != null);
			this.setGroupIdString(groupId.dotSeparated());
		}
//		public AstToken getArtefactId() {
//			return artefactId;
//		}
//		public void setArtefactId(AstToken artefactId) {
//			this.artefactId = artefactId;
//		}
//		public void setArtefactId(Token artefactId) {
//			this.artefactId = new AstToken(artefactId);
//		}
		public AstToken getVersion() {
			return version;
		}
		public void setVersion(Token version) {
			this.version = new AstToken(version);
		}
		/** when version is a reference to an equivalent */
		public void setVersionRef(Token version) {
			String refName = version.getText();
			AstToken realVersion = equivalents.get(refName);
			if(realVersion == null) {
				reporter.error("Undefined reference to import version.", new AstToken(version));
				this.version = new AstToken(0,0,0,0,"","");
			} else {
				this.version = realVersion;
			}
		}
		public String getGroupIdString() {
			return groupIdString;
		}
		public void setGroupIdString(String groupIdString) {
			this.groupIdString = groupIdString;
		}
		
		
	}
	
	private List<ModuleImport> moduleImports = new ArrayList<>(); 
	
	public void addQNameElement(Token element) {
		this.qName.add(element);
	}
	
	public void setVersion(Token version) {
		this.version = new AstToken(version); 
	}
	
	public void addValueEquivalent(Token name, Token value) {
		
		equivalents.put(name.getText(), new AstToken(value));
	}
	
	public ModuleImport addImport(boolean shared) {
		ModuleImport moduleImport = new ModuleImport(shared);
		this.moduleImports.add(moduleImport);
		return moduleImport;
		
	}

	public AstToken getVersion() {
		return version;
	}

	public Map<String, AstToken> getEquivalents() {
		return equivalents;
	}

	public void setEquivalents(Map<String, AstToken> equivalents) {
		this.equivalents = equivalents;
	}

	public QName getqName() {
		return qName;
	}

	public List<ModuleImport> getModuleImports() {
		return moduleImports;
	}

	@Override
	public void visit(AstVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
	
}
