package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.core.PhysicalPath;

public class AstModuleDeclaration implements Visitable {

	private QName qName = new QName();
	private AstToken version = new AstToken(0,0,0,0,"","");
	
	private Reporter reporter; 
	
	private Optional<PhysicalPath> modulePPath = Optional.empty(); 

	private Map<String, AstToken> equivalents = new HashMap<>();
	
	public static class ModuleImport {
		private Reporter reporter; 
		private boolean shared = false; 
		private Optional<AstToken> origin = Optional.empty();
		// The real value, even for if parsed as QName
		private String groupIdString;
		// May be null
		private AstToken groupId; 
//		private AstToken artefactId; 
		private AstToken version;
		private Map<String, AstToken> equivalents;
		
		public ModuleImport(Reporter reporter, boolean shared, Map<String, AstToken> equivalents) {
			super();
			this.reporter = reporter;
			this.shared = shared;
			this.equivalents = equivalents;
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
	
	private List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
	
	private AstPackageDeclaration currentPackage;
	
	public AstModuleDeclaration(Reporter reporter, QName qualifiedName, AstToken version) {
		super();
		this.reporter = reporter;
		this.currentPackage = new AstPackageDeclaration(reporter, qualifiedName);
		this.addPackageDeclaration(this.currentPackage);

		this.qName = qualifiedName;
		PhysicalPath pp = new PhysicalPath(qName/*.toQName()*/.getStringElements());
		this.modulePPath = Optional.of(pp);

		this.version = version; 
	}

	public AstModuleDeclaration(Reporter reporter, QName qualifiedName, Token version) {
		this(reporter, qualifiedName, new AstToken(version));
	}
	
	public static AstModuleDeclaration createUnnamed(Reporter reporter) {
		AstModuleDeclaration mod = new AstModuleDeclaration(reporter, new QName(), new AstToken(0,0,0,0,"",""));
		return mod;
	}

	
	
	public List<AstPackageDeclaration> getPackageDeclarations() {
		return packageDeclarations;
	}

	public Optional<PhysicalPath> getModulePPath() {
		return modulePPath;
	}

	/** Check there's a current package (otherwise create it) and return it.
	 * 
	 * @return
	 */
	public AstPackageDeclaration getCurrentPackage() {
		return currentPackage;
	}
	
	/** Add a new package, update current package reference
	 * 
	 * @param packageDeclaration
	 */
	public void addPackageDeclaration(AstPackageDeclaration packageDeclaration) {
		this.currentPackage = packageDeclaration;
		this.packageDeclarations.add(packageDeclaration);
	}
	
	public AstPackageDeclaration createPackageDeclaration(QName packageQName) {
		AstPackageDeclaration packageDeclaration = new AstPackageDeclaration(reporter, packageQName);
		addPackageDeclaration(packageDeclaration);
		return packageDeclaration;
	}
	
	
	
	
	public void addFunctionDeclaration(AstFunctionDeclaration/*.Builder*/ d) {
		this.getCurrentPackage().addFunctionDeclaration(d);
	}
	public void addClassDeclaration(AstClassDeclaration d) {
		this.getCurrentPackage().addClassDeclaration(d);
	}
	public void addInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
		this.getCurrentPackage().addInterfaceDeclaration(interfaceDeclaration);
	}

	public void addValueEquivalent(Token name, Token value) {
		
		equivalents.put(name.getText(), new AstToken(value));
	}
	
	public ModuleImport addImport(boolean shared) {
		ModuleImport moduleImport = new ModuleImport(reporter, shared, equivalents);
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
		visitor.startModuleDeclaration(this);
		for(AstPackageDeclaration pd : packageDeclarations)
			pd.visit(visitor);
//		packageDeclarations.stream().forEach(pd -> pd.visit(visitor));
		visitor.endModuleDeclaration(this);		
	}
	
	@Override
	public String toString() {
		return "[Mod:" + qName.toString() + "-" + version.getText() + "]";
	}
	
	public void updatePackagesContainer() {
		packageDeclarations.stream()
			.forEach(AstPackageDeclaration::updateContentContainerRefs);
	}
	
	private ModuleDeclaration moduleDeclaration = null;
 
	private class ModuleDeclarationImpl implements ModuleDeclaration {
		private QName moduleQName = qName/*.toQName()*/;

		private List<PackageDeclaration> packageDeclarationList;
		
		public ModuleDeclarationImpl(QName moduleQName) {
			super();
			this.moduleQName = moduleQName;
			this.packageDeclarationList = AstModuleDeclaration.this.packageDeclarations.stream()
					.map(AstPackageDeclaration::getPackageDeclaration)
					.collect(Collectors.toList());
		}

		@Override
		public List<PackageDeclaration> getPackages() {
			return packageDeclarationList;
		}

		@Override
		public QName getQName() {
			return moduleQName;
		}

		@Override
		public PhysicalPath getPhysicalPath() {
			return modulePPath.get();	// TODO: check ???
		}
		@Override
		public String toString() {
			return "\"" + getQName().toString() + "\"";
		}
	}
	
	public ModuleDeclaration getModuleDeclaration() {
		
		if(moduleDeclaration == null) {
			moduleDeclaration = new ModuleDeclarationImpl(qName);		}
		return moduleDeclaration;
	}
	
}
