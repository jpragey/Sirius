package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.common.error.Reporter;

public class ModuleDeclaration implements Visitable {

	private QualifiedName qName = new QualifiedName();
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
		public void setGroupId(Token groupId) {
			this.groupId = new AstToken(groupId);
			this.setGroupIdString(groupId.getText());
		}
		public void setGroupId(QualifiedName groupId) {
			this.groupId = groupId.getTokenElements().get(0);
			assert(this.groupId != null);
			this.setGroupIdString(groupId.toQName().dotSeparated());
		}
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
	
	private List<PackageDeclaration> packageDeclarations = new ArrayList<>();
	private Optional<PackageDeclaration> currentPackage = Optional.empty();
	
	public ModuleDeclaration(Reporter reporter) {
		super();
		this.reporter = reporter;
//		this.addPackageDeclaration(new PackageDeclaration(reporter));
	}

	
	public List<PackageDeclaration> getPackageDeclarations() {
		return packageDeclarations;
	}

	public void setQName(QualifiedName qualifiedName) {
		this.qName = qualifiedName;
	}
//	public void addQNameElement(Token element) {
//		this.qName.add(element);
//	}
	
	/** Check there's a current package (otherwise create it) and return it.
	 * 
	 * @return
	 */
	private PackageDeclaration assertCurrentPackage() {
		if(currentPackage.isEmpty()) {
			addPackageDeclaration(new PackageDeclaration(reporter));
		}
		return currentPackage.get();
	}
	
	/** Add a new package, update current package reference
	 * 
	 * @param packageDeclaration
	 */
	public void addPackageDeclaration(PackageDeclaration packageDeclaration) {
		this.currentPackage = Optional.of(packageDeclaration);
		this.packageDeclarations.add(packageDeclaration);
	}
	
	public void addFunctionDeclaration(FunctionDeclaration d) {
		this.assertCurrentPackage().addFunctionDeclaration(d);
	}
	public void addClassDeclaration(ClassDeclaration d) {
		this.assertCurrentPackage().addClassDeclaration(d);
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

	public QualifiedName getqName() {
		return qName;
	}

	public List<ModuleImport> getModuleImports() {
		return moduleImports;
	}

	@Override
	public void visit(AstVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		return qName.toString();
	}
}
