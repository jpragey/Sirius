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
	
	private List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
	
	private String versionString;

	private ModuleDeclaration cachedModuleDeclaration = null;

	public AstModuleDeclaration(Reporter reporter, QName qualifiedName, AstToken version, ModuleImportEquivalents equiv, List<ModuleImport> moduleImports,
			List<AstPackageDeclaration> packageDeclarations) {
		super();
		this.reporter = reporter;

		this.qName = qualifiedName;
		PhysicalPath pp = new PhysicalPath(qName/*.toQName()*/.getStringElements());
		this.modulePPath = Optional.of(pp);

		this.version = version;
		
		this.equiv = equiv;
		this.moduleImports = moduleImports;
		this.packageDeclarations.addAll(packageDeclarations);
		
		String vs = version.getText();
		assert(vs.length() >=2);	// contains start/end double quotes
		this.versionString = vs.substring(1, vs.length()-1).trim();
	}

	public static AstModuleDeclaration createUnnamed(Reporter reporter, ModuleImportEquivalents equiv, List<ModuleImport> moduleImports, 
			List<AstPackageDeclaration> packageDeclarations) 
	{
		AstModuleDeclaration mod = new AstModuleDeclaration(reporter, new QName(), new AstToken(0,0,0,0,"\"\"",""), equiv, moduleImports, packageDeclarations);
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

	public QName getqName() {
		return qName;
	}
	
	public String getQnameString() {
		return qName.dotSeparated();
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
	
	public void addPackageDeclaration(AstPackageDeclaration pd) {
		this.packageDeclarations.add(pd);
	}

	
	public ModuleDeclaration getModuleDeclaration() {
		
		if(cachedModuleDeclaration == null) {
			cachedModuleDeclaration = new ModuleDeclarationImpl(qName);		}
		return cachedModuleDeclaration;
	}
	
}
