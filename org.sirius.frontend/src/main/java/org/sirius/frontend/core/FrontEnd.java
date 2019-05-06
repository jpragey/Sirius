package org.sirius.frontend.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.sirius.lang.SiriusLangPackage;
import org.sirius.frontend.symbols.GlobalSymbolTable;


/** Builder for frontend parsing.
 * 
 * Usage:
 * - create a Frntend (with a sitable Reporter);
 * - add input code with:
 *   - appendResourceInput : add input as java resource;
 *   - addSiriusLangPackage() : add all sirius.lang sources (as resources)
 *   - appendFileInput(): add a source file
 * 
 * 
 * @author jpragey
 *
 */
public class FrontEnd {
	private Reporter reporter;

	private SiriusLangPackage languagePackage = new SiriusLangPackage();
	
	private List<InputTextProvider> inputTextProviders = new ArrayList<>(); 
	
	public FrontEnd(Reporter reporter) {
		super();
		this.reporter = reporter;
	}
	
	public void appendProviderInput(InputTextProvider provider) {
		
		this.inputTextProviders.add(provider);
	}
	
	public void addSiriusLangPackage() {
		for(String fileName: SiriusLangPackage.getInputFileNames()) {
			appendProviderInput(new ResourceInputTextProvider(reporter, "language", "sirius.lang", fileName));
		}
	}

	private class PackageBuilder {
		private List<InputTextProvider> providers =new ArrayList<>();
		private PhysicalPath physicalPath;
		public PackageBuilder(PhysicalPath physicalPath) {
			super();
			this.physicalPath = physicalPath;
		}
		public void add(InputTextProvider inputTextProvider) {
			this.providers.add(inputTextProvider);
		}
	}
	private class ModuleBuilder {
		private Map<PhysicalPath, PackageBuilder> providers = new HashMap<>();
		
		private InputTextProvider moduleDescriptorProvider;
		public ModuleBuilder(InputTextProvider moduleDescriptorProvider) {
			super();
			this.moduleDescriptorProvider = moduleDescriptorProvider;
		}
		public void addProvider(InputTextProvider provider) {
			PhysicalPath path = provider.getPackagePhysicalPath();
			
			PackageBuilder packageBuilder = providers.get(path);
			if(packageBuilder == null) {
				packageBuilder = new PackageBuilder(path);
				this.providers.put(path, packageBuilder);
			}
			packageBuilder.add(provider);
		}
	}
	
	public List<ModuleContent> parseAll() {
		GlobalSymbolTable globalSymbolTable = new GlobalSymbolTable(); 
		
		// -- Create map <module path> -> <empty module builders>
		HashMap<PhysicalPath, ModuleBuilder> moduleBuilderMap = new HashMap<>();
		for(InputTextProvider provider: this.inputTextProviders) {
			if(provider.isModuleDescriptor()) {
				moduleBuilderMap.put(provider.getPackagePhysicalPath(), new ModuleBuilder(provider));
			}
		}
		
		// -- Add providers to their module, except module descriptors
		List<InputTextProvider> providersOutsideModules = new ArrayList<>();
		for(InputTextProvider provider: this.inputTextProviders) {
			if(!provider.isModuleDescriptor())
				continue;
			
			Optional<PhysicalPath> path = Optional.of(provider.getPackagePhysicalPath());
			ModuleBuilder mb = null;
			while(path.isPresent()) {
				PhysicalPath p = path.get();
				mb = moduleBuilderMap.get(path.get());
				if(mb != null)
					break;
				else
					path = p.getParent();
			}
			if(mb != null) {
				mb.addProvider(provider);
			} else {
				providersOutsideModules.add(provider);
				//reporter.error("" + provider.getInputLocation() + " does not belong to any module.");
			}
		}

		// -- Group InputTextProviders by package names
		HashMap<String, List<InputTextProvider>> packageContents = new LinkedHashMap<>();
		for(InputTextProvider provider: this.inputTextProviders) {
			
			String pkgName = provider.getPackagePhysicalName();
			List<InputTextProvider> list = packageContents.get(pkgName);
			if(list == null) {
				list = new ArrayList<>();
				packageContents.put(pkgName, list);
			}
			
			list.add(provider);
		}
		
		// -- create module contents
		List<ModuleContent> moduleContents = new ArrayList<>();
		for(Map.Entry<String, List<InputTextProvider>> e: packageContents.entrySet()) {
			String pkgName = e.getKey();
			List<InputTextProvider> providers = e.getValue();
			for(InputTextProvider provider: providers) {
				if(provider.isModuleDescriptor()) {
					ModuleContent content = ModuleContent.parseAll(reporter, pkgName, languagePackage, globalSymbolTable, providers);
					moduleContents.add(content);
					break;
				}
			}
		}

		return moduleContents;
	}
}
