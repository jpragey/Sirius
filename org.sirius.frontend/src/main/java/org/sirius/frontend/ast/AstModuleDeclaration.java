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

//	private Map<String, AstToken> equivalents = new HashMap<>();
	private ModuleImportEquivalents equiv = new ModuleImportEquivalents();
	
	private List<ModuleImport> moduleImports = new ArrayList<>(); 
	
	private List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
	
	private AstPackageDeclaration currentPackage;
	// version, without start-=/end double quotes/blanks
	private String versionString;
	
	public AstModuleDeclaration(Reporter reporter, QName qualifiedName, AstToken version, ModuleImportEquivalents equiv, List<ModuleImport> moduleImports) {
		super();
		this.reporter = reporter;
		this.currentPackage = new AstPackageDeclaration(reporter, qualifiedName);
		this.addPackageDeclaration(this.currentPackage);

		this.qName = qualifiedName;
		PhysicalPath pp = new PhysicalPath(qName/*.toQName()*/.getStringElements());
		this.modulePPath = Optional.of(pp);

		this.version = version;
		
		this.equiv = equiv;
		this.moduleImports = moduleImports;
		String vs = version.getText();
		assert(vs.length() >=2);	// contains start/end double quotes
		this.versionString = vs.substring(1, vs.length()-1).trim();
	}

	public AstModuleDeclaration(Reporter reporter, QName qualifiedName, Token version, ModuleImportEquivalents equiv, List<ModuleImport> moduleImports) {
		this(reporter, qualifiedName, new AstToken(version), equiv, moduleImports);
	}
	
	public static AstModuleDeclaration createUnnamed(Reporter reporter, ModuleImportEquivalents equiv, List<ModuleImport> moduleImports) {
		AstModuleDeclaration mod = new AstModuleDeclaration(reporter, new QName(), new AstToken(0,0,0,0,"\"\"",""), equiv, moduleImports);
		return mod;
	}

	
	
	public ModuleImportEquivalents getEquivalents() {
		return equiv;
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
	
	/** Get verion, without start/end double quotes and trimmed.
	 * 
	 * @return
	 */
	public String getVersionString() {
		return versionString;
//		return version.getText();
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
	
	
	
	
	public void addFunctionDeclaration(PartialList d) {
		this.getCurrentPackage().addFunctionDeclaration(d);
	}
	public void addClassDeclaration(AstClassDeclaration d) {
		this.getCurrentPackage().addClassDeclaration(d);
	}
	public void addInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
		this.getCurrentPackage().addInterfaceDeclaration(interfaceDeclaration);
	}

	public void appendImport(ModuleImport moduleImport) {
//		ModuleImport moduleImport = new ModuleImport(reporter, shared, equivalents);
		this.moduleImports.add(moduleImport);
//		return moduleImport;
		
	}

	public AstToken getVersion() {
		return version;
	}

//	public Map<String, AstToken> getEquivalents() {
//		return equivalents;
//	}
//
//	public void setEquivalents(Map<String, AstToken> equivalents) {
//		this.equivalents = equivalents;
//	}

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
