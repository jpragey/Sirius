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

	private ModuleImportEquivalents equiv = new ModuleImportEquivalents();
	
	private List<ModuleImport> moduleImports = new ArrayList<>(); 
	
	private ArrayList<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
	
	private String versionString;
	
	public AstModuleDeclaration(Reporter reporter, QName qualifiedName, AstToken version, ModuleImportEquivalents equiv, List<ModuleImport> moduleImports) {
		super();
		this.reporter = reporter;

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

	public AstModuleDeclaration(Reporter reporter, QName qualifiedName, AstToken version, ModuleImportEquivalents equiv, List<ModuleImport> moduleImports,
			List<AstPackageDeclaration> packageDeclarations) 
	{
		this(reporter, qualifiedName, version, equiv, moduleImports);
		this.packageDeclarations.addAll(packageDeclarations);
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
	
	/** Get verion, without start/end double quotes and trimmed.
	 * 
	 * @return
	 */
	public String getVersionString() {
		return versionString;
	}

	/** Add a new package, update current package reference
	 * 
	 * @param packageDeclaration
	 */
	public void addPackageDeclaration(AstPackageDeclaration packageDeclaration) {
		this.packageDeclarations.add(packageDeclaration);
	}
	
	public AstPackageDeclaration createPackageDeclaration(QName packageQName) {
		AstPackageDeclaration packageDeclaration = new AstPackageDeclaration(reporter, packageQName);
		addPackageDeclaration(packageDeclaration);
		return packageDeclaration;
	}
	
	
	public AstPackageDeclaration getCurrentPackage() {
		if(this.packageDeclarations.isEmpty()) {
			AstPackageDeclaration pkg = new AstPackageDeclaration(reporter, qName);	// default package
			this.packageDeclarations.add(pkg);
			return pkg;
		} else {
			return this.packageDeclarations.get(this.packageDeclarations.size()-1);
		}
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

	public void addPackageDeclarations(List<AstPackageDeclaration> packageDeclaration) {
		packageDeclaration.stream().forEach(d->addPackageDeclaration(d));
	}

	public void addContent(AstModuleContent content) {
		assert(content != null);
		content.getPackageDeclarations().stream().forEach(d->addPackageDeclaration(d));
		content.getInterfaceDeclarations().stream().forEach(d->addInterfaceDeclaration(d));
		content.getClassDeclarations().stream().forEach(d->addClassDeclaration(d));
		content.getFunctionsDeclarations().stream().forEach(d->addFunctionDeclaration(d));
	}

	public void addAllContent(List<AstModuleContent> content) {
		content.forEach(ct -> addContent(ct));
	}

	public void appendImport(ModuleImport moduleImport) {
		this.moduleImports.add(moduleImport);
	}

	public AstToken getVersion() {
		return version;
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
