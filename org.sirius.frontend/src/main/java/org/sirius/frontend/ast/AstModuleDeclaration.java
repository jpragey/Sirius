package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.Constants;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.apiimpl.ModuleDeclarationImpl;
import org.sirius.frontend.core.PhysicalPath;

public class AstModuleDeclaration implements Visitable, Verifiable {

//	private QName qName = new QName();
	private Optional<QName> qName;
	private AstToken version = new AstToken(0,0,0,0,"","");
	
	private Reporter reporter; 
	
	private Optional<PhysicalPath> modulePPath = Optional.empty(); 

	private ModuleImportEquivalents equiv = new ModuleImportEquivalents();
	
	private List<ModuleImport> moduleImports = new ArrayList<>(); 
	
	private List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
	private Map<QName, AstPackageDeclaration> packageDeclarationByQName = new HashMap<>();
	
	
	private String versionString;

	private ModuleDeclaration cachedModuleDeclaration = null;
	private List<AstToken> comments;

	public AstModuleDeclaration(Reporter reporter, Optional<QName> qualifiedName, AstToken version, ModuleImportEquivalents equiv, List<ModuleImport> moduleImports,
			List<AstPackageDeclaration> packageDeclarations, List<AstToken> comments) {
		super();
		this.reporter = reporter;

		this.qName = qualifiedName;
		
//		PhysicalPath pp = new PhysicalPath(qName/*.toQName()*/.getStringElements());
//		this.modulePPath = Optional.of(pp);
		this.modulePPath = qName.map(qn -> new PhysicalPath(qn.getStringElements()));

		
		this.version = version;
		
		this.equiv = equiv;
		this.moduleImports = moduleImports;
		for(AstPackageDeclaration pd: packageDeclarations)
			addPackageDeclaration(pd);
		
		String vs = version.getText();
		assert(vs.length() >=2);	// contains start/end double quotes
		this.versionString = vs.substring(1, vs.length()-1).trim();
		
		this.comments = comments;
	}

	public static AstModuleDeclaration createUnnamed(Reporter reporter, ModuleImportEquivalents equiv, List<ModuleImport> moduleImports, 
			List<AstPackageDeclaration> packageDeclarations) 
	{
		Optional<QName> moduleQName = Optional.of(Constants.topLevelModuleQName);
		AstModuleDeclaration mod = new AstModuleDeclaration(reporter, moduleQName, new AstToken(0,0,0,0,"\"\"",""), equiv, moduleImports, packageDeclarations,
				List.<AstToken>of() /*comments*/);
		return mod;
	}

	@Override
	public void verify(int featureFlags) {
		Verifiable.super.optionalIsPresent(modulePPath, "modulePPath");

		equiv.verify(featureFlags);
		verifyList(moduleImports, featureFlags);
		verifyList(packageDeclarations, featureFlags);
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

	/** Get verion, without start/end double quotes and trimmed.
	 * 
	 * @return
	 */
	public String getVersionString() {
		return versionString;
	}

	public void appendImport(ModuleImport moduleImport) {
		this.moduleImports.add(moduleImport);
	}

	public AstToken getVersion() {
		return version;
	}

	public Optional<QName> getqName() {
		return qName;
	}
	
	public String getQnameString() {
		return qName
				.map(qn -> qn.dotSeparated())
				.orElse("");
	}


	public List<AstToken> getComments() {
		return comments;
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
		cachedModuleDeclaration = null;
	}
	
	@Override
	public String toString() {
		return "[Mod:" + qName.toString() + "-" + version.getText() + "]";
	}
	
	
 
	public void addPackageDeclaration(AstPackageDeclaration pd) {
		this.packageDeclarations.add(pd);
		QName pkgQname = pd.getQname()
				.orElse(new QName("" /** TODO : ??? */));
		this.packageDeclarationByQName.put(pkgQname, pd);
	}
	
	public AstPackageDeclaration getPackage(QName qname) {
		AstPackageDeclaration pd = this.packageDeclarationByQName.get(qname);
		assert(pd != null);
		return pd;
	}

	
	public ModuleDeclaration getModuleDeclaration() {
		
		if(cachedModuleDeclaration == null) {
			List<PackageDeclaration> packageDeclarationList = AstModuleDeclaration.this.packageDeclarations.stream()
					.map(AstPackageDeclaration::getPackageDeclaration)
					.collect(Collectors.toList());
			
			cachedModuleDeclaration = new ModuleDeclarationImpl(qName, 
					versionString,
					modulePPath.get(),	// TODO: check ???
					packageDeclarationList);
			}
		return cachedModuleDeclaration;
	}
	
}
